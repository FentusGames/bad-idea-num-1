package core.entities;

import org.joml.Vector3f;

import core.Core;
import core.interfaces.Disposable;
import core.interfaces.Initable;
import core.interfaces.Renderable;
import core.texture.Animation;

public class Ship implements Initable, Renderable, Disposable {
	private Core core;

	private Animation animation;
	private Vector3f animationPosition = new Vector3f(0, 0, 0);

	public Ship(Core core) {
		this.core = core;
	}

	@Override
	public void dispose() {
		if (animation != null) {
			animation = null;
		}
	}

	@Override
	public void render(float delta) {
		if (animation != null) {
			animation.update(delta);
			animation.render(animationPosition.x, animationPosition.y, 1F / 10F, 1 / 10F); // One 10th of a GL unit.
		}
	}

	@Override
	public void init() {
		// Load Animation
		animation = core.getAnimation("graphics_bomber");
	}
}
