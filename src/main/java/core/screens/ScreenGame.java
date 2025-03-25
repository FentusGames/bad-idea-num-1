package core.screens;

import static jooq.generated.tables.Genes.GENES;
import static org.lwjgl.opengl.GL11.*;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Result;
import org.jooq.impl.DSL;

import core.Core;
import core.camera.Camera;
import core.texture.Texture;
import imgui.ImGui;
import imgui.flag.ImGuiTableFlags;
import jooq.generated.tables.records.GenesRecord;

public class ScreenGame extends Screen {
	private Camera camera = new Camera(core.getWindowPtr());
	private DSLContext db = core.getDB("game");
	private Result<GenesRecord> genesRecords;

	private Texture texture;
	private Vector3f texturePosition = new Vector3f(0, 0, 0);
	private Quaternionf textureRotation = new Quaternionf();

	public ScreenGame(Core core) {
		super(core);
	}

	@Override
	public void init() {
		super.init();
		genesRecords = db.selectFrom(GENES).orderBy(DSL.rand()).limit(16).fetch();

		// Load Textures
		texture = core.getTexture("graphics_delete_test_image_1");
	}

	@Override
	public void render(float delta) {
		camera.apply();

		if (texture != null && texture.getID() != 0) {
			glEnable(GL_TEXTURE_2D); // Ensure texture mapping is enabled
			glBindTexture(GL_TEXTURE_2D, texture.getID());

			matrixQuad(texture, texturePosition, textureRotation);

			glDisable(GL_TEXTURE_2D); // Disable after drawing
		}
	}

	private void matrixQuad(Texture texture, Vector3f position, Quaternionf rotation) {
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();

		glTranslatef(position.x, position.y, position.z);

		// Convert Quaternion to Matrix and Apply Rotation
		float[] matrix = new float[16];
		rotation.get(new Matrix4f()).get(matrix);
		glMultMatrixf(matrix);

		// Scale it relative to the camera
		float scaleFactor = 1F / 64F / 32F; // Adjust this based on your world scale
		float width = texture.getWidth() * scaleFactor;
		float height = texture.getHeight() * scaleFactor;

		glBegin(GL_QUADS);
		glTexCoord2f(0, 0);
		glVertex2f(-width / 2, -height / 2);
		glTexCoord2f(1, 0);
		glVertex2f(width / 2, -height / 2);
		glTexCoord2f(1, 1);
		glVertex2f(width / 2, height / 2);
		glTexCoord2f(0, 1);
		glVertex2f(-width / 2, height / 2);
		glEnd();
	}

	@Override
	public void update(float delta) {
		camera.setPosition(new Vector3f());
	}

	@Override
	public void imgui(float delta, int windowX, int windowY, int windowWidth, int windowHeight) {
		Field<?>[] fields = genesRecords.fields();

		if (ImGui.beginTable("Genes", fields.length, ImGuiTableFlags.RowBg)) {
			for (int column = 0; column < fields.length; column++) {
				ImGui.tableSetupColumn(core.getLang(fields[column].getName()));
			}
			ImGui.tableHeadersRow();

			for (int row = 0; row < genesRecords.size(); row++) {
				ImGui.tableNextRow();

				for (int column = 0; column < fields.length; column++) {
					ImGui.tableSetColumnIndex(column);
					ImGui.text(genesRecords.get(row).get(column).toString());
				}
			}

			ImGui.endTable();
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		if (texture != null) {
			texture.dispose();
		}

	}

	@Override
	public void scroll(long window, double xoffset, double yoffset) {
	}

	@Override
	public void mouseButton(long window, int button, int action, int mods) {
	}

	@Override
	public void key(long window, int key, int scancode, int action, int mods) {
	}

	public String getMenuName() {
		return core.getLang("play");
	}

	public int getOrder() {
		return 0;
	}
}
