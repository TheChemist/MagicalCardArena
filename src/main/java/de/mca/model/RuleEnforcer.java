package de.mca.model;

import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import de.mca.Constants;
import de.mca.PAActivateAbility;
import de.mca.PACastSpell;
import de.mca.PADeclareAttacker;
import de.mca.PADeclareBlocker;
import de.mca.PADiscard;
import de.mca.PASelectCostMap;
import de.mca.PlayerAction;
import de.mca.SAPlayLand;
import de.mca.SpecialAction;
import de.mca.factories.FactoryMagicPermanent;
import de.mca.factories.FactoryMagicSpell;
import de.mca.model.enums.AdditionalCostType;
import de.mca.model.enums.ColorType;
import de.mca.model.enums.PlayerState;
import de.mca.model.enums.ZoneType;
import de.mca.model.interfaces.IsAttackTarget;
import de.mca.model.interfaces.IsManaMap;
import de.mca.model.interfaces.IsPlayer;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.collections.FXCollections;

public class RuleEnforcer {

	/**
	 * Speichert den Logger.
	 */
	private final static Logger LOGGER = LoggerFactory.getLogger("Match");
	/**
	 * Speichert die PermanentFactory zum Erstellen bleibender Karten.
	 */
	private final FactoryMagicPermanent factoryMagicPermanent;
	/**
	 * Speichert die SpellFactory zum Erstellen von Zaubersprüchen.
	 */
	private final FactoryMagicSpell factoryMagicSpell;
	/**
	 * Speichert eine Referenz auf das Match.
	 */
	private Match match;
	/**
	 * Sammelts StateBasedActions. Diese werden zu bestimmten Zeitpunkten
	 * abgearbeitet.
	 */
	private final SetProperty<StateBasedAction> setStateBasedActions;

	@Inject
	RuleEnforcer(EventBus eventBus, FactoryMagicPermanent factoryMagicPermanent, FactoryMagicSpell factoryMagicSpell) {
		eventBus.register(this);
		this.factoryMagicPermanent = factoryMagicPermanent;
		this.factoryMagicSpell = factoryMagicSpell;

		setStateBasedActions = new SimpleSetProperty<>(FXCollections.observableSet(new HashSet<>()));
	}

	@Subscribe
	public void examineMagicEffectProduceMana(EffectProduceMana magicEffectProduceMana) {
		// TODO: Anpassen für Spells, Effekte als EventObjekte?
		LOGGER.debug("{} examineMagicEffectProduceMana({})", this, magicEffectProduceMana);
		final CharacteristicAbility ability = (CharacteristicAbility) magicEffectProduceMana.getSource();
		final MagicPermanent mp = (MagicPermanent) ability.getSource();
		final IsManaMap manaMap = magicEffectProduceMana.getProduceMap();
		final IsPlayer player = match.getPlayer(mp.getPlayerControlling());
		for (final ColorType color : manaMap.getKeySet()) {
			player.addMana(color, manaMap.get(color));
		}
		if (player.isPaying() && checkIsPaid(player)) {
			// Spieler hat alles bezahlt
			actionEndPayment(player);
			match.resetFlagsPassedPriority();
			match.resetPlayerState(player);
		}
	}

	@Subscribe
	public void examinePAActivateAbility(PAActivateAbility playerActionActivatedAbility) {
		LOGGER.debug("{} examinePAActivateAbility({})", this, playerActionActivatedAbility);

		// Führe Aktion aus.
		actionActivateCharacteristicAbility(playerActionActivatedAbility.getSource(),
				playerActionActivatedAbility.getAbility());
	}

	@Subscribe
	public void examinePACastSpell(PACastSpell playerActionCastSpell) {
		LOGGER.debug("{} examinePACastSpell({})", this, playerActionCastSpell);
		final IsPlayer player = playerActionCastSpell.getSource();
		final MagicCard magicCard = playerActionCastSpell.getCard();
		final MagicSpell spell = factoryMagicSpell.create(magicCard);
		if (match.checkCanCast(player, spell)) {
			actionCastSpell(player, spell);
			if (spell.getConvertedManaCost() <= 0) {
				// Karte kostet nichts
				match.resetFlagsPassedPriority();
				match.resetPlayerState(player);
			}
			match.setFlagNeedPlayerInput(true);
		}
	}

	@Subscribe
	public void examinePADeclareAttacker(PADeclareAttacker playerActionDeclareAttackers) {
		LOGGER.debug("{} examinePADeclareAttacker({})", this, playerActionDeclareAttackers);
		match.declareAttacker(playerActionDeclareAttackers.getAttack());
	}

	@Subscribe
	public void examinePADeclareBlocker(PADeclareBlocker playerActionDeclareBlockers) {
		LOGGER.debug("{} examinePADeclareAttacker({})", this, playerActionDeclareBlockers);
		match.declareBlocker(playerActionDeclareBlockers.getAttackIndex(), playerActionDeclareBlockers.getBlocker());
	}

	@Subscribe
	public void examinePADiscard(PADiscard playerActionDiscard) {
		LOGGER.debug("{} examinePADicard({})", this, playerActionDiscard);
		IsPlayer player = playerActionDiscard.getSource();

		// Werfe Karte ab
		actionDiscard(player, playerActionDiscard.getMagicCard());

		// Setze Spielerstatus zurück
		match.setFlagNeedPlayerInput(false);
		match.resetPlayerState(player);
	}

	@Subscribe
	public void examinePASelectCostMap(PASelectCostMap playerActionSelectCostMap) {
		LOGGER.debug("{} examinePASelectCostMap({})", this, playerActionSelectCostMap);
		final IsPlayer player = playerActionSelectCostMap.getSource();
		player.setManaCostGoal(playerActionSelectCostMap.getCostMap());
		actionBeginPayment(player);
		match.setFlagNeedPlayerInput(true);
	}

	@Subscribe
	public void examinePlayerAction(PlayerAction playerAction) {
		LOGGER.debug("{} examinePlayerAction({})", this, playerAction);
		final IsPlayer player = playerAction.getSource();
		switch (playerAction.getPlayerActionType()) {
		case CONCEDE:
			actionConcede();
			break;
		case PASS_PRIORITY:
			actionPassPriority(player);
			break;
		case END_DECLARE_ATTACKERS:
			actionEndDeclareAttackers(player);
			if (match.getTotalAttackers() == 0) {
				match.skipStepDeclareBlockers();
				match.skipStepCombatDamage();
			}
			break;
		case END_DECLARE_BLOCKERS:
			actionEndDeclareBlockers(player);
			break;
		default:
			break;
		}
	}

	@Subscribe
	public void examineSpecialAction(SpecialAction sa) {
		LOGGER.debug("{} examineSpecialAction({})", this, sa);
		final IsPlayer player = sa.getSource();
		switch (sa.getSpecialActionType()) {
		case PLAY_A_LAND:
			final SAPlayLand sapl = (SAPlayLand) sa;
			final MagicCard landCard = sapl.getLandCard();
			if (match.checkCanPlayLandCard(player)) {
				actionPlayLand(player, landCard);
			}
			break;
		}
	}

	@Subscribe
	public void examineStateBasedAction(StateBasedAction stateBasedAction) {
		LOGGER.debug("{} examineStateBasedAction({})", this, stateBasedAction);
		setStateBasedActions.add(stateBasedAction);
	}

	@Subscribe
	public void examineTurnBasedAction(TurnBasedAction tba) {
		LOGGER.debug("{} examineTurnBasedAction({})", this, tba);
		final IsPlayer playerActive = match.getPlayerActive();
		final IsPlayer playerNonactive = match.getPlayerNonactive();
		switch (tba.getTurnBasedActionType()) {
		case BEGINNING_OF_COMBAT_STEP:
			playerActive.setPlayerState(PlayerState.ATTACKING);
			playerNonactive.setPlayerState(PlayerState.DEFENDING);
			break;
		case CLEANUP:
			for (final MagicPermanent mp : match.getCardsBattlefield()) {
				mp.setDamage(0);
			}
			break;
		case COMBAT_DAMAGE_ASSIGNMENT:
			for (final Attack attack : match.getListAttacks()) {
				final MagicPermanent attacker = attack.getSource();
				final IsAttackTarget attackTarget = attack.getTarget();
				if (attacker.isFlagBlocked()) {
					for (final MagicPermanent blocker : attack.propertyListBlockersSorted()) {
						final int blockerPower = blocker.getPower();
						final int attackerPower = attacker.getPower();
						if (blockerPower > 0) {
							attacker.setDamage(blockerPower);
						}
						if (attackerPower > 0) {
							blocker.setDamage(attackerPower);
						}
					}
				} else {
					attackTarget.attackedBy(attacker);
				}
			}
			break;
		case COMBAT_DAMAGE_DEALING:
			// TODO: Schaden korrekt verteilen
			// for (final Attack attack : match.propertyListAttacks()) {
			// for (final MagicPermanent creature : attack.getCombatants())
			// {
			//
			// }
			// }
			break;
		case DECLARE_ATTACKER:
			actionBeginDeclareAttackers(playerActive);
			break;
		case DECLARE_BLOCKER:
			actionBeginDeclareBlockers(playerNonactive);
			break;
		case DISCARD:
			final int originalHandSize = playerActive.propertyHandSize().get();
			for (int i = 0; i < (originalHandSize - Constants.HAND_SIZE); i++) {
				playerActive.setPlayerState(PlayerState.DISCARDING);
				match.setFlagNeedPlayerInput(true);
			}
			break;
		case DRAW:
			actionDraw(playerActive);
			break;
		case CLEAR_MANA_POOLS:
			playerActive.removeManaAll();
			playerNonactive.removeManaAll();
			break;
		case UNTAP:
			for (final MagicPermanent magicPermanent : match.getListControlledCards(playerActive)) {
				magicPermanent.setFlagTapped(false);
				magicPermanent.setFlagSummoningSickness(false);
			}
			break;
		case DECLARE_DAMAGE_ASSIGNMENT_ORDER_ATTACKER:
			for (final Attack attack : match.getListAttacks()) {
				final List<MagicPermanent> blockers = attack.propertyListBlockers();
				if (attack.getSource().isFlagBlocked() && blockers.size() > 1) {
					match.setFlagNeedPlayerInput(true);
					// attack.setBlockers(playerActive.announceDamageAssignmentOrderAttacker(blockers));
				}
			}
			break;
		case DECLARE_DAMAGE_ASSIGNMENT_ORDER_BLOCKER:
			// TODO: Wird erst bei mehreren Blockzielen relevant.
			break;
		case PHASING:
			break;
		}
	}

	@Override
	public String toString() {
		return match.toString();
	}

	/**
	 * Prüft, ob die zusätzlichen Kosten eines Permanents bezahlt werden können.
	 *
	 * @param mp
	 *            das Permanent.
	 * @param act
	 *            der Typ der zusätzlichen Kosten
	 * @return true, wenn die zusätzlichen Kosten bezahlt werden können.
	 */
	private boolean checkCanPay(MagicPermanent mp, AdditionalCostType act) {
		switch (act) {
		case NO_ADDITIONAL_COST:
			return true;
		case TAP:
			return !mp.isFlagTapped();
		default:
			return false;
		}
	}

	private boolean checkCanActivate(IsPlayer p, ActivatedAbility aa) {
		if (aa.isManaAbility()) {
			final boolean prioritised = p.isPrioritised();
			final boolean castingSpell = p.isCastingSpell() || p.isPaying();
			final boolean activatingAbility = p.isActivatingAbility();
			LOGGER.debug("{} checkCanActivate({}, {}) = {}", this, p, aa,
					prioritised || castingSpell || activatingAbility);
			return prioritised || castingSpell || activatingAbility;
		}
		return false;
	}

	private void actionActivateCharacteristicAbility(IsPlayer player, ActivatedAbility activatedAbility) {
		LOGGER.debug("{} actionActivateActivatedAbility({}, {})", this, player, activatedAbility);
		if (checkCanActivate(player, activatedAbility) && checkCanPay((MagicPermanent) activatedAbility.getSource(),
				activatedAbility.getAdditionalCostType())) {
			// Alle Voraussetungen sind erfüllt.

			player.setPlayerState(PlayerState.ACTIVATING_ABILITY);

			// Alle Effekte werden genertiert.
			activatedAbility.generateEffects();

			// Zusätzliche Kosten werden bezahlt.
			switch (activatedAbility.getAdditionalCostType()) {
			case NO_ADDITIONAL_COST:
				return;
			case TAP:
				((MagicPermanent) activatedAbility.getSource()).setFlagTapped(true);
				break;
			}

			match.resetFlagsPassedPriority();
			match.resetPlayerState(player);
			match.determinePlayerPrioritised();
			match.setFlagNeedPlayerInput(true);
		} else {
			// Voraussetzungen sind nicht erfüllt.
			// TODO: Reagiere mit Hinweis an den Spieler.
		}
	}

	private void actionBeginDeclareAttackers(IsPlayer player) {
		LOGGER.debug("{} actionBeginDeclareAttackers({})", this, player);
		player.setFlagDeclareAttackers(true);

		match.setFlagNeedPlayerInput(true);
	}

	private void actionBeginDeclareBlockers(IsPlayer player) {
		LOGGER.debug("{} actionBeginDeclareAttackers({})", this, player);
		player.setFlagDeclareBlockers(true);

		match.setFlagNeedPlayerInput(true);
	}

	private void actionBeginPayment(IsPlayer player) {
		LOGGER.debug("{} beginPayment({})", this, player);
		player.setPlayerState(PlayerState.PAYING);

		final IsManaMap manaCostAlreadyPaid = player.getManaCostAlreadyPaid();
		final IsManaMap manaCostGoal = player.getManaCostAlreadyPaid();
		final IsManaMap manaPool = player.getManaPool();

		final ColorType clm = ColorType.NONE;
		// Bezahle farbloses mit farblosem Mana
		int need = manaCostGoal.get(clm);
		int have = manaPool.get(clm);
		int value = have > need ? need : have;
		if (have > 0) {
			manaCostAlreadyPaid.add(clm, value);
			player.removeMana(clm, value);
		}
		// Bezahle farbiges mit farbigem Mana
		for (final ColorType key : manaCostGoal.getKeySet()) {
			if (manaPool.containsKey(key)) {
				need = manaCostGoal.get(key);
				have = manaPool.get(key);
				value = have > need ? need : have;
				manaCostAlreadyPaid.add(key, value);
				player.removeMana(key, value);
			}
		}
		// Bezahle fabloses mit farbigem Mana
		need = manaCostGoal.get(clm) - manaCostAlreadyPaid.get(clm);
		if (need > 0) {
			final int manaLeft = manaPool.getTotalColoredMana();
			if (need >= manaLeft) {
				manaCostAlreadyPaid.add(clm, manaLeft);
				player.removeManaAll();
			} else {
				for (final ColorType key : manaPool.getKeySet()) {
					value = manaPool.get(key);
					manaCostAlreadyPaid.add(clm, value);
					player.removeMana(key, value);
				}
			}
		}
	}

	/**
	 * Der Spieler legt eine Karte aus dem Spiel in den Friedhof ab.
	 *
	 * @param card
	 *            die Karte, die aus der Kampfzone in den Friedhof abgelegt
	 *            wird.
	 */
	private void actionBury(IsPlayer player, MagicPermanent magicPermanent) {
		LOGGER.debug("{} actionBury({}, {})", this, player, magicPermanent);
		match.removeCard(magicPermanent, ZoneType.BATTLEFIELD);
		player.addCard(new MagicCard(magicPermanent), ZoneType.GRAVEYARD);
	}

	/**
	 * Diese Methode wird vom Spiel aufgerufen, nachdem der Spieler eine Karte
	 * gewählt hat, die in diesem Moment regelkonform gespielt werden kann.
	 * Beschwört einen Zauberspruch (rule = 601.).
	 *
	 * @param magicCard
	 *            Die Karte, die als Zauberspruch auf den Stack gespielt wird.
	 */
	private void actionCastSpell(IsPlayer player, MagicCard magicCard) {
		LOGGER.debug("{} actionCastSpell({}, {})", this, player, magicCard);

		// Neuen Spielerstatus setzen.
		player.setPlayerState(PlayerState.CASTING_SPELL);

		// Karte aus der Hand entfernen und auf den Stack schieben.
		player.removeCard(magicCard, ZoneType.HAND);
		match.pushSpell(factoryMagicSpell.create(magicCard));
	}

	private void actionConcede() {
		match.setFlagNeedPlayerInput(false);
		match.setFlagIsMatchRunning(false);
	}

	/**
	 * Der Spieler legt eine Karte aus der Hand in den Friedhof ab.
	 *
	 * @param card
	 *            Die abzulegende Karte.
	 */
	private void actionDiscard(IsPlayer player, MagicCard magicCard) {
		LOGGER.debug("{} actionDiscard({}, {})", this, player, magicCard);
		player.removeCard(magicCard, ZoneType.HAND);
		player.addCard(magicCard, ZoneType.GRAVEYARD);
	}

	/**
	 * Der Spieler legt alle verbleibende Karten aus der Hand in den Friedhof
	 * ab.
	 */
	private void actionDiscardAll(IsPlayer player) {
		LOGGER.debug("{} actionDiscardAll({})", this, player);
		final List<MagicCard> cardList = player.getCardsHand();
		player.removeAllCards(ZoneType.HAND);
		player.addAllCards(cardList, ZoneType.GRAVEYARD);
	}

	/**
	 * Der Spieler legt eine zufällige Karte aus der Hand in den Friedhof ab.
	 */
	private void actionDiscardRandom(IsPlayer player) {
		LOGGER.debug("{} actionDiscardRandom({})", this, player);
		final List<MagicCard> zoneHand = player.getCardsHand();
		actionDiscard(player, zoneHand.get(new Random().nextInt(zoneHand.size())));
	}

	/**
	 * Der Spieler legt n zufällige Karten aus der Hand in den Friedhof ab.
	 *
	 * @param howMany
	 *            die Anzahl abzulegender Karten.
	 */
	private void actionDiscardRandom(IsPlayer player, int howMany) {
		LOGGER.debug("{} actionDiscardRandom({}, {})", this, player, howMany);
		if (checkCanDiscard(player, howMany)) {
			for (int i = 0; i < howMany; i++) {
				actionDiscardRandom(player);
			}
		} else {
			actionDiscardAll(player);
		}
	}

	/**
	 * Der Spieler zieht eine Karte.
	 */
	private void actionDraw(IsPlayer player) {
		LOGGER.debug("{} actionDraw({})", this, player);
		player.actionDraw();
	}

	/**
	 * Der Spieler zieht alle verbleibenden Karten.
	 */
	private void actionDrawAll(IsPlayer player) {
		LOGGER.debug("{} actionDrawAll({})", this, player);
		final List<MagicCard> cardList = player.getLibraryCards();
		player.removeAllCards(ZoneType.LIBRARY);
		player.addAllCards(cardList, ZoneType.HAND);
	}

	private void actionEndDeclareAttackers(IsPlayer player) {
		LOGGER.debug("{} actionEndDeclareAttackers({})", this, player);
		player.setFlagDeclareAttackers(false);
	}

	private void actionEndDeclareBlockers(IsPlayer player) {
		LOGGER.debug("{} actionEndDeclareBlockers({})", this, player);
		player.setFlagDeclareBlockers(false);
	}

	private void actionEndPayment(IsPlayer player) {
		LOGGER.debug("{} endPayment()", this);
		player.setPlayerState(PlayerState.CASTING_SPELL);
		player.setManaCostAlreadyPaid(new ManaMapDefault());
		player.setManaCostGoal(new ManaMapDefault());
	}

	/**
	 * Verbannt eine Karte aus der Hand ins Exil.
	 *
	 * @param card
	 *            Die zu verbannende Karte.
	 */
	private void actionExile(IsPlayer player, MagicCard magicCard) {
		LOGGER.debug("{} actionExile({}, {})", this, player, magicCard);
		player.removeCard(magicCard, ZoneType.HAND);
		match.addCard(magicCard, ZoneType.EXILE);
	}

	/**
	 * Verbannt ein Permanent aus dem Spielfeld ins Exil.
	 *
	 * @param permanent
	 *            Das zu verbannende Permanent.
	 */
	private void actionExile(MagicPermanent magicPermanent) {
		LOGGER.debug("{} actionExile({})", this, magicPermanent);
		match.removeCard(magicPermanent, ZoneType.BATTLEFIELD);
		match.addCard(new MagicCard(magicPermanent), ZoneType.EXILE);
	}

	private void actionPassPriority(IsPlayer player) {
		LOGGER.debug("{} actionPassPriority({})", this, player);
		player.setFlagPassedPriority(true);
		match.resetPlayerState(player);
		match.setFlagNeedPlayerInput(false);
	}

	private void actionPlayLand(IsPlayer player, MagicCard landCard) {
		LOGGER.debug("{} actionPlayLand({}, {})", this, player, landCard);
		player.setPlayerState(PlayerState.TAKING_SPECIAL_ACTION);
		player.setFlagPlayedLand(true);

		player.removeCard(landCard, ZoneType.HAND);
		match.addCard(factoryMagicPermanent.create(landCard), ZoneType.BATTLEFIELD);

		/**
		 * Setze die priority flag beider Spieler zurück, sowie den
		 * Spielerstatus des handelnden Spielers. Danach muss die Priorität neu
		 * bestimmt werden. Zuletzt wird wieder Input benötigt.
		 */
		match.resetFlagsPassedPriority();
		match.resetPlayerState(player);
		match.determinePlayerPrioritised();
		match.setFlagNeedPlayerInput(true);
	}

	/**
	 * Prüft, ob noch mindestens n Karten auf der Hand des Spielers sind.
	 *
	 * @return true, wenn der Spieler genau n Karte ablegen kann.
	 */
	private boolean checkCanDiscard(IsPlayer player, int howMany) {
		return player.propertyHandSize().get() >= howMany;
	}

	private boolean checkIsPaid(IsPlayer player) {
		LOGGER.debug("{} checkIsPaid({})", this, player);
		return player.getManaCostGoal().equals(player.getManaCostAlreadyPaid());
	}

	/**
	 * Der Spieler zieht n Karten.
	 *
	 * @param howMany
	 *            die Anzahl zu ziehender Karten.
	 */
	void actionDraw(IsPlayer player, int howMany) {
		LOGGER.debug("{} actionDraw({}, {})", this, player, howMany);
		for (int i = 0; i < howMany; i++) {
			actionDraw(player);
		}
	}

	/**
	 * Arbeitet die StateBasedActions ab, die sich während der Zeit seit der
	 * letzten Prüfung angesammelt haben. Wird aufgerufen, bevor die Priorität
	 * neu bestimmt wird.
	 */
	void processStateBasedActions() {
		LOGGER.debug("{} processStateBasedActions()", this);
		for (final StateBasedAction sba : setStateBasedActions) {
			switch (sba.getStateBasedActionType()) {
			case CREATURE_TOUGHNESS_ZERO:
				final SBACreatureToughnessZero sbactz = (SBACreatureToughnessZero) sba;
				actionBury(match.getPlayer(sbactz.getPlayerControlling()), (MagicPermanent) sba.getSource());
				break;
			case PLAYER_CANT_DRAW:
				match.setFlagIsMatchRunning(false);
				break;
			case PLAYER_LIFE_ZERO:
				match.setFlagIsMatchRunning(false);
				break;
			default:
				break;
			}
		}
	}

	void setMatch(Match match) {
		this.match = match;
	}

}
