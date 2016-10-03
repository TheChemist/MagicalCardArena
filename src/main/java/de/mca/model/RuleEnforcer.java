package de.mca.model;

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
import de.mca.model.enums.ColorType;
import de.mca.model.enums.PlayerState;
import de.mca.model.enums.ZoneType;
import de.mca.model.interfaces.IsAttackTarget;
import de.mca.model.interfaces.IsManaMap;
import de.mca.model.interfaces.IsPlayer;
import de.mca.model.interfaces.IsStackable;
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

		setStateBasedActions = new SimpleSetProperty<>(FXCollections.observableSet());
	}

	@Subscribe
	public void examineEffectProduceMana(EffectProduceMana effectProduceMana) {
		LOGGER.debug("{} examineEffectProduceMana({})", this, effectProduceMana);
		final IsManaMap manaMap = effectProduceMana.getProduceMap();
		final IsPlayer player = match.getPlayer(effectProduceMana.getPlayerType());

		manaMap.getKeySet().forEach(key -> player.addMana(key, manaMap.get(key)));

		final boolean paying = player.isPaying();
		final boolean isPaid = checkIsPaid(player);
		if (paying && isPaid) {
			// Spieler hat alles bezahlt
			actionEndPayment(player);
		}
	}

	@Subscribe
	public void examinePAActivateAbility(PAActivateAbility playerActionActivatedAbility) {
		LOGGER.debug("{} examinePAActivateAbility({})", this, playerActionActivatedAbility);

		// Führe Aktion aus.
		actionActivateAbility(playerActionActivatedAbility.getSource(), playerActionActivatedAbility.getAbility());
	}

	@Subscribe
	public void examinePACastSpell(PACastSpell playerActionCastSpell) {
		LOGGER.debug("{} examinePACastSpell({})", this, playerActionCastSpell);

		// Führe Aktion aus.
		actionCBeginCastSpell(playerActionCastSpell.getSource(), playerActionCastSpell.getCard());
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

		// TODO: Braucht Anpassung
		TotalCostInformation totalCostInformation = new TotalCostInformation();
		totalCostInformation.setInitalCost(playerActionSelectCostMap.getCostMap());

		actionBeginPayment(player, totalCostInformation);

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

	private void actionActivateAbility(IsPlayer player, ActivatedAbility activatedAbility) {
		LOGGER.debug("{} actionActivateActivatedAbility({}, {})", this, player, activatedAbility);

		if (checkCanActivate(player, activatedAbility)) {
			// Alle Voraussetungen sind erfüllt.

			if (activatedAbility.isManaAbility()) {
				// Manafertigkeit

				/**
				 * Verkürzung für Manafertigkeiten. Effekte werden sofort
				 * generiert, ohne den Umweg über den Stack.
				 **/
				activatedAbility.generateEffects();

				// Zusätzliche Kosten werden bezahlt.
				switch (activatedAbility.getAdditionalCostType()) {
				case NO_ADDITIONAL_COST:
					return;
				case TAP:
					((MagicPermanent) activatedAbility.getSource()).setFlagTapped(true);
					break;
				}

			} else {
				// Andere aktivierbare Fähigkeit

			}

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

	private void actionBeginPayment(IsPlayer player, TotalCostInformation totalCostInformation) {
		LOGGER.debug("{} beginPayment({})", this, player);
		player.setPlayerState(PlayerState.PAYING);

		player.setManaCostGoal(totalCostInformation.getTotalCost());

		match.setFlagIsMatchRunning(true);
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
	 * Beschwört einen Zauberspruch. Dafür werden alle notwendigen Entscheidunge
	 * abgefragt und Voraussetzungen überprüft.
	 *
	 * @see http://magiccards.info/rule/601-casting-spells.html
	 *
	 * @param player
	 *            - Der Spieler, der den Zauberspruch spielt.
	 * @param magicCard
	 *            - Die Karte, die als Zauberspruch auf den Stack gespielt wird.
	 */
	private void actionCBeginCastSpell(IsPlayer player, MagicCard magicCard) {
		LOGGER.debug("{} actionCastSpell({}, {})", this, player, magicCard);
		final MagicSpell spell = factoryMagicSpell.create(magicCard, player.getPlayerType());

		// TODO: Gleiche Prüfung für Soells, Abilites
		if (checkCanCast(player, spell)) {
			// Alle Voraussetzungen sind erfüllt.

			// Neuen Spielerstatus setzen.
			player.setPlayerState(PlayerState.CASTING_SPELL);

			// Karte aus der Hand entfernen und auf den Stack schieben.
			player.removeCard(magicCard, ZoneType.HAND);
			match.pushSpell(spell);

			TotalCostInformation totalCostInformation = new TotalCostInformation();

			if (spell.isModal()) {
				// TODO: Entscheidung Modus
			}

			if (spell.canSplice()) {
				// TODO: Entscheidung, ob er splicen möchte.
				// TODO: Entscheidung: Handkarten zeigen
			}

			if (spell.hasBuyback() || spell.hasKicker()) {
				// TODO: Entscheidung, ob er Kicker bezahlen möchte.
			}

			if (spell.hasVariableCost()) {
				// TODO: Entscheidung Wert für X aus.
			}

			if (spell.hasHybridCost()) {
				// TODO: Entscheidung passende CostMap aus.
			} else {
				// Wähle erste und einzige CostMap aus.
				totalCostInformation.setInitalCost(spell.propertyListCostMaps().get(0));
			}

			if (spell.hasPhyrexianCost()) {
				// TODO: Entscheidung Zahlweise aus.
			}

			if (spell.requiresTarget()) {
				// TODO: Entscheidung Ziele aus.
				/**
				 * Hier stehen noch einige weitere Entscheidungen aus.
				 */
			}

			// TODO: Kosten aggregieren und konsolodieren. (in eig Objekt?)
			if (spell.hasAdditionalCost()) {
				// TODO: Zusätzliche Kosten dem Objekt hinzufügen.
			}

			// Kosten bezahlen.
			if (totalCostInformation.getTotalConvertedCost() == 0 && !totalCostInformation.hasAdditionalCostType()) {
				// Karte kostet nichts.

				// Abschließen.
				finishAction(player);
			} else {
				// Spieler muss Karte bezahlen

				actionBeginPayment(player, totalCostInformation);
			}

		} else {
			// Voraussetzungen sind nicht erfüllt.
			// TODO: Reagiere mit Hinweis an den Spieler.
		}
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

		// Setze Status zurück.
		player.setPlayerState(PlayerState.CASTING_SPELL);

		// Setze Kostenziele etc. zurück.
		player.setManaCostAlreadyPaid(new ManaMapDefault());
		player.setManaCostGoal(new ManaMapDefault());

		finishAction(player);
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

		finishAction(player);
	}

	private boolean checkCanActivate(IsPlayer player, ActivatedAbility activatedAbility) {
		if (activatedAbility.isManaAbility()) {
			// Prüfe Mana-Fähigkeit

			final boolean prioritized = player.isPrioritised();
			final boolean castingSpell = player.isCastingSpell() || player.isPaying();
			final boolean activatingAbility = player.isActivatingAbility();
			LOGGER.debug("{} checkCanActivate({}, {}) = {}", this, player, activatedAbility,
					prioritized || castingSpell || activatingAbility);
			return prioritized || castingSpell || activatingAbility;
		} else {
			// Prüfe andere Fähigkeiten

			return false;
		}
	}

	/**
	 * Prüft, ob die grundlegenden Voraussetzungen für das Beschwören eines
	 * Zauberspruchs erfüllt sind.
	 *
	 * @param player
	 *            - Der Spieler, für den die Prüfung vorgenommen wird.
	 * @param magicSpell
	 *            - Der Zauberspruch, für den die Prüfung vorgenommen wird.
	 * @return true, wenn alle Voraussetzungen erfüllt sind.
	 */
	private boolean checkCanCast(IsPlayer player, MagicSpell magicSpell) {
		final boolean isActivePlayer = match.isPlayerActive(player);
		final boolean currentStepIsMain = match.getCurrentPhase().isMain();
		LOGGER.debug("{} checkCanCast({}, {}) = {}", this, player, magicSpell, (isActivePlayer && currentStepIsMain));
		return isActivePlayer && currentStepIsMain;
	}

	/**
	 * Durchläuft den Stack und ruft für jedes Element resolve(stackable) auf.
	 * Der Stack wird durchlaufen, jedes mal wenn beide Spieler die Priorität
	 * abgegeben haben (rule=405.5.)
	 */
	void processStack() {
		LOGGER.debug("{} processStack()", this);
		final int sizeMagicStack = match.getZoneStack().getSize();

		for (int i = 0; i < sizeMagicStack; i++) {
			final IsStackable stackable = match.getZoneStack().peek();
			if (stackable.isPermanentSpell()) {
				match.addCard(factoryMagicPermanent.create((MagicSpell) stackable), ZoneType.BATTLEFIELD);
			} else {
				stackable.resolve();
			}

			// TODO: Mehre Kreaturen auf dem Stack werden gleichzeitig gelegt.
			match.popSpell();

			match.resetFlagsPassedPriority();
			match.determinePlayerPrioritised();
			match.setFlagNeedPlayerInput(true);
		}
	}

	/**
	 * Prüft, ob noch mindestens n Karten auf der Hand des Spielers sind.
	 *
	 * @return true, wenn der Spieler genau n Karte ablegen kann.
	 */
	private boolean checkCanDiscard(IsPlayer player, int howMany) {
		return player.propertyHandSize().get() >= howMany;
	}

	// TODO: Prüfung ausweiten und wieder reinnehmen.
	// /**
	// * Prüft, ob die Kosten eines Permanents oder einer Fähigkeit bezahlt
	// werden
	// * können. Prüft auf Mana- sowie zusätzlich Kosten.
	// *
	// * @param magicPermanent
	// * das Permanent.
	// * @param act
	// * der Typ der zusätzlichen Kosten
	// * @return true, wenn die zusätzlichen Kosten bezahlt werden können.
	// */
	// private boolean checkCanPay(TotalCostInformation totalCostInformation) {
	//
	// // Prüfe auf zusätzliche Kosten
	// if (totalCostInformation.hasAdditionalCostType()) {
	// switch (totalCostInformation.getAdditionalCostType()) {
	// case NO_ADDITIONAL_COST:
	// return true;
	// case TAP:
	// return !magicPermanent.isFlagTapped();
	// default:
	// return false;
	// }
	// }
	// }

	private boolean checkIsPaid(IsPlayer player) {
		final IsManaMap manaCostAlreadyPaid = player.getManaCostAlreadyPaid();
		final IsManaMap manaCostGoal = player.getManaCostGoal();
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

		final boolean isPaid = manaCostGoal.equals(manaCostAlreadyPaid);
		LOGGER.debug("{} checkIsPaid({}) = {}", this, player, isPaid);
		return isPaid;
	}

	/**
	 * Setze die priority flag beider Spieler zurück, sowie den Spielerstatus
	 * des handelnden Spielers. Danach muss die Priorität neu bestimmt werden.
	 * Zuletzt wird wieder Input benötigt.
	 *
	 * @param player
	 *            - Der Spieler, dessen Aktion beendet wird.
	 */
	private void finishAction(IsPlayer player) {
		match.resetFlagsPassedPriority();
		match.resetPlayerState(player);
		match.determinePlayerPrioritised();
		match.setFlagNeedPlayerInput(true);
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
