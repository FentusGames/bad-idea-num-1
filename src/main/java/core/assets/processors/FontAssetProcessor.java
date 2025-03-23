package core.assets.processors;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import core.assets.AssetProcessor;
import imgui.ImFont;
import imgui.ImGui;
import imgui.ImGuiIO;

public class FontAssetProcessor implements AssetProcessor<Map<String, ImFont>> {
	private static final Logger logger = LoggerFactory.getLogger(FontAssetProcessor.class);

	private final Map<String, ImFont> fonts = new HashMap<>();
	private final List<Path> collectedFiles = new ArrayList<>();

	@Override
	public boolean isSupportedFile(Path path) {
		return path.toString().toLowerCase().endsWith(".ttf");
	}

	@Override
	public void processFiles(List<Path> files) {
		collectedFiles.clear();
		collectedFiles.addAll(files);
		logger.info("Collected " + collectedFiles.size() + " font file(s).");
	}

	public void process() {
		ImGuiIO io = ImGui.getIO();

		for (Path file : collectedFiles) {
			String filename = file.getFileName().toString();
			String name = filename.substring(0, filename.lastIndexOf('.')).toLowerCase();

			String out = "";
			for (int size = 14; size <= 64; size += 2) {
				ImFont font = io.getFonts().addFontFromFileTTF(file.toAbsolutePath().toString(), size);
				if (font != null) {
					String key = "fonts_" + name + "_" + size;
					fonts.put(key, font);
					out += key + (size < 64 ? ", " : "");
				}
			}

			logger.info("Loaded Font Keys: {}", out);
		}

		collectedFiles.clear();
	}

	public ImFont getFont(String name, int size) {
		return fonts.get("fonts_" + name + "_" + size);
	}
}
