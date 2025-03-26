package core.screens;

import java.util.HashMap;
import java.util.Map;

import core.Core;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiStyleVar;
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

		float baseWidth = ImGui.getContentRegionAvailX() - 100;

		ImGui.pushFont(core.getFont("default", 64));
		ImGui.text(core.getLang("title"));
		ImGui.popFont();

		// Render buttons with left-aligned text
		renderButton("Play", baseWidth, () -> core.setScreen(new ScreenGame(core)));
		renderButton("Load Game", baseWidth, () -> core.setScreen(new ScreenLoadGame(core)));
		renderButton("Settings", baseWidth, () -> core.setScreen(new ScreenSettings(core)));
		renderButton("Exit", baseWidth, () -> System.exit(0));

		ImGui.end();
	}

	private void renderButton(String label, float baseWidth, Runnable action) {
		// Get or initialize the button's width offset
		float widthOffset = buttonWidthOffsets.getOrDefault(label, 0F);
		float buttonWidth = baseWidth + widthOffset;

		// Push left-aligned text style
		ImGui.pushStyleVar(ImGuiStyleVar.ButtonTextAlign, 0.0F, 0.5F); // Align text left (x=0.0), center vertically (y=0.5)

		// Draw the button
		if (ImGui.button(label, buttonWidth, 25)) {
			action.run();
		}

		// Pop style to restore previous settings
		ImGui.popStyleVar();

		// Update the width offset based on hover state
		if (ImGui.isItemHovered()) {
			buttonWidthOffsets.put(label, 100F); // Expand on hover
		} else {
			buttonWidthOffsets.put(label, 0F); // Reset when not hovered
		}
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
