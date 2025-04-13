package core.screens;

import org.lwjgl.opengl.GL11;

import core.Core;
import core.helpers.imgui.DemoWindow;
import core.helpers.imgui.Navigation;

public class ScreenGame extends Screen {
	private final Navigation navigation = new Navigation(this);
	private final DemoWindow demo = new DemoWindow(this);

	public ScreenGame(Core core) {
		super(core);
	}

	@Override
	public void init() {
		super.init();

		camera.setZoomLevel(0.3F);

		navigation.init();
	}

	@Override
	public void render(float delta) {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		camera.apply();

		navigation.render(delta);
	}

	@Override
	public void update(float delta) {
		navigation.update(delta);
	}

	@Override
	public void imgui(float delta) {
		navigation.imgui(delta);
		demo.imgui(delta);
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
		navigation.key(window, key, scancode, action, mods);
	}
}
