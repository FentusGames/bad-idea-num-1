package core.texture;

import static org.lwjgl.opengl.GL11.*;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Animation {
	private static final Logger logger = LoggerFactory.getLogger(Animation.class);
	
	private final List<Texture> frames;
	private final Texture colorMask;
	private int currentFrame;
	private float frameTime;
	private float elapsedTime;

	public Animation(List<Texture> frames, Texture colorMask) {
		this.frames = frames;
		this.colorMask = colorMask;
		this.currentFrame = 0;
		this.frameTime = 30F; // Default frame duration in seconds
		this.elapsedTime = 0;
	}

	public void update(float deltaTime) {
		elapsedTime += deltaTime;
		if (elapsedTime >= frameTime) {
			elapsedTime = 0;
			currentFrame = (currentFrame + 1) % frames.size();
		}
	}

	public void render(float x, float y, float width, float height) {
		Texture frame = frames.get(currentFrame);
		if (frame == null || frame.getID() == 0) {
			logger.warn("Attempted to render a null or invalid frame.");
			return;
		}

		frame.bind();
		drawQuad(x, y, width, height);
		frame.unbind();

		if (colorMask != null) {
			colorMask.bind();
			drawQuad(x, y, width, height);
			colorMask.unbind();
		}
	}

	private void drawQuad(float x, float y, float width, float height) {
		glBegin(GL_QUADS);
		glTexCoord2f(0, 0);
		glVertex2f(x, y);
		glTexCoord2f(1, 0);
		glVertex2f(x + width, y);
		glTexCoord2f(1, 1);
		glVertex2f(x + width, y + height);
		glTexCoord2f(0, 1);
		glVertex2f(x, y + height);
		glEnd();
	}

	public void setFrameTime(float frameTime) {
		this.frameTime = frameTime;
	}
}
