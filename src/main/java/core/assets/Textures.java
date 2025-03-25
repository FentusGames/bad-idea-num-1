package core.assets;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.stb.STBImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import core.texture.Texture;

public class Textures {
	private static final Logger logger = LoggerFactory.getLogger(Textures.class);
	private final Map<String, Texture> textures = new HashMap<>();

	public void loadFrom(Path folderPath) {
		if (!Files.exists(folderPath)) {
			logger.warn("Texture folder does not exist: {}", folderPath.toAbsolutePath());
			return;
		}

		try (Stream<Path> stream = Files.walk(folderPath)) {
			var textureFiles = stream.filter(Files::isRegularFile).filter(path -> {
				String name = path.getFileName().toString().toLowerCase();
				return name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg");
			}).collect(Collectors.toList());

			for (Path file : textureFiles) {
				String filename = file.getFileName().toString();
				String name = filename.substring(0, filename.lastIndexOf('.')).toLowerCase();
				String key = "graphics_" + file.getParent().getFileName().toString() + "_" + name;

				Texture texture = loadTexture(file);
				if (texture != null) {
					textures.put(key, texture);
					logger.info("Loaded Texture: {} -> Key: {}", file.getFileName(), key);
				} else {
					logger.warn("Failed to load texture: {}", file.toAbsolutePath());
				}
			}
		} catch (Exception e) {
			logger.error("Failed to load textures from: {}", folderPath, e);
		}
	}

	private Texture loadTexture(Path filePath) {
		IntBuffer widthBuffer = BufferUtils.createIntBuffer(1);
		IntBuffer heightBuffer = BufferUtils.createIntBuffer(1);
		IntBuffer channelsBuffer = BufferUtils.createIntBuffer(1);

		STBImage.stbi_set_flip_vertically_on_load(true);
		ByteBuffer imageData = STBImage.stbi_load(filePath.toAbsolutePath().toString(), widthBuffer, heightBuffer, channelsBuffer, 4);

		if (imageData == null) {
			logger.error("Failed to load texture: {} - {}", filePath, STBImage.stbi_failure_reason());
			return null;
		}

		int width = widthBuffer.get();
		int height = heightBuffer.get();
		int textureID = GL11.glGenTextures();

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);

		// Texture wrapping and filtering
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL13.GL_CLAMP_TO_BORDER);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL13.GL_CLAMP_TO_BORDER);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

		// Upload texture data
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, imageData);
		// GL11.glGenerateMipmap(GL11.GL_TEXTURE_2D);

		// Unbind
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

		// Free image memory
		STBImage.stbi_image_free(imageData);

		return new Texture(textureID, width, height);
	}

	public Texture getTexture(String key) {
		return textures.get(key);
	}

	public boolean contains(String key) {
		return textures.containsKey(key);
	}
}
