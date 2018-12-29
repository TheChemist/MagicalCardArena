package de.mca.model.interfaces;

import java.util.List;

import de.mca.model.Effect;

/**
 * Taggerinterface zur Markierung von Objekten, die auf dem Stack verarbeitet
 * werden.
 *
 * @author Maximilian Werling
 *
 */
public interface IsStackable {

	/**
	 * Liefert einen kurzen Anzeigenamen.
	 *
	 * @return einen kurzen Anzeigenamen.
	 */
	public String getDisplayName();

	/**
	 * Liefert die ID. Handelt es sich um eine Fähigkeit, wird -1 zurück gegeben.
	 *
	 * @return die ID.
	 */
	public int getId();

	/**
	 * Liefert den Spieler, der das Stackable kontrolliert.
	 *
	 * @return den Spieler, der das Stackable kontrolliert.
	 */
	public IsPlayer getPlayerControlling();

	/**
	 * Prüft, ob es sich bei dem Stackable umd einen Permanent Spell handelt.
	 *
	 * @return true, wenn es sich um eine Permanent Spell handelt.
	 */
	public boolean isPermanentSpell();

	/**
	 * Liefert eine Liste der unterschiedlichen Darstellungen der Kosten.
	 *
	 * @return die CostMap.
	 */
	public List<IsManaMap> getListCostMaps();

	public List<Effect> getListEffects();

	/**
	 * Setzt den Spieler, der das Stackable kontrolliert.
	 *
	 * @param playerType
	 *            Der kontrollierende Spieler.
	 */
	public void setPlayerControlling(IsPlayer playerType);

}
