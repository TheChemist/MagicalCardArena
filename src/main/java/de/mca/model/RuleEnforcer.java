package de.mca.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import de.mca.Constants;
import de.mca.factories.FactoryMagicPermanent;
import de.mca.factories.FactoryMagicSpell;
import de.mca.model.enums.ColorType;
import de.mca.model.enums.PlayerState;
import de.mca.model.enums.PlayerType;
import de.mca.model.enums.StateBasedActionType;
import de.mca.model.enums.ZoneType;
import de.mca.model.interfaces.IsAttackTarget;
import de.mca.model.interfaces.IsManaMap;
import de.mca.model.interfaces.IsPlayer;
import de.mca.model.interfaces.IsStackable;
import de.mca.presenter.ProgressNameChange;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.collections.FXCollections;

/**
 *
 * @author Maximilian Werling
 *
 */
public class RuleEnforcer {

	/**
	 * Speichert den Logger.
	 */
	private final static Logger LOGGER = LoggerFactory.getLogger("Match");
	/**
	 * Speichert den EventBus.
	 */
	private EventBus eventBus;
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
		this.eventBus = eventBus;
		this.factoryMagicPermanent = factoryMagicPermanent;
		this.factoryMagicSpell = factoryMagicSpell;

		setStateBasedActions = new SimpleSetProperty<>(FXCollections.observableSet());
	}

	/**
	 * Wird aufgerufen, wenn der Spieler eine bleibende Karte aktiviert. Sind
	 * alle Voraussetzungen erfüllt, wird die zu aktivierende Fähigkeit bestimmt
	 * und fpr die Aktivierung übergeben.
	 *
	 * @param player
	 *            der priorisierte Spieler.
	 * @param magicPermanent
	 *            das aktivierte Permanent.
	 */
	public void actionBeginActivatePermanent(IsPlayer player, MagicPermanent magicPermanent) {
		LOGGER.debug("{} structureBeginActivatePermanent({}, {})", this, player, magicPermanent);

		List<ActivatedAbility> listActivatedAbities = magicPermanent.propertyListAbilities();

		if (listActivatedAbities.size() <= 0) {
			// Keine Fähigkeiten vorhanden
		} else if (listActivatedAbities.size() == 1) {
			// Wähle einzige Fähigkeit automatisch

			actionActivateAbility(player, listActivatedAbities.get(0));
		} else {
			// TODO MID Entscheidung: Fähigkeit auswählen

		}
	}

	/**
	 * Beschwört einen Zauberspruch. Dafür werden alle notwendigen Entscheidunge
	 * abgefragt und Voraussetzungen überprüft.
	 *
	 * @see http://magiccards.info/rule/601-casting-spells.html
	 *
	 * @param player
	 *            Der Spieler, der den Zauberspruch spielt.
	 * @param magicCard
	 *            Die Karte, die als Zauberspruch auf den Stack gespielt wird.
	 */
	public void actionBeginCastSpell(IsPlayer player, MagicCard magicCard) {
		LOGGER.debug("{} actionBeginCastSpell({}, {})", this, player, magicCard);

		final MagicSpell spell = factoryMagicSpell.create(magicCard, player.getPlayerType());

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
			/**
			 * TODO HIGH Methode unterbrechen und Ergebnis der Abfrage
			 * zwischenspeichern. Die Spielerabfragen werden dann nicht mehr
			 * alle in dieser Methode abgefragt, sondern durch einen
			 * sequenziellen Aufruf der requestInput-Methode dargstellt.
			 */

			// requestInput(player, PlayerActionType.SELECT_COST_MAP, spell);
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
			finishAction(player);
		} else {
			// Spieler muss Karte bezahlen

			stateBasedBeginPayment(player, totalCostInformation);
		}
	}

	/**
	 * Wird aufgerufen, wenn ein Spieler das Match aufgibt.
	 *
	 * @param player
	 *            der Spieler.
	 */
	public void actionConcede(IsPlayer player) {
		LOGGER.debug("{} actionConcede({})", this, player);
		match.setFlagNeedPlayerInput(false, "actionConcede()");
		match.setFlagIsMatchRunning(false);
	}

	/**
	 * Wird aufgerufen, wenn der Spieler einen Angreifer auswählt. Es wird ein
	 * gültiges Angriffsziel ermittelt und der Angriff zur späteren Durchführung
	 * hinterlegt.
	 *
	 * @param playerActive
	 *            Der aktive Spieler.
	 * @param attacker
	 *            Die angreifende Kreatur.
	 */
	public void actionDeclareAttacker(IsPlayer playerActive, MagicPermanent attacker) {
		LOGGER.debug("{} actionDeclareAttacker({}, {})", this, playerActive, attacker);

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
			// TODO MID Entscheidung: Ziel auswählen

		}
	}

	/**
	 * Wird aufgerufen, wenn der Spieler einen Blocker auswählt. Zunächst wird
	 * das Blockziel ausgewählt, dann wird der ausgewählte Blocker registriert.
	 *
	 * @param playerNonactive
	 *            der nichtaktive Spieler.
	 * @param blocker
	 *            der Blocker.
	 */
	public void actionDeclareBlocker(IsPlayer playerNonactive, MagicPermanent blocker) {
		LOGGER.debug("{} actionDeclareBlocker({}, {})", this, playerNonactive, blocker);

		// TODO MID Entscheidung: Angreifer auswählen.
		int attackIndex = 0;

		match.declareBlocker(attackIndex, blocker);
	}

	/**
	 * Wird aufgerufen, wenn der Spieler eine Karte abwirft.
	 *
	 * @param player
	 *            der Spieler.
	 * @param magicCard
	 *            die Karte.
	 */
	public void actionDiscard(IsPlayer player, MagicCard magicCard) {
		LOGGER.debug("{} actionDiscard({}, {})", this, player, magicCard);
		player.removeCard(magicCard, ZoneType.HAND);
		player.addCard(magicCard, ZoneType.GRAVEYARD);
	}

	/**
	 * Setzt die flagDeclareAttackers auf false. Danach wird die Priorität neu
	 * bestimmt und eine Eingabe vom priorisierten Spieler erwartet.
	 *
	 * @param playerActive
	 *            der aktive Spieler.
	 */
	public void actionEndDeclareAttackers(IsPlayer playerActive) {
		LOGGER.debug("{} actionEndDeclareAttackers({})", this, playerActive);

		// Ändere Text des Buttons.
		if (playerActive.equals(PlayerType.HUMAN)) {
			getEventBus().post(new ProgressNameChange(this, "Pass"));
		}

		// Setz flags zum Überspringen.
		if (match.getTotalAttackers() == 0) {
			match.skipStepDeclareBlockers();
			match.skipStepCombatDamage();
		}

		// Setze flags zurück
		playerActive.setFlagDeclareAttackers(false);
		match.setFlagNeedPlayerInput(false, "actionEndDeclareAttackers()");

		// Bestimme Priorität neu.
		match.determinePlayerPrioritised();
	}

	/**
	 * Setzt die flagDeclareBlockers auf false. Danach wird die Priorität neu
	 * bestimmt und eine Eingabe vom priorisierten Spieler erwartet.
	 *
	 * @param playerNonactive
	 *            der nichtaktive Spieler.
	 */
	public void actionEndDeclareBlockers(IsPlayer playerNonactive) {
		LOGGER.debug("{} actionEndDeclareBlockers({})", this, playerNonactive);
		if (playerNonactive.equals(PlayerType.HUMAN)) {
			getEventBus().post(new ProgressNameChange(this, "Pass"));
		}

		playerNonactive.setFlagDeclareBlockers(false);

		// Setze flagNeedPlayerInput zurück.
		match.setFlagNeedPlayerInput(false, "actionEndDeclareBlockers()");

		// Bestimme Priorität neu.
		match.determinePlayerPrioritised();
	}

	/**
	 * Wird aufgerufen, wenn ein Spieler die Priorität abgibt.
	 *
	 * @param player
	 *            der Spieler.
	 */
	public void actionPassPriority(IsPlayer player) {
		LOGGER.debug("{} actionPassPriority({})", this, player);
		player.setFlagPassedPriority(true);
		match.resetPlayerState(player);
		match.setFlagNeedPlayerInput(false, "actionPassPriority()");
	}

	/**
	 * Wird aufgerufen, wenn ein Spieler ein Land spielt.
	 *
	 * @param playerActive
	 *            der aktive Spieler
	 * @param landCard
	 *            das Land.
	 */
	public void actionPlayLand(IsPlayer playerActive, MagicCard landCard) {
		LOGGER.debug("{} actionPlayLand({}, {})", this, playerActive, landCard);
		playerActive.setPlayerState(PlayerState.TAKING_SPECIAL_ACTION);

		// Bewege Karten
		playerActive.removeCard(landCard, ZoneType.HAND);
		match.addCard(factoryMagicPermanent.create(landCard), ZoneType.BATTLEFIELD);

		// Setze Flag
		playerActive.setFlagPlayedLand(true);

		// Abschließen
		playerActive.setPlayerState(PlayerState.PRIORITIZED);
	}

	public void checkInteractable(IsPlayer player) {
		for (final MagicCard magicCard : player.getZoneHand().getAll()) {
			magicCard.setFlagIsInteractable(
					checkCanPlayLand(player, magicCard) || checkCanCast(player, magicCard) ? true : false);
		}

		for (final MagicPermanent magicPermanent : match.getZoneBattlefield().getAll()) {
			magicPermanent.setFlagIsInteractable(checkCanActivatePermanent(player, magicPermanent)
					|| checkCanAttack(player, magicPermanent) || checkCanBlock(player, magicPermanent) ? true : false);
		}
	}

	public void examineEffectProduceMana(EffectProduceMana effectProduceMana) {
		LOGGER.debug("{} examineEffectProduceMana({})", this, effectProduceMana);
		final IsManaMap manaMap = effectProduceMana.getProduceMap();
		final IsPlayer player = match.getPlayer(effectProduceMana.getPlayerType());

		manaMap.getKeySet().forEach(key -> player.addMana(key, manaMap.get(key)));

		final boolean isPaying = player.isPaying();
		final boolean isPaid = isPaying ? checkIsPaid(player) : false;
		if (isPaying && isPaid) {
			// Spieler hat alles bezahlt
			stateBasedEndPayment(player);
		}
	}

	public void addStateBasedAction(StateBasedAction stateBasedAction) {
		LOGGER.debug("{} addStateBasedAction({})", this, stateBasedAction);
		setStateBasedActions.add(stateBasedAction);
	}

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
			turnBasedBeginDeclareAttackers(playerActive);
			break;
		case DECLARE_BLOCKER:
			turnBasedBeginDeclareBlockers(playerNonactive);
			break;
		case DISCARD:
			final int originalHandSize = playerActive.propertyHandSize().get();
			for (int i = 0; i < (originalHandSize - Constants.HAND_SIZE); i++) {
				playerActive.setPlayerState(PlayerState.DISCARDING);
				match.setFlagNeedPlayerInput(true, "examineTurnBasedAction()");
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
			for (final MagicPermanent magicPermanent : match.getZoneBattlefield()
					.getAll(playerActive.getPlayerType())) {
				magicPermanent.setFlagTapped(false);
				magicPermanent.setFlagSummoningSickness(false);
			}
			break;
		case DECLARE_DAMAGE_ASSIGNMENT_ORDER_ATTACKER:
			// TODO HIGH Kann nicht funktionieren.
			turnBasedBeginDamageAssignmentAttacker(playerActive);
			turnBasedEndDamageAssignmentAttacker(playerActive);

			// Hier wird die nächste TBA abgefeuert.
			((Step) tba.getSource()).fireDeclareDamageAssignmentBlocker();
			break;
		case DECLARE_DAMAGE_ASSIGNMENT_ORDER_BLOCKER:
			// TODO HIGH Kann nicht funktionieren.
			turnBasedBeginDamageAssignmentBlocker(playerNonactive);
			turnBasedEndDamageAssignmentBlocker(playerNonactive);

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

	/**
	 * Wird aufgerufen, wenn ein Spieler eine Fähigkeit aktiviert. Es wird
	 * überprüft, ob die Fähigkeit aktiviert werden kann.
	 *
	 * @param player
	 *            der Spieler.
	 * @param activatedAbility
	 *            die Fähigkeit.
	 */
	private void actionActivateAbility(IsPlayer player, ActivatedAbility activatedAbility) {
		LOGGER.debug("{} actionActivateAbility({}, {})", this, player, activatedAbility);

		if (activatedAbility.isManaAbility()) {
			// Manafertigkeit

			// Wechsel des Spielerstatus zum Triggern von KI.
			player.setPlayerState(PlayerState.ACTIVATING_ABILITY);

			/**
			 * Verkürzung für Manafertigkeiten. Effekte werden sofort generiert,
			 * ohne den Umweg über den Stack.
			 **/
			// activatedAbility.generateEffects();
			activatedAbility.propertyListEffects().forEach(effect -> {
				if (effect.getPlayerType() == null || effect.getPlayerType().equals(PlayerType.NONE)) {
					throw new NullPointerException("Effekt wird von keinem Spieler kontrolliert!");
				}

				examineEffectProduceMana((EffectProduceMana) effect);
			});

			// Zusätzliche Kosten werden bezahlt.
			switch (activatedAbility.getAdditionalCostType()) {
			case NO_ADDITIONAL_COST:
				break;
			case TAP:
				((MagicPermanent) activatedAbility.getSource()).setFlagTapped(true);
				break;
			}

			// Zurücksetzen Spielerstatus
			player.setPlayerState(PlayerState.PRIORITIZED);
		} else {
			// Andere aktivierbare Fähigkeit

		}
	}

	/**
	 * Wird aufgerufen, wenn ein Spieler eine bleibende Karte begräbt.
	 *
	 * @param player
	 *            der Spieler.
	 * @param magicPermanent
	 *            die bleibende Karte.
	 */
	private void actionBury(IsPlayer player, MagicPermanent magicPermanent) {
		LOGGER.debug("{} actionBury({}, {})", this, player, magicPermanent);
		match.removeCard(magicPermanent, ZoneType.BATTLEFIELD);
		player.addCard(new MagicCard(magicPermanent), ZoneType.GRAVEYARD);
	}

	/**
	 * Wird aufgerufen, wenn der Spieler alle verbleibenden Karten abwirft.
	 *
	 * @param player
	 *            der Spieler.
	 */
	private void actionDiscardAll(IsPlayer player) {
		LOGGER.debug("{} actionDiscardAll({})", this, player);
		final List<MagicCard> cardList = player.getZoneHand().getAll();
		player.removeAllCards(ZoneType.HAND);
		player.addAllCards(cardList, ZoneType.GRAVEYARD);
	}

	/**
	 * Wird aufgerufen, wenn ein Spieler zufällig eine Karte ablegt.
	 *
	 * @param player
	 *            der Spieler.
	 */
	private void actionDiscardRandom(IsPlayer player) {
		LOGGER.debug("{} actionDiscardRandom({})", this, player);
		final List<MagicCard> zoneHand = player.getZoneHand().getAll();
		actionDiscard(player, zoneHand.get(new Random().nextInt(zoneHand.size())));
	}

	/**
	 * Wird aufgerufen, wenn ein Spieler zufällig mehrere Karten abwirft. Es
	 * wird geprüft, ob genügend Karten abgeworfen werden können. Können nicht,
	 * werden alle Karten abgeworfen.
	 *
	 * @param player
	 *            der Spieler.
	 * @param howMany
	 *            die Anzahl zufällig abgeworfener Karten.
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
	 * Wird aufgerufen, wenn ein Spieler eine Karte zieht.
	 *
	 * @param player
	 *            der Spieler.
	 */
	private void actionDraw(IsPlayer player) {
		LOGGER.debug("{} actionDraw({})", this, player);
		if (checkCanDraw(player)) {
			final MagicCard magicCard = player.getZoneLibrary().getTop();
			player.removeCard(magicCard, ZoneType.LIBRARY);
			player.addCard(magicCard, ZoneType.HAND);
		} else {
			addStateBasedAction(new StateBasedAction(this, StateBasedActionType.PLAYER_CANT_DRAW));
		}
	}

	/**
	 * Wird aufgerufen, wenn ein Spieler alle verbleibenden Karten zieht.
	 *
	 * @param player
	 *            der Spieler.
	 */
	private void actionDrawAll(IsPlayer player) {
		LOGGER.debug("{} actionDrawAll({})", this, player);
		final List<MagicCard> cardList = player.getZoneLibrary().getAll();
		player.removeAllCards(ZoneType.LIBRARY);
		player.addAllCards(cardList, ZoneType.HAND);
	}

	/**
	 * Wird aufgerufen, wenn ein Spieler eine Karte verbannt.
	 *
	 * @param player
	 *            der Spieler.
	 * @param magicCard
	 *            die Karte.
	 */
	private void actionExile(IsPlayer player, MagicCard magicCard) {
		LOGGER.debug("{} actionExile({}, {})", this, player, magicCard);
		player.removeCard(magicCard, ZoneType.HAND);
		match.addCard(magicCard, ZoneType.EXILE);
	}

	/**
	 * Wird aufgerufen, wenn eine bleibende Karte verbannt wird.
	 *
	 * @param magicPermanent
	 *            die bleibende Karte.
	 */
	private void actionExile(MagicPermanent magicPermanent) {
		LOGGER.debug("{} actionExile({})", this, magicPermanent);
		match.removeCard(magicPermanent, ZoneType.BATTLEFIELD);
		match.addCard(new MagicCard(magicPermanent), ZoneType.EXILE);
	}

	/**
	 * Prüft, ob die grundlegenden Voraussetzungen für das Aktivieren einer
	 * bleibenden Karte erfüllt sind (also genauer gesagt für das Aktivieren
	 * einer beliebigen Fähigkeit des Permanents).
	 *
	 * @see http://magiccards.info/rule/602-activating-activated-abilities.html
	 * @param player
	 *            der Spieler.
	 * @param magicPermanent
	 *            die bleibende Karte.
	 * @return true, wenn die grundlegenden Voraussetzungen erfüllt sind.
	 */
	private boolean checkCanActivatePermanent(IsPlayer player, MagicPermanent magicPermanent) {
		if (!magicPermanent.isActivatable()) {
			// Bleibende Karte hat keine aktivierbaren Fähigkeiten.
			return false;
		}

		final boolean isControlling = player.equals(magicPermanent.getPlayerControlling());
		final boolean isUntapped = !magicPermanent.getFlagIsTapped();
		final boolean isPrioritized = player.isPrioritised();
		final boolean isCastingSpell = player.isCastingSpell() || player.isPaying();
		final boolean isActivatingAbility = player.isActivatingAbility();

		final boolean result = isControlling && isUntapped && (isPrioritized || isCastingSpell || isActivatingAbility);
		LOGGER.trace("{} checkCanActivatePermanent({}, {}) = {}", this, player, magicPermanent, result);
		return result;
	}

	/**
	 * Prüft, ob die grundlegenden Voraussetzungen für einen Angriff erfüllt
	 * sind.
	 *
	 * @see http://magiccards.info/rule/508-declare-attackers-step.html
	 * @param player
	 *            der aktive Spieler.
	 * @param magicPermanent
	 *            der Angreifer.
	 * @return true, wenn der designierte Angreifer grundsätzlich angreifen
	 *         kann.
	 */
	private boolean checkCanAttack(IsPlayer player, MagicPermanent magicPermanent) {
		if (!magicPermanent.isCreature()) {
			// Bleibende Karte ist keine Kreatur.
			return false;
		}

		final boolean isControlling = player.equals(magicPermanent.getPlayerControlling());
		final boolean isUnapped = !magicPermanent.getFlagIsTapped();
		final boolean hasSummoningSickness = magicPermanent.getFlagHasSummoningSickness();
		final boolean hasFlagDeclaringAttackers = player.getFlagDeclaringAttackers();

		final boolean result = isControlling && isUnapped && !hasSummoningSickness && hasFlagDeclaringAttackers;
		LOGGER.trace("{} checkCanAttack({}, {}) = {}", this, player, magicPermanent, result);
		return result;
	}

	/**
	 * Prüft, ob die grundlegenden Voraussetzungen für einen Block erfüllt sind.
	 *
	 * @see http://magiccards.info/rule/509-declare-blockers-step.html
	 * @param player
	 *            der nichtaktive Spieler.
	 * @param magicPermanent
	 *            der Blocker.
	 * @return true, wenn der designierte Blocker grundsätzlich Blocken kann.
	 */
	private boolean checkCanBlock(IsPlayer player, MagicPermanent magicPermanent) {
		if (!magicPermanent.isCreature()) {
			// Bleibende Karte ist keine Kreatur.
			return false;
		}

		final boolean isControlling = player.equals(magicPermanent.getPlayerControlling());
		final boolean isUntapped = !magicPermanent.getFlagIsTapped();
		final boolean hasFlagDeclaringBlockers = player.getFlagDeclaringBlockers();

		final boolean result = isControlling && isUntapped && hasFlagDeclaringBlockers;
		LOGGER.trace("{} checkCanBlock({}, {}) = {}", this, player, magicPermanent, result);
		return result;
	}

	/**
	 * Prüft, ob die grundlegenden Voraussetzungen für das Beschwören eines
	 * Zauberspruchs erfüllt sind.
	 *
	 * @see http://magiccards.info/rule/601-casting-spells.html
	 * @param player
	 *            der Spieler.
	 * @param magicCard
	 *            der Zauberspruch.
	 * @return true, wenn alle Voraussetzungen erfüllt sind.
	 */
	private boolean checkCanCast(IsPlayer player, MagicCard magicCard) {
		if (!magicCard.isSpell() && !magicCard.isPermanentSpell()) {
			// Karte ist kein Zaberspruch.
			return false;
		}

		final boolean isActivePlayer = match.isPlayerActive(player);
		final boolean isMain = match.getCurrentPhase().isMain();
		final boolean isStackEmpty = match.getZoneStack().isEmpty();

		// Prüfe, ob eine Kostendarstellung der Karte bezahlt werden kann.
		final IsManaMap availableMana = player.getManaPool();
		boolean canPay = false;
		for (final IsManaMap costMap : magicCard.getListCostMaps()) {
			if (costMap.isEmpty()) {
				// CostMap ist leer, kann bezahlen, verlasse Prüfung
				canPay = true;
				break;
			} else {
				canPay = availableMana.contains(costMap) ? true : false;
				if (canPay) {
					// Kann bezahlen, verlasse Prüfung.
					break;
				}
			}
		}

		final boolean result = isActivePlayer && isMain && isStackEmpty && canPay;
		LOGGER.trace("{} checkCanCast({}, {}) = {}", this, player, magicCard, result);
		return result;
	}

	/**
	 * Prüft, ob ein Spieler eine gewisse Anzahl Handkarten abwerfen kann.
	 *
	 * @param player
	 *            Der Spieler.
	 * @param howMany
	 *            Anzahl Karten.
	 * @return true, wenn eine Anzahl Karten in Höhe howMany abgelegt werden
	 *         kann.
	 */
	private boolean checkCanDiscard(IsPlayer player, int howMany) {
		return player.propertyHandSize().get() >= howMany;
	}

	/**
	 * Prüft, ob noch mindestens eine Karte in der Bibliothek ist.
	 *
	 * @param player
	 *            Der Spieler.
	 * @return true, wenn gezogen werden kann.
	 */
	private boolean checkCanDraw(IsPlayer player) {
		return player.getZoneLibrary().getSize() >= 1;
	}

	/**
	 * Prüft, ob die grundlegenden Vorausetzungen für das Spielen eines Landes
	 * erfüllt sind.
	 *
	 * @see http://magiccards.info/rule/305-lands.html
	 * @param player
	 *            ein Spieler.
	 * @return true, wenn die grundlegenden Voraussetzungen erfüllt sind.
	 */
	private boolean checkCanPlayLand(IsPlayer player, MagicCard magicCard) {
		if (!magicCard.isLand()) {
			// Karte ist kein Land.
			return false;
		}

		final boolean isOwning = player.equals(magicCard.getPlayerOwning());
		final boolean isActivePlayer = match.isPlayerActive(player);
		final boolean isMain = match.getCurrentPhase().isMain();
		final boolean isStackEmpty = match.getZoneStack().isEmpty();
		final boolean hasLandFlag = player.getFlagPlayedLand();

		final boolean result = isOwning && isActivePlayer && isMain && !hasLandFlag && isStackEmpty;
		LOGGER.trace("{} checkCanPlayLand({}, {}) = {}", this, player, magicCard, result);
		return result;
	}

	/**
	 * Prüft, ob ein Spieler seine aktuellen Bezahlziele vollständig erreicht
	 * hat. Eventuell vorhandenes Mana im Manapool wird dabei verbracht.
	 *
	 * @param player
	 *            ein Spieler.
	 * @return true, wenn der Spieler seine Bezahlziele erreicht hat.
	 */
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
			if (key.isTrueColor()) {
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
				final IsManaMap remove = new ManaMapDefault();
				for (final ColorType color : manaPool.getTrueColorKeySet()) {
					for (int i = 0; i < manaPool.get(color); i++) {
						if (need <= 0) {
							break;
						}
						manaCostAlreadyPaid.add(clm, 1);
						remove.add(color, 1);
						need--;
					}
				}
				for (final ColorType key : remove.getKeySet()) {
					player.removeMana(key, remove.get(key));
				}
			}
		}

		final boolean isPaid = manaCostGoal.equals(manaCostAlreadyPaid);
		LOGGER.debug("{} checkIsPaid({}) = {}", this, player, isPaid);
		return isPaid;
	}

	/**
	 * Setze die flagPassedPriority beider Spieler auf false, sowie den
	 * Spielerstatus des betreffenden Spielers. Danach wird die Priorität neu
	 * bestimmt und dem priorisierten Spieler eine Reaktion abverlangt.
	 *
	 * @param player
	 *            Der Spieler, dessen Aktion beendet wird.
	 */
	private void finishAction(IsPlayer player) {
		LOGGER.debug("{} finishAction({})", this, player);
		match.resetFlagsPassedPriority();
		match.resetPlayerState(player);
		match.determinePlayerPrioritised();
	}

	private EventBus getEventBus() {
		return eventBus;
	}

	// private void requestInput(IsPlayer player, PlayerActionType
	// playerActionType, MagicSpell spell) {
	// switch (playerActionType) {
	// case SELECT_COST_MAP:
	// player.fireSelectCostMap(spell);
	// break;
	// }
	// }

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
	private void stateBasedBeginPayment(IsPlayer player, TotalCostInformation totalCostInformation) {
		LOGGER.debug("{} stateBasedBeginPayment({})", this, player);

		// Setze Spielerstatus und Bezahlziel im Spieler.
		player.setPlayerState(PlayerState.PAYING);
		player.setManaCostGoal(totalCostInformation.getTotalCost());

		if (checkIsPaid(player)) {
			// Es war ausreichend Mana im Manapool.

			stateBasedEndPayment(player);
		} else {
			match.setFlagNeedPlayerInput(true, "stateBasedBeginPayment()");
		}
	}

	/**
	 * Setzt den Spielerstatus zurück auf CASTING_SPELL. Setzt die Werte zum
	 * Bezahlvorgang im Spieler zurück. Schließt zuletzt die Spielerhandlung ab.
	 *
	 * @param player
	 *            der bezahlende Spieler.
	 */
	private void stateBasedEndPayment(IsPlayer player) {
		LOGGER.debug("{} stateBasedEndPayment()", this);

		// Setze Status zurück.
		player.setPlayerState(PlayerState.CASTING_SPELL);

		// Setze Kostenziele etc. zurück.
		player.setManaCostAlreadyPaid(new ManaMapDefault());
		player.setManaCostGoal(new ManaMapDefault());

		finishAction(player);
	}

	/**
	 * Setzt den Spielerstatus auf ASSIGNING_DAMAGE_ORDER_ATTACKER und prüft, ob
	 * für den Angreifer eine Schadensreihenfolge festgelegt werden muss. Wenn
	 * ja, wird eine Schadensreihenfolge festgelegt.
	 *
	 * @see http://magiccards.info/rule/510-combat-damage-step.html
	 * @param playerActive
	 *            Der aktive Spieler.
	 */
	private void turnBasedBeginDamageAssignmentAttacker(IsPlayer playerActive) {
		LOGGER.debug("{} actionBeginDamageAssingmentAttacker({})", this, playerActive);
		playerActive.setPlayerState(PlayerState.ASSINGING_DAMAGE_ORDER_ATTACKER);

		for (final Attack attack : match.getListAttacks()) {
			final List<MagicPermanent> blockers = attack.propertyListBlockers();
			if (attack.getSource().isFlagBlocked() && blockers.size() > 1) {
				// TODO MID Entscheidung: Schadensreihenfolge
				attack.setBlockers(attack.propertyListBlockers());
			} else {
				LOGGER.debug("{} actionBeginDamageAssingmentAttacker({}) -> Schadensverteilung nicht notwendig!", this,
						playerActive);
			}
		}
	}

	/**
	 * Setzt den Spielerstatus auf ASSIGNING_DAMAGE_ORDER_BLOCKERS und prüft, ob
	 * für den Blocker eine Schadensreihenfolge festgelegt werden muss. Wenn ja,
	 * wird eine Schadenreihenfolge festgelegt.
	 *
	 * @see http://magiccards.info/rule/510-combat-damage-step.html
	 * @param playerNonactive
	 *            Der nichtaktive Spieler.
	 */
	private void turnBasedBeginDamageAssignmentBlocker(IsPlayer playerNonactive) {
		// TODO LOW Wird erst bei mehreren Blockzielen relevant.
		LOGGER.debug("{} actionBeginDamageAssignmentBlocker({})", this, playerNonactive);
		playerNonactive.setPlayerState(PlayerState.ASSIGNING_DAMAGE_ORDER_BLOCKERS);
		LOGGER.debug("{} actionBeginDamageAssignmentBlocker({}) -> Schadensverteilung nicht notwendig!", this,
				playerNonactive);
	}

	/**
	 * Setzt die flagDeclareAttackers auf true und verlangt vom Spieler eine
	 * Reaktion (flagNeedPlayerInput = true).
	 *
	 * @param playerActive
	 *            der aktive Spieler.
	 */
	private void turnBasedBeginDeclareAttackers(IsPlayer playerActive) {
		LOGGER.debug("{} turnBasedBeginDeclareAttackers({})", this, playerActive);
		if (playerActive.equals(PlayerType.HUMAN)) {
			getEventBus().post(new ProgressNameChange(this, "Finish"));
		}

		playerActive.setPlayerState(PlayerState.SELECTING_ATTACKER);
		playerActive.setFlagDeclareAttackers(true);

		match.setFlagNeedPlayerInput(true, "turnBasedBeginDeclareAttackers()");
	}

	/**
	 * Setzt die flagDeclareBlockers auf true und verlangt vom Spieler eine
	 * Reaktion (flagNeedPlayerInput = true).
	 *
	 * @param playerNonactive
	 *            der nichtaktive Spieler.
	 */
	private void turnBasedBeginDeclareBlockers(IsPlayer playerNonactive) {
		LOGGER.debug("{} turnBasedBeginDeclareBlockers({})", this, playerNonactive);
		if (playerNonactive.equals(PlayerType.HUMAN)) {
			getEventBus().post(new ProgressNameChange(this, "Finish"));
		}

		playerNonactive.setPlayerState(PlayerState.DEFENDING);
		playerNonactive.setFlagDeclareBlockers(true);

		match.setFlagNeedPlayerInput(true, "turnBasedBeginDeclareBlockers()");
	}

	private void turnBasedCleanup() {
		// Setze Kampfschaden aller Permanents auf 0.
		for (final MagicPermanent magicPermanent : match.getZoneBattlefield().getAll()) {
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
				if (creature.getToughness() <= 0) {
					addStateBasedAction(new SBACreatureToughnessZero(creature, creature.getPlayerControlling()));
				}
			}
		}

		for (final IsAttackTarget attackTarget : match.getListAttackTargets()) {
			attackTarget.applyCombatDamage();
		}
	}

	/**
	 * Setzt den Spielerstatus des aktiven Spielers zurück auf
	 * SELECTING_ATTACKER.
	 *
	 * @param playerActive
	 *            der aktive Spieler.
	 */
	private void turnBasedEndDamageAssignmentAttacker(IsPlayer playerActive) {
		LOGGER.debug("{} actionEndDamageAssignmentAttacker({})", this, playerActive);
		playerActive.setPlayerState(PlayerState.SELECTING_ATTACKER);
	}

	/**
	 * Setzt den Spielerstatus des nichtaktiven Spielers zurück auf DEFENDING.
	 *
	 * @param playerNonactive
	 *            der nichtaktive Spieler.
	 */
	private void turnBasedEndDamageAssignmentBlocker(IsPlayer playerNonactive) {
		LOGGER.debug("{} structureEndDamageAssignmentBlocker({})", this, playerNonactive);
		playerNonactive.setPlayerState(PlayerState.DEFENDING);
	}

	/**
	 * Wird aufgerufen, wenn ein Spieler mehrere Karten zieht.
	 *
	 * @param player
	 *            der Spieler.
	 * @param howMany
	 *            Anzahl Karten.
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
	void processStack() {
		LOGGER.debug("{} processStack()", this);
		final int sizeMagicStack = match.getZoneStack().getSize();

		for (int i = 0; i < sizeMagicStack; i++) {
			final IsStackable stackable = match.getZoneStack().peek();
			if (stackable.isPermanentSpell()) {
				match.addCard(factoryMagicPermanent.create((MagicSpell) stackable), ZoneType.BATTLEFIELD);
			} else {
				stackable.propertyListEffects().forEach(effect -> examineEffectProduceMana((EffectProduceMana) effect));
			}

			match.popSpell();

			match.resetFlagsPassedPriority();
			match.determinePlayerPrioritised();
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
