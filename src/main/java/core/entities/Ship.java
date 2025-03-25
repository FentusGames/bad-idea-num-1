package core.entities;

import core.Core;
import core.interfaces.Disposable;
import core.interfaces.Initable;
import core.interfaces.Renderable;
import core.texture.Animation;

public class Ship implements Initable, Renderable, Disposable {
	private Core core;

	private Animation animation;

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
			animation.render();
		}
	}

	@Override
	public void init() {
		// Load Animation
		animation = core.getAnimation("graphics_bomber");
	}
}
