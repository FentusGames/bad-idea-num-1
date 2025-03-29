package core.helpers;

import core.Core;
import core.interfaces.ImguiableCTX;
import imgui.ImGui;

public class ImGuiMoney implements ImguiableCTX {
	private float money = 2500.00F;

	@Override
	public void imgui(SliderRenderContext ctx) {
		Core core = ctx.getCore();

		float windowWidth = ImGui.getWindowWidth();
		float textWidth = ImGui.calcTextSize(String.format("$%.2f", getMoney())).x;

		ImGui.setCursorPosX(windowWidth - textWidth - 50);
		ImGui.setCursorPosY(20);

		ImGui.pushFont(core.getFont("default", 20));
		ImGui.text(String.format("$%.2f", getMoney()));
		ImGui.popFont();
	}

	public float getMoney() {
		return money;
	}

	public void setMoney(float money) {
		this.money = money;
	}
}
