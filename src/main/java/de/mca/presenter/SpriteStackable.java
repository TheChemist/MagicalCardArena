package de.mca.presenter;

import de.mca.model.interfaces.IsStackable;
import javafx.scene.canvas.GraphicsContext;

/**
 * 
 * @author Maximilian Werling
 *
 */
class SpriteStackable extends Sprite {

	private final IsStackable stackable;

	SpriteStackable(IsStackable stackable) {
		this.stackable = stackable;
	}

	public IsStackable getMagicObject() {
		return stackable;
	}

	@Override
	public void render(GraphicsContext gc) {
		gc.fillText(getMagicObject().getDisplayName(), getTextX(), getTextY(), getWidth());
		gc.strokeRect(getX(), getY(), getWidth(), getHeight());
	}

	private double getTextX() {
		return getX() + 5;
	}

	private double getTextY() {
		return getY() + getHeight() / 2.0;
	}

}
