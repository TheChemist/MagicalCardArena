package de.mca.presenter;

import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;

abstract class AdaptableCanvas extends Canvas {

	AdaptableCanvas(Pane parent) {
		widthProperty().bind(parent.widthProperty());
		heightProperty().bind(parent.heightProperty());
	}

	public abstract void draw();

	@Override
	public boolean isResizable() {
		return true;
	}

}
