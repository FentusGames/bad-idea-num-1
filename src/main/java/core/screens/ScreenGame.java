package core.screens;

import java.util.HashMap;
import java.util.Map;

import org.jooq.DSLContext;
import org.lwjgl.opengl.GL11;

import core.Core;
import core.camera.Camera;
import core.helpers.HImGui;
import core.helpers.Slider;
import imgui.ImGui;

public class ScreenGame extends Screen {
	private final Camera camera;
	private final DSLContext db = core.getDB("game");

	private Slider slider = new Slider(core);
	private final Map<String, Float> buttonWidthOffsets = new HashMap<>();

	// Variables that need saved.
	private int saveId;
	private String saveName;
	private int daysPassed = 0;
	private float money = 2500.00F;

	public ScreenGame(Core core) {
		super(core);
		this.camera = new Camera(core.getWindowPtr());
	}

	@Override
	public void init() {
		super.init();

		slider.addScreen(0, 0, "Main", ctx -> {
			ImGui.text("This is the main view!");
			HImGui.renderDays(ctx);
			HImGui.renderNextDay(ctx, buttonWidthOffsets);
			HImGui.renderMoney(ctx);
		});

		slider.addScreen(0, 1, "Customer", ctx -> {
			ImGui.text("This is the customer view!");
		});

		slider.addScreen(0, 2, "Customer Info", ctx -> {
			ImGui.text("This is the customer info view!");
		});

		slider.addScreen(1, 0, "Lab", ctx -> {
			ImGui.text("This is the lab view!");
		});

		slider.addScreen(-1, 0, "Societal Impact", ctx -> {
			ImGui.text("This is the societal impact view!");
		});

		slider.addScreen(0, -1, "Finance", ctx -> {
			ImGui.text("This is the finance view!");
			HImGui.renderMoney(ctx);
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
		slider.update(delta);
	}

	@Override
	public void imgui(float delta, int windowX, int windowY, int windowWidth, int windowHeight) {
		slider.imgui(delta, windowX, windowY, windowWidth, windowHeight);
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

	public int getDaysPassed() {
		return daysPassed;
	}

	public void setDaysPassed(int daysPassed) {
		this.daysPassed = daysPassed;
	}

	public float getMoney() {
		return money;
	}

	public void setMoney(float money) {
		this.money = money;
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
}
