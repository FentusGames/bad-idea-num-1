package core.screens;

import org.lwjgl.opengl.GL11;

import core.Core;
import core.camera.Camera;
import core.helpers.HImGui;
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
		ImGui.begin("Demo Panel");
		{
			ImFont font1 = core.getFont("default", 12);
			ImGui.pushFont(font1);
			{
				ImGui.text("Fixed font (12 @ 1366x768)");
				ImGui.text(String.format("Font Size: %.1f", font1.getFontSize()));
				ImGui.text(String.format("Delta Time: %.3f", delta));
				ImGui.text(String.format("Window Size: %d x %d", windowWidth, windowHeight));
			}
			ImGui.popFont();

			ImGui.newLine();

			ImFont font2 = core.getFont("default", core.getScale(12));
			ImGui.pushFont(font2);
			{
				ImGui.text("Scaled font based on resolution");
				ImGui.text(String.format("Font Size: %.1f", font2.getFontSize()));
				ImGui.text(String.format("Delta Time: %.3f", delta));
				ImGui.text(String.format("Window Size: %d x %d", windowWidth, windowHeight));
			}
			ImGui.popFont();

			ImGui.newLine();

			Texture texture = core.getTexture("graphics_background", 0);
			HImGui.imageRotated(texture.getID(), core.getScale(256), core.getScale(108));
		}
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
