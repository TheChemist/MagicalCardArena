package de.mca;

import de.mca.model.MagicPermanent;
import de.mca.model.interfaces.IsAttackTarget;
import de.mca.model.interfaces.IsPlayer;

/**
 *
 * @author Maximilian Werling
 *
 */
@SuppressWarnings("serial")
public class PADeclareAttacker extends PlayerAction {

	private final IsAttackTarget attackTarget;
	/**
	 * Speichert die gekapselte Information über den Angriff.
	 */
	private final MagicPermanent magicPermanent;

	public PADeclareAttacker(IsPlayer source, MagicPermanent magicPermanent, IsAttackTarget attackTarget) {
		super(source, PlayerActionType.DECLARE_ATTACKER);
		this.magicPermanent = magicPermanent;
		this.attackTarget = attackTarget;
	}

	public MagicPermanent getAttacker() {
		return magicPermanent;
	}

	@Override
	public String toString() {
		return new StringBuilder("[").append(super.toString()).append(" a=[").append(magicPermanent).append("]]")
				.toString();
	}
}
