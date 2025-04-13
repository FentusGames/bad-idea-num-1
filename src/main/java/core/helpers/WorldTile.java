package core.helpers;

import org.joml.Vector2i;

import core.texture.Texture;

public class WorldTile extends Vector2i {
	private Texture texture;
	private String name;

	public WorldTile(Texture texture, String name) {
		this.texture = texture;
		this.name = name;
	}

	public Texture getTexture() {
		return texture;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}