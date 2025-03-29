package core.imgui;

import core.Core;
import core.helpers.SliderRenderContext;
import core.interfaces.ImguiableCTX;
import imgui.ImGui;

public class ImGuiDays implements ImguiableCTX {
	private int daysPassed = 0;

	@Override
	public void imgui(SliderRenderContext ctx) {
		Core core = ctx.getCore();

		float currentWindowWidth = ImGui.getWindowWidth();

		String dayLabel = "Day: " + getDaysPassed();

		ImGui.pushFont(core.getFont("default", 44));

		float textWidth = ImGui.calcTextSize(dayLabel).x;

		ImGui.setCursorPosX((currentWindowWidth - textWidth) / 2);
		ImGui.text(dayLabel);
		ImGui.popFont();
	}

	public int getDaysPassed() {
		return daysPassed;
	}

	public void setDaysPassed(int daysPassed) {
		this.daysPassed = daysPassed;
	}
}
