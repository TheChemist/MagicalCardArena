package de.mca.model.interfaces;

import de.mca.model.MagicCard;
import de.mca.model.MagicPermanent;
import de.mca.model.MagicSpell;
import de.mca.model.Match;
import de.mca.model.RuleEnforcer;
import de.mca.presenter.MatchPresenter;

/**
 *
 * @author Maximilian Werling
 *
 */
public interface IsInput {

	public Match getMatch();

	public MatchPresenter getMatchPresenter();

	public IsPlayer getPlayer();

	public RuleEnforcer getRuleEnforcer();

	/**
	 * Informiert das Spiel über eine Spielerhandlung: Der Spieler hat ein Permanent
	 * aktiviert.
	 *
	 * @param magicPermanent das aktivierte Permanent.
	 */
	public default void inputActivatePermanent(MagicPermanent magicPermanent) {
		if (magicPermanent.getFlagIsInteractable()) {
			// Führe Aktion aus.

			getPlayer().setFlagNeedInput(false, "inputActivatePermanent()");
			getRuleEnforcer().i_activatePermanentStart(getPlayer(), magicPermanent);
			getRuleEnforcer().gui_disableProgressButton();
		} else {
			System.out.println("Nope");
			// Kann Aktion nicht ausführen.
			/**
			 * TODO HIGH throws new InteractionException()? Kann über ähnliches System wie
			 * die requests erledigt werden.
			 */
		}
	}

	/**
	 * Informiert das Spiel über eine Spielerhandlung: Der Spieler beschwört einen
	 * Zauberspruch.
	 *
	 * @param magicCard der Zauberspruch.
	 */
	public default void inputCastSpell(MagicCard magicCard) {
		if (magicCard.getFlagIsInteractable()) {
			// Führe Aktion aus.

			getPlayer().setFlagNeedInput(false, "inputCastSpell()");
			getRuleEnforcer().i_castSpellStart(getPlayer(), magicCard);
			getRuleEnforcer().gui_disableProgressButton();
		} else {
			System.out.println("Nope");
			// Kann Aktion nicht ausführen.
			/**
			 * TODO HIGH throws new InteractionException()? Kann über ähnliches System wie
			 * die requests erledigt werden.
			 */
		}
	}

	/**
	 * Informiert das Spiel über eine Spielerhandlung: Der Spieler gibt auf.
	 */
	public default void inputConcede() {
		getPlayer().setFlagNeedInput(false, "inputConcede()");
		getRuleEnforcer().i_concede(getPlayer());
		getRuleEnforcer().gui_disableProgressButton();
	}

	/**
	 * Informiert das Spiel über eine Spielerhandlung: Der Spieler deklariert einen
	 * Angreifer.
	 *
	 * @param attacker der Angreifer.
	 */
	public default void inputDeclareAttacker(MagicPermanent magicPermanent) {
		if (magicPermanent.getFlagIsInteractable()) {
			// Führe Aktion aus.

			getPlayer().setFlagNeedInput(false, "inputDeclareAttacker()");
			getRuleEnforcer().i_declareAttacker(getPlayer(), magicPermanent);
			getRuleEnforcer().gui_disableProgressButton();
		} else {
			System.out.println("Nope");
			// Kann Aktion nicht ausführen.
			/**
			 * TODO HIGH throws new InteractionException()? Kann über ähnliches System wie
			 * die requests erledigt werden.
			 */
		}
	}

	/**
	 * Informiert das Spiel über eine Spielerhandlung: Der Spieler deklariert einen
	 * Blocker.
	 *
	 * @param attackerIndex  der Index des Angreifer, der geblockt werden soll.
	 * @param magicPermanent der Blocker.
	 */
	public default void inputDeclareBlocker(MagicPermanent magicPermanent) {
		if (magicPermanent.getFlagIsInteractable()) {
			// Führe Aktion aus.

			getPlayer().setFlagNeedInput(false, "inputDeclareBlocker()");
			getRuleEnforcer().i_declareBlocker(getPlayer(), magicPermanent);
			getRuleEnforcer().gui_disableProgressButton();
		} else {
			System.out.println("Nope");
			// Kann Aktion nicht ausführen.
			/**
			 * TODO HIGH throws new InteractionException()? Kann über ähnliches System wie
			 * die requests erledigt werden.
			 */
		}
	}

	/**
	 * Informiert das Spiel über eine Spielerhandlung: Der Spieler möchte ein
	 * Blockziel deklarieren.
	 *
	 * @param blockTarget das Blockziel.
	 */
	public default void inputDeclareBlockTarget(MagicPermanent blockTarget) {
		if (blockTarget.getFlagIsInteractable()) {
			// Führe Aktion aus.

			getPlayer().setFlagNeedInput(false, "inputDeclareBlockTarget()");
			getRuleEnforcer().i_declareBlockTarget(getPlayer(), blockTarget);
			getRuleEnforcer().gui_disableProgressButton();
		} else {
			System.out.println("Nope");
			// Kann Aktion nicht ausführen.
			/**
			 * TODO HIGH throws new InteractionException()? Kann über ähnliches System wie
			 * die requests erledigt werden.
			 */
		}
	}

	/**
	 * Informiert das Spiel über eine Spielerhandlung: Der Spieler wirft eine
	 * bestimmte Karte ab.
	 *
	 * @param magicCard Die abzuwerfende Karte.
	 */
	public default void inputDiscard(MagicCard magicCard) {
		getPlayer().setFlagNeedInput(false, "inputDiscard()");
		getRuleEnforcer().i_discard(getPlayer(), magicCard);
		getRuleEnforcer().gui_disableProgressButton();
	}

	/**
	 * Informiert das Spiel über eine Spielerhandlung: Der Spieler wirft N zufällige
	 * Karten ab.
	 *
	 * @param howMany Anzahl abzuwerfender Karten.
	 */
	public default void inputDiscardRandom(int howMany) {
		getPlayer().setFlagNeedInput(false, "inputDiscardRandom()");
		getRuleEnforcer().i_discardRandom(getPlayer(), howMany);
		getRuleEnforcer().gui_disableProgressButton();
	}

	/**
	 * Informiert das Spiel über eine Spielerhandlung: Der Spieler hat möchte keine
	 * weiteren Angreifer deklarieren.
	 */
	public default void inputEndDeclareAttackers() {
		getPlayer().setFlagNeedInput(false, "inputEndDeclareAttackers()");
		getRuleEnforcer().i_declareAttackersStop(getPlayer());
		getRuleEnforcer().gui_disableProgressButton();
	}

	/**
	 * Informiert das Spiel über eine Spielerhandlung: Der Spieler möchte keine
	 * weiteren Blocker deklarieren.
	 */
	public default void inputEndDeclareBlockers() {
		getPlayer().setFlagNeedInput(false, "inputEndDeclareBlockers()");
		getRuleEnforcer().i_declareBlockersStop(getPlayer());
		getRuleEnforcer().gui_disableProgressButton();
	}

	/**
	 * Informiert das Spiel über eine Spielerhandlung: Der Spieler gibt die
	 * Priorität ab.
	 */
	public default void inputPassPriority() {
		getPlayer().setFlagNeedInput(false, "inputPassPriority()");
		getRuleEnforcer().i_passPriority(getPlayer());
		getRuleEnforcer().gui_disableProgressButton();
	}

	/**
	 * Informiert das Spiel über eine Spielerhandlung: Der Spieler möchte ein Land
	 * spielen.
	 *
	 * @param magicCard das Land.
	 */
	public default void inputPlayLand(MagicCard magicCard) {
		if (magicCard.getFlagIsInteractable()) {
			// Führe Aktion aus.

			getPlayer().setFlagNeedInput(false, "inputPlayLand()");
			getRuleEnforcer().i_playLand(getPlayer(), magicCard);
			getRuleEnforcer().gui_disableProgressButton();
		} else {
			System.out.println("Nope");
			// Kann Aktion nicht ausführen.
			/**
			 * TODO HIGH throws new InteractionException()? Kann über ähnliches System wie
			 * die requests erledigt werden.
			 */
		}
	}

	public default void inputSelectCostMap(MagicSpell magicSpell) {
		// TODO MID Fertig implementieren.
	}

}
