package de.mca;

import de.mca.model.MagicPermanent;
import de.mca.model.interfaces.IsPlayer;

@SuppressWarnings("serial")
public class PADeclareBlocker extends PlayerAction {

	/**
	 * Speichert den Angriff, der geblockt wird.
	 */
	private final int attackIndex;
	/**
	 * Speichert die verteidigende Kreatur.
	 */
	private final MagicPermanent blocker;

	public PADeclareBlocker(IsPlayer source, int attackIndex, MagicPermanent blocker) {
		super(source, PlayerActionType.DECLARE_BLOCKER);
		this.attackIndex = attackIndex;
		this.blocker = blocker;
	}

	public int getAttackIndex() {
		return attackIndex;
	}

	public MagicPermanent getBlocker() {
		return blocker;
	}

	@Override
	public String toString() {
		return new StringBuilder("[").append(super.toString()).append(" b=[").append(blocker).append("]]").toString();
	}

}
