package core.screens;

import java.util.HashMap;
import java.util.Map;

import core.Core;
import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;

public class ScreenSettings extends Screen {
	
	private final Map<String, Float> buttonWidthOffsets = new HashMap<>();

	public ScreenSettings(Core core) {
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
		float menuWidth = 400;
		float centerX = (windowWidth - menuWidth) * 0.5F;

		ImGui.setNextWindowPos(centerX, 0, ImGuiCond.Appearing);
		ImGui.setNextWindowSizeConstraints(menuWidth, -1, menuWidth, -1);
		ImGui.begin("##MainMenu", ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.AlwaysAutoResize | ImGuiWindowFlags.NoBackground);

		float menuHeight = ImGui.getWindowSizeY();
		float centerY = (windowHeight - menuHeight) * 0.5F;

		ImGui.setWindowPos(centerX, centerY);
		ImGui.pushFont(core.getFont("default", 64));
		ImGui.text("Settings");
		ImGui.popFont();
		ImGui.spacing();

		//FIXME: This doesn't resume, it just starts the game over
		if (customSlantedButton("Resume", 250F)) {
			core.setScreen(new ScreenGame(core));
		}
		ImGui.spacing();
		if (customSlantedButton("Save Game", 225F)) {
			core.setScreen(new ScreenLoadGame(core));
		}

		ImGui.spacing();
		if (customSlantedButton("Exit", 200F)) {
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
	
	private boolean customSlantedButton(String label, float width) {
		float baseWidth = width;
		float height = 25f;
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

		ImVec2 textSize = ImGui.calcTextSize(label);
		float textX = cursorPos.x + 10f;
		float textY = cursorPos.y + (height - textSize.y) * 0.5f;

		int textColor = colorFromImVec4(ImGui.getStyle().getColor(ImGuiCol.Text));

		drawList.addText(textX, textY, textColor, label);

		return ImGui.isItemClicked();
	}
	
	private int colorFromImVec4(ImVec4 vec) {
		int r = (int) (vec.x * 255.0f) & 0xFF;
		int g = (int) (vec.y * 255.0f) & 0xFF;
		int b = (int) (vec.z * 255.0f) & 0xFF;
		int a = (int) (vec.w * 255.0f) & 0xFF;

		return (a << 24) | (b << 16) | (g << 8) | r;
	}

}
