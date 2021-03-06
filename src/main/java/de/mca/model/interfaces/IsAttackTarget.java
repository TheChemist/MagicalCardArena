package de.mca.model.interfaces;

import de.mca.model.MagicPermanent;
import javafx.beans.property.IntegerProperty;

/**
 *
 * @author Maximilian Werling
 *
 */
public interface IsAttackTarget {

	public void addLife(int life);

	/**
	 * Prüft, ob das Objekt für eine angreifende Kreatur ein legales Ziel
	 * darstellt.
	 *
	 * @param attacker
	 *            die angreifende Kreatur
	 * @return true, wenn das Objekt ein legales Ziel darstellt.
	 */
	public boolean chechIsValidAttackTarget(MagicPermanent attacker);

	/**
	 * Liefert den aktuellen Schaden des Angriffsziels.
	 *
	 * @return der aktuelle Schaden des Ziels.
	 */
	public int getDamage();

	public int getLife();

	public IntegerProperty getPropertyLife();

	/**
	 * Liefert die Schaden-Eigenschaft.
	 *
	 * @return die Kampfschaden-Eigenschaft.
	 */
	public IntegerProperty propertyDamage();

	/**
	 * Setzt den Schaden Angriffsziel auf 0. Wird zu jedem CLEANUP aufgerufen.
	 */
	public void resetDamage();

	/**
	 * Setzt den Schaden des Angriffziels.
	 *
	 * @param damage
	 *            der Schaden.
	 */
	public void setDamage(int damage);

	public void setLife(int life);

	public void substractLife(int life);

}
