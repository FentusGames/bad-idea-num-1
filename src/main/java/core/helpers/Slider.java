package core.helpers;

import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import core.Core;
import core.interfaces.Imguiable;
import core.interfaces.Updateable;
import core.texture.Texture;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;

public class Slider implements Updateable, Imguiable {
	private Core core;

	private float offsetX = 0, offsetY = 0;
	private float targetOffsetX = 0, targetOffsetY = 0;

	private int coordX = 0;
	private int coordY = 0;

	private final Set<Point> allowedCoords = new HashSet<>();
	private final Map<Point, String> screenNames = new HashMap<>();

	public Slider(Core core) {
		this.core = core;

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
	public void update(float delta) {
		float moveSpeed = 80F;
		offsetX = approach(offsetX, targetOffsetX, moveSpeed * delta);
		offsetY = approach(offsetY, targetOffsetY, moveSpeed * delta);
	}

	@Override
	public void imgui(float delta, int windowX, int windowY, int windowWidth, int windowHeight) {
		renderNavigationButtons(windowWidth, windowHeight);
	}

	private float approach(float current, float target, float maxDelta) {
		if (current < target) {
			return Math.min(current + maxDelta, target);
		} else {
			return Math.max(current - maxDelta, target);
		}
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
	
	public float getOffsetX() {
		return offsetX;
	}
	
	public float getOffsetY() {
		return offsetY;
	}

	public void setNextWindowSlideable(int x, int y) {
		ImGui.setNextWindowPos(offsetX + x, offsetY + y, ImGuiCond.Always);
	}
}
