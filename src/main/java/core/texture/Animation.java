package core.texture;

import static org.lwjgl.opengl.GL11.*;

import java.util.List;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import core.interfaces.Renderable;

public class Animation implements Renderable {
	private static final Logger logger = LoggerFactory.getLogger(Animation.class);

	private Vector3f pos = new Vector3f(0, 0, 0);
	private Quaternionf rotation = new Quaternionf();

	private final List<Texture> frames;

	private final Texture colorMask;
	private float r = 1;
	private float g = 1;
	private float b = 1;
	private float a = 1;

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

	@Override
	public void render(float delta) {
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
			glColor4f(getR(), getG(), getB(), getA());
			drawQuad(frame, pos, rotation);
			glColor4f(1, 1, 1, 1);
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

	public Vector3f getPos() {
		return pos;
	}

	public void setPos(Vector3f pos) {
		this.pos = pos;
	}

	public Quaternionf getRotation() {
		return rotation;
	}

	public void setRotation(Quaternionf rotation) {
		this.rotation = rotation;
	}

	public void setColorMask(int r, int g, int b, int a) {
		this.setR(r);
		this.setG(g);
		this.setB(b);
		this.setA(a);
	}

	public float getR() {
		return r;
	}

	public void setR(float r) {
		this.r = r;
	}

	public float getG() {
		return g;
	}

	public void setG(float g) {
		this.g = g;
	}

	public float getB() {
		return b;
	}

	public void setB(float b) {
		this.b = b;
	}

	public float getA() {
		return a;
	}

	public void setA(float a) {
		this.a = a;
	}
	
	public List<Texture> getFrames() {
		return frames;
	}
}
