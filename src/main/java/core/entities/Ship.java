package core.entities;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import core.Core;
import core.interfaces.Initable;
import core.interfaces.Renderable;
import core.interfaces.Updateable;
import core.texture.Animation;

public class Ship implements Initable, Renderable, Updateable {
	private Core core;

	private Animation animation;

	public Ship(Core core) {
		this.core = core;
	}

	@Override
	public void render(float delta) {
		if (animation != null) {
			animation.render(delta);
		}
	}

	@Override
	public void update(float delta) {
		if (animation != null) {
			animation.update(delta);
		}
	}

	@Override
	public void init() {
		animation = core.getAnimation("graphics_bomber");
	}

	public void setFrameTime(float frameTime) {
		animation.setFrameTime(frameTime);
	}

	public Vector3f getPos() {
		return animation.getPos();
	}

	public void setPos(Vector3f pos) {
		animation.setPos(pos);
	}

	public Quaternionf getRotation() {
		return animation.getRotation();
	}

	public void setRotation(Quaternionf rotation) {
		animation.setRotation(rotation);
	}

	public void setColorMask(int r, int g, int b, int a) {
		animation.setR(r);
		animation.setG(g);
		animation.setB(b);
		animation.setA(a);
	}
}
