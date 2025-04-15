package core.helpers.imgui;

import core.Core;
import core.helpers.HImGui;
import core.interfaces.Imguiable;
import core.screens.Screen;
import core.texture.Texture;
import imgui.ImFont;
import imgui.ImGui;

public class DemoWindow implements Imguiable {

	private Screen screen;

	public DemoWindow(Screen screen) {
		this.screen = screen;
	}

	@Override
	public void imgui(float delta) {
		Core core = screen.getCore();

		ImGui.begin("Demo Panel");
		{
			ImFont font1 = core.getFont("default", 12);
			ImGui.pushFont(font1);
			{
				ImGui.text("Fixed font (12 @ 1366x768)");
				ImGui.text(String.format("Font Size: %.1f", font1.getFontSize()));
				ImGui.text(String.format("Delta Time: %.3f", delta));
				ImGui.text(String.format("Window Size: %d x %d", core.getWindowWidth(), core.getWindowHeight()));
			}
			ImGui.popFont();

			ImGui.newLine();

			ImFont font2 = core.getFont("default", core.getScale(12));
			ImGui.pushFont(font2);
			{
				ImGui.text("Scaled font based on resolution");
				ImGui.text(String.format("Font Size: %.1f", font2.getFontSize()));
				ImGui.text(String.format("Delta Time: %.3f", delta));
				ImGui.text(String.format("Window Size: %d x %d", core.getWindowWidth(), core.getWindowHeight()));
			}
			ImGui.popFont();

			ImGui.newLine();

			Texture texture = core.getTexture("graphics_background", 0);
			HImGui.imageRotated(texture.getID(), core.getScale(256), core.getScale(108));

			ImGui.newLine();
		}
		ImGui.end();
	}
}
