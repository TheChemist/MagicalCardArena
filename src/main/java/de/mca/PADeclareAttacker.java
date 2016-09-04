package de.mca;

import de.mca.model.Attack;
import de.mca.model.interfaces.IsPlayer;

/**
 *
 * @author Maximilian Werling
 *
 */
@SuppressWarnings("serial")
public class PADeclareAttacker extends PlayerAction {

	/**
	 * Speichert die gekapselte Information über den Angriff.
	 */
	private final Attack attack;

	public PADeclareAttacker(IsPlayer source, Attack attack) {
		super(source, PlayerActionType.DECLARE_ATTACKER);
		this.attack = attack;
	}

	public Attack getAttack() {
		return attack;
	}

	@Override
	public String toString() {
		return new StringBuilder("[").append(super.toString()).append(" a=[").append(attack.getSource()).append("]]")
				.toString();
	}
}
