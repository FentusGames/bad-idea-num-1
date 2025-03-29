package core.helpers;

import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import core.Core;
import core.interfaces.Imguiable;
import core.interfaces.Updateable;
import core.texture.Texture;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;

public class ImGuiSlider implements Updateable, Imguiable {
	private static final float MOVE_SPEED = 20;

	private Core core;

	private float offsetX = 0, offsetY = 0;
	private float targetOffsetX = 0, targetOffsetY = 0;

	private int coordX = 0;
	private int coordY = 0;

	private final Set<Point> allowedCoords = new HashSet<>();
	private final Map<Point, String> screenNames = new HashMap<>();
	private final Map<Point, Consumer<SliderRenderContext>> screenRenderers = new HashMap<>();

	public ImGuiSlider(Core core) {
		this.core = core;
	}

	@Override
	public void update(float delta) {
		offsetX = approach(offsetX, targetOffsetX, MOVE_SPEED * delta);
		offsetY = approach(offsetY, targetOffsetY, MOVE_SPEED * delta);
	}

	@Override
	public void imgui(float delta, int windowX, int windowY, int windowWidth, int windowHeight) {
		renderNavigationButtons(windowWidth, windowHeight);

		int contentWidth = windowWidth - 100;
		int contentHeight = windowHeight - 100;
		int contentOffset = 100 / 2;

		for (Map.Entry<Point, String> entry : screenNames.entrySet()) {
			Point coord = entry.getKey();
			String label = entry.getValue();

			int x = coord.x * windowWidth + contentOffset;
			int y = -coord.y * windowHeight + contentOffset;

			setNextWindowSlideable(x, y);
			ImGui.setNextWindowSize(contentWidth, contentHeight);
			ImGui.begin(label, ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoTitleBar);

			var renderer = screenRenderers.get(coord);
			if (renderer != null) {
				renderer.accept(new SliderRenderContext(core, delta, windowX, windowY, windowWidth, windowHeight));
			} else {
				ImGui.text("No renderer attached for: " + label);
			}

			ImGui.end();
		}
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

		DirButton[] buttons = new DirButton[] { new DirButton("Up", 0, 1, windowWidth * 0.5f, margin, 0.5f, 0.0f, "up"), new DirButton("Down", 0, -1, windowWidth * 0.5f, windowHeight - margin, 0.5f, 1.0f, "down"), new DirButton("Left", -1, 0, margin, windowHeight * 0.5f, 0.0f, 0.5f, "left"), new DirButton("Right", 1, 0, windowWidth - margin, windowHeight * 0.5f, 1.0f, 0.5f, "right"), };

		for (DirButton btn : buttons) {
			int nextX = coordX + btn.dx;
			int nextY = coordY + btn.dy;

			if (!allowedCoords.contains(new Point(nextX, nextY))) {
				continue; // hide button if not accessible
			}

			ImGui.setNextWindowPos(btn.x, btn.y, ImGuiCond.Always, btn.alignX, btn.alignY);
			ImGui.begin(btn.label + " Button", flags);
			Texture normal = core.getTexture("graphics_buttons_" + btn.textureBase, 0);
			Texture hover = core.getTexture("graphics_buttons_" + btn.textureBase + "_hover", 0);
			Texture current = ImGui.isMouseHoveringRect(ImGui.getCursorScreenPosX(), ImGui.getCursorScreenPosY(), ImGui.getCursorScreenPosX() + normal.getWidth(), ImGui.getCursorScreenPosY() + normal.getHeight()) ? hover : normal;

			if (ImGui.imageButton(current.getID(), current.getWidth(), current.getHeight(), 0, 0, 1, 1, 0)) {
				targetOffsetX += btn.dx * windowWidth;
				targetOffsetY += btn.dy * windowHeight;
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

	public void addScreen(int x, int y, String label, Consumer<SliderRenderContext> renderer) {
		Point p = new Point(x, y);
		allowedCoords.add(p);
		screenNames.put(p, label);
		screenRenderers.put(p, renderer);
	}
}
