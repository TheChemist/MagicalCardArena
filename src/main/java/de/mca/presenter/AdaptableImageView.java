package de.mca.presenter;

import javafx.beans.binding.DoubleExpression;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Hilfsklasse zur schnelleren Erstellung der Icon-Labels.
 * 
 * @author Maximilian Werling
 *
 */
class AdaptableImageView extends ImageView {

	AdaptableImageView(Image image, DoubleExpression heightProperty, DoubleExpression widthProperty) {
		super(image);

		fitHeightProperty().bind(heightProperty);
		fitWidthProperty().bind(widthProperty);
		setPreserveRatio(true);
	}

}
