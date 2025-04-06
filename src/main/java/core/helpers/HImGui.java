package core.helpers;

import core.Core;
import core.texture.Texture;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiHoveredFlags;
import imgui.flag.ImGuiMouseButton;

public class HImGui {
	public enum TextAlign {
		LEFT, CENTER, RIGHT
	}

	public static boolean imageButton(Core core, String key, String text, TextAlign textAlign) {
		Texture idleTexture = core.getTexture(key, 2);
		Texture hoverTexture = core.getTexture(key, 1);
		Texture activeTexture = core.getTexture(key, 0);

		float width = idleTexture.getWidth();
		float height = idleTexture.getHeight();

		ImGui.pushID(key);

		ImGui.invisibleButton("##statefulButton", width, height);

		boolean hovered = ImGui.isItemHovered(ImGuiHoveredFlags.AllowWhenBlockedByActiveItem);
		boolean active = ImGui.isItemActive();
		boolean justReleased = ImGui.isItemDeactivated();

		int texId;
		if (active && ImGui.isMouseDown(ImGuiMouseButton.Left)) {
			texId = activeTexture.getID();
		} else if (hovered) {
			texId = hoverTexture.getID();
		} else {
			texId = idleTexture.getID();
		}

		ImVec2 pos = ImGui.getItemRectMin();
		ImGui.setCursorScreenPos(pos.x, pos.y);
		ImGui.image(texId, width, height);

		if (text != null && !text.isEmpty()) {
			ImVec2 textSize = ImGui.calcTextSize(text);

			float textX;
			switch (textAlign) {
			case RIGHT:
				textX = pos.x + width - textSize.x - 4;
				break;
			case LEFT:
				textX = pos.x + 4;
				break;
			case CENTER:
			default:
				textX = pos.x + (width - textSize.x) * 0.5f;
				break;
			}

			float textY = pos.y + (height - textSize.y) * 0.5f;
			ImGui.setCursorScreenPos(textX, textY);
			ImGui.text(text);
		}

		ImGui.popID();

		return hovered && justReleased;
	}
}
