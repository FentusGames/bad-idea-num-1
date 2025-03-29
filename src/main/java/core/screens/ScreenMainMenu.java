package core.screens;

import java.util.HashMap;
import java.util.Map;

import core.Core;
import core.helpers.HImGui;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;

public class ScreenMainMenu extends Screen {
	private final Map<String, Float> buttonWidthOffsets = new HashMap<>();

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

		float menuWidth = 400;
		float centerX = (windowWidth - menuWidth) * 0.5F;

		ImGui.setNextWindowPos(centerX, 0, ImGuiCond.Appearing);
		ImGui.setNextWindowSizeConstraints(menuWidth, -1, menuWidth, -1);
		ImGui.begin("##MainMenu", ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.AlwaysAutoResize | ImGuiWindowFlags.NoBackground);

		float menuHeight = ImGui.getWindowSizeY();
		float centerY = (windowHeight - menuHeight) * 0.5F;

		ImGui.setWindowPos(centerX, centerY);
		ImGui.pushFont(core.getFont("default", 64));
		ImGui.text(core.getLang("title"));
		ImGui.popFont();
		ImGui.spacing();

		if (HImGui.customSlantedButton("Play", 250F, buttonWidthOffsets)) {
			core.setScreen(new ScreenGame(core));
		}
		ImGui.spacing();
		if (HImGui.customSlantedButton("Load Game", 225F, buttonWidthOffsets)) {
			core.setScreen(new ScreenLoadGame(core));
		}
		ImGui.spacing();
		if (HImGui.customSlantedButton("Settings", 200F, buttonWidthOffsets)) {
			core.setScreen(new ScreenSettings(core));
		}
		ImGui.spacing();
		if (HImGui.customSlantedButton("Exit", 175F, buttonWidthOffsets)) {
			System.exit(0);
		}

		ImGui.spacing();
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
