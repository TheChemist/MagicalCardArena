package de.mca.model;

import java.util.List;
import java.util.ListIterator;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import de.mca.model.enums.PhaseType;
import de.mca.model.enums.StepType;
import de.mca.model.enums.TurnBasedActionType;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 *
 * @author Maximilian Werling
 *
 */
public class Phase {

	/**
	 * Speichert den EventBus.
	 */
	private final EventBus eventBus;
	/**
	 * Speichert den Iterator, der über die Spielschritte läuft.
	 */
	private ListIterator<Step> iteratorSteps;
	/**
	 * Speichert die Spielschritte der Phase.
	 */
	private final List<Step> listSteps;
	/**
	 * Speichert eine Referenz auf das Match.
	 */
	private Match parent;
	/**
	 * Speichert den Phasentyp.
	 */
	private final PhaseType phaseType;
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

	@Inject
	Phase(EventBus eventBus, @Assisted PhaseType phaseType, @Assisted List<Step> listSteps, @Assisted Match parent) {
		this.eventBus = eventBus;
		this.listSteps = listSteps;
		this.parent = parent;
		this.phaseType = phaseType;

		propertyFlagPhaseRepeated = new SimpleBooleanProperty(false);
		propertyFlagPhaseRunning = new SimpleBooleanProperty(false);
		propertyFlagPhaseSkipped = new SimpleBooleanProperty(false);

		iteratorSteps = this.listSteps.listIterator();
		parent.propertyCurrentStep().set(listSteps.get(0));
	}

	@Override
	public String toString() {
		return phaseType.toString();
	}

	private void fireEndTBA() {
		eventBus.post(new TurnBasedAction(this, TurnBasedActionType.CLEAR_MANA_POOLS));
	}

	boolean equals(PhaseType phaseType) {
		return phaseType.equals(phaseType);
	}

	Step getCurrentStep() {
		return parent.propertyCurrentStep().get();
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

	boolean isMain() {
		return phaseType.isMain();
	}

	void phaseBegin() {
		setFlagPhaseRunning(true);
		iteratorSteps = listSteps.listIterator();
		parent.propertyCurrentStep().set(listSteps.get(0));
	}

	void phaseEnd() {
		setFlagPhaseRunning(false);
		if (isMain()) {
			fireEndTBA();
		}
	}

	void setCurrentStep() {
		parent.propertyCurrentStep().set(iteratorSteps.next());
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
		getCurrentStep().stepBegin();
	}

	void stepEnd() {
		getCurrentStep().stepEnd();
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
