package core.helpers;

public class DirButton {
	String label;
	int dx, dy;
	float x, y;
	float alignX, alignY;
	String textureBase;

	DirButton(String label, int dx, int dy, float x, float y, float alignX, float alignY, String textureBase) {
		this.label = label;
		this.dx = dx;
		this.dy = dy;
		this.x = x;
		this.y = y;
		this.alignX = alignX;
		this.alignY = alignY;
		this.textureBase = textureBase;
	}
}