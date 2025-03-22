package launchers;

import core.Core;
import core.screens.ScreenMainMenu;

public class DesktopLauncher {
	public static void main(String[] args) {

		// Core client passed form screen to screen.
		Core core = new Core();

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
