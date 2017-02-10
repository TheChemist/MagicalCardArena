package de.mca.presenter;

import java.util.List;

import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;

abstract class AdaptableCanvas<T> extends Canvas {

	private List<? extends T> listSprites;

	AdaptableCanvas(Pane parent) {
		widthProperty().bind(parent.widthProperty());
		heightProperty().bind(parent.heightProperty());
	}

	@Override
	public boolean isResizable() {
		return true;
	}

	abstract void draw();

	List<? extends T> getListSprites() {
		return listSprites;
	}

	void setListSprites(List<T> listSprites) {
		this.listSprites = listSprites;
	}

}
