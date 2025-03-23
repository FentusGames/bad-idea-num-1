package core.screens;

import static jooq.generated.tables.Genes.GENES;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Result;
import org.jooq.impl.DSL;

import core.Core;
import imgui.ImGui;
import imgui.flag.ImGuiTableFlags;
import jooq.generated.tables.records.GenesRecord;

public class ScreenGame extends Screen {
	private DSLContext db = core.getDB("game");
	private Result<GenesRecord> genesRecords;

	public ScreenGame(Core core) {
		super(core);
	}

	@Override
	public void init() {
		super.init();

		genesRecords = db.selectFrom(GENES).orderBy(DSL.rand()).limit(16).fetch();
	}

	@Override
	public void render(float delta) {

	}

	@Override
	public void update(float delta) {

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
