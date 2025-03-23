package core.assets;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

public class Language {
	private static final Logger logger = LoggerFactory.getLogger(Language.class);

	private Map<String, String> language;

	public void loadFrom(Path folderPath) {
		if (!Files.exists(folderPath)) {
			logger.warn("Language folder does not exist: {}", folderPath.toAbsolutePath());
			return;
		}

		try (Stream<Path> stream = Files.walk(folderPath)) {
			Path langFile = stream.filter(Files::isRegularFile).filter(path -> {
				String name = path.getFileName().toString().toLowerCase();
				return name.endsWith(".yml") || name.endsWith(".yaml");
			}).findFirst().orElse(null);

			if (langFile == null) {
				logger.warn("No language YAML file found in: {}", folderPath);
				return;
			}

			Yaml yaml = new Yaml();
			try (InputStream input = Files.newInputStream(langFile)) {
				Object loaded = yaml.load(input);

				if (loaded instanceof Map) {
					@SuppressWarnings("unchecked")
					Map<String, String> parsed = (Map<String, String>) loaded;
					this.language = parsed;
					logger.info("Loaded language file: {}", langFile.getFileName());
				} else {
					logger.warn("Unexpected YAML root type (expected Map): {}", loaded.getClass().getSimpleName());
				}
			}
		} catch (Exception e) {
			logger.error("Failed to load YAML file from: {}", folderPath, e);
		}
	}

	public String get(String key) {
		return language != null ? language.get(key) : null;
	}
}
