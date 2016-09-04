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

	private PlayerType playerControlling;

	public SBACreatureToughnessZero(MagicPermanent source, PlayerType playerControlling) {
		super(source, StateBasedActionType.CREATURE_TOUGHNESS_ZERO);
	}

	public PlayerType getPlayerControlling() {
		return playerControlling;
	}

}
