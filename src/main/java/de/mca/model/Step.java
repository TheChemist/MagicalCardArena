package de.mca.model;

import java.util.Set;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

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
	 * Speichert die rundenbasiert Aktion, die am Ende jeden Spielschrittes
	 * gefeuert wird.
	 */
	private final TurnBasedActionType endTBA;
	/**
	 * Speichert den EventBus.
	 */
	private final EventBus eventBus;
	/**
	 * Zeigt an, ob der Spielschritt wiederholt wird.
	 */
	private final BooleanProperty propertyFlagStepRepeated;
	/**
	 * Zeigt an, ob der Schritt gerade läuft.
	 */
	private BooleanProperty propertyFlagStepRunning;
	/**
	 * Zeigt an, ob der Spielschritt übersprungen wird.
	 */
	private final BooleanProperty propertyFlagStepSkipped;
	/**
	 * Speichert die rundenbasiertes Aktionen, die zu Beginn des Spielschrittes
	 * gefeuert werden.
	 */
	private final Set<TurnBasedActionType> setStartTBAs;
	/**
	 * Speichert den Typ des Spielschrittes.
	 */
	private final StepType stepType;

	@Inject
	Step(EventBus eventBus, @Assisted StepType stepType, @Assisted Set<TurnBasedActionType> setStartTBAs) {
		this.eventBus = eventBus;
		this.stepType = stepType;
		this.setStartTBAs = setStartTBAs;
		endTBA = TurnBasedActionType.CLEAR_MANA_POOLS;
		propertyFlagStepSkipped = new SimpleBooleanProperty(false);
		propertyFlagStepRepeated = new SimpleBooleanProperty(false);
		propertyFlagStepRunning = new SimpleBooleanProperty(false);
	}

	@Override
	public String toString() {
		return stepType.toString();
	}

	private void fireEndTBA() {
		eventBus.post(new TurnBasedAction(this, endTBA));
	}

	private void fireStartTBAs() {
		setStartTBAs.forEach(tbat -> eventBus.post(new TurnBasedAction(this, tbat)));
	}

	private void setFlagStepRunning(boolean flagRunning) {
		propertyFlagStepRunning.set(flagRunning);
	}

	boolean equals(StepType stepType) {
		return this.stepType.equals(stepType);
	}

	boolean getFlagPlayersGetPriority() {
		return stepType.playersGetPriority();
	}

	boolean getFlagStepRepeated() {
		return propertyFlagStepRepeated.get();
	}

	boolean getFlagStepRunning() {
		return propertyFlagStepRunning.get();
	}

	boolean getFlagStepSkipped() {
		return propertyFlagStepSkipped.get();
	}

	void setFlagRepeated(boolean flagRepeated) {
		this.propertyFlagStepRepeated.set(flagRepeated);
	}

	void setFlagSkipped(boolean flagSkipped) {
		this.propertyFlagStepSkipped.set(flagSkipped);
	}

	void stepBegin() {
		setFlagStepRunning(true);
		fireStartTBAs();
	}

	void stepEnd() {
		setFlagStepRunning(false);
		fireEndTBA();
	}
}
