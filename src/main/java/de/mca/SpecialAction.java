package de.mca;

import java.util.EventObject;

import de.mca.model.interfaces.IsPlayer;

/**
 * Bildet Special Actions im Sinne von Regel 115 des offiziellen Regelbuchs ab.
 *
 * @author Maximilian Werling
 *
 */
@SuppressWarnings("serial")
public abstract class SpecialAction extends EventObject {

	/**
	 * Speichert den Eventtyp.
	 */
	private final SpecialActionType sat;

	public SpecialAction(IsPlayer source, SpecialActionType sat) {
		super(source);
		this.sat = sat;
	}

	@Override
	public IsPlayer getSource() {
		return (IsPlayer) super.getSource();
	}

	public SpecialActionType getSpecialActionType() {
		return sat;
	}

	@Override
	public String toString() {
		return sat.toString();
	}

}
