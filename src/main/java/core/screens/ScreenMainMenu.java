package core.screens;

import core.Core;
import imgui.ImGui;

public class ScreenMainMenu extends Screen {
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
		// DEBUG
		if (core.getDebug()) {
			ImGui.showDemoWindow();
		}

		renderScreenSelector(windowX, windowY, windowWidth, windowHeight);
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

	@Override
	protected String getMenuName() {
		return null;
	}

	public int getOrder() {
		return -1;
	}
}
