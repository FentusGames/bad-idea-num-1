package core.screens;

import core.Core;
import core.camera.Camera;
import core.interfaces.Disposable;
import core.interfaces.Imguiable;
import core.interfaces.Initable;
import core.interfaces.KeyCallback;
import core.interfaces.MouseButtonCallback;
import core.interfaces.Renderable;
import core.interfaces.ScrollCallback;
import core.interfaces.Updateable;

public abstract class Screen implements Initable, Renderable, Updateable, Imguiable, Disposable, ScrollCallback, MouseButtonCallback, KeyCallback {
	protected Core core;
	protected Camera camera;

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
		camera = new Camera(this.getCore().getWindowPtr());

		core.setScrollCallback(this);
		core.setMouseButtonCallback(this);
		core.setKeyCallback(this);
	}

	@Override
	public abstract void render(float delta);

	@Override
	public abstract void update(float delta);

	@Override
	public abstract void imgui(float delta);

	@Override
	public void dispose() {
		core.setScrollCallback(this);
		core.setMouseButtonCallback(this);
		core.setKeyCallback(this);
	}

	public Camera getCamera() {
		return camera;
	}
}
