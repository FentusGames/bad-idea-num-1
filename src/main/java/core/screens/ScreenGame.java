package core.screens;

import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jooq.DSLContext;
import org.lwjgl.opengl.GL11;

import core.Core;
import core.camera.Camera;
import core.texture.Texture;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;

public class ScreenGame extends Screen {

	private final Camera camera;
	private final DSLContext db;

	private float offsetX = 0, offsetY = 0;
	private float targetOffsetX = 0, targetOffsetY = 0;

	private int coordX = 0;
	private int coordY = 0;

	private final Set<Point> allowedCoords = new HashSet<>();
	private final Map<Point, String> screenNames = new HashMap<>();

	public ScreenGame(Core core) {
		super(core);
		this.camera = new Camera(core.getWindowPtr());
		this.db = core.getDB("game");

		// Define accessible screens and names
		allowedCoords.add(new Point(0, 0));
		screenNames.put(new Point(0, 0), "Main");

		allowedCoords.add(new Point(0, 1));
		screenNames.put(new Point(0, 1), "Minigame 1");

		allowedCoords.add(new Point(0, -1));
		screenNames.put(new Point(0, -1), "Minigame 2");

		allowedCoords.add(new Point(1, 0));
		screenNames.put(new Point(1, 0), "Minigame 3");

		allowedCoords.add(new Point(-1, 0));
		screenNames.put(new Point(-1, 0), "Minigame 4");
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
		float moveSpeed = 80F;
		offsetX = approach(offsetX, targetOffsetX, moveSpeed * delta);
		offsetY = approach(offsetY, targetOffsetY, moveSpeed * delta);
	}

	@Override
	public void imgui(float delta, int windowX, int windowY, int windowWidth, int windowHeight) {
		renderMovableUI(windowWidth, windowHeight);

		renderNavigationButtons(windowWidth, windowHeight);

		renderDebugWindow();

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

	private void renderMovableUI(int windowWidth, int windowHeight) {
		ImGui.setNextWindowPos(offsetX + 100, offsetY + 100, ImGuiCond.Always);
		ImGui.begin("Main Screen", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove);
		ImGui.text("Resolution: " + windowWidth + " x " + windowHeight);
		ImGui.text(String.format("Offset: (%.1f, %.1f)", offsetX, offsetY));
		ImGui.end();
	}

	private void renderNavigationButtons(int windowWidth, int windowHeight) {
		float margin = 10f;
		int flags = ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoBackground | ImGuiWindowFlags.AlwaysAutoResize;

		// Direction button helper
		class DirButton {
			String label;
			int dx, dy;
			float x, y;
			float alignX, alignY;
			String textureBase;

			DirButton(String label, int dx, int dy, float x, float y, float alignX, float alignY, String textureBase) {
				this.label = label;
				this.dx = dx;
				this.dy = dy;
				this.x = x;
				this.y = y;
				this.alignX = alignX;
				this.alignY = alignY;
				this.textureBase = textureBase;
			}
		}

		DirButton[] buttons = new DirButton[] { new DirButton("Up", 0, 1, windowWidth * 0.5f, margin, 0.5f, 0.0f, "up"), new DirButton("Down", 0, -1, windowWidth * 0.5f, windowHeight - margin, 0.5f, 1.0f, "down"), new DirButton("Left", -1, 0, margin, windowHeight * 0.5f, 0.0f, 0.5f, "left"), new DirButton("Right", 1, 0, windowWidth - margin, windowHeight * 0.5f, 1.0f, 0.5f, "right"), };

		for (DirButton btn : buttons) {
			int nextX = coordX + btn.dx;
			int nextY = coordY + btn.dy;

			if (!allowedCoords.contains(new Point(nextX, nextY))) {
				continue; // hide button if not accessible
			}

			ImGui.setNextWindowPos(btn.x, btn.y, ImGuiCond.Always, btn.alignX, btn.alignY);
			ImGui.begin(btn.label + " Button", flags);
			Texture normal = core.getTexture("graphics_" + btn.textureBase, 0);
			Texture hover = core.getTexture("graphics_" + btn.textureBase + "_hover", 0);
			Texture current = ImGui.isMouseHoveringRect(ImGui.getCursorScreenPosX(), ImGui.getCursorScreenPosY(), ImGui.getCursorScreenPosX() + normal.getWidth(), ImGui.getCursorScreenPosY() + normal.getHeight()) ? hover : normal;

			if (ImGui.imageButton(current.getID(), current.getWidth(), current.getHeight(), 0, 0, 1, 1, 0)) {
				targetOffsetX += btn.dx * windowWidth;
				targetOffsetY -= btn.dy * windowHeight; // Y is inverted
				coordX += btn.dx;
				coordY += btn.dy;
			}
			ImGui.end();
		}
	}

	private void renderDebugWindow() {
		ImGui.setNextWindowPos(10, 10, ImGuiCond.Once);
		ImGui.begin("Debug", ImGuiWindowFlags.AlwaysAutoResize | ImGuiWindowFlags.NoCollapse);
		ImGui.text("Logical Coordinates:");
		ImGui.text(String.format("X: %d", coordX));
		ImGui.text(String.format("Y: %d", coordY));

		Point current = new Point(coordX, coordY);
		String name = screenNames.getOrDefault(current, "Unknown");

		ImGui.separator();
		ImGui.text("Current Screen:");
		ImGui.textColored(0.8f, 1.0f, 0.4f, 1.0f, name);
		ImGui.end();
	}

	private float approach(float current, float target, float maxDelta) {
		if (current < target) {
			return Math.min(current + maxDelta, target);
		} else {
			return Math.max(current - maxDelta, target);
		}
	}
}
