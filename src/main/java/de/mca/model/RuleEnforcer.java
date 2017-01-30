package de.mca.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import de.mca.Constants;
import de.mca.PAActivatePermanent;
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
			structureEndPayment(player);
		}
	}

	@Subscribe
	public void examinePAActivateAbility(PAActivatePermanent playerActionActivatedAbility) {
		LOGGER.debug("{} examinePAActivateAbility({})", this, playerActionActivatedAbility);

		// Führe Aktion aus.
		structureBeginActivatePermanent(playerActionActivatedAbility.getSource(),
				playerActionActivatedAbility.getAbility());
	}

	@Subscribe
	public void examinePACastSpell(PACastSpell playerActionCastSpell) {
		LOGGER.debug("{} examinePACastSpell({})", this, playerActionCastSpell);

		// Führe Aktion aus.
		structureBeginCastSpell(playerActionCastSpell.getSource(), playerActionCastSpell.getCard());
	}

	@Subscribe
	public void examinePADeclareAttacker(PADeclareAttacker playerActionDeclareAttacker) {
		LOGGER.debug("{} examinePADeclareAttacker({})", this, playerActionDeclareAttacker);

		// Führe Aktion aus.
		actionDeclareAttacker(playerActionDeclareAttacker.getSource(), playerActionDeclareAttacker.getAttacker());
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

		// TODO MID Braucht Anpassung
		TotalCostInformation totalCostInformation = new TotalCostInformation();
		totalCostInformation.setInitalCost(playerActionSelectCostMap.getCostMap());

		structureBeginPayment(player, totalCostInformation);

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
			structureEndDeclareAttackers(player);
			if (match.getTotalAttackers() == 0) {
				match.skipStepDeclareBlockers();
				match.skipStepCombatDamage();
			}
			break;
		case END_DECLARE_BLOCKERS:
			structureEndDeclareBlockers(player);
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
			playerActive.setPlayerState(PlayerState.SELECTING_ATTACKER);
			playerNonactive.setPlayerState(PlayerState.DEFENDING);
			break;
		case CLEANUP:
			turnBasedCleanup();
			break;
		case COMBAT_DAMAGE_ASSIGNMENT:
			turnBasedCombatDamageAssignment();

			// Hier wird die nächste TBA abgefeuert.
			((Step) tba.getSource()).fireCombatDamageDealing();
			break;
		case COMBAT_DAMAGE_DEALING:
			turnBasedCombatDamageDealing();
			break;
		case DECLARE_ATTACKER:
			structureBeginDeclareAttackers(playerActive);
			break;
		case DECLARE_BLOCKER:
			structureBeginDeclareBlockers(playerNonactive);
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
			structureBeginDamageAssignmentAttacker(playerActive);
			structureEndDamageAssignmentAttacker(playerActive);

			// Hier wird die nächste TBA abgefeuert.
			((Step) tba.getSource()).fireDeclareDamageAssignmentBlocker();
			break;
		case DECLARE_DAMAGE_ASSIGNMENT_ORDER_BLOCKER:
			structureBeginDamageAssignmentBlocker(playerNonactive);
			structureEndDamageAssignmentBlocker(playerNonactive);

			// Hier wird die nächste TBA abgefeuert.
			((Step) tba.getSource()).fireCombatDamageAssignment();
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
		LOGGER.debug("{} actionActivateAbility({}, {})", this, player, activatedAbility);

		if (activatedAbility.isManaAbility()) {
			// Manafertigkeit

			/**
			 * Verkürzung für Manafertigkeiten. Effekte werden sofort generiert,
			 * ohne den Umweg über den Stack.
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

	private void actionConcede() {
		match.setFlagNeedPlayerInput(false);
		match.setFlagIsMatchRunning(false);
	}

	/**
	 * Wird aufgerufen, wenn der Spieler einen Angreifer auswählt. Zuerst wird
	 * überprüft, ob die Kreatur grundsätzlich angreifen kann. Sind alle
	 * Voraussetzungen erfüllt, wird ein gültiges Angriffsziel ermittelt und der
	 * Angriff zur späteren Durchführung hinterlegt.
	 *
	 * @param playerActive
	 *            Der aktive Spieler.
	 * @param attacker
	 *            Die angreifende Kreatur.
	 */
	private void actionDeclareAttacker(IsPlayer playerActive, MagicPermanent attacker) {
		LOGGER.debug("{} actionDeclareAttacker({}, {})", this, playerActive, attacker);

		if (checkCanAttack(playerActive, attacker)) {
			// Alle Voraussetzungen sind erfüllt.

			List<IsAttackTarget> validAttackTargets = new ArrayList<>();
			for (IsAttackTarget attackTarget : match.getListAttackTargets()) {
				if (attackTarget.chechIsValidAttackTarget(attacker)) {
					validAttackTargets.add(attackTarget);
				}
			}

			if (validAttackTargets.size() <= 0) {
				// Keine gültigen Ziele vorhanden.
			} else if (validAttackTargets.size() == 1) {
				// Wähle einzig gültiges Ziel automatisch.

				match.addAttack(new Attack(attacker, validAttackTargets.get(0)));
			} else {
				// TODO HIGH Entscheidung: Ziel auswählen

			}
		} else {
			// Voraussetzungen sind nicht erfüllt.
			// TODO MID Reagiere mit Hinweis an den Spieler.
		}
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

		structureFinishAction(player);
	}

	private boolean checkCanActivatePermanent(IsPlayer player, MagicPermanent magicPermanent) {
		final boolean isUntapped = !magicPermanent.getFlagIsTapped();
		final boolean prioritized = player.isPrioritised();
		final boolean castingSpell = player.isCastingSpell() || player.isPaying();
		final boolean activatingAbility = player.isActivatingAbility();
		LOGGER.debug("{} checkCanActivatePermanent({}, {}) = {}", this, player, magicPermanent,
				prioritized || castingSpell || activatingAbility);
		return prioritized || castingSpell || activatingAbility || isUntapped;
	}

	/**
	 * Prüft, ob die grundlegenden Voraussetzungen für einen Angriff erfüllt
	 * sind.
	 *
	 * @see http://magiccards.info/rule/508-declare-attackers-step.html
	 * @param playerActive
	 *            Der aktive Spieler.
	 * @param attacker
	 *            Der Angreifer.
	 * @return true, wenn der Angreifer grundsätzlich angreifen kann.
	 */
	private boolean checkCanAttack(IsPlayer playerActive, MagicPermanent attacker) {
		final boolean isTapped = attacker.getFlagIsTapped();
		final boolean hasSummoningSickness = attacker.getFlagHasSummoningSickness();
		LOGGER.trace("{} checkCanAttack({}, {}) = {}", this, playerActive, attacker,
				(!isTapped && !hasSummoningSickness));
		return !isTapped && !hasSummoningSickness;
	}

	/**
	 * Prüft, ob die grundlegenden Voraussetzungen für das Beschwören eines
	 * Zauberspruchs erfüllt sind.
	 *
	 * @param player
	 *            Der priorisierte Spieler.
	 * @param magicSpell
	 *            Der Zauberspruch, für den die Prüfung vorgenommen wird.
	 * @return true, wenn alle Voraussetzungen erfüllt sind.
	 */
	private boolean checkCanCast(IsPlayer player, MagicSpell magicSpell) {
		final boolean isActivePlayer = match.isPlayerActive(player);
		final boolean currentStepIsMain = match.getCurrentPhase().isMain();
		LOGGER.trace("{} checkCanCast({}, {}) = {}", this, player, magicSpell, (isActivePlayer && currentStepIsMain));
		return isActivePlayer && currentStepIsMain;
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
	 * Wird aufgerufen, wenn der Spieler ein Permanent anklickt. Es wird
	 * geprüft, ob das Permanent grundsätzlich aktiviert werden kann. Sind alle
	 * Voraussetzungen erfüllt, wird die zu aktivierende Fähigkeit bestimmt und
	 * fpr die Aktivierung übergeben.
	 *
	 * @param player
	 *            der priorisierte Spieler.
	 * @param magicPermanent
	 *            das aktivierte Permanent.
	 */
	private void structureBeginActivatePermanent(IsPlayer player, MagicPermanent magicPermanent) {
		LOGGER.debug("{} structureBeginActivatePermanent({}, {})", this, player, magicPermanent);

		if (checkCanActivatePermanent(player, magicPermanent)) {
			// Alle Voraussetungen sind erfüllt.

			List<ActivatedAbility> listActivatedAbities = magicPermanent.propertyListAbilities();

			if (listActivatedAbities.size() <= 0) {
				// Keine Fähigkeiten vorhanden
			} else if (listActivatedAbities.size() == 1) {
				// Wähle einzige Fähigkeit automatisch

				actionActivateAbility(player, listActivatedAbities.get(0));
			} else {
				// TODO HIGH Entscheidung: Fähigkeit auswählen

			}
		} else {
			// Voraussetzungen sind nicht erfüllt.
			// TODO MID Reagiere mit Hinweis an den Spieler.
		}
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
	private void structureBeginCastSpell(IsPlayer player, MagicCard magicCard) {
		LOGGER.debug("{} actionCastSpell({}, {})", this, player, magicCard);
		final MagicSpell spell = factoryMagicSpell.create(magicCard, player.getPlayerType());

		if (checkCanCast(player, spell)) {
			// Alle Voraussetzungen sind erfüllt.

			// Neuen Spielerstatus setzen.
			player.setPlayerState(PlayerState.CASTING_SPELL);

			// Karte aus der Hand entfernen und auf den Stack schieben.
			player.removeCard(magicCard, ZoneType.HAND);
			match.pushSpell(spell);

			TotalCostInformation totalCostInformation = new TotalCostInformation();

			if (spell.isModal()) {
				// TODO LOW Entscheidung: Modus wählen.
			}

			if (spell.canSplice()) {
				// TODO LOW Entscheidung: Splicen.
			}

			if (spell.hasBuyback() || spell.hasKicker()) {
				// TODO LOW Entscheidung: Kicker.
			}

			if (spell.hasVariableCost()) {
				// TODO LOW Entscheidung: Wähle Wert für X aus.
			}

			if (spell.hasHybridCost()) {
				// TODO HIGH Entscheidung: CostMap auswählen.
			} else {
				// Wähle erste und einzige CostMap aus.
				totalCostInformation.setInitalCost(spell.propertyListCostMaps().get(0));
			}

			if (spell.hasPhyrexianCost()) {
				// TODO LOW Entscheidung: Wähle Zahlweise aus.
			}

			if (spell.requiresTarget()) {
				// TODO MID Entscheidung: Wähle Ziele aus.
				/**
				 * Hier stehen noch einige weitere Entscheidungen aus.
				 */
			}

			if (spell.hasAdditionalCost()) {
				// TODO LOW Zusätzliche Kosten dem Objekt hinzufügen.
			}

			// Kosten bezahlen.
			if (totalCostInformation.getTotalConvertedCost() == 0 && !totalCostInformation.hasAdditionalCostType()) {
				// Karte kostet nichts.

				// Abschließen.
				structureFinishAction(player);
			} else {
				// Spieler muss Karte bezahlen

				structureBeginPayment(player, totalCostInformation);
			}

		} else {
			// Voraussetzungen sind nicht erfüllt.
			// TODO MID Reagiere mit Hinweis an den Spieler.
		}
	}

	private void structureBeginDamageAssignmentAttacker(IsPlayer playerActive) {
		LOGGER.debug("{} actionBeginDamageAssingmentAttacker({})", this, playerActive);
		playerActive.setPlayerState(PlayerState.ASSINGING_DAMAGE_ORDER_ATTACKER);

		for (final Attack attack : match.getListAttacks()) {
			final List<MagicPermanent> blockers = attack.propertyListBlockers();
			if (attack.getSource().isFlagBlocked() && blockers.size() > 1) {
				// TODO HIGH Hier muss eigentlich eine Auswahl des Spielers
				// erfolgen
				attack.setBlockers(attack.propertyListBlockers());
			} else {
				LOGGER.debug("{} actionBeginDamageAssingmentAttacker({}) -> Schadensverteilung nicht notwendig!", this,
						playerActive);
			}
		}
	}

	// TODO HIGH Dummy-Methode implementieren
	private void structureBeginDamageAssignmentBlocker(IsPlayer playerNonactive) {
		// TODO LOW Wird erst bei mehreren Blockzielen relevant.
		LOGGER.debug("{} actionBeginDamageAssignmentBlocker({})", this, playerNonactive);
		playerNonactive.setPlayerState(PlayerState.ASSIGNING_DAMAGE_ORDER_BLOCKERS);
	}

	/**
	 * Setzt die flagDeclareAttackers auf true und verlangt vom Spieler eine
	 * Reaktion (flagNeedPlayerInput = true).
	 *
	 * @param playerActive
	 *            der aktive Spieler.
	 */
	private void structureBeginDeclareAttackers(IsPlayer playerActive) {
		LOGGER.debug("{} actionBeginDeclareAttackers({})", this, playerActive);
		playerActive.setPlayerState(PlayerState.SELECTING_ATTACKER);
		playerActive.setFlagDeclareAttackers(true);

		match.setFlagNeedPlayerInput(true);
	}

	/**
	 * Setzt die flagDeclareBlockers auf true und verlangt vom Spieler eine
	 * Reaktion (flagNeedPlayerInput = true).
	 *
	 * @param playerNonactive
	 *            der nichtaktive Spieler.
	 */
	private void structureBeginDeclareBlockers(IsPlayer playerNonactive) {
		LOGGER.debug("{} actionBeginDeclareBlockers({})", this, playerNonactive);
		playerNonactive.setPlayerState(PlayerState.DEFENDING);
		playerNonactive.setFlagDeclareBlockers(true);

		match.setFlagNeedPlayerInput(true);
	}

	/**
	 * Setzt den Spielerstatus eines Spielers auf PAYING. Zudem wird dem Spieler
	 * ein Objekt übergeben, in dem alle Informationen über den aktuellen
	 * Bezahlvorgang gespeichert sind. Zuletzt wird dem bezahlenden Spieler eine
	 * Reaktion abverlangt.
	 *
	 * @param player
	 *            der bezahlende Spieler.
	 * @param totalCostInformation
	 *            Hilfsobjekt zur Kapselung aller Informationen zu einem
	 *            Bezahlvorgang.
	 */
	private void structureBeginPayment(IsPlayer player, TotalCostInformation totalCostInformation) {
		LOGGER.debug("{} beginPayment({})", this, player);
		player.setPlayerState(PlayerState.PAYING);
		player.setManaCostGoal(totalCostInformation.getTotalCost());

		match.setFlagNeedPlayerInput(true);
	}

	/**
	 * Setzt den Spielerstatus des aktiven Spielers zurück auf
	 * SELECTING_ATTACKER.
	 *
	 * @param playerActive
	 *            der aktive Spieler.
	 */
	private void structureEndDamageAssignmentAttacker(IsPlayer playerActive) {
		LOGGER.debug("{} actionEndDamageAssignmentAttacker({})", this, playerActive);
		playerActive.setPlayerState(PlayerState.SELECTING_ATTACKER);
	}

	/**
	 * Setzt den Spielerstatus des nichtaktiven Spielers zurück auf DEFENDING.
	 *
	 * @param playerNonactive
	 *            der nichtaktive Spieler.
	 */
	// TODO HIGH Dummy-Methode analog Angriff implementieren.
	private void structureEndDamageAssignmentBlocker(IsPlayer playerNonactive) {
		// TODO LOW Wird erst bei mehreren Blockzielen relevant.
		LOGGER.debug("{} actionEndDamageAssignmentAttacker({})", this, playerNonactive);
		playerNonactive.setPlayerState(PlayerState.DEFENDING);
	}

	/**
	 * Setzt die flagDeclareAttackers auf false. Danach wird die Priorität neu
	 * bestimmt und eine Eingabe vom priorisierten Spieler erwartet.
	 *
	 * @param player
	 *            der aktive Spieler.
	 */
	private void structureEndDeclareAttackers(IsPlayer player) {
		LOGGER.debug("{} actionEndDeclareAttackers({})", this, player);
		player.setFlagDeclareAttackers(false);

		// Setze flagNeedPlayerInput zurück.
		match.setFlagNeedPlayerInput(false);

		// Bestimme Priorität neu, setze flagNeedPlayerInput auf true.
		match.determinePlayerPrioritised();
		match.setFlagNeedPlayerInput(true);
	}

	/**
	 * Setzt die flagDeclareBlockers auf false. Danach wird die Priorität neu
	 * bestimmt und eine Eingabe vom priorisierten Spieler erwartet.
	 *
	 * @param playerNonactive
	 *            der nichtaktive Spieler.
	 */
	private void structureEndDeclareBlockers(IsPlayer playerNonactive) {
		LOGGER.debug("{} actionEndDeclareBlockers({})", this, playerNonactive);
		playerNonactive.setFlagDeclareBlockers(false);

		// Setze flagNeedPlayerInput zurück.
		match.setFlagNeedPlayerInput(false);

		// Bestimme Priorität neu, setze flagNeedPlayerInput auf true.
		match.determinePlayerPrioritised();
		match.setFlagNeedPlayerInput(true);
	}

	/**
	 * Setzt den Spielerstatus zurück auf CASTING_SPELL. Setzt die Werte zum
	 * Bezahlvorgang im Spieler zurück. Schließt zuletzt die Spielerhandlung ab.
	 *
	 * @param player
	 *            der bezahlende Spieler.
	 */
	private void structureEndPayment(IsPlayer player) {
		LOGGER.debug("{} endPayment()", this);

		// Setze Status zurück.
		player.setPlayerState(PlayerState.CASTING_SPELL);

		// Setze Kostenziele etc. zurück.
		player.setManaCostAlreadyPaid(new ManaMapDefault());
		player.setManaCostGoal(new ManaMapDefault());

		structureFinishAction(player);
	}

	/**
	 * Setze die flagPassedPriority beider Spieler auf false, sowie den
	 * Spielerstatus des betreffenden Spielers. Danach wird die Priorität neu
	 * bestimmt und dem priorisierten Spieler eine Reaktion abverlangt.
	 *
	 * @param player
	 *            Der Spieler, dessen Aktion beendet wird.
	 */
	private void structureFinishAction(IsPlayer player) {
		match.resetFlagsPassedPriority();
		match.resetPlayerState(player);
		match.determinePlayerPrioritised();
		match.setFlagNeedPlayerInput(true);
	}

	private void turnBasedCleanup() {
		// Setze Kampfschaden aller Permanents auf 0.
		for (final MagicPermanent magicPermanent : match.getCardsBattlefield()) {
			magicPermanent.setDamage(0);
		}

		// Setze Kampfschaden aller Angriffziele auf 0.
		for (final IsAttackTarget attackTarget : match.getListAttackTargets()) {
			attackTarget.resetCombatDamage();
		}

		// Leere Liste mit Angriffen.
		match.resetListAttacks();
	}

	private void turnBasedCombatDamageAssignment() {
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
				attackTarget.assignCombatDamage(attacker.getPower());
			}
		}
	}

	// TODO MID Prüfung ausweiten und wieder reinnehmen.
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

	private void turnBasedCombatDamageDealing() {
		for (final Attack attack : match.getListAttacks()) {
			for (final MagicPermanent creature : attack.getCombatants()) {
				creature.applyCombatDamage();
			}
		}

		for (final IsAttackTarget attackTarget : match.getListAttackTargets()) {
			attackTarget.applyCombatDamage();
		}
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
	 * Durchläuft den Stack und ruft für jedes Element resolve(stackable) auf.
	 * Der Stack wird durchlaufen, jedes mal wenn beide Spieler die Priorität
	 * abgegeben haben (rule=405.5.)
	 */
	// TODO HIGH Funktionsweise des Stacks genau nachlesen.
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

			match.popSpell();

			match.resetFlagsPassedPriority();
			match.determinePlayerPrioritised();
			match.setFlagNeedPlayerInput(true);
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
