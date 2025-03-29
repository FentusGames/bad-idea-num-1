package core.screens;

import java.util.Random;

import org.jooq.DSLContext;
import org.lwjgl.opengl.GL11;

import core.Core;
import core.camera.Camera;
import core.imgui.ImGuiDays;
import core.imgui.ImGuiMoney;
import core.imgui.ImGuiNextDay;
import core.imgui.ImGuiSlider;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.type.ImInt;

public class ScreenGame extends Screen {
	private final Camera camera;
	private final DSLContext db = core.getDB("game");

	private ImGuiSlider imGuiSlider = new ImGuiSlider(core);
	private ImGuiDays imGuiDays = new ImGuiDays();
	private ImGuiMoney imGuiMoney = new ImGuiMoney();
	private ImGuiNextDay imGuiNextDay = new ImGuiNextDay();

	// Lab state
	private static final int ROW_SIZE = 100;
	private static final int VISIBLE_AT_ONCE = 10;
	private final char[] topRow = new char[ROW_SIZE];
	private final char[] bottomRow = new char[ROW_SIZE];
	private final ImInt labPage = new ImInt(0); // Current lab view page

	// Variables that need saved.
	private int saveId;
	private String saveName;

	public ScreenGame(Core core) {
		super(core);
		this.camera = new Camera(core.getWindowPtr());
	}

	@Override
	public void init() {
		super.init();

		initializeLabNucleotides();

		imGuiSlider.addScreen(0, 0, "Main", ctx -> {
			ImGui.text("This is the main view!");
			imGuiDays.imgui(ctx);
			imGuiMoney.imgui(ctx);
			imGuiNextDay.imgui(ctx);
		});

		imGuiSlider.addScreen(0, 1, "Customer", ctx -> {
			ImGui.text("This is the customer view!");
		});

		imGuiSlider.addScreen(0, 2, "Customer Info", ctx -> {
			ImGui.text("This is the customer info view!");
		});

		imGuiSlider.addScreen(1, 0, "Lab", ctx -> {
			ImGui.text("This is the lab view!");
			renderLabView();
		});

		imGuiSlider.addScreen(-1, 0, "Societal Impact", ctx -> {
			ImGui.text("This is the societal impact view!");
		});

		imGuiSlider.addScreen(0, -1, "Finance", ctx -> {
			ImGui.text("This is the finance view!");
			imGuiMoney.imgui(ctx);
		});
	}

	private void initializeLabNucleotides() {
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

	private void renderLabView() {
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

	@Override
	public void render(float delta) {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		camera.apply();
	}

	@Override
	public void update(float delta) {
		imGuiSlider.update(delta);
	}

	@Override
	public void imgui(float delta, int windowX, int windowY, int windowWidth, int windowHeight) {
		imGuiSlider.imgui(delta, windowX, windowY, windowWidth, windowHeight);
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

	public DSLContext getDb() {
		return db;
	}

	public int getSaveId() {
		return saveId;
	}

	public void setSaveId(int saveId) {
		this.saveId = saveId;
	}

	public String getSaveName() {
		return saveName;
	}

	public void setSaveName(String saveName) {
		this.saveName = saveName;
	}

	public ImGuiDays getImGuiDays() {
		return imGuiDays;
	}

	public ImGuiMoney getImGuiMoney() {
		return imGuiMoney;
	}

	public ImGuiSlider getImGuiSlider() {
		return imGuiSlider;
	}
}
