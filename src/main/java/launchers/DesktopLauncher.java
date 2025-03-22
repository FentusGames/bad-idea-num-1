package launchers;

import core.Core;
import core.assets.AssetLoader;
import core.assets.processors.FontAssetProcessor;
import core.screens.ScreenMainMenu;

public class DesktopLauncher {
	public static void main(String[] args) {
		// Asset Loader
		AssetLoader loader = new AssetLoader();
		FontAssetProcessor fontProcessor = new FontAssetProcessor();

		loader.registerProcessor("fonts", fontProcessor);
		loader.loadAssets("assets");

		// Core client passed form screen to screen.
		Core core = new Core();

		// Load Font Processor
		core.setFontProcessor(fontProcessor);

		// Client Settings
		core.setWidth(1366);
		core.setHeight(768);
		core.setTitle("Game Client"); // @TODO: Load via en_US

		// Debug
		core.setDebug(true);

		// The initial screen. (Only needs to call start one time.)
		core.setScreen(new ScreenMainMenu(core)).start();
	}
}
