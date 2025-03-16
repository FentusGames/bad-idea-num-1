package launchers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

public class GeneProcessor {
	// CMD

	/* @formatter:off

	set OLLAMA_FLASH_ATTENTION=true
	set OLLAMA_GPU_OVERHEAD=0
	set OLLAMA_NUM_PARALLEL=8
	set OLLAMA_KV_CACHE_TYPE=auto
	set OLLAMA_LLM_LIBRARY=cuda
	set OLLAMA_MAX_LOADED_MODELS=1
	
	@formatter:on */

	// ollama serve

	private static final Pattern ENCODED_SYMBOL_PATTERN = Pattern.compile("^[ATCG]{20}$");
	private static final Pattern GENE_ID_PATTERN = Pattern.compile("^\\d{10}$");
	private static final Set<String> REQUIRED_KEYS = Set.of("description", "diseases", "encodedSymbol", "gene_id");

	private static final String OLLAMA_API_URL = "http://localhost:11434/api/chat";
	private static final String MODEL_NAME = "gemma3:1b";
	private static final HttpClient httpClient = HttpClient.newHttpClient();
	private static final String INPUT_FILE_PATH = "gene_info"; // Input file
	private static final String OUTPUT_FILE_PATH = "genes_output.json"; // Output JSON file

	public static void main(String[] args) {
		// Clear the JSON file at start
		try (BufferedWriter clearWriter = new BufferedWriter(new FileWriter(OUTPUT_FILE_PATH, StandardCharsets.UTF_8))) {
			clearWriter.write("[]"); // Initialize as an empty JSON array
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		try (BufferedReader reader = new BufferedReader(new FileReader(INPUT_FILE_PATH, StandardCharsets.UTF_8)); RandomAccessFile file = new RandomAccessFile(OUTPUT_FILE_PATH, "rw")) {
			String line;
			boolean firstLine = true;
			int lineNumber = 0;

			// Move to the position to start appending inside JSON array
			file.seek(file.length() - 1);
			if (file.length() > 2)
				file.writeBytes(",");

			while ((line = reader.readLine()) != null) {
				if (firstLine) {
					firstLine = false;
					continue; // Skip the header row
				}
				lineNumber++;

				// Parse line data
				String[] columns = line.split("\t");
				if (columns.length < 15)
					continue; // Skip malformed rows

				String geneId = columns[1];
				String symbol = columns[2];
				String locusTag = columns[3];
				String description = columns[8];

				// Print progress
				System.out.println("Processing line " + lineNumber + ": GeneID " + geneId + " (" + symbol + ")");

				// Generate gene-specific metadata
				String geneIdPadded = String.format("%010d", Integer.parseInt(geneId));
				String encodedSymbol = generateATCGCode(symbol);

				// Query Ollama API
				String prompt = createPrompt(geneId, symbol, locusTag, description);
				String ollamaResponse = queryOllama(prompt);

				if (!ollamaResponse.isEmpty()) {
					JSONObject jsonObject = processJsonResponse(ollamaResponse, geneIdPadded, encodedSymbol);

					System.out.println(jsonObject);

					try {
						if (validateGeneEntry(jsonObject)) {
							if (jsonObject != null) {
								// Write each record to JSON file immediately
								file.writeBytes("\n\t" + jsonObject.toString() + ",\n");
								file.seek(file.length() - 1); // Adjust file pointer for the next write
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			// Close JSON array properly
			file.writeBytes("]");

			System.out.println("JSON processing complete. Data saved to: " + OUTPUT_FILE_PATH);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static JSONObject processJsonResponse(String ollamaResponse, String geneIdPadded, String encodedSymbol) {
		String extractedJson = extractJson(ollamaResponse);
		if (extractedJson != null) {
			try {
				JSONObject jsonObject = new JSONObject(extractedJson);

				jsonObject.put("gene_id", geneIdPadded);
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

		if (start != -1 && end != -1 && start < end) {
			return line.substring(start + 7, end).trim(); // Extract JSON portion
		}
		return null;
	}

	private static String generateATCGCode(String symbol) {
		char[] bases = { 'A', 'T', 'C', 'G' };
		StringBuilder sequence = new StringBuilder(10);

		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(symbol.getBytes(StandardCharsets.UTF_8));

			for (int i = 0; i < 20; i++) {
				sequence.append(bases[Math.abs(hash[i] % 4)]); // Map hash bytes to A, T, C, G
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "AAAAAAAAAA"; // Fallback if hashing fails
		}

		return sequence.toString();
	}

	private static String createPrompt(String geneId, String symbol, String locusTag, String description) {
		return String.format("""
                Quickly Answer:
                
                Follow the output format exactly.
                Provide a brief description of this gene's function, no more than 10 words. 
                Then, list up to three plausible or possible diseases, disorders, infections, etc... 
                If no known information exists, return an empty JSON object {}.

                Description: [%s].

                Output format example:
                {
                  "description": "Regulates cell wall synthesis, essential for bacterial survival.",
                  "diseases": ["Pneumonia", "Septicemia", "Bacterial Endocarditis"]
                }
                If no diseases are known, return:
                {}""", description);
	}

	private static String queryOllama(String prompt) {
		try {
			// Ensure the prompt is JSON-safe
			prompt = prompt.replace("\"", "\\\"").replace("\n", " ").replace("\r", " ");

			String jsonRequest = """
                {
                    "model": "%s",
                    "messages": [{"role": "user", "content": "%s"}],
                    "stream": false
                }
                """.formatted(MODEL_NAME, prompt);

			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(OLLAMA_API_URL)).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(jsonRequest)).build();

			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

			// Parse JSON response
			JSONObject jsonResponse = new JSONObject(response.body());

			// Extract response text (if available)
			if (jsonResponse.has("message") && jsonResponse.getJSONObject("message").has("content")) {
				String rawResponse = jsonResponse.getJSONObject("message").getString("content").trim();

				// Ensure the response is structured correctly, otherwise return an empty JSON object
				if (rawResponse.contains("```json") && rawResponse.contains("```")) {
					return rawResponse.replace("\n", " ").replace("\r", " "); // Keep response clean
				}
			}
			return "{}"; // If no valid response, return an empty JSON object
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
		return "{}";
	}

	public static boolean validateGeneEntry(JSONObject geneObject) {
		if (geneObject == null) {
			System.err.println("Validation failed: JSON object is null.");
			return false;
		}

		// Ensure the object contains ONLY the required fields
		if (!geneObject.keySet().equals(REQUIRED_KEYS)) {
			System.err.println("Validation failed: Object contains unexpected fields -> " + geneObject.keySet());
			return false;
		}

		// Validate 'description'
		if (!geneObject.has("description") || !(geneObject.get("description") instanceof String) || geneObject.getString("description").isEmpty()) {
			System.err.println("Validation failed: 'description' must be a non-empty string.");
			return false;
		}

		// Validate 'diseases'
		if (!geneObject.has("diseases") || !(geneObject.get("diseases") instanceof JSONArray)) {
			System.err.println("Validation failed: 'diseases' must be a JSON array.");
			return false;
		}
		JSONArray diseasesArray = geneObject.getJSONArray("diseases");
		for (int i = 0; i < diseasesArray.length(); i++) {
			if (!(diseasesArray.get(i) instanceof String)) {
				System.err.println("Validation failed: 'diseases' array must contain only strings.");
				return false;
			}
		}

		// Validate 'encodedSymbol'
		if (!geneObject.has("encodedSymbol") || !(geneObject.get("encodedSymbol") instanceof String)) {
			System.err.println("Validation failed: 'encodedSymbol' must be a string.");
			return false;
		}
		String encodedSymbol = geneObject.getString("encodedSymbol");
		if (!ENCODED_SYMBOL_PATTERN.matcher(encodedSymbol).matches()) {
			System.err.println("Validation failed: 'encodedSymbol' must be exactly 20 characters of A, T, C, G.");
			return false;
		}

		// Validate 'gene_id'
		if (!geneObject.has("gene_id") || !(geneObject.get("gene_id") instanceof String)) {
			System.err.println("Validation failed: 'gene_id' must be a string.");
			return false;
		}
		String geneId = geneObject.getString("gene_id");
		if (!GENE_ID_PATTERN.matcher(geneId).matches()) {
			System.err.println("Validation failed: 'gene_id' must be a 10-digit string.");
			return false;
		}

		// All checks passed, valid entry
		return true;
	}
}
