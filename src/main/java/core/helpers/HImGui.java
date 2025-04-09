package core.helpers;

import imgui.ImGui;

public class HImGui {

	public static void imageRotated(int textureId, float width, float height) {
		imageRotated(textureId, width, height, 0, false, true); // default flip Y
	}

	public static void imageRotated(int textureId, float width, float height, int rotationDegrees) {
		imageRotated(textureId, width, height, rotationDegrees, false, true); // default flip Y
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
}
