package core.texture;

import static org.lwjgl.opengl.GL11.*;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import core.interfaces.Disposable;
import core.interfaces.Renderable;

public class Texture implements Renderable, Disposable {
	private static final Logger logger = LoggerFactory.getLogger(Texture.class);

	private final int id;
	private final int width;
	private final int height;

	private Vector3f pos = new Vector3f(0, 0, 0);
	private Quaternionf rotation = new Quaternionf();

	public Texture(int id, int width, int height) {
		this.id = id;
		this.width = width;
		this.height = height;
	}

	public void bind() {
		glBindTexture(GL_TEXTURE_2D, id);
	}

	public void unbind() {
		glBindTexture(GL_TEXTURE_2D, 0);
	}

	@Override
	public void render(float delta) {
		if (id == 0) {
			logger.warn("Attempted to render a null or invalid frame.");
			return;
		}

		drawQuad(this, pos, rotation);
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

	public int getHeight() {
		return height;
	}
}
