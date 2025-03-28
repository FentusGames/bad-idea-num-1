package core.screens;

import org.jooq.DSLContext;
import org.lwjgl.opengl.GL11;

import core.Core;
import core.camera.Camera;
import core.helpers.Slider;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;

public class ScreenGame extends Screen {

	private final Camera camera;
	private final DSLContext db = core.getDB("game");

	private Slider slider = new Slider(core);

	public ScreenGame(Core core) {
		super(core);
		this.camera = new Camera(core.getWindowPtr());
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
		slider.update(delta);
	}

	@Override
	public void imgui(float delta, int windowX, int windowY, int windowWidth, int windowHeight) {
		slider.imgui(delta, windowX, windowY, windowWidth, windowHeight);

		slider.setNextWindowSlideable(windowWidth / 2, windowHeight / 2);

		ImGui.begin("Main Screen", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove);
		ImGui.text("Resolution: " + windowWidth + " x " + windowHeight);
		ImGui.text(String.format("Offset: (%.1f, %.1f)", slider.getOffsetX(), slider.getOffsetY()));
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

	public DSLContext getDb() {
		return db;
	}
}
