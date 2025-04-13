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
	public void init(int windowX, int windowY, int windowWidth, int windowHeight) {
		super.init(windowX, windowY, windowWidth, windowHeight);

		navigation.init(windowX, windowY, windowWidth, windowHeight);
	}

	@Override
	public void render(float delta, int windowX, int windowY, int windowWidth, int windowHeight) {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		camera.apply();

		navigation.render(delta, windowX, windowY, windowWidth, windowHeight);
	}

	@Override
	public void update(float delta, int windowX, int windowY, int windowWidth, int windowHeight) {
		navigation.update(delta, windowX, windowY, windowWidth, windowHeight);
	}

	@Override
	public void imgui(float delta, int windowX, int windowY, int windowWidth, int windowHeight) {
		navigation.imgui(delta, windowX, windowY, windowWidth, windowHeight);
		demo.imgui(delta, windowX, windowY, windowWidth, windowHeight);
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
		final float moveSpeed = 5.0f;

		if (action == org.lwjgl.glfw.GLFW.GLFW_PRESS || action == org.lwjgl.glfw.GLFW.GLFW_REPEAT) {
			switch (key) {
			case org.lwjgl.glfw.GLFW.GLFW_KEY_W:
				camera.translate(0, -moveSpeed);
				break;
			case org.lwjgl.glfw.GLFW.GLFW_KEY_S:
				camera.translate(0, moveSpeed);
				break;
			case org.lwjgl.glfw.GLFW.GLFW_KEY_A:
				camera.translate(-moveSpeed, 0);
				break;
			case org.lwjgl.glfw.GLFW.GLFW_KEY_D:
				camera.translate(moveSpeed, 0);
				break;
			default:
				break;
			}
		}
	}
}
