package de.mca.model.interfaces;

import de.mca.model.MagicCard;
import de.mca.model.MagicPermanent;
import de.mca.model.MagicSpell;
import de.mca.model.RuleEnforcer;
import de.mca.presenter.MatchPresenter;

/**
 *
 * @author Maximilian Werling
 *
 */
public interface IsInput {

	// TODO HIGH Wieder request ermöglich, aber über Input.
	public MatchPresenter getMatchPresenter();

	public IsPlayer getPlayer();

	public RuleEnforcer getRuleEnforcer();

	/**
	 * Informiert das Spiel über eine Spielerhandlung: Der Spieler hat ein
	 * Permanent aktiviert.
	 *
	 * @param magicPermanent
	 *            das aktivierte Permanent.
	 */
	public default void inputActivatePermanent(MagicPermanent magicPermanent) {
		if (magicPermanent.getFlagIsInteractable()) {
			// Führe Aktion aus.

			getRuleEnforcer().actionBeginActivatePermanent(getPlayer(), magicPermanent);
		} else {
			// Kann Aktion nicht ausführen.
			/**
			 * TODO HIGH throws new InteractionException()? Kann über ähnliches
			 * System wie die requests erledigt werden.
			 */
		}
	}

	/**
	 * Informiert das Spiel über eine Spielerhandlung: Der Spieler beschwört
	 * einen Zauberspruch.
	 *
	 * @param magicCard
	 *            der Zauberspruch.
	 */
	public default void inputCastSpell(MagicCard magicCard) {
		if (magicCard.getFlagIsInteractable()) {
			// Führe Aktion aus.

			getRuleEnforcer().actionBeginCastSpell(getPlayer(), magicCard);
		} else {
			// Kann Aktion nicht ausführen.
			/**
			 * TODO HIGH throws new InteractionException()? Kann über ähnliches
			 * System wie die requests erledigt werden.
			 */
		}
	}

	/**
	 * Informiert das Spiel über eine Spielerhandlung: Der Spieler gibt auf.
	 */
	public default void inputConcede() {
		getRuleEnforcer().actionConcede(getPlayer());
	}

	/**
	 * Informiert das Spiel über eine Spielerhandlung: Der Spieler deklariert
	 * einen Angreifer.
	 *
	 * @param attacker
	 *            der Angreifer.
	 */
	public default void inputDeclareAttacker(MagicPermanent magicPermanent) {
		if (magicPermanent.getFlagIsInteractable()) {
			// Führe Aktion aus.

			getRuleEnforcer().actionDeclareAttacker(getPlayer(), magicPermanent);
		} else {
			// Kann Aktion nicht ausführen.
			/**
			 * TODO HIGH throws new InteractionException()? Kann über ähnliches
			 * System wie die requests erledigt werden.
			 */
		}
	}

	/**
	 * Informiert das Spiel über eine Spielerhandlung: Der Spieler deklariert
	 * einen Blocker.
	 *
	 * @param attackerIndex
	 *            der Index des Angreifer, der geblockt werden soll.
	 * @param magicPermanent
	 *            der Blocker.
	 */
	public default void inputDeclareBlocker(MagicPermanent magicPermanent) {
		if (magicPermanent.getFlagIsInteractable()) {
			// Führe Aktion aus.

			getRuleEnforcer().actionDeclareBlocker(getPlayer(), magicPermanent);
		} else {
			// Kann Aktion nicht ausführen.
			/**
			 * TODO HIGH throws new InteractionException()? Kann über ähnliches
			 * System wie die requests erledigt werden.
			 */
		}
	}

	/**
	 * Informiert das Spiel über eine Spielerhandlung: Der Spieler wirft eine
	 * Karte ab.
	 *
	 * @param magicCard
	 *            die abgeworfene Karte.
	 */
	public default void inputDiscard(MagicCard magicCard) {
		getRuleEnforcer().actionDiscard(getPlayer(), magicCard);
	}

	/**
	 * Informiert das Spiel über eine Spielerhandlung: Der Spieler hat möchte
	 * keine weiteren Angreifer deklarieren.
	 */
	public default void inputEndDeclareAttackers() {
		getRuleEnforcer().actionEndDeclareAttackers(getPlayer());
	}

	/**
	 * Informiert das Spiel über eine Spielerhandlung: Der Spieler möchte keine
	 * weiteren Blocker deklarieren.
	 */
	public default void inputEndDeclareBlockers() {
		getRuleEnforcer().actionEndDeclareBlockers(getPlayer());
	}

	/**
	 * Informiert das Spiel über eine Spielerhandlung: Der Spieler gibt die
	 * Priorität ab.
	 */
	public default void inputPassPriority() {
		getRuleEnforcer().actionPassPriority(getPlayer());
	}

	/**
	 * Informiert das Spiel über eine Spielerhandlung: Der Spieler möchte ein
	 * Land spielen.
	 *
	 * @param magicCard
	 *            das Land.
	 */
	public default void inputPlayLand(MagicCard magicCard) {
		if (magicCard.getFlagIsInteractable()) {
			getRuleEnforcer().actionPlayLand(getPlayer(), magicCard);
		} else {
			// Kann Aktion nicht ausführen.
			/**
			 * TODO HIGH throws new InteractionException()? Kann über ähnliches
			 * System wie die requests erledigt werden.
			 */
		}
	}

	public default void inputSelectCostMap(MagicSpell magicSpell) {
		// TODO MID Fertig implementieren.
	}

	public void setMatchPresenter(MatchPresenter matchPresenter);

	public void setPlayer(IsPlayer player);

}
