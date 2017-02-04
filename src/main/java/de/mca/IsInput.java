package de.mca;

import de.mca.model.MagicCard;
import de.mca.model.MagicPermanent;
import de.mca.model.interfaces.IsPlayer;

/**
 *
 * @author Maximilian Werling
 *
 */
public interface IsInput {

	// public void requestInput(boolean flagNeedPlayerInput);

	public IsPlayer getPlayer();

	/**
	 * Informiert das Spiel über eine Spielerhandlung: Der Spieler hat ein
	 * Permanent aktiviert.
	 *
	 * @param magicPermanent
	 *            das aktivierte Permanent.
	 */
	public default void inputActivatedPermanent(MagicPermanent magicPermanent) {
		getPlayer().fireActivatePermanent(magicPermanent);
	}

	/**
	 * Informiert das Spiel über eine Spielerhandlung: Der Spieler beschwört
	 * einen Zauberspruch.
	 *
	 * @param magicCard
	 *            der Zauberspruch.
	 */
	public default void inputCastSpell(MagicCard magicCard) {
		getPlayer().fireCastSpell(magicCard);
	}

	/**
	 * Informiert das Spiel über eine Spielerhandlung: Der Spieler gibt auf.
	 */
	public default void inputConcede() {
		getPlayer().fireConcede();
	}

	/**
	 * Informiert das Spiel über eine Spielerhandlung: Der Spieler deklariert
	 * einen Angreifer.
	 *
	 * @param attacker
	 *            der Angreifer.
	 */
	public default void inputDeclareAttacker(MagicPermanent attacker) {
		getPlayer().fireDeclareAttacker(attacker);
	}

	/**
	 * Informiert das Spiel über eine Spielerhandlung: Der Spieler deklariert
	 * einen Blocker.
	 *
	 * @param attackerIndex
	 *            der Index des Angreifer, der geblockt werden soll.
	 * @param blocker
	 *            der Blocker.
	 */
	public default void inputDeclareBlocker(MagicPermanent blocker) {
		getPlayer().fireDeclareBlocker(blocker);
	}

	/**
	 * Informiert das Spiel über eine Spielerhandlung: Der Spieler wirft eine
	 * Karte ab.
	 *
	 * @param magicCard
	 *            die abgeworfene Karte.
	 */
	public default void inputDiscard(MagicCard magicCard) {
		getPlayer().fireDiscard(magicCard);
	}

	/**
	 * Informiert das Spiel über eine Spielerhandlung: Der Spieler hat möchte
	 * keine weiteren Angreifer deklarieren.
	 */
	public default void inputEndDeclareAttackers() {
		getPlayer().fireEndDeclareAttackers();
	}

	/**
	 * Informiert das Spiel über eine Spielerhandlung: Der Spieler möchte keine
	 * weiteren Blocker deklarieren.
	 */
	public default void inputEndDeclareBlockers() {
		getPlayer().fireEndDeclareBlockers();
	}

	/**
	 * Informiert das Spiel über eine Spielerhandlung: Der Spieler gibt die
	 * Priorität ab.
	 */
	public default void inputPassPriority() {
		getPlayer().firePassPriority();
	}

	/**
	 * Informiert das Spiel über eine Spielerhandlung: Der Spieler möchte ein
	 * Land spielen.
	 *
	 * @param magicCard
	 *            das Land.
	 */
	public default void inputPlayLand(MagicCard magicCard) {
		getPlayer().firePlayLand(magicCard);
	}

	public void setPlayer(IsPlayer player);

}
