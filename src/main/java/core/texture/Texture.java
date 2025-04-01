package core.texture;

import static org.lwjgl.opengl.GL11.*;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import core.interfaces.Disposable;
import core.interfaces.Renderable;

public class Texture implements Renderable, Disposable {
	private final int id;
	private float width;
	private float height;
	private final int originalWidth;
	private final int originalHeight;

	private Vector3f pos = new Vector3f(0, 0, 0);
	private Quaternionf rotation = new Quaternionf();

	public Texture(int id, int width, int height) {
		this.id = id;
		this.width = width;
		this.height = height;
		this.originalWidth = width;
		this.originalHeight = height;
	}

	@Override
	public void render(float delta, int windowX, int windowY, int windowWidth, int windowHeight) {
		glBindTexture(GL_TEXTURE_2D, id);
		drawQuad(this, pos, rotation);
		glBindTexture(GL_TEXTURE_2D, 0);
	}

	private void drawQuad(Texture texture, Vector3f position, Quaternionf rotation) {
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		glTranslatef(position.x, position.y, position.z);
		glRotatef(rotation.x, 1, 0, 0);
		glRotatef(rotation.y, 0, 1, 0);
		glRotatef(rotation.z, 0, 0, 1);

		float halfWidth = texture.getWidth() / 2;
		float halfHeight = texture.getHeight() / 2;

		glBegin(GL_QUADS);
		glTexCoord2f(0, 0);
		glVertex2f(-halfWidth, -halfHeight);
		glTexCoord2f(1, 0);
		glVertex2f(halfWidth, -halfHeight);
		glTexCoord2f(1, 1);
		glVertex2f(halfWidth, halfHeight);
		glTexCoord2f(0, 1);
		glVertex2f(-halfWidth, halfHeight);
		glEnd();
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

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
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

	public int getOriginalWidth() {
		return originalWidth;
	}

	public int getOriginalHeight() {
		return originalHeight;
	}
}
