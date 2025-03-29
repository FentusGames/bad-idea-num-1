package core.imgui;

import core.Core;
import core.helpers.SliderRenderContext;
import core.interfaces.ImguiableCTX;
import core.screens.ScreenGame;
import imgui.ImGui;

public class ImGuiNextDay implements ImguiableCTX {
	@Override
	public void imgui(SliderRenderContext ctx) {
		Core core = ctx.getCore();
		ScreenGame screen = ((ScreenGame) core.getScreen());
		ImGuiDays days = screen.getImGuiDays();
		ImGuiLab lab = screen.getImGuiLab();

		float buttonHeight = 40;
		float windowWidth = ImGui.getWindowWidth();
		float windowHeight = ImGui.getWindowHeight();

		ImGui.setCursorPosY(windowHeight - buttonHeight - 10);
		ImGui.pushFont(core.getFont("default", 20));

		float buttonWidth = ImGui.calcTextSize("Next Day").x + 10;
		ImGui.setCursorPosX(windowWidth - buttonWidth - 10);

		if (ImGui.button("Next Day", buttonWidth, buttonHeight)) {
			days.setDaysPassed(days.getDaysPassed() + 1);
			lab.reset();
		}
		ImGui.popFont();
	}
}
