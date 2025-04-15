package core.helpers.imgui;

import core.Core;
import core.helpers.HImGui;
import core.interfaces.Imguiable;
import core.screens.ScreenGame;
import imgui.ImFont;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;

public class NextDay implements Imguiable {
	private ScreenGame screenGame;

	private int currentDay = 0;
	private boolean nextDayEnabled = true;

	public NextDay(ScreenGame screenGame) {
		this.screenGame = screenGame;
	}

	@Override
	public void imgui(float delta) {
		Core core = screenGame.getCore();

		int flags = ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoNav | ImGuiWindowFlags.NoBackground;

		float windowW = core.getScale(128);
		float windowH = core.getScale(128);
		float screenW = core.getWindowWidth();
		float screenH = core.getWindowHeight();

		// Bottom-right position
		ImGui.setNextWindowPos(screenW - windowW - 10, screenH - windowH - 10);
		ImGui.setNextWindowSize(windowW, windowH);
		ImGui.begin("Days Passed", flags);

		// Push large font
		ImFont bigFont = core.getFont("default", core.getScale(12));
		ImGui.pushFont(bigFont);

		// Size measurements
		float textHeight = ImGui.getFontSize();
		float buttonSize = core.getScale(core.getAnimation("graphics_buttons_nextday").getFrames().get(0).getHeight());
		float centerX = (windowW - buttonSize) * 0.5f;
		float centerY = (windowH - Math.max(buttonSize, textHeight)) * 0.5f;

		// Draw button first (under)
		ImGui.setCursorPos(centerX, centerY);
		ImGui.beginDisabled(!nextDayEnabled);
		if (HImGui.imageButton(core, "graphics_buttons_nextday", "NextDayOverlay", true, 0, "")) {
			currentDay++;
		}
		ImGui.endDisabled();

		// Draw text on top (overlapping), shifted 15 pixels more to the right
		ImGui.setCursorPos(centerX + core.getScale(24), centerY + (buttonSize - textHeight) * 0.5f);
		ImGui.text("Day: " + currentDay);

		ImGui.popFont();
		ImGui.end();
	}

	public int getCurrentDay() {
		return currentDay;
	}

	public void setNextDayEnabled(boolean enabled) {
		this.nextDayEnabled = enabled;
	}

	public boolean isNextDayEnabled() {
		return nextDayEnabled;
	}
}
