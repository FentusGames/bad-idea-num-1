package core.assets;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.stb.STBImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import core.texture.Animation;
import core.texture.Texture;

public class Animations {
	private static final Logger logger = LoggerFactory.getLogger(Animations.class);
	private final Map<String, Animation> animations = new HashMap<>();

	public void loadFrom(Path folderPath) {
		if (!Files.exists(folderPath)) {
			logger.warn("Texture folder does not exist: {}", folderPath.toAbsolutePath());
			return;
		}

		try (Stream<Path> stream = Files.walk(folderPath)) {
			Map<String, List<Path>> groupedFiles = stream.filter(Files::isRegularFile).filter(path -> path.toString().toLowerCase().endsWith(".png")).collect(Collectors.groupingBy(path -> folderPath.relativize(path.getParent()).toString().toLowerCase().replace("\\", "_")));

			for (Map.Entry<String, List<Path>> entry : groupedFiles.entrySet()) {
				String folderKey = entry.getKey();
				List<Path> images = entry.getValue();

				images.sort(Comparator.comparingInt(this::extractFrameNumber));
				Path colorMask = images.stream().filter(p -> p.getFileName().toString().equalsIgnoreCase("ColorMask.png")).findFirst().orElse(null);
				images.remove(colorMask);

				List<Texture> frames = images.stream().map(this::loadTexture).filter(Objects::nonNull).toList();
				Texture maskTexture = (colorMask != null) ? loadTexture(colorMask) : null;

				if (!frames.isEmpty()) {
					String baseFolderName = folderPath.getFileName().toString().toLowerCase();
					String key = baseFolderName + "_" + folderKey;
					Animation animation = new Animation(frames, maskTexture);
					animations.put(key, animation);
					logger.info("Loaded Animation: {} -> Key: {} with {} frames", folderKey, key, frames.size());
				}
			}
		} catch (Exception e) {
			logger.error("Failed to load animations from: {}", folderPath, e);
		}
	}

	private int extractFrameNumber(Path path) {
		String name = path.getFileName().toString().replaceAll("\\D+", "");
		return name.isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(name);
	}

	private Texture loadTexture(Path filePath) {
		IntBuffer widthBuffer = BufferUtils.createIntBuffer(1);
		IntBuffer heightBuffer = BufferUtils.createIntBuffer(1);
		IntBuffer channelsBuffer = BufferUtils.createIntBuffer(1);

		ByteBuffer imageData = STBImage.stbi_load(filePath.toAbsolutePath().toString(), widthBuffer, heightBuffer, channelsBuffer, 4);

		if (imageData == null) {
			logger.error("Failed to load texture: {} - {}", filePath, STBImage.stbi_failure_reason());
			return null;
		}

		int width = widthBuffer.get();
		int height = heightBuffer.get();
		int textureID = GL11.glGenTextures();

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL13.GL_CLAMP_TO_BORDER);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL13.GL_CLAMP_TO_BORDER);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, imageData);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		STBImage.stbi_image_free(imageData);

		return new Texture(textureID, width, height);
	}

	public Animation getAnimation(String key) {
		Animation animation = animations.get(key);
		if (animation == null) {
			logger.warn("Animation not found for key: {}", key);
		}
		return animation;
	}
}