package core.helpers;

import core.Core;

public class SliderRenderContext {
	public final float delta;
	public final int windowX, windowY, windowWidth, windowHeight;
	public Core core;

	public SliderRenderContext(Core core, float delta, int windowX, int windowY, int windowWidth, int windowHeight) {
		this.core = core;
		this.delta = delta;
		this.windowX = windowX;
		this.windowY = windowY;
		this.windowWidth = windowWidth;
		this.windowHeight = windowHeight;
	}
}
