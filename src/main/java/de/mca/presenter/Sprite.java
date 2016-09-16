package de.mca.presenter;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;

/**
 * 
 * @author Maximilian Werling
 *
 */
abstract class Sprite {

	private double positionX;
	private double positionY;
	private final DoubleProperty propertyHeight;
	private final DoubleProperty propertyWidth;

	Sprite() {
		positionX = 0;
		positionY = 0;
		propertyHeight = new SimpleDoubleProperty(0);
		propertyWidth = new SimpleDoubleProperty(0);
	}

	public Rectangle2D getBoundary() {
		return new Rectangle2D(getX(), getY(), getWidth(), getHeight());
	}

	public double getHeight() {
		return propertyHeight.get();
	}

	public double getWidth() {
		return propertyWidth.get();
	}

	public double getX() {
		return positionX;
	}

	public double getY() {
		return positionY;
	}

	public void setPosition(double x, double y) {
		positionX = x;
		positionY = y;
	}

	@Override
	public String toString() {
		return "Position: [" + positionX + "," + positionY + "]";
	}

	DoubleProperty propertyHeight() {
		return propertyHeight;
	}

	DoubleProperty propertyWidth() {
		return propertyWidth;
	}

	abstract void render(GraphicsContext gc);

}
