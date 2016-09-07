package de.mca;

import java.util.List;

import de.mca.model.Attack;
import de.mca.model.CharacteristicAbility;
import de.mca.model.MagicCard;
import de.mca.model.MagicPermanent;
import de.mca.model.enums.PlayerState;
import de.mca.model.interfaces.IsAttackTarget;
import de.mca.model.interfaces.IsManaMap;
import de.mca.model.interfaces.IsPlayer;

/**
 *
 * @author Maximilian Werling
 *
 */
public interface IsInput {

	public CharacteristicAbility determineAbility(List<CharacteristicAbility> listLegalAbilities);

	public MagicPermanent determineAttacker(List<MagicPermanent> legalAttackers);

	public IsAttackTarget determineAttackTarget(List<IsAttackTarget> legalAttackTargets);

	public MagicPermanent determineBlocker(List<MagicPermanent> legalBlockers);

	public int determineBlockTarget(List<Attack> listAttacks);

	public MagicPermanent determineCardToActivate(List<MagicPermanent> legalPermanents);

	public MagicCard determineCardToCast(List<MagicCard> legalCards);

	public MagicCard determineCardToDiscard(List<MagicCard> handCards);

	public IsManaMap determineCostGoal(List<IsManaMap> costMaps);

	public List<MagicPermanent> determineDamageAssignmentOrderAttacker(List<MagicPermanent> blockers);

	public void determineInput(PlayerState ps);

	public IsPlayer getPlayer();

	public default void inputActivatedAbility(CharacteristicAbility characteristicAbility) {
		getPlayer().fireActivateActivatedAbility(characteristicAbility);
	}

	public default void inputCastSpell(MagicCard magicCard) {
		getPlayer().fireCastSpell(magicCard);
	}

	public default void inputConcede() {
		getPlayer().fireConcede();
	}

	public default void inputDiscard(MagicCard magicCard) {
		getPlayer().fireDiscard(magicCard);
	}

	public default void inputDeclareAttacker(Attack attack) {
		getPlayer().fireDeclareAttacker(attack);
	}

	public default void inputDeclareBlocker(int attackerIndex, MagicPermanent blocker) {
		// TODO: Vllt lieber Permanent anstatt Index übergeben
		getPlayer().fireDeclareBlocker(attackerIndex, blocker);
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
