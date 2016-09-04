package de.mca.model.interfaces;

import de.mca.model.MagicPermanent;

/**
 *
 * @author Maximilian Werling
 *
 */
public interface IsAttackTarget {

	/**
	 * Diese Methode wird aufgerufen, wenn das Objekt von einer Kreatur
	 * angegriffen wird.
	 *
	 * @param attacker
	 *            die angreifende Kreatur.
	 */
	public void attackedBy(MagicPermanent attacker);

	/**
	 * Prüft, ob das Objekt für eine angreifende Kreatur ein legales Ziel
	 * darstellt.
	 *
	 * @param attacker
	 *            die angreifende Kreatur
	 * @return true, wenn das Objekt ein legales Ziel darstellt.
	 */
	public boolean chechIsValidAttackTarget(MagicPermanent attacker);

}
