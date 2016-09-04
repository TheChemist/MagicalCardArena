package de.mca.model;

import java.util.EventObject;

import de.mca.model.enums.TurnBasedActionType;

/**
 *
 * @author Maximilian Werling
 *
 */
@SuppressWarnings("serial")
public class TurnBasedAction extends EventObject {

	/**
	 * Speichert den Eventtyp.
	 */
	private final TurnBasedActionType tbat;

	public TurnBasedAction(Phase source, TurnBasedActionType tbat) {
		super(source);
		this.tbat = tbat;
	}

	public TurnBasedAction(Step source, TurnBasedActionType tbat) {
		super(source);
		this.tbat = tbat;
	}

	public TurnBasedActionType getTurnBasedActionType() {
		return tbat;
	}

	@Override
	public String toString() {
		return new StringBuilder("[").append(tbat).append("]").toString();
	}

}
