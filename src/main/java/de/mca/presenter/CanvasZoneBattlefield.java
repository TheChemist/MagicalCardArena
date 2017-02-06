package de.mca.presenter;

import java.util.ArrayList;
import java.util.List;

import de.mca.Constants;
import de.mca.model.InputHuman;
import de.mca.model.MagicPermanent;
import de.mca.model.enums.PlayerType;
import de.mca.model.enums.ZoneType;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

class CanvasZoneBattlefield extends AdaptableCanvas {

	private final static double HEIGHT_DEFAULT = 75;
	private final static double X_OFFSET = 1.1;
	private final static double Y_OFFSET = 10.0;

	private int indexComputerRowOne;
	private int indexComputerRowTwo;
	private int indexHumanRowOne;
	private int indexHumanRowTwo;
	private final List<SpriteMagicPermanent> spriteList;

	CanvasZoneBattlefield(Pane parent, InputHuman input, List<SpriteMagicPermanent> spriteList, ZoneType zoneType,
			ImageView zoomView) {
		super(parent);
		this.spriteList = spriteList;
		indexComputerRowOne = 0;
		indexComputerRowTwo = 0;
		indexHumanRowOne = 0;
		indexHumanRowTwo = 0;

		initializeMouseClicked(input, spriteList, zoneType);
		initializeMouseMoved(spriteList, zoomView);
	}

	@Override
	public void draw() {
		GraphicsContext gc = getGraphicsContext2D();

		// Zeichenfläche leeren
		gc.clearRect(0, 0, getWidth(), getHeight());

		// Karten zeichnen
		spriteList.forEach(sprite -> drawCard(sprite, gc));

		// Hilfsindizes zurücksetzen
		indexComputerRowOne = 0;
		indexComputerRowTwo = 0;
		indexHumanRowOne = 0;
		indexHumanRowTwo = 0;
	}

	private void drawCard(SpriteMagicPermanent sprite, GraphicsContext gc) {
		MagicPermanent magicPermanent = sprite.getMagicPermanent();
		PlayerType playerControlling = magicPermanent.getPlayerControlling();

		double positionX = 0;
		double positionY = 0;

		DoubleProperty propertyHeight = new SimpleDoubleProperty(HEIGHT_DEFAULT);
		DoubleBinding propertyWidth = propertyHeight.multiply(Constants.CARD_RATIO);

		sprite.propertyHeight().bind(propertyHeight);
		sprite.propertyWidth().bind(propertyWidth);

		if (playerControlling.equals(PlayerType.HUMAN)) {
			// Wird unten gezeichnet.

			if (magicPermanent.isCreature()) {
				// Wird in Reihe zwei gezeichnet.

				positionX = sprite.getWidth() * indexHumanRowTwo * X_OFFSET;
				positionY = getHeight() / 2.0 + Y_OFFSET / 2;

				indexHumanRowTwo++;
			} else {
				// Wird in Reihe eins gezeichnet.

				positionX = sprite.getWidth() * indexHumanRowOne * X_OFFSET;
				positionY = getHeight() / 4.0 * 3.0 + Y_OFFSET / 2;

				indexHumanRowOne++;
			}

		} else if (playerControlling.equals(PlayerType.COMPUTER)) {
			// Wird oben gezeichnet.

			if (magicPermanent.isCreature()) {
				// Wird in Reihe zwei gezeichnet.

				positionX = sprite.getWidth() * indexComputerRowTwo * X_OFFSET;
				positionY = getHeight() / 2.0 - Y_OFFSET / 2 - sprite.getHeight();

				indexComputerRowTwo++;
			} else {
				// Wird in Reihe eins gezeichnet.

				positionX = sprite.getWidth() * indexComputerRowOne * X_OFFSET;
				positionY = getHeight() / 4.0 - Y_OFFSET / 2 - sprite.getHeight();

				indexComputerRowOne++;
			}

		}

		sprite.setPosition(positionX, positionY);
		sprite.render(gc);
	}

	private void initializeMouseClicked(InputHuman input, List<SpriteMagicPermanent> spriteList, ZoneType zoneType) {
		addEventHandler(MouseEvent.MOUSE_CLICKED, (mouseEvent) -> {
			int zCoordinate = -1;
			List<MagicPermanent> listClicked = new ArrayList<>();
			for (SpriteMagicPermanent sprite : spriteList) {
				if (sprite.getBoundary().contains(new Point2D(mouseEvent.getX(), mouseEvent.getY()))) {
					listClicked.add(sprite.getMagicPermanent());
					zCoordinate++;
				}
			}
			input.input(listClicked.get(zCoordinate), zoneType);
		});
	}

	private void initializeMouseMoved(List<SpriteMagicPermanent> spriteList, ImageView zoomView) {
		addEventHandler(MouseEvent.MOUSE_MOVED, (mouseEvent) -> {
			SpriteMagicPermanent mouseOverSprite = null;
			for (SpriteMagicPermanent sprite : spriteList) {
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
