package launchers;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import core.Core;
import core.screens.ScreenMainMenu;

public class DesktopLauncher {
	// Logger
	private static final Logger logger = LoggerFactory.getLogger(DesktopLauncher.class);

	public static void main(String[] args) {
		// Load Database
		DSLContext db = null;

		try {
			Connection connection = DriverManager.getConnection("jdbc:sqlite:game.db"); // Change to actual DB path
			db = DSL.using(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Load language file
		Yaml yaml = new Yaml();

		Map<String, String> data = null;

		try (InputStream inputStream = Yaml.class.getClassLoader().getResourceAsStream("en_US.yml")) {
			if (inputStream == null) {
				throw new RuntimeException("File en_US.yml not found in resources.");
			}

			logger.info("Added en_US.yml language file.");

			data = yaml.load(inputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Core client passed form screen to screen.
		Core core = new Core();

		// Load language
		core.setDB(db);

		// Load language
		core.setLanguage(data);

		// Client Settings
		core.setWidth(1366);
		core.setHeight(768);
		core.setTitle("Game Client");
		core.setFontMinSize(12);
		core.setFontMaxSize(72);
		core.setFontStepAmt(2);

		// Debug
		core.setDebug(true);

		// The initial screen. (Only needs to call start one time.)
		core.setScreen(new ScreenMainMenu(core)).start();
	}
}
