package core.helpers;

public class SliderRenderContext {
	public final float delta;
	public final int windowX, windowY, windowWidth, windowHeight;

	public SliderRenderContext(float delta, int windowX, int windowY, int windowWidth, int windowHeight) {
		this.delta = delta;
		this.windowX = windowX;
		this.windowY = windowY;
		this.windowWidth = windowWidth;
		this.windowHeight = windowHeight;
	}
}
