package core.screens;

import org.jooq.DSLContext;
import org.lwjgl.opengl.GL11;

import core.Core;
import core.camera.Camera;
import core.imgui.ImGuiDays;
import core.imgui.ImGuiLab;
import core.imgui.ImGuiMoney;
import core.imgui.ImGuiNextDay;
import core.imgui.ImGuiSlider;
import imgui.ImGui;

public class ScreenGame extends Screen {
	private final Camera camera;
	private final DSLContext db = core.getDB("game");

	private ImGuiSlider imGuiSlider = new ImGuiSlider(core);
	private ImGuiDays imGuiDays = new ImGuiDays();
	private ImGuiMoney imGuiMoney = new ImGuiMoney();
	private ImGuiNextDay imGuiNextDay = new ImGuiNextDay();
	private ImGuiLab imGuiLab = new ImGuiLab();

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

		imGuiLab.init();

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
			imGuiLab.imgui(ctx);
		});

		imGuiSlider.addScreen(-1, 0, "Societal Impact", ctx -> {
			ImGui.text("This is the societal impact view!");
		});

		imGuiSlider.addScreen(0, -1, "Finance", ctx -> {
			ImGui.text("This is the finance view!");
			imGuiMoney.imgui(ctx);
		});
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
	
	public ImGuiLab getImGuiLab() {
		return imGuiLab;
	}
}
