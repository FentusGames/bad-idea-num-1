package launchers;

import java.nio.file.Paths;

import core.Core;
import core.assets.processors.Fonts;
import core.assets.processors.Language;
import core.assets.processors.SQLite;
import core.screens.ScreenMainMenu;

public class DesktopLauncher {
	public static void main(String[] args) {
		// Processor Lang
		Language language = new Language();
		language.loadFrom(Paths.get("assets/languages/" + (args.length == 0 || args[0] == null ? "en_US" : args[0]))); // Load from args

		// Processor Font
		Fonts fonts = new Fonts();

		// Processor SQLite
		SQLite sqlite = new SQLite();
		sqlite.loadFrom(Paths.get("assets/sqlite/"));

		// Core client passed form screen to screen.
		Core core = new Core();

		// Load Processor Lang
		core.setLanguage(language);

		// Load Processor Font
		core.setFontProcessor(fonts);

		// Load Processor SQLite
		core.setSQLite(sqlite);

		// Client Settings
		core.setWidth(1366);
		core.setHeight(768);
		core.setTitle(language.get("title"));

		// Debug
		core.setDebug(true);

		// The initial screen. (Only needs to call start one time.)
		core.setScreen(new ScreenMainMenu(core)).start();
	}
}
