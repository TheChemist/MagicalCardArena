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
 * @author Maxiilian Werling
 *
 */
public class Sprite {

	private final MagicCard card;
	private DoubleProperty height;
	private Image image;
	private double positionX;
	private double positionY;
	private DoubleProperty width;

	public Sprite(MagicCard card) {
		this.card = card;
		positionX = 0;
		positionY = 0;

		image = FileManager.getCardImage(card.getFileName());
		height = new SimpleDoubleProperty(image.getHeight());
		width = new SimpleDoubleProperty(image.getWidth());

	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Sprite other = (Sprite) obj;
		if (this.card == null) {
			if (other.card != null)
				return false;
		} else if (!this.card.equals(other.card))
			return false;
		return true;
	}

	public Rectangle2D getBoundary() {
		return new Rectangle2D(positionX, positionY, getWidth(), getHeight());
	}

	public MagicCard getCard() {
		return card;
	}

	public double getHeight() {
		return propertyHeight().doubleValue();
	}

	public Image getImage() {
		return image;
	}

	public double getWidth() {
		return propertyWidth().doubleValue();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.card == null) ? 0 : this.card.hashCode());
		return result;
	}

	public DoubleProperty propertyHeight() {
		return height;
	}

	public DoubleProperty propertyWidth() {
		return width;
	}

	public void render(GraphicsContext gc) {
		gc.drawImage(image, positionX, positionY, width.get(), height.get());
	}

	public void setImage(Image i) {
		image = i;
		width.set(i.getWidth());
		height.set(i.getHeight());
	}

	public void setPosition(double x, double y) {
		positionX = x;
		positionY = y;
	}

	@Override
	public String toString() {
		return card.getDisplayName() + " Position: [" + positionX + "," + positionY + "]";
	}

}
