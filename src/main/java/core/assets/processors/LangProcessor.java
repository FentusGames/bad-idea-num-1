package core.assets.processors;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import core.assets.AssetProcessor;

public class LangProcessor implements AssetProcessor<Map<String, Object>> {
	private static final Logger logger = LoggerFactory.getLogger(LangProcessor.class);

	private final List<Path> collectedFiles = new ArrayList<>();
	private Map<String, String> language;

	@Override
	public boolean isSupportedFile(Path path) {
		String name = path.getFileName().toString().toLowerCase();
		return name.endsWith(".yml") || name.endsWith(".yaml");
	}

	@Override
	public void processFiles(List<Path> files) {
		collectedFiles.clear();
		collectedFiles.addAll(files);
		logger.info("Collected language file.");

		process();
	}

	public void process() {
		if (collectedFiles.isEmpty()) {
			logger.warn("No language files to process.");
			return;
		}

		Path file = collectedFiles.get(0); // Only the first YAML file is loaded
		Yaml yaml = new Yaml();

		try (InputStream input = Files.newInputStream(file)) {
			Object loaded = yaml.load(input);

			if (loaded instanceof Map) {
				@SuppressWarnings("unchecked")
				Map<String, String> parsed = (Map<String, String>) loaded;
				this.language = parsed;
				logger.info("Loaded language file: {}", file.getFileName());
			} else {
				logger.warn("Unexpected YAML root type (expected Map): {}", loaded.getClass().getSimpleName());
			}
		} catch (Exception e) {
			logger.error("Failed to load YAML file: {}", file.getFileName(), e);
		}

		collectedFiles.clear();
	}

	public String get(String key) {
		return language.get(key);
	}
}
