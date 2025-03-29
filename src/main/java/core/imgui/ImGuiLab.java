package core.imgui;

import java.util.Random;

import core.helpers.SliderRenderContext;
import core.interfaces.ImguiableCTX;
import core.interfaces.Initable;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.type.ImInt;

public class ImGuiLab implements Initable, ImguiableCTX {
	private static final int ROW_SIZE = 100;
	private static final int VISIBLE_AT_ONCE = 10;
	private final char[] topRow = new char[ROW_SIZE];
	private final char[] bottomRow = new char[ROW_SIZE];
	private final ImInt labPage = new ImInt(0); // Current lab view page

	@Override
	public void init() {
		reset();
	}

	public void reset() {
		Random random = new Random();
		char[] options = { 'A', 'T', 'C', 'G' };

		for (int i = 0; i < ROW_SIZE; i++) {
			topRow[i] = options[random.nextInt(options.length)];

			char bottom;
			do {
				bottom = options[random.nextInt(options.length)];
			} while (bottom == topRow[i]);

			bottomRow[i] = bottom;
		}
	}

	@Override
	public void imgui(SliderRenderContext ctx) {
		ImGui.text("Nucleotide Display - Page " + (labPage.get() + 1));

		int startIdx = labPage.get() * VISIBLE_AT_ONCE;
		int endIdx = Math.min(startIdx + VISIBLE_AT_ONCE, ROW_SIZE);

		float totalWidth = VISIBLE_AT_ONCE * 24f; // 20px + 4px spacing
		float centerX = (ImGui.getWindowWidth() - totalWidth) / 2;

		// Top row
		ImGui.setCursorPosX(centerX);
		for (int i = startIdx; i < endIdx; i++) {
			char base = topRow[i];
			setColorForBase(base);
			ImGui.pushID(i);
			if (ImGui.button(String.valueOf(base), 30, 75)) {
				System.out.println("Clicked TOP: " + base + " at index " + i);
			}
			ImGui.popID();
			ImGui.popStyleColor();
			ImGui.sameLine();
		}
		ImGui.newLine();

		// Bottom row
		ImGui.setCursorPosX(centerX);
		for (int i = startIdx; i < endIdx; i++) {
			char base = bottomRow[i];
			setColorForBase(base);
			ImGui.pushID(i + ROW_SIZE);
			if (ImGui.button(String.valueOf(base), 30, 75)) {
				System.out.println("Clicked BOTTOM: " + base + " at index " + i);
			}
			ImGui.popID();
			ImGui.popStyleColor();
			ImGui.sameLine();
		}
		ImGui.newLine();

		// Pagination
		ImGui.spacing();
		if (ImGui.button("Prev") && labPage.get() > 0) {
			labPage.set(labPage.get() - 1);
		}
		ImGui.sameLine();
		if (ImGui.button("Next") && (labPage.get() + 1) * VISIBLE_AT_ONCE < ROW_SIZE) {
			labPage.set(labPage.get() + 1);
		}
		ImGui.text("Showing " + VISIBLE_AT_ONCE + " per row. Total pages: " + (ROW_SIZE / VISIBLE_AT_ONCE));
	}

	private void setColorForBase(char base) {
		switch (base) {
		case 'A':
			ImGui.pushStyleColor(ImGuiCol.Button, 0.0f, 0.5f, 1.0f, 0.7f);
			break;
		case 'T':
			ImGui.pushStyleColor(ImGuiCol.Button, 1.0f, 1.0f, 0.2f, 0.7f);
			break;
		case 'G':
			ImGui.pushStyleColor(ImGuiCol.Button, 0.0f, 1.0f, 0.0f, 0.7f);
			break;
		case 'C':
			ImGui.pushStyleColor(ImGuiCol.Button, 1.0f, 0.0f, 0.2f, 0.7f);
			break;
		default:
			ImGui.pushStyleColor(ImGuiCol.Button, 0.8f, 0.8f, 0.8f, 0.7f);
		}
	}
}
