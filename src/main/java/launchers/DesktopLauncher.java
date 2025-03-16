package launchers;

import java.io.InputStream;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import core.Core;
import core.screens.ScreenMainMenu;

public class DesktopLauncher {
	public static void main(String[] args) {
		// Load language file
		Yaml yaml = new Yaml();

		Map<String, String> data = null;

		try (InputStream inputStream = Yaml.class.getClassLoader().getResourceAsStream("en_US.yml")) {
			if (inputStream == null) {
				throw new RuntimeException("File en_US.yml not found in resources.");
			}
			data = yaml.load(inputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Core client passed form screen to screen.
		Core core = new Core();

		// Load language
		core.setLanguage(data);

		// Client Settings
		core.setWidth(1366);
		core.setHeight(768);
		core.setTitle("Livin Covida Loca");
		core.setFontMinSize(12);
		core.setFontMaxSize(72);
		core.setFontStepAmt(2);

		// Debug
		core.setDebug(true);

		// The initial screen. (Only needs to call start one time.)
		core.setScreen(new ScreenMainMenu(core)).start();
	}
}
