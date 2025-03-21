package launchers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

public class GeneProcessor {
	private static final Pattern ENCODED_SYMBOL_PATTERN = Pattern.compile("^[ATCG]{20}$");
	private static final Set<String> REQUIRED_KEYS = Set.of("description", "disease", "encodedSymbol", "gene_id");

	private static final String OLLAMA_API_URL = "http://localhost:11434/api/chat";
	private static final String MODEL_NAME = "gemma3:1b";
	private static final String INPUT_FILE_PATH = "gene_info";

	private static final String DB_URL = "jdbc:sqlite:game.db";
	private static final int THREAD_COUNT = 4;

	public static void main(String[] args) {
		setupDatabase();

		try (BufferedReader reader = new BufferedReader(new FileReader(INPUT_FILE_PATH, StandardCharsets.UTF_8))) {
			String line;
			boolean firstLine = true;
			int lineNumber = 0;

			while ((line = reader.readLine()) != null) {
				if (firstLine) {
					firstLine = false;
					continue; // Skip header row
				}
				lineNumber++;

				final int ln = lineNumber;
				String[] columns = line.split("\t");
				if (columns.length < 15)
					continue;

				String geneId = columns[1];
				String symbol = columns[2];
				String locusTag = columns[3];
				String description = columns[8];

				// Create a new thread for each line and process it
				ExecutorService singleThreadExecutor = Executors.newFixedThreadPool(THREAD_COUNT);
				singleThreadExecutor.submit(() -> {
					try {
						processGeneEntry(ln, geneId, symbol, locusTag, description);
					} finally {
						singleThreadExecutor.shutdown();
					}
				});

				try {
					singleThreadExecutor.awaitTermination(1, java.util.concurrent.TimeUnit.MINUTES);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void setupDatabase() {
		try (Connection conn = DriverManager.getConnection(DB_URL); Statement stmt = conn.createStatement()) {
			String createTableSQL = """
                CREATE TABLE IF NOT EXISTS genes (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    gene_id TEXT NOT NULL,
                    encodedSymbol TEXT NOT NULL,
                    description TEXT,
                    disease TEXT
                );
                """;
			stmt.execute(createTableSQL);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void processGeneEntry(int lineNumber, String geneId, String symbol, String locusTag, String description) {
		HttpClient threadHttpClient = HttpClient.newHttpClient(); // New client per thread

		try {
			System.out.println("Processing line " + lineNumber + ": GeneID " + geneId + " (" + symbol + ")");

			String encodedSymbol = generateATCGCode(symbol);
			String prompt = createPrompt(description);
			String ollamaResponse = queryOllama(threadHttpClient, prompt);

			System.out.println(ollamaResponse);
			
			if (!ollamaResponse.isEmpty()) {
				JSONObject jsonObject = processJsonResponse(ollamaResponse, geneId, encodedSymbol);
				if (jsonObject != null && validateGeneEntry(jsonObject)) {
					saveToDatabase(jsonObject);
				}
			}
		} finally {
			// Cleanup
			threadHttpClient = null;
		}
	}

	private static void saveToDatabase(JSONObject geneObject) {
		String sql = "INSERT INTO genes (gene_id, encodedSymbol, description, disease) VALUES (?, ?, ?, ?)";
		try (Connection conn = DriverManager.getConnection(DB_URL); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, geneObject.getString("gene_id"));
			pstmt.setString(2, geneObject.getString("encodedSymbol"));
			pstmt.setString(3, geneObject.getString("description"));
			pstmt.setString(4, geneObject.getString("disease"));

			pstmt.executeUpdate();
			System.out.println("Saved to DB: " + geneObject.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static JSONObject processJsonResponse(String ollamaResponse, String geneId, String encodedSymbol) {
		String extractedJson = extractJson(ollamaResponse);
		if (extractedJson != null) {
			try {
				JSONObject jsonObject = new JSONObject(extractedJson);
				jsonObject.put("gene_id", geneId);
				jsonObject.put("encodedSymbol", encodedSymbol);
				return jsonObject;
			} catch (Exception e) {
				System.err.println("Error parsing JSON: " + extractedJson);
				e.printStackTrace();
			}
		}
		return null;
	}

	private static String extractJson(String line) {
		int start = line.indexOf("```json");
		int end = line.lastIndexOf("```");
		return (start != -1 && end != -1 && start < end) ? line.substring(start + 7, end).trim() : null;
	}

	private static String generateATCGCode(String symbol) {
		char[] bases = { 'A', 'T', 'C', 'G' };
		StringBuilder sequence = new StringBuilder(20);
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(symbol.getBytes(StandardCharsets.UTF_8));
			for (int i = 0; i < 20; i++) {
				sequence.append(bases[Math.abs(hash[i] % 4)]);
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "AAAAAAAAAA";
		}
		return sequence.toString();
	}

	private static String createPrompt(String description) {
		return String.format("""
                Quickly Answer:
                
                Provide a brief description of this gene's function, no more than 10 words. 
                Then, come up with a funny unrealistic and grotest disease, infection and or mutation.
                If no known information exists, return an empty JSON object {}.
                
                Description: [%s].
                
                Output format example:
                {
                  "description": "Regulates cell wall synthesis.",
                  "disease": "Pneumonia"
                }""", description);
	}

	private static String queryOllama(HttpClient client, String prompt) {
		try {
			String jsonRequest = """
                {
                    "model": "%s",
                    "messages": [{"role": "user", "content": "%s"}],
                    "stream": false
                }
                """.formatted(MODEL_NAME, prompt.replace("\"", "\\\"").replace("\n", " ").replace("\r", " "));

			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(OLLAMA_API_URL)).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(jsonRequest)).build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			JSONObject jsonResponse = new JSONObject(response.body());
			return jsonResponse.has("message") ? jsonResponse.getJSONObject("message").getString("content").trim() : "{}";
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
		return "{}";
	}

	public static boolean validateGeneEntry(JSONObject geneObject) {
		return geneObject != null && geneObject.keySet().equals(REQUIRED_KEYS) && geneObject.get("description") instanceof String && geneObject.get("disease") instanceof String && ENCODED_SYMBOL_PATTERN.matcher(geneObject.getString("encodedSymbol")).matches();
	}
}