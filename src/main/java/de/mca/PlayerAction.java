package de.mca;

import java.util.EventObject;

import de.mca.model.interfaces.IsPlayer;

/**
 *
 * @author Maximilian Werling
 *
 */
@SuppressWarnings("serial")
public class PlayerAction extends EventObject {

	/**
	 * Speichert den Eventtyp.
	 */
	private final PlayerActionType pat;

	public PlayerAction(IsPlayer source, PlayerActionType pat) {
		super(source);
		this.pat = pat;
	}

	public PlayerActionType getPlayerActionType() {
		return pat;
	}

	@Override
	public IsPlayer getSource() {
		return (IsPlayer) super.getSource();
	}

	@Override
	public String toString() {
		return new StringBuilder("[").append(pat).append("]").toString();
	}
}
