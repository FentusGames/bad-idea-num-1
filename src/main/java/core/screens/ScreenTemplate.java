package core.screens;

import core.Core;

public class ScreenTemplate extends Screen {
	public ScreenTemplate(Core core) {
		super(core);
	}

	@Override
	public void init(int windowX, int windowY, int windowWidth, int windowHeight) {
		super.init(windowX, windowY, windowWidth, windowHeight);
	}

	@Override
	public void render(float delta, int windowX, int windowY, int windowWidth, int windowHeight) {

	}

	@Override
	public void update(float delta, int windowX, int windowY, int windowWidth, int windowHeight) {

	}

	@Override
	public void imgui(float delta, int windowX, int windowY, int windowWidth, int windowHeight) {

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
