package core.helpers.imgui;

import java.util.HashMap;
import java.util.Map;

import org.joml.Vector2i;
import org.joml.Vector3f;

import core.Core;
import core.camera.Camera;
import core.helpers.HImGui;
import core.interfaces.Imguiable;
import core.interfaces.Initable;
import core.interfaces.Renderable;
import core.interfaces.Updateable;
import core.screens.Screen;
import core.texture.Texture;
import imgui.ImGui;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;

public class Navigation implements Initable, Renderable, Updateable, Imguiable {
	private Screen screen;

	private float targetCameraX = 0.0f;
	private float targetCameraY = 0.0f;

	private static final float CAMERA_LERP_FACTOR = 0.1f;

	private final Vector2i pos = new Vector2i(0, 0);

	private final Map<Vector2i, Texture> world = new HashMap<>();

	public Navigation(Screen screen) {
		this.screen = screen;
	}

	@Override
	public void init(int windowX, int windowY, int windowWidth, int windowHeight) {
		Core core = screen.getCore();

		world.put(new Vector2i(0, 0), core.getTexture("graphics_background"));
		world.put(new Vector2i(-1, 0), core.getTexture("graphics_background"));
		world.put(new Vector2i(0, 1), core.getTexture("graphics_background"));
		world.put(new Vector2i(1, 1), core.getTexture("graphics_background"));
	}

	@Override
	public void render(float delta, int windowX, int windowY, int windowWidth, int windowHeight) {
		world.forEach((point, texture) -> {
			texture.setX(point.x * texture.getWidth());
			texture.setY(point.y * texture.getHeight());
			texture.render(delta, 0, 0, 0, 0);
		});
	}

	@Override
	public void update(float delta, int windowX, int windowY, int windowWidth, int windowHeight) {
		Camera camera = screen.getCamera();

		float currentX = camera.getPosition().x;
		float currentY = camera.getPosition().y;

		float newX = currentX + CAMERA_LERP_FACTOR * (targetCameraX - currentX);
		float newY = currentY + CAMERA_LERP_FACTOR * (targetCameraY - currentY);

		camera.setPosition(new Vector3f(newX, newY, 0));
	}

	@Override
	public void imgui(float delta, int windowX, int windowY, int windowWidth, int windowHeight) {
		Core core = screen.getCore();

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
}