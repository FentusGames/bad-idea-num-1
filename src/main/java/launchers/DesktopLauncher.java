package launchers;

import java.nio.file.Paths;

import core.Core;
import core.assets.Fonts;
import core.assets.Language;
import core.assets.SQLite;
import core.assets.Textures;
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

		// Textures
		Textures textures = new Textures();

		// Core client passed form screen to screen.
		Core core = new Core();

		// Load Processor Lang
		core.setLanguage(language);

		// Load Processor Font
		core.setFontProcessor(fonts);

		// Load Processor SQLite
		core.setSQLite(sqlite);

		// Load Processor Textures
		core.setTextures(textures);

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
