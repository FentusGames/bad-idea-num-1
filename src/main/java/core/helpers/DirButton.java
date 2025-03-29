package core.helpers;

public class DirButton {
	private String label;
	private int dx, dy;
	private float x, y;
	private float alignX, alignY;
	private String textureBase;

	public DirButton(String label, int dx, int dy, float x, float y, float alignX, float alignY, String textureBase) {
		this.label = label;
		this.dx = dx;
		this.dy = dy;
		this.x = x;
		this.y = y;
		this.alignX = alignX;
		this.alignY = alignY;
		this.textureBase = textureBase;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getDx() {
		return dx;
	}

	public void setDx(int dx) {
		this.dx = dx;
	}

	public int getDy() {
		return dy;
	}

	public void setDy(int dy) {
		this.dy = dy;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getAlignX() {
		return alignX;
	}

	public void setAlignX(float alignX) {
		this.alignX = alignX;
	}

	public float getAlignY() {
		return alignY;
	}

	public void setAlignY(float alignY) {
		this.alignY = alignY;
	}

	public String getTextureBase() {
		return textureBase;
	}

	public void setTextureBase(String textureBase) {
		this.textureBase = textureBase;
	}
}