package de.mca.presenter;

import de.mca.io.FileManager;
import de.mca.model.MagicCard;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 *
 * @author Maximilian Werling
 *
 */
class SpriteMagicCard extends Sprite {

	private final Image image;
	private final MagicCard magicCard;

	SpriteMagicCard(MagicCard magicCard) {
		super();

		this.magicCard = magicCard;

		image = FileManager.getCardImage(magicCard.getFileName());

		propertyWidth().set(getImage().getWidth());
		propertyHeight().set(getImage().getHeight());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}

		SpriteMagicCard other = (SpriteMagicCard) obj;
		if (this.magicCard == null) {
			if (other.magicCard != null) {
				return false;
			}
		} else if (!this.magicCard.equals(other.magicCard)) {
			return false;
		}
		return true;
	}

	public MagicCard getMagicObject() {
		return magicCard;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.magicCard == null) ? 0 : this.magicCard.hashCode());
		return result;
	}

	@Override
	public void render(GraphicsContext gc) {
		gc.drawImage(getImage(), getX(), getY(), getWidth(), getHeight());

		if (getMagicObject().getFlagIsInteractable()) {
			// Zeichne grünen Rand

			gc.setStroke(Color.GREEN);
			gc.setLineWidth(5.0);
			gc.strokeRect(getX(), getY(), getWidth(), getHeight());
		}
	}

	@Override
	public String toString() {
		return new StringBuilder(magicCard.getDisplayName()).append(" ").append(super.toString()).toString();
	}

	Image getImage() {
		return image;
	}

}
