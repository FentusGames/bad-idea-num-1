package core.texture;

import static org.lwjgl.opengl.GL11.*;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import core.interfaces.Disposable;
import core.interfaces.Renderable;

public class Texture implements Renderable, Disposable {
	private final int id;
	private int width;
	private int height;

	private Vector3f pos = new Vector3f(0, 0, 0);
	private Quaternionf rotation = new Quaternionf();

	public Texture(int id, int width, int height) {
		this.id = id;
		this.width = width;
		this.height = height;
	}

	@Override
	public void render(float delta) {
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

	@Override
	public void dispose() {
		glDeleteTextures(id);
	}

	public int getID() {
		return id;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
}
