package core.screens;

import core.Core;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;

public class ScreenMainMenu extends Screen {
	private final ImBoolean showMenu = new ImBoolean(true);

	public ScreenMainMenu(Core core) {
		super(core);
	}

	@Override
	public void init() {
		super.init();
	}

	@Override
	public void render(float delta) {
	}

	@Override
	public void update(float delta) {
	}

	@Override
	public void imgui(float delta, int windowX, int windowY, int windowWidth, int windowHeight) {
		if (core.getDebug()) {
			ImGui.showDemoWindow();
		}

		if (!showMenu.get()) {
			return;
		}

		float menuWidth = 400;

		float centerX = (windowWidth - menuWidth) * 0.5f;
		ImGui.setNextWindowPos(centerX, 0, ImGuiCond.Appearing);

		ImGui.setNextWindowSizeConstraints(menuWidth, -1, menuWidth, -1);

		ImGui.begin("##MainMenu", showMenu, ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.AlwaysAutoResize);

		float menuHeight = ImGui.getWindowSizeY();

		float centerY = (windowHeight - menuHeight) * 0.5f;
		ImGui.setWindowPos(centerX, centerY);

		float buttonWidth = ImGui.getContentRegionAvailX();
		float buttonHeight = 50;

		if (ImGui.button("Game Screen", buttonWidth, buttonHeight)) {
			core.setScreen(new ScreenGame(core));
		}
		if (ImGui.button("Load Game", buttonWidth, buttonHeight)) {
			core.setScreen(new ScreenLoadGame(core));
		}
		if (ImGui.button("Settings", buttonWidth, buttonHeight)) {
			core.setScreen(new ScreenSettings(core));
		}
		if (ImGui.button("Exit", buttonWidth, buttonHeight)) {
			System.exit(0);
		}

		ImGui.end();
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
}
