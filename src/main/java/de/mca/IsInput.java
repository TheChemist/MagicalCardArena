package de.mca;

import de.mca.model.MagicCard;
import de.mca.model.MagicPermanent;
import de.mca.model.interfaces.IsPlayer;

/**
 *
 * @author Maximilian Werling
 *
 */
public interface IsInput {

	public void buttonProgressClicked(boolean flagNeedPlayerInput);

	public IsPlayer getPlayer();

	public default void inputActivatedPermanent(MagicPermanent magicPermanent) {
		getPlayer().fireActivatePermanent(magicPermanent);
	}

	public default void inputCastSpell(MagicCard magicCard) {
		getPlayer().fireCastSpell(magicCard);
	}

	public default void inputConcede() {
		getPlayer().fireConcede();
	}

	public default void inputDeclareAttacker(MagicPermanent attacker) {
		getPlayer().fireDeclareAttacker(attacker);
	}

	public default void inputDeclareBlocker(int attackerIndex, MagicPermanent blocker) {
		getPlayer().fireDeclareBlocker(attackerIndex, blocker);
	}

	public default void inputDiscard(MagicCard magicCard) {
		getPlayer().fireDiscard(magicCard);
	}

	public default void inputEndDeclareAttackers() {
		getPlayer().fireEndDeclareAttackers();
	}

	public default void inputEndDeclareBlockers() {
		getPlayer().fireEndDeclareBlockers();
	}

	public default void inputPassPriority() {
		getPlayer().firePassPriority();
	}

	public default void inputPlayLand(MagicCard magicCard) {
		getPlayer().firePlayLand(magicCard);
	}

	public default void inputSelectBlockTarget(MagicPermanent blockTarget) {
		getPlayer().fireSelectBlockTarget(blockTarget);
	}

	public void setPlayer(IsPlayer player);

}
