package de.mca.model;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.ListIterator;

import com.google.common.collect.ImmutableList;

import de.mca.model.enums.PhaseType;
import de.mca.model.enums.PlayerType;
import de.mca.model.enums.StepType;
import de.mca.model.enums.TurnBasedActionType;
import de.mca.model.interfaces.IsPlayer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

/**
 *
 * @author Maximilian Werling
 *
 */
public class Turn {

	/**
	 * Speichert den Iterator, der sie Phasen durchläuft.
	 */
	private final ListIterator<Phase> iteratorPhases;
	/**
	 * Speichert die verschiedenen Spielphasen.
	 */
	private final List<Phase> listPhases;
	/**
	 * Speichert eine Referenz auf das Match.
	 */
	private Match match;
	/**
	 * Speichert den computergesteuerten Spieler.
	 */
	private final IsPlayer playerComputer;
	/**
	 * Speichert den menschlichen Spieler.
	 */
	private final IsPlayer playerHuman;
	/**
	 * Zeigt an, ob die Runde gerade läuft.
	 */
	private final BooleanProperty propertyFlagTurnRunning;
	/**
	 * Zeigt an, ob die nächste Runde übersprungen wird.
	 */
	private final BooleanProperty propertyFlagTurnSkipped;
	/**
	 * Speichert die Rundennummer.
	 */
	private final IntegerProperty propertyTurnNumber;

	Turn(Match match, IsPlayer playerComputer, IsPlayer playerHuman) {
		this.match = match;
		this.playerComputer = playerComputer;
		this.playerHuman = playerHuman;

		listPhases = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
		propertyFlagTurnRunning = new SimpleBooleanProperty(false);
		propertyFlagTurnSkipped = new SimpleBooleanProperty(false);
		propertyTurnNumber = new SimpleIntegerProperty(0);

		final Step untap = new Step(match.getRuleEnforcer(), StepType.UNTAP_STEP,
				EnumSet.of(TurnBasedActionType.UNTAP));
		final Step upkeep = new Step(match.getRuleEnforcer(), StepType.UPKEEP_STEP,
				EnumSet.noneOf(TurnBasedActionType.class));
		final Step draw = new Step(match.getRuleEnforcer(), StepType.DRAW_STEP, EnumSet.of(TurnBasedActionType.DRAW));
		final Phase beginning = new Phase(match, PhaseType.BEGINNING_PHASE, ImmutableList.of(untap, upkeep, draw));

		final Phase precombatMain = new Phase(match, PhaseType.PRECOMBAT_MAIN_PHASE, ImmutableList
				.of(new Step(match.getRuleEnforcer(), StepType.NONE, EnumSet.noneOf(TurnBasedActionType.class))));

		final Step beginningOfCombat = new Step(match.getRuleEnforcer(), StepType.BEGINNING_OF_COMBAT_STEP,
				EnumSet.of(TurnBasedActionType.BEGINNING_OF_COMBAT_STEP));
		final Step declareAttackers = new Step(match.getRuleEnforcer(), StepType.DECLARE_ATTACKERS,
				EnumSet.of(TurnBasedActionType.DECLARE_ATTACKER));
		final Step declareBlockers = new Step(match.getRuleEnforcer(), StepType.DECLARE_BLOCKERS,
				EnumSet.of(TurnBasedActionType.DECLARE_BLOCKER));
		final Step combatDamage = new Step(match.getRuleEnforcer(), StepType.COMBAT_DAMAGE_STEP,
				EnumSet.of(TurnBasedActionType.DECLARE_DAMAGE_ASSIGNMENT_ORDER_ATTACKER));
		final Step endOfCombat = new Step(match.getRuleEnforcer(), StepType.END_OF_COMBAT,
				EnumSet.of(TurnBasedActionType.END_OF_COMBAT));
		final Phase combat = new Phase(match, PhaseType.COMBAT_PHASE,
				ImmutableList.of(beginningOfCombat, declareAttackers, declareBlockers, combatDamage, endOfCombat));

		final Phase postcombatMain = new Phase(match, PhaseType.POSTCOMBAT_MAIN_PHASE, ImmutableList
				.of(new Step(match.getRuleEnforcer(), StepType.NONE, EnumSet.noneOf(TurnBasedActionType.class))));

		final Step end = new Step(match.getRuleEnforcer(), StepType.END_STEP,
				EnumSet.noneOf(TurnBasedActionType.class));
		final Step cleanup = new Step(match.getRuleEnforcer(), StepType.CLEANUP_STEP,
				EnumSet.of(TurnBasedActionType.DISCARD, TurnBasedActionType.CLEANUP));
		final Phase ending = new Phase(match, PhaseType.ENDING_PHASE, ImmutableList.of(end, cleanup));

		listPhases.add(beginning);
		listPhases.add(precombatMain);
		listPhases.add(combat);
		listPhases.add(postcombatMain);
		listPhases.add(ending);

		iteratorPhases = listPhases.listIterator();
		match.propertyCurrentPhase().set(beginning);
	}

	Turn(Turn turn, Match match) {
		this.match = match;
		playerComputer = turn.getPlayerComputer();
		playerHuman = turn.getPlayerHuman();

		listPhases = turn.getListPhases();
		propertyFlagTurnRunning = turn.propertyFlagTurnRunning();
		propertyFlagTurnSkipped = turn.propertyFlagTurnSkipped();
		propertyTurnNumber = turn.propertyTurnNumber();

		iteratorPhases = listPhases.listIterator();
		match.propertyCurrentPhase().set(listPhases.get(0));
	}

	private List<Phase> getListPhases() {
		return listPhases;
	}

	private IsPlayer getPlayerComputer() {
		return playerComputer;
	}

	private IsPlayer getPlayerHuman() {
		return playerHuman;
	}

	private int getTurnNumber() {
		return propertyTurnNumber().get();
	}

	private void setFlagTurnRunning(boolean flagRunning) {
		propertyFlagTurnRunning.set(flagRunning);
	}

	boolean getFlagTurnRunning() {
		return propertyFlagTurnRunning().get();
	}

	boolean getFlagTurnSkipped() {
		return propertyFlagTurnSkipped().get();
	}

	/**
	 * Liefert einen Spieler anhand seines Spielertyps.
	 *
	 * @param playerType
	 *            ein Spielertyp.
	 * @return des Spieler mit dem übergebenen Spielertyp.
	 */
	IsPlayer getPlayer(PlayerType playerType) {
		if (playerType.equals(PlayerType.NONE)) {
			// Sollte nicht vorkommen

			throw new IllegalArgumentException("Kein Spieler mit Typ " + playerType.toString());
		}
		return playerType.equals(PlayerType.HUMAN) ? getPlayerHuman() : getPlayerComputer();
	}

	/**
	 * Liefert den gegnerischen Spieler zu einen gegeben Spieler.
	 *
	 * @param player
	 *            der Spieler, dessen Gegener zurückgegeben werden soll.
	 * @return Gegener des übergeben Spielers.
	 */
	IsPlayer getPlayerOpponent(PlayerType playerType) {
		if (playerType.equals(PlayerType.NONE)) {
			// Sollte nicht vorkommen

			throw new IllegalArgumentException("Kein Spieler mit Typ " + playerType.toString());
		}
		return playerType.equals(PlayerType.HUMAN) ? getPlayerComputer() : getPlayerHuman();
	}

	boolean hasNextPhase() {
		return iteratorPhases.hasNext();
	}

	/**
	 * Überprüft, ob ein Spieler der aktive Spieler ist.
	 *
	 * @param player
	 *            Der zu prüfende Spieler.
	 * @return true, wenn der Spieler den Spielerstatus "aktiv" oder sein Gegner
	 *         den Spielerstatus "nichtaktiv" besitzt.
	 */
	boolean isPlayerActive(IsPlayer player) {
		return (player.isActive() || getPlayerOpponent(player.getPlayerType()).isNonactive());
	}

	void phaseBegin() {
		match.getCurrentPhase().phaseBegin();
	}

	void phaseEnd() {
		if (match.getCurrentPhase().isMain()) {
			getPlayerHuman().setFlagPassedPriority(false);
			getPlayerComputer().setFlagPassedPriority(false);
		}
		match.getCurrentPhase().phaseEnd();
	}

	BooleanProperty propertyFlagTurnRunning() {
		return propertyFlagTurnRunning;
	}

	BooleanProperty propertyFlagTurnSkipped() {
		return propertyFlagTurnSkipped;
	}

	IntegerProperty propertyTurnNumber() {
		return propertyTurnNumber;
	}

	void setCurrentPhase() {
		match.propertyCurrentPhase().set(iteratorPhases.next());
	}

	void setCurrentStep() {
		match.getCurrentPhase().setCurrentStep();
	}

	void skipStepCombatDamage() {
		for (final Phase phase : getListPhases()) {
			if (phase.equals(PhaseType.COMBAT_PHASE)) {
				phase.stepSkip(StepType.COMBAT_DAMAGE_STEP);
			}
		}
	}

	void skipStepDeclareBlockers() {
		for (final Phase phase : getListPhases()) {
			if (phase.equals(PhaseType.COMBAT_PHASE)) {
				phase.stepSkip(StepType.DECLARE_BLOCKERS);
			}
		}
	}

	void stepBegin() {
		match.getCurrentPhase().stepBegin();
	}

	void stepEnd() {
		getPlayerHuman().setFlagPassedPriority(false);
		getPlayerComputer().setFlagPassedPriority(false);
		match.getCurrentPhase().stepEnd();
	}

	void turnBegin() {
		setFlagTurnRunning(true);
		propertyTurnNumber.set(getTurnNumber() + 1);
	}

	void turnEnd() {
		setFlagTurnRunning(false);
		match.getPlayerActive().setFlagPlayedLand(false);
	}

}
