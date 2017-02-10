package de.mca.presenter;

import java.util.ArrayList;
import java.util.List;

import de.mca.Constants;
import de.mca.model.InputHuman;
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
final class CanvasZoneDefault extends AdaptableCanvas<SpriteMagicCard> {

	private static final double X_OFFSET = 1.1;
	private static final double Y_OFFSET = 10.0;

	private final InputHuman inputHuman;
	private final ZoneType zoneType;
	private final ImageView zoomView;

	CanvasZoneDefault(Pane parent, InputHuman inputHuman, ZoneType zoneType, ImageView zoomView) {
		super(parent);
		this.inputHuman = inputHuman;
		this.zoneType = zoneType;
		this.zoomView = zoomView;
	}

	@Override
	public void draw() {
		GraphicsContext gc = getGraphicsContext2D();

		gc.clearRect(0, 0, getWidth(), getHeight());

		for (int i = 0; i < getListSprites().size(); i++) {
			Sprite sprite = getListSprites().get(i);
			double positionX = 0;
			double positionY = 0;

			DoubleBinding heightBoundSpriteHeight = heightProperty().subtract(Y_OFFSET);
			DoubleBinding heightBoundSpriteWidth = heightBoundSpriteHeight.multiply(Constants.CARD_RATIO);

			DoubleBinding widthBoundSpriteWidth = widthProperty().divide(getListSprites().size()).divide(X_OFFSET);
			DoubleBinding widthBoundSpriteHeight = widthBoundSpriteWidth.divide(Constants.CARD_RATIO);

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

	private void initializeMouseClicked() {
		addEventHandler(MouseEvent.MOUSE_CLICKED, (mouseEvent) -> {
			int zCoordinate = -1;
			List<MagicCard> listClicked = new ArrayList<>();
			for (SpriteMagicCard sprite : getListSprites()) {
				if (sprite.getBoundary().contains(new Point2D(mouseEvent.getX(), mouseEvent.getY()))) {
					listClicked.add(sprite.getMagicObject());
					zCoordinate++;
				}
			}
			inputHuman.input(listClicked.get(zCoordinate), zoneType);
		});
	}

	private void initializeMouseMoved() {
		addEventHandler(MouseEvent.MOUSE_MOVED, (mouseEvent) -> {
			SpriteMagicCard mouseOverSprite = null;
			for (SpriteMagicCard sprite : getListSprites()) {
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

	@Override
	void setListSprites(List<SpriteMagicCard> listSprites) {
		super.setListSprites(listSprites);

		initializeMouseClicked();
		initializeMouseMoved();
	}
}