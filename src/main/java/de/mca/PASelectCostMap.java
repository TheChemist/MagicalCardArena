package de.mca;

import de.mca.model.MagicSpell;
import de.mca.model.interfaces.IsPlayer;

/**
 *
 * @author Maximilian Werling
 *
 */
@SuppressWarnings("serial")
public class PASelectCostMap extends PlayerAction {

	private MagicSpell magicSpell;

	public PASelectCostMap(IsPlayer source, MagicSpell magicSpell) {
		super(source, PlayerActionType.SELECT_COST_MAP);
		this.magicSpell = magicSpell;
	}

	public MagicSpell getMagicSpell() {
		return magicSpell;
	}

}
