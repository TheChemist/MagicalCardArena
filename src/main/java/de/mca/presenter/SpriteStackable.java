package de.mca.presenter;

import de.mca.model.interfaces.IsStackable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;

/**
 * 
 * @author Maximilian Werling
 *
 */
public class SpriteStackable implements Sprite {

	private double positionX;
	private double positionY;
	private final DoubleProperty propertyHeight;
	private final DoubleProperty propertyWidth;
	private final IsStackable stackable;

	SpriteStackable(IsStackable stackable) {
		this.stackable = stackable;
		positionX = 0;
		positionY = 0;
		propertyHeight = new SimpleDoubleProperty(0);
		propertyWidth = new SimpleDoubleProperty(0);
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
		SpriteStackable other = (SpriteStackable) obj;
		if (this.stackable == null) {
			if (other.stackable != null) {
				return false;
			}
		} else if (!this.stackable.equals(other.stackable)) {
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
		return propertyHeight.get();
	}

	public IsStackable getMagicObject() {
		return stackable;
	}

	@Override
	public double getWidth() {
		return propertyWidth.get();
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
		result = prime * result + ((this.stackable == null) ? 0 : this.stackable.hashCode());
		return result;
	}

	@Override
	public void render(GraphicsContext gc) {
		gc.fillText(getMagicObject().getDisplayName(), getTextX(), getTextY(), getWidth());
		gc.strokeRect(getX(), getY(), getWidth(), getHeight());
	}

	@Override
	public void setPosition(double x, double y) {
		positionX = x;
		positionY = y;
	}

	@Override
	public String toString() {
		return stackable.getDisplayName() + " Position: [" + positionX + "," + positionY + "]";
	}

	private double getTextX() {
		return getX() + 5;
	}

	private double getTextY() {
		return getY() + getHeight() / 2.0;
	}

	DoubleProperty propertyHeight() {
		return propertyHeight;
	}

	DoubleProperty propertyWidth() {
		return propertyWidth;
	}

}
