package de.mca;

import de.mca.model.MagicPermanent;
import de.mca.model.interfaces.IsPlayer;

/**
 *
 * @author Maximilian Werling
 *
 */
@SuppressWarnings("serial")
public class PAActivatePermanent extends PlayerAction {

	/**
	 * Speichert aktivierte Permanent.
	 */
	private final MagicPermanent magicPermanent;

	public PAActivatePermanent(IsPlayer source, MagicPermanent magicPermanent) {
		super(source, PlayerActionType.ACTIVATE_PERMANENT);
		this.magicPermanent = magicPermanent;
	}

	public MagicPermanent getAbility() {
		return magicPermanent;
	}

	@Override
	public String toString() {
		return new StringBuilder("[").append(super.toString()).append(" a=").append(magicPermanent).append("]").toString();
	}

}
