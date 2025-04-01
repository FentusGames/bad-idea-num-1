package core.screens;

import core.Core;
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
	public void init(int windowX, int windowY, int windowWidth, int windowHeight) {
		core.setScrollCallback(this);
		core.setMouseButtonCallback(this);
		core.setKeyCallback(this);
	}

	@Override
	public abstract void render(float delta, int windowX, int windowY, int windowWidth, int windowHeight);

	@Override
	public abstract void update(float delta, int windowX, int windowY, int windowWidth, int windowHeight);

	@Override
	public abstract void imgui(float delta, int windowX, int windowY, int windowWidth, int windowHeight);

	@Override
	public void dispose() {
		core.setScrollCallback(this);
		core.setMouseButtonCallback(this);
		core.setKeyCallback(this);
	}
}
