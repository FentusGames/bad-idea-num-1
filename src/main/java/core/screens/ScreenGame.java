package core.screens;

import static jooq.generated.tables.Genes.GENES;

import org.joml.Vector3f;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.lwjgl.opengl.GL11;

import core.Core;
import core.camera.Camera;
import core.entities.Ship;
import imgui.ImGui;
import imgui.flag.ImGuiTableFlags;
import jooq.generated.tables.records.GenesRecord;

public class ScreenGame extends Screen {
	private Camera camera = new Camera(core.getWindowPtr());
	private DSLContext db = core.getDB("game");
	private Result<GenesRecord> genesRecords;

	private Ship ship = new Ship(core);

	public ScreenGame(Core core) {
		super(core);
	}

	@Override
	public void init() {
		super.init();
		genesRecords = db.selectFrom(GENES).orderBy(DSL.rand()).limit(8).fetch();

		ship.init();
		ship.setPos(new Vector3f(0, 1, 0));
		ship.getRotation().z = 30F;
	}

	@Override
	public void render(float delta) {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		camera.apply();

		ship.render(delta);
	}

	@Override
	public void update(float delta) {
		ship.update(delta);
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