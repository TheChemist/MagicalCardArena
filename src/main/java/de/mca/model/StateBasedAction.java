package de.mca.model;

import java.util.EventObject;

import de.mca.model.enums.StateBasedActionType;

/**
 *
 * @author Maximilian Werling
 *
 */
@SuppressWarnings("serial")
public class StateBasedAction extends EventObject {

	/**
	 * Speichert den Eventtyp.
	 */
	private final StateBasedActionType sbat;

	public StateBasedAction(Object source, StateBasedActionType sbat) {
		super(source);
		this.sbat = sbat;
	}

	public StateBasedActionType getStateBasedActionType() {
		return sbat;
	}

	@Override
	public String toString() {
		return new StringBuilder("[").append(sbat).append("]").toString();
	}
}
