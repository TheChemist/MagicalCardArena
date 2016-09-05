package de.mca.model;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.ListIterator;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import de.mca.factories.FactoryPhase;
import de.mca.factories.FactoryStep;
import de.mca.model.enums.PhaseType;
import de.mca.model.enums.PlayerState;
import de.mca.model.enums.PlayerType;
import de.mca.model.enums.StepType;
import de.mca.model.enums.TurnBasedActionType;
import de.mca.model.interfaces.IsPlayer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
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
	 * Spiechert die verschiedenen Spielphasen.
	 */
	private final List<Phase> listPhases;
	/**
	 * Speichert den computergesteuerten Spieler.
	 */
	private final IsPlayer playerComputer;
	/**
	 * Speichert den menschlichen Spieler.
	 */
	private final IsPlayer playerHuman;
	/**
	 * Speichert die aktuelle Spielphase.
	 */
	private final ObjectProperty<Phase> propertyCurrentPhase;
	/**
	 * Zeigt an, ob die Runde gerade läuft
	 */
	private final BooleanProperty propertyFlagTurnRunning;
	/**
	 * Zeigt an, ob die nächste Runde übersprungen wird.
	 */
	private final BooleanProperty propertyFlagTurnSkipped;
	/**
	 * Speichert den aktiven Spieler.
	 */
	private final ObjectProperty<IsPlayer> propertyPlayerActive;
	/**
	 * Speichert den priorisierten Spieler.
	 */
	private final ObjectProperty<IsPlayer> propertyPlayerPrioritized;
	/**
	 * Speichert die Rundennummer.
	 */
	private final IntegerProperty propertyTurnNumber;

	@Inject
	Turn(FactoryPhase phaseFactory, FactoryStep stepFactory, @Assisted("playerComputer") IsPlayer playerComputer,
			@Assisted("playerHuman") IsPlayer playerHuman) {
		this.playerComputer = playerComputer;
		this.playerHuman = playerHuman;

		listPhases = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
		propertyFlagTurnRunning = new SimpleBooleanProperty(false);
		propertyFlagTurnSkipped = new SimpleBooleanProperty(false);
		propertyPlayerActive = new SimpleObjectProperty<>(playerComputer);
		propertyPlayerPrioritized = new SimpleObjectProperty<>(playerComputer);
		propertyTurnNumber = new SimpleIntegerProperty(0);

		final Step untap = stepFactory.create(StepType.UNTAP_STEP, EnumSet.of(TurnBasedActionType.UNTAP));
		final Step upkeep = stepFactory.create(StepType.UPKEEP_STEP, EnumSet.noneOf(TurnBasedActionType.class));
		final Step draw = stepFactory.create(StepType.DRAW_STEP, EnumSet.of(TurnBasedActionType.DRAW));
		final Phase beginning = phaseFactory.create(PhaseType.BEGINNING_PHASE, ImmutableList.of(untap, upkeep, draw));

		final Phase precombatMain = phaseFactory.create(PhaseType.PRECOMBAT_MAIN_PHASE,
				ImmutableList.of(stepFactory.create(StepType.NONE, EnumSet.noneOf(TurnBasedActionType.class))));

		final Step beginningOfCombat = stepFactory.create(StepType.BEGINNING_OF_COMBAT_STEP,
				EnumSet.of(TurnBasedActionType.BEGINNING_OF_COMBAT_STEP));
		final Step declareAttackers = stepFactory.create(StepType.DECLARE_ATTACKERS,
				EnumSet.of(TurnBasedActionType.DECLARE_ATTACKER));
		final Step declareBlockers = stepFactory.create(StepType.DECLARE_BLOCKERS,
				EnumSet.of(TurnBasedActionType.DECLARE_BLOCKER,
						TurnBasedActionType.DECLARE_DAMAGE_ASSIGNMENT_ORDER_ATTACKER,
						TurnBasedActionType.DECLARE_DAMAGE_ASSIGNMENT_ORDER_BLOCKER));
		final Step combatDamage = stepFactory.create(StepType.COMBAT_DAMAGE_STEP,
				EnumSet.of(TurnBasedActionType.COMBAT_DAMAGE_ASSIGNMENT, TurnBasedActionType.COMBAT_DAMAGE_DEALING));
		final Step endOfCombat = stepFactory.create(StepType.END_OF_COMBAT, EnumSet.noneOf(TurnBasedActionType.class));
		final Phase combat = phaseFactory.create(PhaseType.COMBAT_PHASE,
				ImmutableList.of(beginningOfCombat, declareAttackers, declareBlockers, combatDamage, endOfCombat));

		final Phase postcombatMain = phaseFactory.create(PhaseType.POSTCOMBAT_MAIN_PHASE,
				ImmutableList.of(stepFactory.create(StepType.NONE, EnumSet.noneOf(TurnBasedActionType.class))));

		final Step end = stepFactory.create(StepType.END_STEP, EnumSet.noneOf(TurnBasedActionType.class));
		final Step cleanup = stepFactory.create(StepType.CLEANUP_STEP,
				EnumSet.of(TurnBasedActionType.DISCARD, TurnBasedActionType.CLEANUP));
		final Phase ending = phaseFactory.create(PhaseType.ENDING_PHASE, ImmutableList.of(end, cleanup));

		propertyCurrentPhase = new SimpleObjectProperty<>(beginning);

		listPhases.add(beginning);
		listPhases.add(precombatMain);
		listPhases.add(combat);
		listPhases.add(postcombatMain);
		listPhases.add(ending);

		iteratorPhases = listPhases.listIterator();
	}

	Turn(Turn turn) {
		playerComputer = turn.getPlayerComputer();
		playerHuman = turn.getPlayerHuman();

		listPhases = turn.getListPhases();
		propertyCurrentPhase = turn.propertyCurrentPhase();
		propertyFlagTurnRunning = turn.propertyFlagTurnRunning();
		propertyFlagTurnSkipped = turn.propertyFlagTurnSkipped();
		propertyPlayerActive = turn.propertyPlayerActive();
		propertyPlayerPrioritized = turn.propertyPlayerPrioritized();
		propertyTurnNumber = turn.propertyTurnNumber();

		iteratorPhases = listPhases.listIterator();
		propertyCurrentPhase.set(listPhases.get(0));
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

	/**
	 * Liefert den gegnerischen Spieler zu einen gegeben Spieler.
	 *
	 * @param player
	 *            der Spieler, dessen Gegener zurückgegeben werden soll.
	 * @return Gegener des übergeben Spielers.
	 */
	private IsPlayer getPlayerOpponent(IsPlayer player) {
		return player.equals(PlayerType.HUMAN) ? getPlayerComputer() : getPlayerHuman();
	}

	private int getTurnNumber() {
		return propertyTurnNumber().get();
	}

	private void setFlagTurnRunning(boolean flagRunning) {
		propertyFlagTurnRunning.set(flagRunning);
	}

	Phase getCurrentPhase() {
		return propertyCurrentPhase().get();
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
		return getPlayerActive().equals(PlayerType.HUMAN) ? getPlayerHuman() : getPlayerComputer();
	}

	/**
	 * Liefert den aktiven Spieler.
	 *
	 * @return den aktiven Spieler.
	 */
	IsPlayer getPlayerActive() {
		return propertyPlayerActive().get();
	}

	/**
	 * Liefert den nichtaktiven Spieler.
	 *
	 * @return den nichtaktiven Spieler.
	 */
	IsPlayer getPlayerNonactive() {
		return getPlayerOpponent(getPlayerActive());
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
		return (player.isActive() || getPlayerOpponent(player).isNonactive());
	}

	void phaseBegin() {
		getCurrentPhase().phaseBegin();
	}

	void phaseEnd() {
		if (getCurrentPhase().isMain()) {
			getPlayerHuman().setFlagPassedPriority(false);
			getPlayerComputer().setFlagPassedPriority(false);
		}
		getCurrentPhase().phaseEnd();
	}

	ObjectProperty<Phase> propertyCurrentPhase() {
		return propertyCurrentPhase;
	}

	BooleanProperty propertyFlagTurnRunning() {
		return propertyFlagTurnRunning;
	}

	BooleanProperty propertyFlagTurnSkipped() {
		return propertyFlagTurnSkipped;
	}

	ObjectProperty<IsPlayer> propertyPlayerActive() {
		return propertyPlayerActive;
	}

	ObjectProperty<IsPlayer> propertyPlayerPrioritized() {
		return propertyPlayerPrioritized;
	}

	IntegerProperty propertyTurnNumber() {
		return propertyTurnNumber;
	}

	void setCurrentPhase() {
		propertyCurrentPhase().set(iteratorPhases.next());
	}

	void setCurrentStep() {
		getCurrentPhase().setCurrentStep();
	}

	void setPlayerActive(IsPlayer playerActive) {
		propertyPlayerActive().set(playerActive);
		playerActive.setPlayerState(PlayerState.ACTIVE);
		getPlayerOpponent(playerActive).setPlayerState(PlayerState.NONACTIVE);
	}

	void setPlayerPrioritized(IsPlayer playerPrioritized) {
		this.propertyPlayerPrioritized.set(playerPrioritized);
		playerPrioritized.setPlayerState(PlayerState.PRIORITIZED);
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
		getCurrentPhase().stepBegin();
	}

	void stepEnd() {
		getPlayerHuman().setFlagPassedPriority(false);
		getPlayerComputer().setFlagPassedPriority(false);
		getCurrentPhase().stepEnd();
	}

	void turnBegin() {
		setFlagTurnRunning(true);
		propertyTurnNumber.set(getTurnNumber() + 1);
	}

	void turnEnd() {
		setFlagTurnRunning(false);
		getPlayerActive().setFlagPlayedLand(false);
	}

}
