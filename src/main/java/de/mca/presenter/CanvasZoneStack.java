package de.mca.presenter;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

/**
 *
 * @author Maximilian Werling
 *
 */
final class CanvasZoneStack extends AdaptableCanvas<SpriteStackable> {

	CanvasZoneStack(Pane parent) {
		super(parent);
	}

	@Override
	public void draw() {
		GraphicsContext gc = getGraphicsContext2D();

		gc.clearRect(0, 0, getWidth(), getHeight());

		for (int i = 0; i < getListSprites().size(); i++) {
			Sprite sprite = getListSprites().get(i);

			sprite.propertyHeight().set(50);
			sprite.propertyWidth().bind(widthProperty());

			double positionX = 0;
			double positionY = getHeight() - sprite.getHeight() * (i + 1);

			sprite.setPosition(positionX, positionY);

			sprite.render(gc);
		}
	}
}
