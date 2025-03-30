package core.screens;

import org.lwjgl.opengl.GL11;

import core.Core;
import core.camera.Camera;
import imgui.ImGui;

public class ScreenGame extends Screen {
	private final Camera camera = new Camera(core.getWindowPtr());

	public ScreenGame(Core core) {
		super(core);
	}

	@Override
	public void init() {
		super.init();
	}

	@Override
	public void render(float delta) {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		camera.apply();
	}

	@Override
	public void update(float delta) {

	}

	@Override
	public void imgui(float delta, int windowX, int windowY, int windowWidth, int windowHeight) {
		ImGui.begin("Debug");
		ImGui.text(String.format("X: %s Y: %s", camera.getPosition().x, camera.getPosition().y));
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
}
