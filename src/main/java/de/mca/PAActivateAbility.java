package de.mca;

import de.mca.model.ActivatedAbility;
import de.mca.model.interfaces.IsPlayer;

/**
 *
 * @author Maximilian Werling
 *
 */
@SuppressWarnings("serial")
public class PAActivateAbility extends PlayerAction {

	/**
	 * Speichert die aktivierte Fähigkeit.
	 */
	private final ActivatedAbility ability;

	public PAActivateAbility(IsPlayer source, ActivatedAbility ability) {
		super(source, PlayerActionType.ACTIVATE_ACTIVATED_ABILITY);
		this.ability = ability;
	}

	public ActivatedAbility getAbility() {
		return ability;
	}

	@Override
	public String toString() {
		return new StringBuilder("[").append(super.toString()).append(" a=").append(ability).append("]").toString();
	}

}
