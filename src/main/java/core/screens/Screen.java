package core.screens;

import java.util.List;
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
import imgui.flag.ImGuiCond;
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

	public void renderScreenSelector(int windowX, int windowY, int windowWidth, int windowHeight) {
		// Filter screen classes based on getOrder() != -1 and excluding current screen
		List<Class<? extends Screen>> filteredScreens = screenClasses.stream().filter(screenClass -> {
			try {
				Screen screenInstance = screenClass.getDeclaredConstructor(Core.class).newInstance(core);
				return screenInstance.getOrder() != -1 && !screenClass.equals(this.getClass());
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Failed to create screen instance for: " + screenClass.getName());
				return false; // Exclude if instantiation fails
			}
		}).toList();

		int screenCount = filteredScreens.size(); // Store filtered size

		// Button dimensions
		float buttonHeight = 30; // Consistent button height
		float menuWidth = 250;
		float separatorHeight = ImGui.getStyle().getItemSpacingY();

		// Calculate dynamic window height based on content (+ exit button & separator)
		float menuHeight = ImGui.getStyle().getWindowPaddingY() * 2 // Top and bottom padding
				+ (buttonHeight * screenCount) // Buttons for available screens
				+ (screenCount > 0 ? ImGui.getStyle().getItemSpacingY() * (screenCount - 1) : 0) // Space between screen buttons
				+ separatorHeight // Height of the separator before Exit button
				+ buttonHeight // Additional space for Exit button
				+ ImGui.getStyle().getItemSpacingY(); // Space above the Exit button

		// Centering calculation using provided window dimensions
		float posX = windowX + (windowWidth - menuWidth) * 0.5f;
		float posY = windowY + (windowHeight - menuHeight) * 0.5f;

		// Set position and size before creating the window
		ImGui.setNextWindowPos(posX, posY, ImGuiCond.Always);
		ImGui.setNextWindowSize(menuWidth, menuHeight);

		// Set window flags to remove background, title bar, and prevent resizing/moving
		int windowFlags = ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.AlwaysAutoResize;

		ImGui.begin(core.getLanguage("main-menu"), windowFlags);

		// Manually set button width to fit inside the window
		float buttonWidth = menuWidth - ImGui.getStyle().getWindowPaddingX() * 2;

		// List filtered screens
		filteredScreens.stream().sorted((c1, c2) -> {
			try {
				Screen s1 = c1.getDeclaredConstructor(Core.class).newInstance(core);
				Screen s2 = c2.getDeclaredConstructor(Core.class).newInstance(core);
				return Integer.compare(s1.getOrder(), s2.getOrder());
			} catch (Exception e) {
				e.printStackTrace();
				return 0; // Default to no order change in case of failure
			}
		}).forEach(screenClass -> {
			try {
				// Create a temporary instance of the screen to retrieve getMenuName()
				Screen screenInstance = screenClass.getDeclaredConstructor(Core.class).newInstance(core);
				String menuName = screenInstance.getMenuName(); // Get menu name from the instance

				if (ImGui.button(menuName, buttonWidth, buttonHeight)) {
					switchScreen(screenClass);
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Failed to create screen instance for: " + screenClass.getName());
			}
		});

		// Separator before the exit button
		ImGui.separator();

		// Exit Button
		if (ImGui.button(core.getLanguage("exit"), buttonWidth, buttonHeight)) {
			core.exit();
		}

		ImGui.end();
	}

	protected abstract String getMenuName();

	protected abstract int getOrder();

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
