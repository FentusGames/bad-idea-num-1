package core.helpers.imgui;

import java.util.HashMap;
import java.util.Map;

import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import core.Core;
import core.camera.Camera;
import core.helpers.HImGui;
import core.helpers.WorldTile;
import core.interfaces.Imguiable;
import core.interfaces.Initable;
import core.interfaces.KeyCallback;
import core.interfaces.Renderable;
import core.interfaces.Updateable;
import core.screens.Screen;
import core.texture.Texture;
import imgui.ImGui;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;

public class Navigation implements Initable, Renderable, Updateable, Imguiable, KeyCallback {
	private static final float CAMERA_LERP_FACTOR = 0.1f;

	private Screen screen;

	private float targetCameraX = 0.0f;
	private float targetCameraY = 0.0f;

	private final Vector2i pos = new Vector2i(0, 0);
	private final Map<Vector2i, WorldTile> world = new HashMap<>();

	public Navigation(Screen screen) {
		this.screen = screen;
	}

	@Override
	public void init(int windowX, int windowY, int windowWidth, int windowHeight) {
		Core core = screen.getCore();

		world.put(new Vector2i(0, 0), new WorldTile(core.getTexture("graphics_background"), core.getLang("tile-main")));
		world.put(new Vector2i(1, 0), new WorldTile(core.getTexture("graphics_background"), core.getLang("tile-1")));
		world.put(new Vector2i(0, 1), new WorldTile(core.getTexture("graphics_background"), core.getLang("tile-2")));
		world.put(new Vector2i(1, 1), new WorldTile(core.getTexture("graphics_background"), core.getLang("tile-3")));
	}

	@Override
	public void render(float delta, int windowX, int windowY, int windowWidth, int windowHeight) {
		world.forEach((pos, worldTile) -> {
			Texture texture = worldTile.getTexture();

			texture.setX(pos.x * texture.getWidth());
			texture.setY(pos.y * texture.getHeight());
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

		Texture texture = core.getAnimation("graphics_buttons_test").getFrames().get(0);

		float imageW = core.getScale(texture.getWidth());
		float imageH = core.getScale(texture.getHeight());
		float imageHW = imageW * 0.5f;
		float imageHH = imageH * 0.5f;
		float halfW = windowWidth * 0.5f;
		float halfH = windowHeight * 0.5f;

		int flags = ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoNav | ImGuiWindowFlags.NoBackground;

		ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);

		Vector2i upTile = new Vector2i(pos.x, pos.y + 1);
		if (world.containsKey(upTile)) {
			ImGui.setNextWindowPos(halfW - imageHW, 10);
			ImGui.setNextWindowSize(imageW, imageH);
			ImGui.begin("ButtonWindow_Up", flags);
			{
				if (HImGui.imageButton(core, "graphics_buttons_test", "UpOverlay", true, 0, world.get(upTile).getName())) {
					up(core);
				}
			}
			ImGui.end();
		}

		Vector2i rightTile = new Vector2i(pos.x + 1, pos.y);
		if (world.containsKey(rightTile)) {
			ImGui.setNextWindowPos(windowWidth - imageW - 10, halfH - imageHH);
			ImGui.setNextWindowSize(imageW, imageH);
			ImGui.begin("ButtonWindow_Right", flags);
			{
				if (HImGui.imageButton(core, "graphics_buttons_test", "RightOverlay", true, 90, world.get(rightTile).getName())) {
					right(core);
				}
			}
			ImGui.end();
		}

		Vector2i downTile = new Vector2i(pos.x, pos.y - 1);
		if (world.containsKey(downTile)) {
			ImGui.setNextWindowPos(halfW - imageHW, windowHeight - imageH - 10);
			ImGui.setNextWindowSize(imageW, imageH);
			ImGui.begin("ButtonWindow_Down", flags);
			{
				if (HImGui.imageButton(core, "graphics_buttons_test", "DownOverlay", true, 180, world.get(downTile).getName())) {
					down(core);
				}
			}
			ImGui.end();
		}

		Vector2i leftTile = new Vector2i(pos.x - 1, pos.y);
		if (world.containsKey(leftTile)) {
			ImGui.setNextWindowPos(10, halfH - imageHH);
			ImGui.setNextWindowSize(imageW, imageH);
			ImGui.begin("ButtonWindow_Left", flags);
			{
				if (HImGui.imageButton(core, "graphics_buttons_test", "LeftOverlay", true, 270, world.get(leftTile).getName())) {
					left(core);
				}
			}
			ImGui.end();
		}

		ImGui.popStyleVar();
	}

	@Override
	public void key(long window, int key, int scancode, int action, int mods) {
		Core core = screen.getCore();

		if (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT) {
			switch (key) {
			case GLFW.GLFW_KEY_W:
				up(core);
				break;
			case GLFW.GLFW_KEY_D:
				right(core);
				break;
			case GLFW.GLFW_KEY_S:
				down(core);
				break;
			case GLFW.GLFW_KEY_A:
				left(core);
				break;
			default:
				break;
			}
		}
	}

	private void up(Core core) {
		if (world.containsKey(new Vector2i(pos.x, pos.y + 1))) {
			targetCameraY += core.getTexture("graphics_background", 0).getHeight();
			pos.y += 1;
		}
	}

	private void right(Core core) {
		if (world.containsKey(new Vector2i(pos.x + 1, pos.y))) {
			targetCameraX += core.getTexture("graphics_background", 0).getWidth();
			pos.x += 1;
		}
	}

	private void down(Core core) {
		if (world.containsKey(new Vector2i(pos.x, pos.y - 1))) {
			targetCameraY -= core.getTexture("graphics_background", 0).getHeight();
			pos.y -= 1;
		}
	}

	private void left(Core core) {
		if (world.containsKey(new Vector2i(pos.x - 1, pos.y))) {
			targetCameraX -= core.getTexture("graphics_background", 0).getWidth();
			pos.x -= 1;
		}
	}
}