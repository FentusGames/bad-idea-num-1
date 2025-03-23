package core.screens;

import static jooq.generated.tables.Genes.GENES;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import core.Core;
import jooq.generated.tables.records.GenesRecord;

public class ScreenGame extends Screen {
	private static final Logger logger = LoggerFactory.getLogger(ScreenGame.class);
	private DSLContext db = core.getDB("game");

	public ScreenGame(Core core) {
		super(core);
	}

	@Override
	public void init() {
		super.init();

		// TODO: Remove Example JOOQ
		GenesRecord genesRecord = db.selectFrom(GENES).orderBy(DSL.rand()).limit(1).fetchOne();
		logger.info("\n" + genesRecord.toString());
	}

	@Override
	public void render(float delta) {

	}

	@Override
	public void update(float delta) {

	}

	@Override
	public void imgui(float delta, int windowX, int windowY, int windowWidth, int windowHeight) {

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
