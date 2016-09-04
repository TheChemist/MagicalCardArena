package de.mca;

import de.mca.model.MagicCard;
import de.mca.model.interfaces.IsPlayer;

/**
 *
 * @author Maximilian Werling
 *
 */
@SuppressWarnings("serial")
public class SAPlayLand extends SpecialAction {

	/**
	 * Speichert die Landkarte, die gespielt wird.
	 */
	private final MagicCard landCard;

	public SAPlayLand(IsPlayer source, MagicCard landCard) {
		super(source, SpecialActionType.PLAY_A_LAND);
		this.landCard = landCard;
	}

	public MagicCard getLandCard() {
		return landCard;
	}

	@Override
	public String toString() {
		return new StringBuilder("[").append(super.toString()).append(" lc=").append(landCard).append("]").toString();
	}

}
