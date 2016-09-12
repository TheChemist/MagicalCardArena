package de.mca.presenter;

import java.util.List;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

/**
 * 
 * @author Maximilian Werling
 *
 */
final class CanvasZoneStack extends AdaptableCanvas {

	private List<SpriteStackable> spriteList;

	CanvasZoneStack(Pane parent, List<SpriteStackable> spriteList) {
		super(parent);
		this.spriteList = spriteList;
	}

	@Override
	public void draw() {
		GraphicsContext gc = getGraphicsContext2D();

		gc.clearRect(0, 0, getWidth(), getHeight());

		for (int i = 0; i < spriteList.size(); i++) {
			SpriteStackable sprite = spriteList.get(i);

			sprite.propertyHeight().set(50);
			sprite.propertyWidth().bind(widthProperty());

			double positionX = 0;
			double positionY = getHeight() - sprite.getHeight();

			sprite.setPosition(positionX, positionY);

			sprite.render(gc);
		}
	}
}
