package core.screens;

import org.jooq.DSLContext;
import org.lwjgl.opengl.GL11;

import core.Core;
import core.camera.Camera;
import core.helpers.Slider;
import imgui.ImGui;

public class ScreenGame extends Screen {
	private final Camera camera;
	private final DSLContext db = core.getDB("game");

	private Slider slider = new Slider(core);
	private int daysPassed = 0;

	public ScreenGame(Core core) {
		super(core);
		this.camera = new Camera(core.getWindowPtr());
	}

	@Override
	public void init() {
		super.init();

		slider.addScreen(0, 0, "Main", ctx -> {
			// Day
			ImGui.pushFont(core.getFont("default", 44));
			float windowWidth = ImGui.getWindowWidth();
			String dayLabel = "Day: " + daysPassed;
			float textWidth = ImGui.calcTextSize(dayLabel).x;
			ImGui.setCursorPosX((windowWidth - textWidth) * 0.5f);
			ImGui.text(dayLabel);
			ImGui.popFont();

			// Next Day
			ImGui.pushFont(core.getFont("default", 26));
			float buttonWidth = ImGui.calcTextSize("Next Day").x + 20;
			float buttonHeight = 40;
			float windowHeight = ImGui.getWindowHeight();
			ImGui.setCursorPosX(windowWidth - buttonWidth - 10);
			ImGui.setCursorPosY(windowHeight - buttonHeight - 10);

			if (ImGui.button("Next Day", buttonWidth, buttonHeight)) {
				daysPassed++;
			}

			ImGui.popFont();
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
}
