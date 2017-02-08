package de.mca.model.interfaces;

import javafx.beans.property.IntegerProperty;

/**
 *
 * @author Maximilian Werling
 *
 */
public interface IsCombatant {

	/**
	 * Verrechnet den Kampfschaden mit den Lebenspunkten.
	 */
	public void applyCombatDamage();

	/**
	 * Teilt dem Ziel eine gewisse Menge Kampfschaden zu.
	 *
	 * @param combatDamage
	 *            die Menge an Kampfschaden, die dem Ziel zugeteilt werden.
	 */
	public void assignCombatDamage(int combatDamage);

	public int getBasePower();

	public int getBaseToughness();

	/**
	 * Liefert den aktuellen Schaden des Angriffsziels.
	 *
	 * @return der aktuelle Schaden des Ziels.
	 */
	public int getDamage();

	public boolean getFlagBlocked();

	public int getPower();

	public int getToughness();

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

	public void setFlagAttacking(boolean b);

	public void setFlagBlocked(boolean b);

	public void setFlagBlocking(boolean b);

	public void setFlagTapped(boolean b);

}
