package core.helpers;

import core.Core;
import core.texture.Texture;
import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiButtonFlags;

public class HImGui {

	public static void imageRotated(int textureId, float width, float height) {
		imageRotated(textureId, width, height, 0, false, false);
	}

	public static void imageRotated(int textureId, float width, float height, int rotationDegrees) {
		imageRotated(textureId, width, height, rotationDegrees, false, false);
	}

	public static void imageRotated(int textureId, float width, float height, float rotationDegrees, boolean flipX, boolean flipY) {
		final ImDrawList drawList = ImGui.getWindowDrawList();

		ImVec2 screenPos = ImGui.getCursorScreenPos();
		float x = screenPos.x;
		float y = screenPos.y;

		float cx = x + width * 0.5f;
		float cy = y + height * 0.5f;

		// @formatter:off
		ImVec2[] corners = new ImVec2[] {
			new ImVec2(-width * 0.5f, -height * 0.5f),	// top-left
			new ImVec2(width * 0.5f, -height * 0.5f),	// top-right
			new ImVec2(width * 0.5f, height * 0.5f),	// bottom-right
			new ImVec2(-width * 0.5f, height * 0.5f),	// bottom-left
		};
		// @formatter:on

		// Convert degrees -> radians
		float rad = (float) Math.toRadians(rotationDegrees);
		float cosA = (float) Math.cos(rad);
		float sinA = (float) Math.sin(rad);

		for (ImVec2 corner : corners) {
			float oldX = corner.x;
			float oldY = corner.y;

			float rx = oldX * cosA - oldY * sinA;
			float ry = oldX * sinA + oldY * cosA;

			corner.x = rx + cx;
			corner.y = ry + cy;
		}

		// @formatter:off
		float[][] uv = {
			{ 0.0f, 0.0f }, // top-left
			{ 1.0f, 0.0f }, // top-right
			{ 1.0f, 1.0f }, // bottom-right
			{ 0.0f, 1.0f }, // bottom-left
		};
		// @formatter:on

		// Flip in X?
		if (flipX) {
			for (float[] u : uv) {
				u[0] = 1.0f - u[0];
			}
		}

		// Flip in Y?
		if (flipY) {
			for (float[] u : uv) {
				u[1] = 1.0f - u[1];
			}
		}

		// @formatter:off
		drawList.addImageQuad(
			textureId, 
			corners[0].x, corners[0].y, 
			corners[1].x, corners[1].y, 
			corners[2].x, corners[2].y, 
			corners[3].x, corners[3].y, 
			uv[0][0], uv[0][1], 
			uv[1][0], uv[1][1], 
			uv[2][0], uv[2][1], 
			uv[3][0], uv[3][1], 
			0xFFFFFFFF
		);
		// @formatter:on

		ImGui.setCursorScreenPos(x, y + height);
	}

	public static boolean imageButton(Core core, String key, String name, float width, float height, String tooltip) {
		return imageButton(core, key, name, width, height, 0, false, false, tooltip);
	}

	public static boolean imageButton(Core core, String key, String name, Boolean scale, String tooltip) {
		return imageButton(core, key, name, scale, 0, tooltip);
	}

	public static boolean imageButton(Core core, String key, String name, Boolean scale, int rotationDegrees, String tooltip) {
		Texture texture = core.getAnimation(key).getFrames().get(0);

		if (scale) {
			return imageButton(core, key, name, core.getScale(texture.getWidth()), core.getScale(texture.getHeight()), rotationDegrees, false, false, tooltip);
		} else {
			return imageButton(core, key, name, texture.getWidth(), texture.getHeight(), rotationDegrees, false, false, tooltip);
		}
	}

	public static boolean imageButton(Core core, String key, String name, float width, float height, int rotationDegrees, boolean flipX, boolean flipY, String tooltip) {
		if (core.getAnimation(key).getFrames().size() < 3) {
			throw new IllegalArgumentException("Animation must have >= 3 frames: 0=active,1=hover,2=idle");
		}

		ImGui.pushID(core.getAnimation(key).getFrames().get(2).getID());

		ImVec2 startPos = new ImVec2();
		ImGui.getCursorPos(startPos);

		boolean clicked = ImGui.invisibleButton("##imageButton_" + name, width, height, ImGuiButtonFlags.None);

		boolean isActive = ImGui.isItemActive();
		boolean isHovered = ImGui.isItemHovered();

		int textureId;
		if (isActive) {
			textureId = core.getAnimation(key).getFrames().get(0).getID(); // ACTIVE
		} else if (isHovered) {
			textureId = core.getAnimation(key).getFrames().get(1).getID(); // HOVER
		} else {
			textureId = core.getAnimation(key).getFrames().get(2).getID(); // IDLE
		}

		ImGui.setCursorPos(startPos.x, startPos.y);

		imageRotated(textureId, width, height, rotationDegrees, flipX, flipY);

		if (isHovered) {
	        ImGui.beginTooltip();
	        ImGui.text(tooltip);
	        ImGui.endTooltip();
	    }
		
		ImGui.setCursorPos(startPos.x, startPos.y + height);

		ImGui.popID();

		return clicked;
	}
}
