package de.mca.presenter;

import de.mca.io.FileManager;
import de.mca.model.MagicPermanent;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.transform.Rotate;

class SpriteMagicPermanent extends Sprite {

	private final Image image;
	private final MagicPermanent magicPermanent;

	SpriteMagicPermanent(MagicPermanent magicPermanent) {
		super();

		this.magicPermanent = magicPermanent;

		image = FileManager.getCardImage(magicPermanent.getFileName());

		propertyWidth().set(getImage().getWidth());
		propertyHeight().set(getImage().getHeight());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SpriteMagicPermanent other = (SpriteMagicPermanent) obj;
		if (this.magicPermanent == null) {
			if (other.magicPermanent != null)
				return false;
		} else if (!this.magicPermanent.equals(other.magicPermanent))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.magicPermanent == null) ? 0 : this.magicPermanent.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return new StringBuilder(magicPermanent.getDisplayName()).append(" ").append(super.toString()).toString();
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

	Image getImage() {
		return image;
	}

	MagicPermanent getMagicPermanent() {
		return magicPermanent;
	}

	@Override
	void render(GraphicsContext gc) {
		if (getMagicPermanent().getFlagIsTapped()) {
			gc.save();
			rotate(gc, 90, getX() + getWidth() / 2, getY() + getHeight() / 2);
			gc.drawImage(getImage(), getX(), getY(), getWidth(), getHeight());
			gc.restore();
		} else {
			gc.drawImage(getImage(), getX(), getY(), getWidth(), getHeight());
		}
	}

}
