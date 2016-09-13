package de.mca.presenter;

import de.mca.io.FileManager;
import de.mca.model.MagicCard;
import de.mca.model.MagicPermanent;
import de.mca.model.interfaces.IsObject;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.transform.Rotate;

/**
 * 
 * @author Maximilian Werling
 *
 */
class SpriteMagicObject implements Sprite {

	private final IsObject magicObject;
	private Image image;
	private double positionX;
	private double positionY;
	private final DoubleProperty propertyHeight;
	private final DoubleProperty propertyWidth;

	SpriteMagicObject(IsObject magicObject) {
		this.magicObject = magicObject;
		propertyHeight = new SimpleDoubleProperty(0);
		positionX = 0;
		positionY = 0;
		propertyWidth = new SimpleDoubleProperty(0);

		if (magicObject instanceof MagicPermanent) {
			setImage(FileManager.getCardImage(((MagicPermanent) magicObject).getFileName()));
		} else {
			// TODO: Über neuen Konstruktor lösen. Schauen wie aufgerufen wird.
			setImage(FileManager.getCardImage(((MagicCard) magicObject).getFileName()));
		}
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
		if (this.magicObject == null) {
			if (other.magicObject != null) {
				return false;
			}
		} else if (!this.magicObject.equals(other.magicObject)) {
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

	public IsObject getMagicObject() {
		return magicObject;
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
		result = prime * result + ((this.magicObject == null) ? 0 : this.magicObject.hashCode());
		return result;
	}

	/**
	 * Sets the transform for the GraphicsContext to rotate around a pivot
	 * point.
	 *
	 * @param gc
	 *            the graphics context the transform to applied to.
	 * @param angle
	 *            the angle of rotation.
	 * @param px
	 *            the x pivot co-ordinate for the rotation (in canvas
	 *            co-ordinates).
	 * @param py
	 *            the y pivot co-ordinate for the rotation (in canvas
	 *            co-ordinates).
	 */
	private void rotate(GraphicsContext gc, double angle, double px, double py) {
		Rotate r = new Rotate(angle, px, py);
		gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
	}

	@Override
	public void render(GraphicsContext gc) {
		// TODO: Besser gestalten, keine instanceof prüfung im Loop
		if (getMagicObject() instanceof MagicPermanent) {
			if (((MagicPermanent) getMagicObject()).isFlagTapped()) {
				gc.save();
				rotate(gc, 90, getX() + getWidth() / 2, getY() + getHeight() / 2);
				gc.drawImage(getImage(), getX(), getY());
				gc.restore();
			} else {
				gc.drawImage(getImage(), getX(), getY(), getWidth(), getHeight());
			}
		} else {
			gc.drawImage(getImage(), getX(), getY(), getWidth(), getHeight());
		}
	}

	@Override
	public void setPosition(double x, double y) {
		positionX = x;
		positionY = y;
	}

	@Override
	public String toString() {
		return magicObject.getDisplayName() + " Position: [" + positionX + "," + positionY + "]";
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
		propertyWidth().set(i.getWidth());
		propertyHeight().set(i.getHeight());
	}

}
