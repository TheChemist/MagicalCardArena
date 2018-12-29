package de.mca.model;

import java.util.Set;

import de.mca.model.enums.StepType;
import de.mca.model.enums.TurnBasedActionType;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 *
 * @author Maximilian Werling
 *
 */
public class Step {

	/**
	 * Speichert die rundenbasiert Aktion, die am Ende jeden Spielschrittes gefeuert
	 * wird.
	 */
	private final TurnBasedActionType endTBA;
	/**
	 * Zeigt an, ob der Spielschritt wiederholt wird.
	 */
	private final BooleanProperty propertyFlagStepRepeated;
	/**
	 * Zeigt an, ob der Schritt gerade läuft.
	 */
	private final BooleanProperty propertyFlagStepRunning;
	/**
	 * Zeigt an, ob der Spielschritt übersprungen wird.
	 */
	private final BooleanProperty propertyFlagStepSkipped;
	/**
	 * Speichert den RuleEnforcer.
	 */
	private final RuleEnforcer ruleEnforcer;
	/**
	 * Speichert die rundenbasiertes Aktionen, die zu Beginn des Spielschrittes
	 * gefeuert werden.
	 */
	private final Set<TurnBasedActionType> setStartTBAs;
	/**
	 * Speichert den Typ des Spielschrittes.
	 */
	private final StepType stepType;

	Step(RuleEnforcer ruleEnforcer, StepType stepType, Set<TurnBasedActionType> setStartTBAs) {
		this.ruleEnforcer = ruleEnforcer;
		this.stepType = stepType;
		this.setStartTBAs = setStartTBAs;
		endTBA = TurnBasedActionType.CLEAR_MANA_POOLS;
		propertyFlagStepSkipped = new SimpleBooleanProperty(false);
		propertyFlagStepRepeated = new SimpleBooleanProperty(false);
		propertyFlagStepRunning = new SimpleBooleanProperty(false);
	}

	public void fireCombatDamageAssignment() {
		ruleEnforcer.examineTurnBasedAction(new TurnBasedAction(this, TurnBasedActionType.COMBAT_DAMAGE_ASSIGNMENT));
	}

	public void fireCombatDamageDealing() {
		ruleEnforcer.examineTurnBasedAction(new TurnBasedAction(this, TurnBasedActionType.COMBAT_DAMAGE_DEALING));
	}

	public void fireDeclareDamageAssignmentBlocker() {
		/**
		 * Hierbei handelt es sich um einen Sonderfall. Mehrere TurnBasedActions werden
		 * nacheinander abgefeuert, jedoch sind die späteren abhängig von Spielerinput
		 * der in den ersten TBAs eingeholt wird. Um eine sequenzielle Abarbeitung zu
		 * gewährleisten muss der Aufruf in diesem Fall an einen Ort verlegt werden.
		 */
		ruleEnforcer.examineTurnBasedAction(
				new TurnBasedAction(this, TurnBasedActionType.DECLARE_DAMAGE_ASSIGNMENT_ORDER_BLOCKER));
	}

	public boolean getFlagStepRepeated() {
		return propertyFlagStepRepeated.get();
	}

	public boolean getFlagStepRunning() {
		return propertyFlagStepRunning.get();
	}

	public boolean getFlagStepSkipped() {
		return propertyFlagStepSkipped.get();
	}

	public StepType getStepType() {
		return stepType;
	}

	@Override
	public String toString() {
		return stepType.toString();
	}

	private void fireEndTBA() {
		ruleEnforcer.examineTurnBasedAction(new TurnBasedAction(this, endTBA));
	}

	private void fireStartTBAs() {
		setStartTBAs.forEach(tbat -> ruleEnforcer.examineTurnBasedAction(new TurnBasedAction(this, tbat)));
	}

	private void setFlagStepRunning(boolean flagRunning) {
		propertyFlagStepRunning.set(flagRunning);
	}

	void begin() {
		setFlagStepRunning(true);
		fireStartTBAs();
	}

	void end() {
		setFlagStepRunning(false);
		fireEndTBA();
	}

	boolean equals(StepType stepType) {
		return this.stepType.equals(stepType);
	}

	boolean getFlagPlayersGetPriority() {
		return stepType.playersGetPriority();
	}

	void setFlagRepeated(boolean flagRepeated) {
		this.propertyFlagStepRepeated.set(flagRepeated);
	}

	void setFlagSkipped(boolean flagSkipped) {
		this.propertyFlagStepSkipped.set(flagSkipped);
	}

}
