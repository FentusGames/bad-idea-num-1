package core.screens;

import java.util.HashMap;
import java.util.Map;

import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import core.Core;
import core.camera.Camera;
import core.helpers.HImGui;
import core.texture.Texture;
import imgui.ImFont;
import imgui.ImGui;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;

public class ScreenGame extends Screen {
	private final Camera camera = new Camera(core.getWindowPtr());

	private float targetCameraX = 0.0f;
	private float targetCameraY = 0.0f;

	private static final float CAMERA_LERP_FACTOR = 0.1f;

	private final Vector2i pos = new Vector2i(0, 0);

	private final Map<Vector2i, Texture> world = new HashMap<>();

	public ScreenGame(Core core) {
		super(core);
	}

	@Override
	public void init(int windowX, int windowY, int windowWidth, int windowHeight) {
		super.init(windowX, windowY, windowWidth, windowHeight);

		world.put(new Vector2i(0, 0), core.getTexture("graphics_background"));
		world.put(new Vector2i(-1, 0), core.getTexture("graphics_background"));
		world.put(new Vector2i(0, 1), core.getTexture("graphics_background"));
		world.put(new Vector2i(1, 1), core.getTexture("graphics_background"));

		camera.setZoomLevel(0.2F);
	}

	@Override
	public void render(float delta, int windowX, int windowY, int windowWidth, int windowHeight) {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		camera.apply();

		world.forEach((point, texture) -> {
			texture.setX(point.x * texture.getWidth());
			texture.setY(point.y * texture.getHeight());
			texture.render(delta, 0, 0, 0, 0);
		});
	}

	@Override
	public void update(float delta, int windowX, int windowY, int windowWidth, int windowHeight) {
		float currentX = camera.getPosition().x;
		float currentY = camera.getPosition().y;

		float newX = currentX + CAMERA_LERP_FACTOR * (targetCameraX - currentX);
		float newY = currentY + CAMERA_LERP_FACTOR * (targetCameraY - currentY);

		camera.setPosition(new Vector3f(newX, newY, 0));

		System.out.println((int) camera.getPosition().x + " x " + (int) camera.getPosition().y);
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

			ImGui.newLine();

			core.getTexture("graphics_buttons_test", 0);

			// UP
			if (HImGui.imageButton(core, "graphics_buttons_test", "Up", true, 0)) {
				targetCameraY -= core.getTexture("graphics_background", 0).getHeight();
			}

			// DOWN
			if (HImGui.imageButton(core, "graphics_buttons_test", "Down", true, 180)) {
				targetCameraY += core.getTexture("graphics_background", 0).getHeight();
			}
		}
		ImGui.end();

		float imageW = core.getScale(64);
		float imageH = core.getScale(64);
		float imageHW = imageW * 0.5f;
		float imageHH = imageH * 0.5f;
		float halfW = windowWidth * 0.5f;
		float halfH = windowHeight * 0.5f;

		int flags = ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoNav | ImGuiWindowFlags.NoBackground;

		ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);

		if (world.containsKey(new Vector2i(pos.x, pos.y + 1))) {
			ImGui.setNextWindowPos(halfW - imageHW, 10);
			ImGui.setNextWindowSize(imageW, imageH);
			ImGui.begin("ButtonWindow_Up", flags);
			{
				if (HImGui.imageButton(core, "graphics_buttons_test", "UpOverlay", true, 0)) {
					targetCameraY += core.getTexture("graphics_background", 0).getHeight();
					pos.y += 1;
				}
			}
			ImGui.end();
		}

		if (world.containsKey(new Vector2i(pos.x + 1, pos.y))) {
			ImGui.setNextWindowPos(windowWidth - imageW - 10, halfH - imageHH);
			ImGui.setNextWindowSize(imageW, imageH);
			ImGui.begin("ButtonWindow_Right", flags);
			{
				if (HImGui.imageButton(core, "graphics_buttons_test", "RightOverlay", true, 90)) {
					targetCameraX += core.getTexture("graphics_background", 0).getWidth();
					pos.x += 1;
				}
			}
			ImGui.end();
		}

		if (world.containsKey(new Vector2i(pos.x, pos.y - 1))) {
			ImGui.setNextWindowPos(halfW - imageHW, windowHeight - imageH - 10);
			ImGui.setNextWindowSize(imageW, imageH);
			ImGui.begin("ButtonWindow_Down", flags);
			{
				if (HImGui.imageButton(core, "graphics_buttons_test", "DownOverlay", true, 180)) {
					targetCameraY -= core.getTexture("graphics_background", 0).getHeight();
					pos.y -= 1;
				}
			}
			ImGui.end();
		}

		if (world.containsKey(new Vector2i(pos.x - 1, pos.y))) {
			ImGui.setNextWindowPos(10, halfH - imageHH);
			ImGui.setNextWindowSize(imageW, imageH);
			ImGui.begin("ButtonWindow_Left", flags);
			{
				if (HImGui.imageButton(core, "graphics_buttons_test", "LeftOverlay", true, 270)) {
					targetCameraX -= core.getTexture("graphics_background", 0).getWidth();
					pos.x -= 1;
				}
			}
			ImGui.end();
		}

		ImGui.popStyleVar();
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
