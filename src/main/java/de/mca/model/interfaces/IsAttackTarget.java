package de.mca.model.interfaces;

import de.mca.model.MagicPermanent;
import javafx.beans.property.IntegerProperty;

/**
 *
 * @author Maximilian Werling
 *
 */
public interface IsAttackTarget {

	/**
	 * Verrechnet den zugeteilten Kampfschaden mit den Lebenspunkten des Ziels.
	 */
	public void applyCombatDamage();

	/**
	 * Teilt dem Ziel eine gewisse Menge Kampfschaden zu.
	 *
	 * @param combatDamage
	 *            die Menge an Kampfschaden, die dem Ziel zugeteilt werden.
	 */
	public void assignCombatDamage(int combatDamage);

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
	 * Liefert die Kampfschaden-Eigenschaft.
	 *
	 * @return die Kampfschaden-Eigenschaft.
	 */
	public IntegerProperty propertyCombatDamage();

	/**
	 * Setzt den Kampfschaden Angriffsziel auf 0. Wird zu jedem CLEANUP
	 * aufgerufen.
	 */
	public void resetCombatDamage();

}
