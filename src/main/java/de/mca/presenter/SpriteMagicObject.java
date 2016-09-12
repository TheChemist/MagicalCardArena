package de.mca.presenter;

import de.mca.io.FileManager;
import de.mca.model.MagicCard;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * 
 * @author Maximilian Werling
 *
 */
public class SpriteMagicObject implements Sprite {

	private final MagicCard card;
	private Image image;
	private double positionX;
	private double positionY;
	private final DoubleProperty propertyHeight;
	private final DoubleProperty propertyWidth;

	SpriteMagicObject(MagicCard card) {
		this.card = card;
		positionX = 0;
		positionY = 0;

		image = FileManager.getCardImage(card.getFileName());
		propertyHeight = new SimpleDoubleProperty(image.getHeight());
		propertyWidth = new SimpleDoubleProperty(image.getWidth());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SpriteMagicObject other = (SpriteMagicObject) obj;
		if (this.card == null) {
			if (other.card != null) {
				return false;
			}
		} else if (!this.card.equals(other.card)) {
			return false;
		}
		return true;
	}

	@Override
	public Rectangle2D getBoundary() {
		return new Rectangle2D(getX(), getY(), getWidth(), getHeight());
	}

	@Override
	public double getHeight() {
		return propertyHeight().get();
	}

	public MagicCard getMagicObject() {
		return card;
	}

	@Override
	public double getWidth() {
		return propertyWidth().get();
	}

	@Override
	public double getX() {
		return positionX;
	}

	@Override
	public double getY() {
		return positionY;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.card == null) ? 0 : this.card.hashCode());
		return result;
	}

	@Override
	public void render(GraphicsContext gc) {
		gc.drawImage(getImage(), getX(), getY(), getWidth(), getHeight());
	}

	@Override
	public void setPosition(double x, double y) {
		positionX = x;
		positionY = y;
	}

	@Override
	public String toString() {
		return card.getDisplayName() + " Position: [" + positionX + "," + positionY + "]";
	}

	Image getImage() {
		return image;
	}

	DoubleProperty propertyHeight() {
		return propertyHeight;
	}

	DoubleProperty propertyWidth() {
		return propertyWidth;
	}

	void setImage(Image i) {
		image = i;
		propertyWidth.set(i.getWidth());
		propertyHeight.set(i.getHeight());
	}

}
