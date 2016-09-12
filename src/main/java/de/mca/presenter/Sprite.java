package de.mca.presenter;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;

/**
 * 
 * @author Maximilian Werling
 *
 */
public interface Sprite {

	Rectangle2D getBoundary();

	double getHeight();

	double getWidth();

	double getX();

	double getY();

	void render(GraphicsContext gc);

	void setPosition(double x, double y);

}
