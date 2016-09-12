package de.mca.presenter;

import java.util.ArrayList;
import java.util.List;

import de.mca.Constants;
import de.mca.InputHuman;
import de.mca.model.MagicCard;
import de.mca.model.enums.ZoneType;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
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

	private final List<SpriteMagicObject> spriteList;

	CanvasZoneDefault(Pane parent, InputHuman input, List<SpriteMagicObject> spriteList, ZoneType zoneType,
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
			SpriteMagicObject sprite = spriteList.get(i);
			double positionX = 0;
			double positionY = 0;

			DoubleProperty heightBoundSpriteHeight = heightProperty();
			DoubleBinding heightBoundSpriteWidth = heightBoundSpriteHeight.multiply(Constants.CARD_RATIO);

			DoubleBinding widthBoundSpriteWidth = widthProperty().divide(spriteList.size());
			DoubleBinding widthBoundSpriteHeight = widthBoundSpriteWidth.divide(Constants.CARD_RATIO);

			if (heightBoundSpriteWidth.greaterThan(widthBoundSpriteWidth).get()) {
				sprite.propertyHeight().bind(widthBoundSpriteHeight);
				sprite.propertyWidth().bind(widthBoundSpriteWidth);
			} else {
				sprite.propertyHeight().bind(heightBoundSpriteHeight);
				sprite.propertyWidth().bind(heightBoundSpriteWidth);
			}

			positionX = sprite.getWidth() * i;
			positionY = 0;

			sprite.setPosition(positionX, positionY);

			sprite.render(gc);
		}
	}

	private void initializeMouseClicked(InputHuman input, List<SpriteMagicObject> spriteList, ZoneType zoneType) {
		addEventHandler(MouseEvent.MOUSE_CLICKED, (mouseEvent) -> {
			int zCoordinate = -1;
			List<MagicCard> listClicked = new ArrayList<>();
			for (SpriteMagicObject sprite : spriteList) {
				if (sprite.getBoundary().contains(new Point2D(mouseEvent.getX(), mouseEvent.getY()))) {
					listClicked.add(sprite.getMagicObject());
					zCoordinate++;
				}
			}
			input.input(listClicked.get(zCoordinate), zoneType);
		});
	}

	private void initializeMouseMoved(List<SpriteMagicObject> spriteList, ImageView zoomView) {
		addEventHandler(MouseEvent.MOUSE_MOVED, (mouseEvent) -> {
			SpriteMagicObject mouseOverSprite = null;
			for (SpriteMagicObject sprite : spriteList) {
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