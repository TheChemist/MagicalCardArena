package de.mca;

import de.mca.model.MagicCard;
import de.mca.model.interfaces.IsPlayer;

/**
 *
 * @author Maximilian Werling
 *
 */
@SuppressWarnings("serial")
public class PACastSpell extends PlayerAction {

	/**
	 * Speichert die Karte, die als Zauberspruch auf den Stapel gespielt wird.
	 */
	private final MagicCard spell;

	public PACastSpell(IsPlayer source, MagicCard spell) {
		super(source, PlayerActionType.CAST_SPELL);
		this.spell = spell;
	}

	public MagicCard getCard() {
		return spell;
	}

	@Override
	public String toString() {
		return new StringBuilder("[").append(super.toString()).append(" s=").append(spell).append("]").toString();
	}

}
