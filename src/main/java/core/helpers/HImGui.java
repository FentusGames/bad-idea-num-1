package core.helpers;

import java.util.Map;

import core.screens.ScreenGame;
import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.flag.ImGuiCol;

public class HImGui {
	public static void renderDays(SliderRenderContext ctx) {
		ScreenGame screen = ((ScreenGame) ctx.core.getScreen());

		float windowWidth = ImGui.getWindowWidth();

		System.out.println(windowWidth);

		String dayLabel = "Day: " + screen.getDaysPassed();

		ImGui.pushFont(ctx.core.getFont("default", 44));

		float textWidth = ImGui.calcTextSize(dayLabel).x;

		ImGui.setCursorPosX((windowWidth - textWidth) / 2);
		ImGui.text(dayLabel);
		ImGui.popFont();
	}

	public static void renderNextDay(SliderRenderContext ctx, Map<String, Float> buttonWidthOffsets) {
		ScreenGame screen = ((ScreenGame) ctx.core.getScreen());

		float windowWidth = ImGui.getWindowWidth();
		float buttonWidth = ImGui.calcTextSize("Next Day").x + 90;
		float buttonHeight = 40;
		float windowHeight = ImGui.getWindowHeight();

		ImGui.setCursorPosX(windowWidth - buttonWidth - 90);
		ImGui.setCursorPosY(windowHeight - buttonHeight - 10);

		ImGui.pushFont(ctx.core.getFont("default", 20));
		if (customSlantedButton("Next Day", buttonWidth, buttonWidthOffsets)) {
			screen.setDaysPassed(screen.getDaysPassed() + 1);
		}
		ImGui.popFont();
	}

	public static void renderMoney(SliderRenderContext ctx) {
		ScreenGame screen = ((ScreenGame) ctx.core.getScreen());

		float windowWidth = ImGui.getWindowWidth();
		float textWidth = ImGui.calcTextSize(String.format("$%.2f", screen.getMoney())).x;

		ImGui.setCursorPosX(windowWidth - textWidth - 50);
		ImGui.setCursorPosY(20);

		ImGui.pushFont(ctx.core.getFont("default", 20));
		ImGui.text(String.format("$%.2f", screen.getMoney()));
		ImGui.popFont();
	}

	public static boolean customSlantedButton(String label, float width, Map<String, Float> buttonWidthOffsets) {
		float baseWidth = width;
		float height = 27f;
		float slantOffset = 20f;
		float extension = 4f;
		float expansion = buttonWidthOffsets.getOrDefault(label, 0f);
		float boundingWidth = baseWidth + expansion + slantOffset;
		float boundingHeight = height;
		float totalWidth = boundingWidth + extension;

		ImVec2 cursorPos = ImGui.getCursorScreenPos();
		ImGui.invisibleButton(label, totalWidth, boundingHeight);

		boolean hovered = ImGui.isItemHovered();
		boolean active = ImGui.isItemActive();

		int mainColor;

		if (active) {
			mainColor = colorFromImVec4(ImGui.getStyle().getColor(ImGuiCol.ButtonActive));
		} else if (hovered) {
			mainColor = colorFromImVec4(ImGui.getStyle().getColor(ImGuiCol.ButtonHovered));
		} else {
			mainColor = colorFromImVec4(ImGui.getStyle().getColor(ImGuiCol.Button));
		}

		if (hovered) {
			buttonWidthOffsets.put(label, 50F);
		} else {
			buttonWidthOffsets.put(label, 0f);
		}

		ImDrawList drawList = ImGui.getWindowDrawList();

		int whiteColor = 0xFFFFFFFF;

		// @formatter:off
		drawList.addQuadFilled(
				cursorPos.x + baseWidth + expansion + slantOffset,
				cursorPos.y,
				cursorPos.x + baseWidth + expansion + slantOffset + extension,
				cursorPos.y,
				cursorPos.x + baseWidth + expansion + extension,
				cursorPos.y + height,
				cursorPos.x + baseWidth + expansion,
				cursorPos.y + height, whiteColor
		);
		drawList.addQuadFilled(
				cursorPos.x,
				cursorPos.y,
				cursorPos.x + baseWidth + expansion + slantOffset,
				cursorPos.y,
				cursorPos.x + baseWidth + expansion,
				cursorPos.y + height,
				cursorPos.x,
				cursorPos.y + height,
				mainColor
		);
		// @formatter:on

		ImVec2 textSize = ImGui.calcTextSize(label);
		float textX = cursorPos.x + 10f;
		float textY = cursorPos.y + (height - textSize.y) * 0.5f;

		int textColor = colorFromImVec4(ImGui.getStyle().getColor(ImGuiCol.Text));

		drawList.addText(textX, textY, textColor, label);

		return ImGui.isItemClicked();
	}

	private static int colorFromImVec4(ImVec4 vec) {
		int r = (int) (vec.x * 255.0f) & 0xFF;
		int g = (int) (vec.y * 255.0f) & 0xFF;
		int b = (int) (vec.z * 255.0f) & 0xFF;
		int a = (int) (vec.w * 255.0f) & 0xFF;

		return (a << 24) | (b << 16) | (g << 8) | r;
	}
}
