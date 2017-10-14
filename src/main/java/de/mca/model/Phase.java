package de.mca.model;

import java.util.List;
import java.util.ListIterator;

import de.mca.model.enums.PhaseType;
import de.mca.model.enums.StepType;
import de.mca.model.enums.TurnBasedActionType;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 *
 * @author Maximilian Werling
 *
 */
public class Phase {

	/**
	 * Speichert den Iterator, der über die Spielschritte läuft.
	 */
	private ListIterator<Step> iteratorSteps;
	/**
	 * Speichert die Spielschritte der Phase.
	 */
	private final List<Step> listSteps;
	/**
	 * Speichert den Phasentyp.
	 */
	private final PhaseType phaseType;
	/**
	 * Speichert den aktuellen Spielschritt.
	 */
	private final ObjectProperty<Step> propertyCurrentStep;
	/**
	 * Zeigt an, ob die Phase wiederholt wird.
	 */
	private final BooleanProperty propertyFlagPhaseRepeated;
	/**
	 * Zeigt an, ob die Phase gerade läuft.
	 */
	private final BooleanProperty propertyFlagPhaseRunning;
	/**
	 * Zeigt an, ob die Phase übersprungen wird.
	 */
	private final BooleanProperty propertyFlagPhaseSkipped;
	/**
	 * Speichert den RuleEnforcer.
	 */
	private RuleEnforcer ruleEnforcer;

	Phase(RuleEnforcer ruleEnforcer, PhaseType phaseType, List<Step> listSteps) {
		this.listSteps = listSteps;
		this.phaseType = phaseType;
		this.ruleEnforcer = ruleEnforcer;

		propertyFlagPhaseRepeated = new SimpleBooleanProperty(false);
		propertyFlagPhaseRunning = new SimpleBooleanProperty(false);
		propertyFlagPhaseSkipped = new SimpleBooleanProperty(false);
		propertyCurrentStep = new SimpleObjectProperty<>(listSteps.get(0));

		iteratorSteps = listSteps.listIterator();
	}

	public boolean isMain() {
		return phaseType.isMain();
	}

	@Override
	public String toString() {
		return phaseType.toString();
	}

	private void fireEndTBA() {
		ruleEnforcer.examineTurnBasedAction(new TurnBasedAction(this, TurnBasedActionType.CLEAR_MANA_POOLS));
	}

	boolean equals(PhaseType phaseType) {
		return phaseType.equals(phaseType);
	}

	Step getCurrentStep() {
		return propertyCurrentStep().get();
	}

	boolean getFlagPhaseRunning() {
		return propertyFlagPhaseRunning.get();
	}

	boolean getFlagRepeated() {
		return propertyFlagPhaseRepeated.get();
	}

	boolean getFlagSkipped() {
		return propertyFlagPhaseSkipped.get();
	}

	boolean hasNextStep() {
		return iteratorSteps.hasNext();
	}

	boolean isCombatPhase() {
		return phaseType.isCombatPhase();
	}

	void phaseBegin() {
		setFlagPhaseRunning(true);
		iteratorSteps = listSteps.listIterator();
		propertyCurrentStep().set(listSteps.get(0));
	}

	void phaseEnd() {
		setFlagPhaseRunning(false);
		if (isMain()) {
			fireEndTBA();
		}
	}

	ObjectProperty<Step> propertyCurrentStep() {
		return propertyCurrentStep;
	}

	void setCurrentStep() {
		propertyCurrentStep().set(iteratorSteps.next());
	}

	void setFlagPhaseRunning(boolean flagRunning) {
		this.propertyFlagPhaseRunning.set(flagRunning);
	}

	void setFlagRepeated(boolean flagRepeated) {
		this.propertyFlagPhaseRepeated.set(flagRepeated);
	}

	void setFlagSkipped(boolean flagSkipped) {
		this.propertyFlagPhaseSkipped.set(flagSkipped);
	}

	void stepBegin() {
		getCurrentStep().begin();
	}

	void stepEnd() {
		getCurrentStep().end();
	}

	/**
	 * Markiert einen Spielschritt, sodass er übersprungen wird.
	 *
	 * @param stepType
	 *            Type des Spielschitts, der übersprungen werden soll.
	 */
	void stepSkip(StepType stepType) {
		for (final Step step : listSteps) {
			if (step.equals(stepType)) {
				step.setFlagSkipped(true);
				return;
			}
		}
	}
}
