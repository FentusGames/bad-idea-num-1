package core.screens;

import java.util.Set;

import org.reflections.Reflections;

import core.Core;
import core.interfaces.Disposable;
import core.interfaces.Imguiable;
import core.interfaces.Initable;
import core.interfaces.KeyCallback;
import core.interfaces.MouseButtonCallback;
import core.interfaces.Renderable;
import core.interfaces.ScrollCallback;
import core.interfaces.Updateable;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;

public abstract class Screen implements Initable, Renderable, Updateable, Imguiable, Disposable, ScrollCallback, MouseButtonCallback, KeyCallback {
	protected Core core;

	private Set<Class<? extends Screen>> screenClasses;

	public Screen(Core core) {
		this.core = core;
	}

	public Core getCore() {
		return core;
	}

	public void setCore(Core core) {
		this.core = core;
	}

	@Override
	public void init() {
		core.setScrollCallback(this);
		core.setMouseButtonCallback(this);
		core.setKeyCallback(this);

		// Load all screens dynamically
		screenClasses = getAllScreens();
	}

	@Override
	public abstract void render(float delta);

	@Override
	public abstract void update(float delta);

	@Override
	public abstract void imgui(float delta, int windowX, int windowY, int windowWidth, int windowHeight);

	@Override
	public void dispose() {
		core.setScrollCallback(this);
		core.setMouseButtonCallback(this);
		core.setKeyCallback(this);
	}

	private static Set<Class<? extends Screen>> getAllScreens() {
		Reflections reflections = new Reflections("core.screens");
		return reflections.getSubTypesOf(Screen.class);
	}

	public void renderScreenSelector() {
		ImGui.setNextWindowSize(300, 400);
		ImGui.begin(core.getLanguage("core.screens.ScreenMainMenu"), ImGuiWindowFlags.NoResize);

		ImGui.separator();

		// List all available screens
		for (Class<? extends Screen> screenClass : screenClasses) {
			if (!core.getIgnoredScreens().contains(screenClass)) {
				if (ImGui.button(core.getLanguage(screenClass.getName()))) {
					switchScreen(screenClass);
				}
			}
		}

		ImGui.end();
	}

	private void switchScreen(Class<? extends Screen> screenClass) {
		try {
			// Create a new instance of the screen using its constructor with Core
			Screen newScreen = screenClass.getDeclaredConstructor(Core.class).newInstance(core);
			core.setScreen(newScreen); // Method in Core to switch screens
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Failed to switch to screen: " + screenClass.getName());
		}
	}
}
