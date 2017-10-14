package de.mca.model;

import de.mca.model.enums.StateBasedActionType;
import de.mca.model.interfaces.IsPlayer;

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

	public IsPlayer getPlayerControlling() {
		return ((MagicPermanent) getSource()).getPlayerControlling();
	}

}
