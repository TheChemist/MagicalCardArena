package de.mca;

import de.mca.model.MagicPermanent;
import de.mca.model.interfaces.IsPlayer;

/**
 *
 * @author Maximilian Werling
 *
 */
@SuppressWarnings("serial")
public class PASelectPermanent extends PlayerAction {

	/**
	 * Speichert die bleibende Karte.
	 */
	private final MagicPermanent magicPermanent;

	public PASelectPermanent(IsPlayer source, MagicPermanent magicPermanent, PlayerActionType playerActionType) {
		super(source, playerActionType);
		this.magicPermanent = magicPermanent;
	}

	public MagicPermanent getMagicPermanent() {
		return magicPermanent;
	}

	@Override
	public String toString() {
		return new StringBuilder("[").append(super.toString()).append(" mp=[").append(magicPermanent).append("]]")
				.toString();
	}
}
