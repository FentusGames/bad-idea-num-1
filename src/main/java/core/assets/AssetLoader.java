package core.assets;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class AssetLoader {

	private final Map<String, AssetProcessor<?>> processors = new HashMap<>();

	public void registerProcessor(String folder, AssetProcessor<?> processor) {
		processors.put(folder, processor);
	}

	public void loadAssets(String baseFolderPath) {
		for (Map.Entry<String, AssetProcessor<?>> entry : processors.entrySet()) {
			String folder = entry.getKey();
			AssetProcessor<?> processor = entry.getValue();
			String fullPath = baseFolderPath + "/" + folder;

			try {
				List<Path> files = listFiles(Paths.get(fullPath), processor);
				processor.processFiles(files);
			} catch (IOException e) {
				System.err.println("[AssetLoader] Failed to load assets from: " + fullPath);
				e.printStackTrace();
			}
		}
	}

	private List<Path> listFiles(Path basePath, AssetProcessor<?> processor) throws IOException {
		List<Path> result = new ArrayList<>();

		if (!Files.exists(basePath)) {
			throw new IOException("Asset folder not found: " + basePath.toAbsolutePath());
		}

		try (Stream<Path> stream = Files.walk(basePath)) {
			stream.filter(Files::isRegularFile).filter(processor::isSupportedFile).forEach(result::add);
		}

		return result;
	}
}
