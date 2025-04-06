package core.screens;

import org.lwjgl.opengl.GL11;

import core.Core;
import core.camera.Camera;
import core.helpers.HImGui;
import core.helpers.HImGui.TextAlign;
import core.texture.Texture;
import imgui.ImFont;
import imgui.ImGui;

public class ScreenGame extends Screen {
	private final Camera camera = new Camera(core.getWindowPtr());
	private final Texture background = core.getTexture("graphics_background", 0);

	public ScreenGame(Core core) {
		super(core);
	}

	@Override
	public void init(int windowX, int windowY, int windowWidth, int windowHeight) {
		super.init(windowX, windowY, windowWidth, windowHeight);
	}

	@Override
	public void render(float delta, int windowX, int windowY, int windowWidth, int windowHeight) {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		camera.apply();

		background.render(delta, windowX, windowY, windowWidth, windowHeight);
	}

	@Override
	public void update(float delta, int windowX, int windowY, int windowWidth, int windowHeight) {

	}

	@Override
	public void imgui(float delta, int windowX, int windowY, int windowWidth, int windowHeight) {
		// Panel #1 — fixed font size
		ImFont fixedFont = core.getFont("default", 12);
		if (fixedFont != null) {
			ImGui.pushFont(fixedFont);
			ImGui.begin("Demo Panel #1");
			ImGui.text("Fixed font (12 @ 1366x768)");
			ImGui.text(String.format("Font Size: %.1f", fixedFont.getFontSize()));
			ImGui.text(String.format("Delta Time: %.3f", delta));
			ImGui.text(String.format("Window Size: %d x %d", windowWidth, windowHeight));

			if (HImGui.imageButton(core, "graphics_buttons_test", "TEST", TextAlign.CENTER)) {
				System.out.println("TEST BUTTON");
			}

			ImGui.end();
			ImGui.popFont();
		}

		// Panel #2 — dynamically scaled font
		ImFont scaledFont = core.getScaledFont("default", 40);
		if (scaledFont != null) {
			ImGui.pushFont(scaledFont);
			ImGui.begin("Demo Panel #2");
			ImGui.text("Scaled font based on resolution");
			ImGui.text(String.format("Font Size: %.1f", scaledFont.getFontSize()));
			ImGui.text(String.format("Delta Time: %.3f", delta));
			ImGui.text(String.format("Window Size: %d x %d", windowWidth, windowHeight));
			ImGui.end();
			ImGui.popFont();
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
