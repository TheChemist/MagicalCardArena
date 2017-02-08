package de.mca.model;

import de.mca.model.enums.PlayerType;
import de.mca.model.enums.StateBasedActionType;

/**
 *
 * @author Maximilian Werling
 *
 */
@SuppressWarnings("serial")
public class SBACreatureToughnessZero extends StateBasedAction {

	public SBACreatureToughnessZero(MagicPermanent source) {
		super(source, StateBasedActionType.CREATURE_TOUGHNESS_ZERO);
	}

	public PlayerType getPlayerControlling() {
		return ((MagicPermanent) getSource()).getPlayerControlling();
	}

}
