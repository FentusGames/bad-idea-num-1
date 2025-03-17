package core;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import org.jooq.DSLContext;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.opengl.GL;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import core.interfaces.KeyCallback;
import core.interfaces.MouseButtonCallback;
import core.interfaces.ScrollCallback;
import core.screens.Screen;
import imgui.ImFont;
import imgui.ImFontAtlas;
import imgui.ImFontConfig;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;

public class Core {
	// Settings
	private int width;
	private int height;
	private String title;
	private int fontMinSize;
	private int fontMaxSize;
	private int fontStepAmt;

	private int[] windowX = new int[1];
	private int[] windowY = new int[1];
	private int[] windowWidth = new int[1];
	private int[] windowHeight = new int[1];

	private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
	private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

	private String glslVersion = null;
	private long windowPtr;

	// Current Screen
	private Screen screen;

	// Fonts
	private HashMap<String, HashMap<Integer, ImFont>> fonts = new HashMap<String, HashMap<Integer, ImFont>>();

	// GLFW callbacks
	public ScrollCallback scrollCallback;
	public MouseButtonCallback mouseButtonCallback;
	public KeyCallback keyCallback;

	private DSLContext db;
	private Map<String, String> language;

	private boolean debug = false;

	public void init() {
		initWindow();
		initImGui();
		imGuiGlfw.init(windowPtr, true);
		imGuiGl3.init(glslVersion);
	}

	public void dispose() {
		imGuiGl3.dispose();
		imGuiGlfw.dispose();
		ImGui.destroyContext();
		Callbacks.glfwFreeCallbacks(windowPtr);
		glfwDestroyWindow(windowPtr);
		glfwTerminate();
	}

	private void initWindow() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!glfwInit()) {
			System.exit(-1);
		}

		glslVersion = "#version 130";
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);

		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		windowPtr = glfwCreateWindow(width, height, title, NULL, NULL);
		glfwCenterWindow(windowPtr, width, height);

		if (windowPtr == NULL) {
			System.exit(-1);
		}

		glfwMakeContextCurrent(windowPtr);
		glfwSwapInterval(1);
		glfwShowWindow(windowPtr);

		GL.createCapabilities();

		// TODO: glfwSetIcon(windowPtr, "assets/icons/favicon.png");

		glfwSetScrollCallback(windowPtr, (window, xoffset, yoffset) -> {
			if (scrollCallback != null) {
				scrollCallback.scroll(window, xoffset, yoffset);
			}
		});

		glfwSetMouseButtonCallback(windowPtr, (window, button, action, mods) -> {
			if (mouseButtonCallback != null) {
				mouseButtonCallback.mouseButton(window, button, action, mods);
			}
		});

		glfwSetKeyCallback(windowPtr, (window, key, scancode, action, mods) -> {
			if (keyCallback != null) {
				keyCallback.key(window, key, scancode, action, mods);
			}
		});
	}

	private void initImGui() {
		ImGui.createContext();
		ImGuiIO io = ImGui.getIO();
		io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);

		imguiLoadFonts(io);

		// @TODO: Pull data from config file. Make converter for Style Editor. Use the style editor to allow custom styles.

		// Borders
		ImGui.getStyle().setWindowBorderSize(0.0f);
		ImGui.getStyle().setFrameBorderSize(0.0f);
		ImGui.getStyle().setPopupBorderSize(0.0f);

		// Rounding
		float rounding = 3F;

		ImGui.getStyle().setWindowRounding(rounding);
		ImGui.getStyle().setChildRounding(rounding);
		ImGui.getStyle().setFrameRounding(rounding);
		ImGui.getStyle().setPopupRounding(rounding);
		ImGui.getStyle().setScrollbarRounding(rounding);
		ImGui.getStyle().setGrabRounding(rounding);
		ImGui.getStyle().setTabRounding(rounding);

		// Slider Deadzone
		ImGui.getStyle().setLogSliderDeadzone(0.0f);

		// Colors
		float[][] colors = ImGui.getStyle().getColors();

		// @formatter:off
		colors[ImGuiCol.Text]                   = new float[] {1.00f, 1.00f, 1.00f, 1.00f};
		colors[ImGuiCol.TextDisabled]           = new float[] {0.49f, 0.49f, 0.49f, 1.00f};
		colors[ImGuiCol.WindowBg]               = new float[] {0.07f, 0.07f, 0.07f, 1.00f};
		colors[ImGuiCol.ChildBg]                = new float[] {0.00f, 0.00f, 0.00f, 0.00f};
		colors[ImGuiCol.PopupBg]                = new float[] {0.07f, 0.07f, 0.07f, 1.00f};
		colors[ImGuiCol.Border]                 = new float[] {0.40f, 0.40f, 0.40f, 1.00f};
		colors[ImGuiCol.BorderShadow]           = new float[] {0.00f, 0.00f, 0.00f, 0.00f};
		colors[ImGuiCol.FrameBg]                = new float[] {0.20f, 0.40f, 0.20f, 1.00f};
		colors[ImGuiCol.FrameBgHovered]         = new float[] {0.30f, 0.60f, 0.30f, 1.00f};
		colors[ImGuiCol.FrameBgActive]          = new float[] {0.40f, 0.80f, 0.40f, 0.80f};
		colors[ImGuiCol.TitleBg]                = new float[] {0.08f, 0.15f, 0.08f, 1.00f};
		colors[ImGuiCol.TitleBgActive]          = new float[] {0.10f, 0.20f, 0.10f, 1.00f};
		colors[ImGuiCol.TitleBgCollapsed]       = new float[] {0.00f, 0.10f, 0.00f, 0.60f};
		colors[ImGuiCol.MenuBarBg]              = new float[] {0.15f, 0.30f, 0.15f, 1.00f};
		colors[ImGuiCol.ScrollbarBg]            = new float[] {0.08f, 0.08f, 0.08f, 1.00f};
		colors[ImGuiCol.ScrollbarGrab]          = new float[] {0.15f, 0.40f, 0.15f, 1.00f};
		colors[ImGuiCol.ScrollbarGrabHovered]   = new float[] {0.25f, 0.50f, 0.25f, 1.00f};
		colors[ImGuiCol.ScrollbarGrabActive]    = new float[] {0.30f, 0.60f, 0.30f, 1.00f};
		colors[ImGuiCol.CheckMark]              = new float[] {0.20f, 0.80f, 0.20f, 0.80f};
		colors[ImGuiCol.SliderGrab]             = new float[] {0.30f, 0.80f, 0.30f, 0.80f};
		colors[ImGuiCol.SliderGrabActive]       = new float[] {0.40f, 1.00f, 0.40f, 0.80f};
		colors[ImGuiCol.Button]                 = new float[] {0.20f, 0.60f, 0.20f, 0.40f};
		colors[ImGuiCol.ButtonHovered]          = new float[] {0.30f, 0.80f, 0.30f, 0.60f};
		colors[ImGuiCol.ButtonActive]           = new float[] {0.40f, 1.00f, 0.40f, 0.80f};
		colors[ImGuiCol.Header]                 = new float[] {0.20f, 0.60f, 0.20f, 0.40f};
		colors[ImGuiCol.HeaderHovered]          = new float[] {0.30f, 0.80f, 0.30f, 0.60f};
		colors[ImGuiCol.HeaderActive]           = new float[] {0.40f, 1.00f, 0.40f, 0.80f};
		colors[ImGuiCol.Separator]              = new float[] {0.20f, 0.60f, 0.20f, 0.40f};
		colors[ImGuiCol.SeparatorHovered]       = new float[] {0.30f, 0.80f, 0.30f, 0.60f};
		colors[ImGuiCol.SeparatorActive]        = new float[] {0.40f, 1.00f, 0.40f, 0.80f};
		colors[ImGuiCol.ResizeGrip]             = new float[] {0.20f, 0.60f, 0.20f, 0.40f};
		colors[ImGuiCol.ResizeGripHovered]      = new float[] {0.30f, 0.80f, 0.30f, 0.60f};
		colors[ImGuiCol.ResizeGripActive]       = new float[] {0.40f, 1.00f, 0.40f, 0.80f};
		colors[ImGuiCol.Tab]                    = new float[] {0.20f, 0.60f, 0.20f, 0.80f};
		colors[ImGuiCol.TabHovered]             = new float[] {0.30f, 0.80f, 0.30f, 0.80f};
		colors[ImGuiCol.TabActive]              = new float[] {0.40f, 1.00f, 0.40f, 0.80f};
		colors[ImGuiCol.TabUnfocused]           = new float[] {0.15f, 0.30f, 0.15f, 1.00f};
		colors[ImGuiCol.TabUnfocusedActive]     = new float[] {0.20f, 0.40f, 0.20f, 1.00f};
		colors[ImGuiCol.DockingPreview]         = new float[] {0.40f, 1.00f, 0.40f, 0.80f};
		colors[ImGuiCol.DockingEmptyBg]         = new float[] {0.08f, 0.08f, 0.08f, 1.00f};
		colors[ImGuiCol.PlotLines]              = new float[] {0.50f, 1.00f, 0.50f, 1.00f};
		colors[ImGuiCol.PlotLinesHovered]       = new float[] {0.60f, 1.00f, 0.60f, 1.00f};
		colors[ImGuiCol.PlotHistogram]          = new float[] {0.70f, 1.00f, 0.30f, 1.00f};
		colors[ImGuiCol.PlotHistogramHovered]   = new float[] {0.80f, 1.00f, 0.40f, 1.00f};
		colors[ImGuiCol.TableHeaderBg]          = new float[] {0.15f, 0.30f, 0.15f, 1.00f};
		colors[ImGuiCol.TableBorderStrong]      = new float[] {0.20f, 0.40f, 0.20f, 1.00f};
		colors[ImGuiCol.TableBorderLight]       = new float[] {0.15f, 0.35f, 0.15f, 1.00f};
		colors[ImGuiCol.TableRowBg]             = new float[] {0.00f, 0.00f, 0.00f, 0.00f};
		colors[ImGuiCol.TableRowBgAlt]          = new float[] {0.10f, 0.30f, 0.10f, 1.00f};
		colors[ImGuiCol.TextSelectedBg]         = new float[] {0.26f, 0.80f, 0.26f, 0.35f};
		colors[ImGuiCol.DragDropTarget]         = new float[] {0.80f, 1.00f, 0.40f, 0.90f};
		colors[ImGuiCol.NavHighlight]           = new float[] {0.26f, 0.80f, 0.26f, 1.00f};
		// @formatter:on

		ImGui.getStyle().setColors(colors);
	}

	public void start() {
		long lastTime = System.nanoTime();
		double nsPerTick = 1000000000.0 / 60.0;
		float delta = 0.0F;
		int frames = 0;
		int updates = 0;
		long timer = System.currentTimeMillis();

		init();

		while (!glfwWindowShouldClose(windowPtr)) {
			glClearColor(0.1F, 0.1F, 0.1F, 1.0F);
			glClear(GL_COLOR_BUFFER_BIT);

			glfwGetWindowPos(windowPtr, windowX, windowY);
			glfwGetWindowSize(windowPtr, windowWidth, windowHeight);

			long now = System.nanoTime();
			delta += (now - lastTime) / nsPerTick;
			lastTime = now;

			while (delta >= 1.0F) {
				screen.update(delta);
				updates++;
				delta--;
			}

			screen.render(delta);

			imGuiGlfw.newFrame();
			ImGui.newFrame();

			ImGui.pushFont(getFont("default", 16));

			screen.imgui(delta, windowX[0], windowY[0], windowWidth[0], windowHeight[0]);

			ImGui.popFont();

			ImGui.render();
			imGuiGl3.renderDrawData(ImGui.getDrawData());

			frames++;

			if (System.currentTimeMillis() - timer > 1000) {
				glfwSetWindowTitle(windowPtr, String.format("%s FPS: %s UPS: %s", title, frames, updates));
				frames = 0;
				updates = 0;
				timer += 1000;
			}

			if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
				final long backupWindowPtr = glfwGetCurrentContext();
				ImGui.updatePlatformWindows();
				ImGui.renderPlatformWindowsDefault();
				glfwMakeContextCurrent(backupWindowPtr);
			}

			glfwSwapBuffers(windowPtr);
			glfwPollEvents();
		}

		screen.dispose();

		dispose();
	}

	public static void glfwCenterWindow(long windowPtr, int width, int height) {
		// Get the dimensions of the screen
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();

		// Set the position of the window to the center of the screen
		int windowX = (dimension.width - width) / 2;
		int windowY = (dimension.height - height) / 2;

		glfwSetWindowPos(windowPtr, windowX, windowY);
	}

	public Core setScreen(Screen screen) {
		if (this.screen != null) {
			this.screen.dispose();
		}

		this.screen = screen;

		screen.init();

		return screen.getCore();
	}

	public static void glfwSetIcon(long windowPtr, String filePath) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);
			IntBuffer comp = stack.mallocInt(1);

			ByteBuffer iconBuffer = STBImage.stbi_load(filePath, w, h, comp, 4);
			if (iconBuffer == null) {
				throw new RuntimeException("Failed to load icon image file: " + STBImage.stbi_failure_reason());
			}

			// Set the icon image as the GLFW window's icon
			GLFWImage.Buffer icons = GLFWImage.malloc(1);
			icons.position(0).width(w.get(0)).height(h.get(0)).pixels(iconBuffer);
			glfwSetWindowIcon(windowPtr, icons);

			// Clean up
			STBImage.stbi_image_free(iconBuffer);
			icons.free();
		}
	}

	public long getWindowPtr() {
		return windowPtr;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ImFont getFont(String name, int size) {
		return fonts.get(name).get(size);
	}

	public void setFontMinSize(int fontMinSize) {
		this.fontMinSize = fontMinSize;
	}

	public void setFontMaxSize(int fontMaxSize) {
		this.fontMaxSize = fontMaxSize;
	}

	public void setFontStepAmt(int fontStepAmt) {
		this.fontStepAmt = fontStepAmt;
	}

	private void imguiLoadFonts(ImGuiIO io) {
		ImFontAtlas fontAtlas = io.getFonts();

		ImFontConfig fontConfig = new ImFontConfig();
		fontConfig.setGlyphRanges(fontAtlas.getGlyphRangesDefault());

		File folder = new File("assets/fonts");

		// Get all the files in the folder
		// Iterate through each file in the folder
		for (File file : folder.listFiles()) {
			if (file.isFile() && file.getName().toLowerCase().endsWith(".ttf")) {
				String fileName = file.getName();

				int dotIndex = fileName.lastIndexOf(".");
				if (dotIndex > 0) {
					HashMap<Integer, ImFont> sizes = fonts.getOrDefault(fileName.substring(0, dotIndex), new HashMap<>());

					for (int size = fontMinSize; size <= fontMaxSize; size += fontStepAmt) {
						sizes.put(size, fontAtlas.addFontFromFileTTF(file.getPath(), size, fontConfig));
					}

					fonts.put(fileName.substring(0, dotIndex), sizes);
				}
			}
		}
	}

	public void exit() {
		glfwSetWindowShouldClose(windowPtr, true);
	}

	public ScrollCallback getScrollCallback() {
		return scrollCallback;
	}

	public void setScrollCallback(ScrollCallback scrollCallback) {
		this.scrollCallback = scrollCallback;
	}

	public MouseButtonCallback getMouseButtonCallback() {
		return mouseButtonCallback;
	}

	public void setMouseButtonCallback(MouseButtonCallback mouseButtonCallback) {
		this.mouseButtonCallback = mouseButtonCallback;
	}

	public KeyCallback getKeyCallback() {
		return keyCallback;
	}

	public void setKeyCallback(KeyCallback keyCallback) {
		this.keyCallback = keyCallback;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public boolean getDebug() {
		return debug;
	}

	public void setLanguage(Map<String, String> language) {
		this.language = language;
	}

	public String getLanguage(String key) {
		if (language == null) {
			System.err.println("language can't be null.");
			exit();

			System.exit(-1); // Stops LWJGL from crashing.
		}

		if (!language.containsKey(key)) {
			System.err.println(String.format("Key \"%s\" missing.", key));
			exit();

			System.exit(-1); // Stops LWJGL from crashing.
		}
		
		return language.get(key);
	}

	public void setDB(DSLContext db) {
		this.db = db;
	}
	
	public DSLContext getDB() {
		return db;
	}
}
