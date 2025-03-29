package core.helpers;

import core.Core;

public class SliderRenderContext {
	private final float delta;
	private final int windowX, windowY, windowWidth, windowHeight;
	private Core core;

	public SliderRenderContext(Core core, float delta, int windowX, int windowY, int windowWidth, int windowHeight) {
		this.core = core;
		this.delta = delta;
		this.windowX = windowX;
		this.windowY = windowY;
		this.windowWidth = windowWidth;
		this.windowHeight = windowHeight;
	}

	public Core getCore() {
		return core;
	}

	public void setCore(Core core) {
		this.core = core;
	}

	public float getDelta() {
		return delta;
	}

	public int getWindowX() {
		return windowX;
	}

	public int getWindowY() {
		return windowY;
	}

	public int getWindowWidth() {
		return windowWidth;
	}

	public int getWindowHeight() {
		return windowHeight;
	}
}