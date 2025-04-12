package core.helpers;

import core.texture.Animation;
import core.texture.Texture;
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

	public static void imageRotated(int textureId, float width, float height, int rotationDegrees, boolean flipX, boolean flipY) {
		// Normalize rotation
		int rotation = ((rotationDegrees % 360) + 360) % 360;

		float[][] uv = {
			// @formatter:off
			{ 0.0f, 0.0f }, // top-left
			{ 1.0f, 0.0f }, // top-right
			{ 1.0f, 1.0f }, // bottom-right
			{ 0.0f, 1.0f }, // bottom-left
			// @formatter:on
		};

		// Flip X (left-right)
		if (flipX) {
			for (float[] corner : uv) {
				corner[0] = 1.0f - corner[0];
			}
		}

		// Flip Y (top-bottom)
		if (flipY) {
			for (float[] corner : uv) {
				corner[1] = 1.0f - corner[1];
			}
		}

		// Rotate by shifting the corners
		int shift = rotation / 90;
		float[] uv0 = uv[(0 + shift) % 4];
		float[] uv2 = uv[(2 + shift) % 4];

		// Draw using upper-left and lower-right bounds
		ImGui.image(textureId, width, height, uv0[0], uv0[1], uv2[0], uv2[1]);
	}

	public static boolean imageButton(Animation animation, float width, float height) {
		return imageButton(animation, width, height, 0, false, false);
	}

	public static boolean imageButton(Animation animation) {
		Texture texture = animation.getFrames().get(0);

		return imageButton(animation, texture.getWidth(), texture.getHeight(), 0, false, false);
	}

	public static boolean imageButton(Animation animation, float width, float height, int rotationDegrees, boolean flipX, boolean flipY) {
		if (animation.getFrames().size() < 3) {
			throw new IllegalArgumentException("Animation must have >= 3 frames: 0=active,1=hover,2=idle");
		}

		ImGui.pushID(animation.getFrames().get(2).getID());

		ImVec2 startPos = new ImVec2();
		ImGui.getCursorPos(startPos);

		boolean clicked = ImGui.invisibleButton("##imageButton", width, height, ImGuiButtonFlags.None);

		boolean isActive = ImGui.isItemActive();
		boolean isHovered = ImGui.isItemHovered();

		int textureId;
		if (isActive) {
			textureId = animation.getFrames().get(0).getID(); // ACTIVE
		} else if (isHovered) {
			textureId = animation.getFrames().get(1).getID(); // HOVER
		} else {
			textureId = animation.getFrames().get(2).getID(); // IDLE
		}

		ImGui.setCursorPos(startPos.x, startPos.y);

		imageRotated(textureId, width, height, rotationDegrees, flipX, flipY);

		ImGui.setCursorPos(startPos.x, startPos.y + height);

		ImGui.popID();

		return clicked;
	}
}
