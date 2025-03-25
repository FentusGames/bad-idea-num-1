package core.texture;

import static org.lwjgl.opengl.GL11.*;

import java.util.List;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Animation {
	private static final Logger logger = LoggerFactory.getLogger(Animation.class);

	private Vector3f pos = new Vector3f(0, 0, 0);
	private Quaternionf rotation = new Quaternionf();

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

	public void render() {
		Texture frame = frames.get(currentFrame);
		if (frame == null || frame.getID() == 0) {
			logger.warn("Attempted to render a null or invalid frame.");
			return;
		}

		frame.bind();
		drawQuad(frame, pos, rotation);
		frame.unbind();

		if (colorMask != null) {
			colorMask.bind();
			drawQuad(frame, pos, rotation);
			colorMask.unbind();
		}
	}

	private void drawQuad(Texture texture, Vector3f position, Quaternionf rotation) {
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		glTranslatef(position.x, position.y, position.z);
		glRotatef(rotation.x, 1, 0, 0);
		glRotatef(rotation.y, 0, 1, 0);
		glRotatef(rotation.z, 0, 0, 1);

		glBegin(GL_QUADS);
		glTexCoord2f(0, 0);
		glVertex2f(-texture.getWidth(), -texture.getHeight());
		glTexCoord2f(1, 0);
		glVertex2f(texture.getWidth(), -texture.getHeight());
		glTexCoord2f(1, 1);
		glVertex2f(texture.getWidth(), texture.getHeight());
		glTexCoord2f(0, 1);
		glVertex2f(-texture.getWidth(), texture.getHeight());
		glEnd();
	}

	public void setFrameTime(float frameTime) {
		this.frameTime = frameTime;
	}
}
