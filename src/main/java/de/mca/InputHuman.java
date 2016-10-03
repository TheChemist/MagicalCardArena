package de.mca;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mca.model.Attack;
import de.mca.model.ActivatedAbility;
import de.mca.model.MagicCard;
import de.mca.model.MagicPermanent;
import de.mca.model.ManaMapDefault;
import de.mca.model.Match;
import de.mca.model.enums.PlayerState;
import de.mca.model.enums.ZoneType;
import de.mca.model.interfaces.IsAttackTarget;
import de.mca.model.interfaces.IsManaMap;
import de.mca.model.interfaces.IsPlayer;

/**
 *
 * @author Maximilian Werling
 *
 */
public class InputHuman implements IsInput {

	/**
	 * Speichert den Logger.
	 */
	private final static Logger LOGGER = LoggerFactory.getLogger("Input");
	private final static Scanner sc = new Scanner(System.in);
	private Match match;
	private IsPlayer player;

	InputHuman() {

	}

	public List<MagicPermanent> announceDamageAssignmentOrderAttacker(List<MagicPermanent> blockers) {
		return determineDamageAssignmentOrderAttacker(blockers);
	}

	@Override
	public ActivatedAbility determineAbility(List<ActivatedAbility> listLegalAbilities) {
		LOGGER.debug("{} determineManaAbility({})", player, listLegalAbilities);
		if (listLegalAbilities.isEmpty()) {
			throw new RuntimeException("Keine Fähigkeit zum auswählen!");
		} else if (listLegalAbilities.size() == 1) {
			return listLegalAbilities.get(0);
		} else {
			throw new RuntimeException("Mehrere Fähigkeiten zum auswählen! Wähle zufällig...");
		}
	}

	@Override
	public MagicPermanent determineAttacker(List<MagicPermanent> listLegalAttackers) {
		LOGGER.debug("{} determineAttacker({})", player, listLegalAttackers);
		LOGGER.debug("{} -> Actions: [Angreifer auswählen]", player);
		for (int i = 0; i < listLegalAttackers.size(); i++) {
			LOGGER.debug("{} {}", i, listLegalAttackers.get(i));
		}
		try {
			return listLegalAttackers.get(Integer.parseInt(sc.nextLine()));
		} catch (final Exception e) {
			e.printStackTrace();
			return determineAttacker(listLegalAttackers);
		}
	}

	@Override
	public IsAttackTarget determineAttackTarget(List<IsAttackTarget> legalAttackTargets) {
		LOGGER.debug("{} determineAttackTarget({})", player, legalAttackTargets);
		LOGGER.debug("{} -> Actions: [Angriffsziel auswählen]", player);
		for (int i = 0; i < legalAttackTargets.size(); i++) {
			LOGGER.debug("{} {}", i, legalAttackTargets.get(i));
		}
		try {
			return legalAttackTargets.get(Integer.parseInt(sc.nextLine()));
		} catch (final Exception e) {
			e.printStackTrace();
			return determineAttackTarget(legalAttackTargets);
		}
	}

	@Override
	public MagicPermanent determineBlocker(List<MagicPermanent> legalBlockers) {
		LOGGER.debug("{} determineBlocker({})", player, legalBlockers);
		LOGGER.debug("{} -> Actions: [Verteidiger auswählen]", player);
		for (int i = 0; i < legalBlockers.size(); i++) {
			LOGGER.debug("{} {}", i, legalBlockers.get(i));
		}
		try {
			return legalBlockers.get(Integer.parseInt(sc.nextLine()));
		} catch (final Exception e) {
			e.printStackTrace();
			return determineBlocker(legalBlockers);
		}
	}

	@Override
	public int determineBlockTarget(List<Attack> listAttacks) {
		LOGGER.debug("{} determineBlockTarget({})", player, listAttacks);
		LOGGER.debug("{} -> Actions: [Blockziel auswählen]", player);
		for (int i = 0; i < listAttacks.size(); i++) {
			LOGGER.debug("{} {}", i, listAttacks.get(i).getSource());
		}
		try {
			return Integer.parseInt(sc.nextLine());
		} catch (final Exception e) {
			e.printStackTrace();
			return determineBlockTarget(listAttacks);
		}
	}

	@Override
	public MagicPermanent determineCardToActivate(List<MagicPermanent> listLegalPermanents) {
		LOGGER.debug("{} determineCardToActivate({})", player, listLegalPermanents);
		LOGGER.debug("{} -> Actions: [Karte auswählen]", player);
		for (int i = 0; i < listLegalPermanents.size(); i++) {
			LOGGER.debug("{} {}", i, listLegalPermanents.get(i));
		}
		try {
			return listLegalPermanents.get(Integer.parseInt(sc.nextLine()));
		} catch (final Exception e) {
			e.printStackTrace();
			return determineCardToActivate(listLegalPermanents);
		}
	}

	@Override
	public MagicCard determineCardToCast(List<MagicCard> legalCards) {
		LOGGER.debug("{} determineCardToCast({})", player, legalCards);
		LOGGER.debug("{} -> Actions: [Karte auswählen]", player);
		for (int i = 0; i < legalCards.size(); i++) {
			LOGGER.debug("{} {}", i, legalCards.get(i));
		}
		try {
			return legalCards.get(Integer.parseInt(sc.nextLine()));
		} catch (final Exception e) {
			e.printStackTrace();
			return determineCardToCast(legalCards);
		}
	}

	@Override
	public MagicCard determineCardToDiscard(List<MagicCard> handCards) {
		LOGGER.debug("{} determineCardToDiscard({})", player, handCards);
		LOGGER.debug("{} -> Actions: [Karte auswählen]", player);
		for (int i = 0; i < handCards.size(); i++) {
			LOGGER.debug("{} {}", i, handCards.get(i));
		}
		try {
			return handCards.get(Integer.parseInt(sc.nextLine()));
		} catch (final Exception e) {
			e.printStackTrace();
			return determineCardToDiscard(handCards);
		}
	}

	@Override
	public IsManaMap determineCostGoal(List<IsManaMap> costMaps) {
		LOGGER.debug("{} determineCostGoal({})", player, costMaps);
		LOGGER.debug("{} -> Actions: [Kosten auswählen]", player);
		for (int i = 0; i < costMaps.size(); i++) {
			LOGGER.debug("{} {}", i, costMaps.get(i));
		}
		try {
			return costMaps.get(Integer.parseInt(sc.nextLine()));
		} catch (final Exception e) {
			return determineCostGoal(costMaps);
		}
	}

	@Override
	public List<MagicPermanent> determineDamageAssignmentOrderAttacker(List<MagicPermanent> blockers) {
		LOGGER.debug("{} determineDamageAssignmentOrderAttacker({})", player, blockers);
		LOGGER.debug("{} -> Actions: [Angriffsreihenfolge bestimmen]", player);
		return blockers;
	}

	@Override
	public void determineInput(PlayerState ps) {

	}

	@Override
	public IsPlayer getPlayer() {
		return player;
	}

	public void input(MagicCard object, ZoneType zoneType) {
		if (zoneType.equals(ZoneType.HAND)) {
			// Karte in Hand geklickt

			MagicCard magicCard = object;
			if (getPlayer().isDiscarding()) {

				getPlayer().fireDiscard(magicCard);

			} else {

				if (magicCard.isLand()) {
					getPlayer().firePlayLand(magicCard);
				} else {
					getPlayer().fireCastSpell(magicCard);
				}

			}

		} else if (zoneType.equals(ZoneType.BATTLEFIELD)) {
			// Karte auf Spielfeld geklickt

			MagicPermanent magicPermanent = (MagicPermanent) object;

			getPlayer().fireActivateActivatedAbility(determineAbility(magicPermanent.propertyListAbilities()));
		}
	}

	public void setMatch(Match match) {
		this.match = match;
	}

	@Override
	public void setPlayer(IsPlayer player) {
		this.player = player;
	}

	@Override
	public String toString() {
		return "Human";
	}

	private Attack buildAttack(MagicPermanent attacker, List<IsAttackTarget> listAttackTargets) {
		final List<IsAttackTarget> legalAttackTargets = new ArrayList<>();
		for (final IsAttackTarget attackTarget : listAttackTargets) {
			if (attackTarget.chechIsValidAttackTarget(attacker)) {
				legalAttackTargets.add(attackTarget);
			}
		}
		if (legalAttackTargets.size() < 2) {
			return new Attack(attacker, legalAttackTargets.get(0));
		} else {
			return new Attack(attacker, determineAttackTarget(legalAttackTargets));
		}
	}

	private IsManaMap filterCostGoalStackable(List<IsManaMap> listCostMaps) {
		if (listCostMaps.isEmpty()) {
			return new ManaMapDefault();
		} else if (listCostMaps.size() == 1) {
			return listCostMaps.get(0);
		} else {
			return determineCostGoal(listCostMaps);
		}
	}

	private MagicPermanent filterLegalAttackers(List<MagicPermanent> listLegalAttackers) {
		if (listLegalAttackers.size() < 2) {
			return listLegalAttackers.get(0);
		} else {
			return determineAttacker(listLegalAttackers);
		}
	}

	private MagicPermanent filterLegalBlockers(List<MagicPermanent> listLegalBlockers) {
		if (listLegalBlockers.size() < 2) {
			return listLegalBlockers.get(0);
		} else {
			return determineBlocker(listLegalBlockers);
		}
	}

	private MagicPermanent filterManaSources(List<MagicPermanent> listLegalPermanents) {
		final List<MagicPermanent> manaSources = new ArrayList<>();
		for (final MagicPermanent magicPermanent : listLegalPermanents) {
			for (final ActivatedAbility ability : magicPermanent.propertyListAbilities()) {
				if (ability.isManaAbility()) {
					manaSources.add(magicPermanent);
					break;
				}
			}
		}
		if (manaSources.size() < 2) {
			return manaSources.get(0);
		} else {
			return determineCardToActivate(manaSources);
		}
	}
}
