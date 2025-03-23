package launchers;

import core.Core;
import core.assets.AssetLoader;
import core.assets.processors.FontAssetProcessor;
import core.assets.processors.LangProcessor;
import core.assets.processors.SQLiteProcessor;
import core.screens.ScreenMainMenu;

public class DesktopLauncher {
	public static void main(String[] args) {
		// Asset Loader
		AssetLoader loader = new AssetLoader();

		// Processor Lang
		LangProcessor langProcessor = new LangProcessor();
		loader.registerProcessor("languages/" + (args.length == 0 || args[0] == null ? "en_US" : args[0]), langProcessor); // Load from args

		// Processor Font
		FontAssetProcessor fontProcessor = new FontAssetProcessor();
		loader.registerProcessor("fonts/", fontProcessor);

		// Processor SQLite
		SQLiteProcessor sqliteProcessor = new SQLiteProcessor();
		loader.registerProcessor("sqlite/", sqliteProcessor);

		// Asset Loader
		loader.loadAssets("assets");

		// Core client passed form screen to screen.
		Core core = new Core();

		// Load Processor Lang
		core.setLangProcessor(langProcessor);

		// Load Processor Font
		core.setFontProcessor(fontProcessor);

		// Load Processor SQLite
		core.setSQLiteProcessor(sqliteProcessor);

		// Client Settings
		core.setWidth(1366);
		core.setHeight(768);
		core.setTitle(langProcessor.get("title"));

		// Debug
		core.setDebug(true);

		// The initial screen. (Only needs to call start one time.)
		core.setScreen(new ScreenMainMenu(core)).start();
	}
}
