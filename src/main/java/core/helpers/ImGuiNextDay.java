package core.helpers;

import core.Core;
import core.interfaces.ImguiableCTX;
import core.screens.ScreenGame;
import imgui.ImGui;

public class ImGuiNextDay implements ImguiableCTX {
	@Override
	public void imgui(SliderRenderContext ctx) {
		Core core = ctx.getCore();
		ImGuiDays days = ((ScreenGame) core.getScreen()).getImGuiDays();

		float buttonHeight = 40;
		float windowWidth = ImGui.getWindowWidth();
		float windowHeight = ImGui.getWindowHeight();

		ImGui.setCursorPosY(windowHeight - buttonHeight - 10);
		ImGui.pushFont(core.getFont("default", 20));

		float buttonWidth = ImGui.calcTextSize("Next Day").x + 10;
		ImGui.setCursorPosX(windowWidth - buttonWidth - 10);

		if (ImGui.button("Next Day", buttonWidth, buttonHeight)) {
			days.setDaysPassed(days.getDaysPassed() + 1);
		}
		ImGui.popFont();
	}
}
