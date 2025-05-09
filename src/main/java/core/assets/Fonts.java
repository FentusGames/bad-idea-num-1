package core.assets;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import imgui.ImFont;
import imgui.ImGui;
import imgui.ImGuiIO;

public class Fonts {
	private static final Logger logger = LoggerFactory.getLogger(Fonts.class);

	private final Map<String, ImFont> fonts = new HashMap<>();

	private static final int MIN = 12;
	private static final int MAX = 64;
	private static final int STEP = 1;

	public void loadFrom(Path folderPath) {
		if (!Files.exists(folderPath)) {
			logger.warn("Font folder does not exist: {}", folderPath.toAbsolutePath());
			return;
		}

		try (Stream<Path> stream = Files.walk(folderPath)) {
			var fontFiles = stream.filter(Files::isRegularFile).filter(path -> path.toString().toLowerCase().endsWith(".ttf")).collect(Collectors.toList());

			ImGuiIO io = ImGui.getIO();

			for (Path file : fontFiles) {
				String filename = file.getFileName().toString();
				String name = filename.substring(0, filename.lastIndexOf('.')).toLowerCase();

				StringBuilder loadedKeys = new StringBuilder();
				for (int size = MIN; size <= MAX; size += STEP) {
					ImFont font = io.getFonts().addFontFromFileTTF(file.toAbsolutePath().toString(), size);
					if (font != null) {
						String key = "fonts_" + name + "_" + size;
						fonts.put(key, font);
						if (size == MIN || size == MAX) {
							loadedKeys.append(key).append(size < MAX ? " to " : "");
						}
					}
				}
				logger.info("Loaded Font Keys: {}", loadedKeys);
			}
		} catch (Exception e) {
			logger.error("Failed to load fonts from: {}", folderPath, e);
		}
	}

	public ImFont getFont(String name, int size) {
		size = Math.max(MIN, Math.min(MAX, size));
		return fonts.get("fonts_" + name + "_" + size);
	}
}
