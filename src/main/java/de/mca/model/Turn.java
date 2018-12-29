package de.mca.model;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.ListIterator;

import com.google.common.collect.ImmutableList;

import de.mca.model.enums.PhaseType;
import de.mca.model.enums.StepType;
import de.mca.model.enums.TurnBasedActionType;
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
	 * Speichert den Iterator, der die Phasen durchläuft.
	 */
	private final ListIterator<Phase> iteratorPhases;
	/**
	 * Speichert die verschiedenen Spielphasen.
	 */
	private final List<Phase> listPhases;
	/**
	 * Speichert die aktuelle Spielphase.
	 */
	private final ObjectProperty<Phase> propertyCurrentPhase;
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

	Turn(RuleEnforcer ruleEnforcer) {
		listPhases = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
		propertyFlagTurnRunning = new SimpleBooleanProperty(false);
		propertyFlagTurnSkipped = new SimpleBooleanProperty(false);
		propertyTurnNumber = new SimpleIntegerProperty(0);

		final Step untap = new Step(ruleEnforcer, StepType.UNTAP_STEP, EnumSet.of(TurnBasedActionType.UNTAP));
		final Step upkeep = new Step(ruleEnforcer, StepType.UPKEEP_STEP, EnumSet.noneOf(TurnBasedActionType.class));
		final Step draw = new Step(ruleEnforcer, StepType.DRAW_STEP, EnumSet.of(TurnBasedActionType.DRAW));
		final Phase beginning = new Phase(ruleEnforcer, PhaseType.BEGINNING_PHASE,
				ImmutableList.of(untap, upkeep, draw));

		final Phase precombatMain = new Phase(ruleEnforcer, PhaseType.PRECOMBAT_MAIN_PHASE,
				ImmutableList.of(new Step(ruleEnforcer, StepType.NONE, EnumSet.noneOf(TurnBasedActionType.class))));

		final Step beginningOfCombat = new Step(ruleEnforcer, StepType.BEGINNING_OF_COMBAT_STEP,
				EnumSet.of(TurnBasedActionType.BEGINNING_OF_COMBAT_STEP));
		final Step declareAttackers = new Step(ruleEnforcer, StepType.DECLARE_ATTACKERS,
				EnumSet.of(TurnBasedActionType.DECLARE_ATTACKER));
		final Step declareBlockers = new Step(ruleEnforcer, StepType.DECLARE_BLOCKERS,
				EnumSet.of(TurnBasedActionType.DECLARE_BLOCKER));
		final Step combatDamage = new Step(ruleEnforcer, StepType.COMBAT_DAMAGE_STEP,
				EnumSet.of(TurnBasedActionType.DECLARE_DAMAGE_ASSIGNMENT_ORDER_ATTACKER));
		final Step endOfCombat = new Step(ruleEnforcer, StepType.END_OF_COMBAT,
				EnumSet.of(TurnBasedActionType.END_OF_COMBAT));
		final Phase combat = new Phase(ruleEnforcer, PhaseType.COMBAT_PHASE,
				ImmutableList.of(beginningOfCombat, declareAttackers, declareBlockers, combatDamage, endOfCombat));

		final Phase postcombatMain = new Phase(ruleEnforcer, PhaseType.POSTCOMBAT_MAIN_PHASE,
				ImmutableList.of(new Step(ruleEnforcer, StepType.NONE, EnumSet.noneOf(TurnBasedActionType.class))));

		final Step end = new Step(ruleEnforcer, StepType.END_STEP, EnumSet.noneOf(TurnBasedActionType.class));
		final Step cleanup = new Step(ruleEnforcer, StepType.CLEANUP_STEP,
				EnumSet.of(TurnBasedActionType.DISCARD, TurnBasedActionType.CLEANUP));
		final Phase ending = new Phase(ruleEnforcer, PhaseType.ENDING_PHASE, ImmutableList.of(end, cleanup));

		listPhases.add(beginning);
		listPhases.add(precombatMain);
		listPhases.add(combat);
		listPhases.add(postcombatMain);
		listPhases.add(ending);

		propertyCurrentPhase = new SimpleObjectProperty<>(beginning);

		iteratorPhases = listPhases.listIterator();
	}

	Turn(Turn turn) {
		listPhases = turn.getListPhases();
		propertyFlagTurnRunning = turn.propertyFlagTurnRunning();
		propertyFlagTurnSkipped = turn.propertyFlagTurnSkipped();
		propertyTurnNumber = turn.propertyTurnNumber();
		propertyCurrentPhase = new SimpleObjectProperty<>(listPhases.get(0));

		iteratorPhases = listPhases.listIterator();
	}

	public Phase getCurrentPhase() {
		return propertyCurrentPhase().get();
	}

	public boolean getFlagTurnRunning() {
		return propertyFlagTurnRunning().get();
	}

	public boolean getFlagTurnSkipped() {
		return propertyFlagTurnSkipped().get();
	}

	public List<Phase> getListPhases() {
		return listPhases;
	}

	public int getTurnNumber() {
		return propertyTurnNumber().get();
	}

	public void skipStepDraw() {
		for (final Phase phase : getListPhases()) {
			if (phase.equals(PhaseType.BEGINNING_PHASE)) {
				phase.stepSkip(StepType.DRAW_STEP);
			}
		}
	}

	private void setFlagTurnRunning(boolean flagRunning) {
		propertyFlagTurnRunning.set(flagRunning);
	}

	boolean hasNextPhase() {
		return iteratorPhases.hasNext();
	}

	void phaseBegin() {
		getCurrentPhase().phaseBegin();
	}

	void phaseEnd() {
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

	IntegerProperty propertyTurnNumber() {
		return propertyTurnNumber;
	}

	void setCurrentPhase() {
		propertyCurrentPhase().set(iteratorPhases.next());
	}

	void setCurrentStep() {
		getCurrentPhase().setCurrentStep();
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
		getCurrentPhase().stepEnd();
	}

	void turnBegin() {
		setFlagTurnRunning(true);
		propertyTurnNumber().set(getTurnNumber() + 1);
	}

	void turnEnd() {
		setFlagTurnRunning(false);
	}	

}
