package de.mca.presenter;

import java.util.ArrayList;
import java.util.List;

import de.mca.Constants;
import de.mca.InputHuman;
import de.mca.model.MagicCard;
import de.mca.model.enums.ZoneType;
import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

/**
 * 
 * @author Maximilian Werling
 *
 */
final class CanvasZoneDefault extends AdaptableCanvas {

	private static final double X_OFFSET = 1.1;
	private static final double Y_OFFSET = 10.0;

	private final List<SpriteMagicCard> spriteList;

	CanvasZoneDefault(Pane parent, InputHuman input, List<SpriteMagicCard> spriteList, ZoneType zoneType,
			ImageView zoomView) {
		super(parent);
		this.spriteList = spriteList;

		initializeMouseClicked(input, spriteList, zoneType);
		initializeMouseMoved(spriteList, zoomView);
	}

	@Override
	public void draw() {
		GraphicsContext gc = getGraphicsContext2D();

		gc.clearRect(0, 0, getWidth(), getHeight());

		for (int i = 0; i < spriteList.size(); i++) {
			SpriteMagicCard sprite = spriteList.get(i);
			double positionX = 0;
			double positionY = 0;

			DoubleBinding heightBoundSpriteHeight = heightProperty().subtract(Y_OFFSET);
			DoubleBinding heightBoundSpriteWidth = heightBoundSpriteHeight.multiply(Constants.CARD_RATIO);

			DoubleBinding widthBoundSpriteWidth = widthProperty().divide(spriteList.size()).divide(X_OFFSET);
			DoubleBinding widthBoundSpriteHeight = widthBoundSpriteWidth.divide(Constants.CARD_RATIO)
					.subtract(Y_OFFSET);

			if (heightBoundSpriteWidth.multiply(X_OFFSET).greaterThan(widthBoundSpriteWidth).get()) {
				sprite.propertyHeight().bind(widthBoundSpriteHeight);
				sprite.propertyWidth().bind(widthBoundSpriteWidth);
			} else {
				sprite.propertyHeight().bind(heightBoundSpriteHeight);
				sprite.propertyWidth().bind(heightBoundSpriteWidth);
			}

			positionX = sprite.getWidth() * i * X_OFFSET;
			positionY = Y_OFFSET / 2.0;

			sprite.setPosition(positionX, positionY);

			sprite.render(gc);
		}
	}

	private void initializeMouseClicked(InputHuman input, List<SpriteMagicCard> spriteList, ZoneType zoneType) {
		addEventHandler(MouseEvent.MOUSE_CLICKED, (mouseEvent) -> {
			int zCoordinate = -1;
			List<MagicCard> listClicked = new ArrayList<>();
			for (SpriteMagicCard sprite : spriteList) {
				if (sprite.getBoundary().contains(new Point2D(mouseEvent.getX(), mouseEvent.getY()))) {
					listClicked.add(sprite.getMagicObject());
					zCoordinate++;
				}
			}
			input.input(listClicked.get(zCoordinate), zoneType);
		});
	}

	private void initializeMouseMoved(List<SpriteMagicCard> spriteList, ImageView zoomView) {
		addEventHandler(MouseEvent.MOUSE_MOVED, (mouseEvent) -> {
			SpriteMagicCard mouseOverSprite = null;
			for (SpriteMagicCard sprite : spriteList) {
				if (sprite.getBoundary().contains(new Point2D(mouseEvent.getX(), mouseEvent.getY()))) {
					mouseOverSprite = sprite;
					break;
				}
			}
			if (mouseOverSprite != null) {
				zoomView.setImage(mouseOverSprite.getImage());
				zoomView.setPreserveRatio(true);
			} else {
				zoomView.setImage(null);
			}
		});
	}
}