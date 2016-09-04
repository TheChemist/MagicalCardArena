package de.mca;

import de.mca.model.interfaces.IsManaMap;
import de.mca.model.interfaces.IsPlayer;

@SuppressWarnings("serial")
public class PASelectCostMap extends PlayerAction {

	private final IsManaMap costMap;

	public PASelectCostMap(IsPlayer source, IsManaMap costMap) {
		super(source, PlayerActionType.SELECT_COST_MAP);
		this.costMap = costMap;
	}

	public IsManaMap getCostMap() {
		return costMap;
	}

}
