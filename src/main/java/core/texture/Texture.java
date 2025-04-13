package core.texture;

import static org.lwjgl.opengl.GL11.*;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import core.interfaces.Disposable;
import core.interfaces.Renderable;

public class Texture implements Renderable, Disposable {
	private final int id;
	private float width, height;
	private final int originalWidth, originalHeight;
	private final Vector3f pos = new Vector3f(0, 0, 0);
	private final Quaternionf rotation = new Quaternionf();

	public Texture(int id, int width, int height) {
		this.id = id;
		this.width = width;
		this.height = height;
		this.originalWidth = width;
		this.originalHeight = height;
	}

	@Override
	public void render(float delta) {
		glBindTexture(GL_TEXTURE_2D, id);
		glPushMatrix();

		glTranslatef(pos.x, pos.y, pos.z);

		glRotatef(rotation.x, 1, 0, 0);
		glRotatef(rotation.y, 0, 1, 0);
		glRotatef(rotation.z, 0, 0, 1);

		float halfW = width * 0.5f;
		float halfH = height * 0.5f;

		glBegin(GL_QUADS);
		glTexCoord2f(0, 1);
		glVertex2f(-halfW, -halfH);

		glTexCoord2f(1, 1);
		glVertex2f(halfW, -halfH);

		glTexCoord2f(1, 0);
		glVertex2f(halfW, halfH);

		glTexCoord2f(0, 0);
		glVertex2f(-halfW, halfH);
		glEnd();

		glPopMatrix();
		glBindTexture(GL_TEXTURE_2D, 0);
	}

	@Override
	public void dispose() {
		glDeleteTextures(id);
	}

	public int getID() {
		return id;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float w) {
		width = w;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float h) {
		height = h;
	}

	public int getOriginalWidth() {
		return originalWidth;
	}

	public int getOriginalHeight() {
		return originalHeight;
	}

	public void setX(float x) {
		pos.x = x;
	}

	public float getX() {
		return pos.x;
	}

	public void setY(float y) {
		pos.y = y;
	}

	public float getY() {
		return pos.y;
	}

	public Vector3f getPosition() {
		return pos;
	}

	public Quaternionf getRotation() {
		return rotation;
	}
}
