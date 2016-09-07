package de.mca;

import de.mca.model.MagicCard;
import de.mca.model.interfaces.IsPlayer;

/**
 * 
 * @author Maximilian Werling
 *
 */
@SuppressWarnings("serial")
public class PADiscard extends PlayerAction {

	private final MagicCard magicCard;

	public PADiscard(IsPlayer source, MagicCard magicCard) {
		super(source, PlayerActionType.DISCARD);
		this.magicCard = magicCard;
	}

	public MagicCard getMagicCard() {
		return magicCard;
	}

	@Override
	public String toString() {
		return new StringBuilder("[").append(super.toString()).append(" c=").append(magicCard).append("]").toString();
	}

}
