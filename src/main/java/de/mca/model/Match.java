package de.mca.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import de.mca.Constants;
import de.mca.factories.FactoryMagicPermanent;
import de.mca.factories.FactoryTurn;
import de.mca.factories.FactoryZone;
import de.mca.model.enums.AdditionalCostType;
import de.mca.model.enums.ObjectType;
import de.mca.model.enums.PlayerState;
import de.mca.model.enums.PlayerType;
import de.mca.model.enums.ZoneType;
import de.mca.model.interfaces.IsAttackTarget;
import de.mca.model.interfaces.IsMatch;
import de.mca.model.interfaces.IsPlayer;
import de.mca.model.interfaces.IsStackable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.collections.FXCollections;

/**
 *
 * @author Maximilian Werling
 *
 */
public final class Match implements IsMatch {

	/**
	 * Speichert den Logger.
	 */
	private final static Logger LOGGER = LoggerFactory.getLogger("Match");
	/**
	 * Speichert die PermanentFactory zum Erstellen bleibender Karten.
	 */
	private final FactoryMagicPermanent magicPermanentFactory;
	/**
	 * Speichert den geteilten Stack.
	 */
	private final MagicStack magicStack;
	/**
	 * Speichert alle Angriffe.
	 */
	private final ListProperty<Attack> propertyListAttacks;
	/**
	 * Speichert die Liste aller angreifbaren Ziele. Diese Liste wird auch an
	 * Spieler weitergegeben.
	 */
	private final ListProperty<IsAttackTarget> propertyListAttackTargets;
	/**
	 * Speichert die Runden.
	 */
	private final ListProperty<Turn> propertyListTurns;
	/**
	 * Zeigt an, ob das Match gerade läuft.
	 */
	private final BooleanProperty propertyMatchRunning;
	/**
	 * Zeigt an, ob Input von Spieler benötigt wird.
	 */
	private final BooleanProperty propertyNeedPlayerInput;
	/**
	 * Speichert den RuleEnforcer.
	 */
	private final RuleEnforcer ruleEnforcer;
	/**
	 * Sammelts StateBasedActions. Diese werden zu bestimmten Zeitpunkten
	 * abgearbeitet.
	 */
	private final SetProperty<StateBasedAction> setStateBasedActions;
	/**
	 * Speichert die geteilte Spieldfeld-Zone.
	 */
	private final ZoneDefault<MagicPermanent> zoneBattlefield;
	/**
	 * Speichert das geteilte Exil.
	 */
	private final ZoneDefault<MagicCard> zoneExile;

	@Inject
	private Match(FactoryMagicPermanent magicPermanentFactory, FactoryTurn turnFactory, FactoryZone zoneFactory,
			MagicStack magicStack, RuleEnforcer ruleEnforcer, @Assisted("playerHuman") IsPlayer playerHuman,
			@Assisted("playerComputer") IsPlayer playerComputer, @Assisted("nameHuman") String nameHuman,
			@Assisted("nameComputer") String nameComputer, @Assisted("deckHuman") Deck deckHuman,
			@Assisted("deckComputer") Deck deckComputer, ZoneDefault<MagicPermanent> zoneBattlefield) {
		playerComputer.setDisplayName(nameComputer);
		playerComputer.setDeck(deckComputer);
		playerHuman.setDisplayName(nameHuman);
		playerHuman.setDeck(deckHuman);

		this.magicPermanentFactory = magicPermanentFactory;
		this.magicStack = magicStack;
		this.ruleEnforcer = ruleEnforcer;
		this.zoneBattlefield = zoneBattlefield;
		this.zoneExile = zoneFactory.create(PlayerType.NONE, ZoneType.EXILE);

		ruleEnforcer.setMatch(this);

		propertyMatchRunning = new SimpleBooleanProperty(false);
		propertyNeedPlayerInput = new SimpleBooleanProperty(false);
		propertyListAttacks = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
		propertyListAttackTargets = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
		propertyListTurns = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
		setStateBasedActions = new SimpleSetProperty<>(FXCollections.observableSet(new HashSet<>()));

		propertyListAttackTargets.add(playerComputer);
		propertyListAttackTargets.add(playerHuman);

		final Turn firstTurn = turnFactory.create(playerComputer, playerHuman);
		propertyListTurns.add(firstTurn);
	}

	public void addStateBasedAction(StateBasedAction stateBasedAction) {
		setStateBasedActions.add(stateBasedAction);
	}

	public boolean checkCanActivate(IsPlayer p, ActivatedAbility aa) {
		if (aa.isManaAbility()) {
			final boolean prioritised = p.isPrioritised();
			final boolean castingSpell = p.isCastingSpell() || p.isPaying();
			final boolean activatingAbility = p.isActivatingAbility();
			final boolean checkCanPay = checkCanPay((MagicPermanent) aa.getSource(), aa.getAdditionalCostType());
			LOGGER.debug("{} checkCanActivate({}, {}) = {}", this, p, aa,
					(prioritised || castingSpell || activatingAbility) && checkCanPay);
			return (prioritised || castingSpell || activatingAbility) && checkCanPay;
		}
		return false;
	}

	public boolean checkCanCast(IsPlayer p, MagicSpell ms) {
		final boolean isActivePlayer = isPlayerActive(p);
		final boolean currentStepIsMain = getCurrentPhase().isMain();
		final boolean stackEmpty = magicStack.isEmpty();
		LOGGER.debug("{} checkCanCast({}, {}) = {}", this, p, ms, (isActivePlayer && currentStepIsMain && stackEmpty));
		return isActivePlayer && currentStepIsMain && stackEmpty;
	}

	public boolean checkCanPlayLandCard(IsPlayer p) {
		final boolean isActivePlayer = isPlayerActive(p);
		final boolean currentStepIsMain = getCurrentPhase().isMain();
		final boolean stackEmpty = magicStack.isEmpty();
		final boolean landFlag = !getPlayerActive().getFlagPlayedLand();
		LOGGER.debug("{} checkCanPlayLandCard({}) = {}", this, p,
				(isActivePlayer && currentStepIsMain && landFlag && stackEmpty));
		return isActivePlayer && currentStepIsMain && landFlag && stackEmpty;
	}

	@Override
	public boolean checkSkipPhase() {
		return getCurrentPhase().getFlagSkipped();
	}

	@Override
	public boolean checkSkipStep() {
		return getCurrentStep().getFlagStepSkipped();
	}

	@Override
	public boolean checkSkipTurn() {
		return getCurrentTurn().getFlagTurnSkipped();
	}

	/**
	 * Fügt dem Spieler einen neuen Angriff auf ihn oder einen von ihm
	 * kontrollierten Planeswalker hinzu.
	 *
	 * @param attack
	 *            ein Angriff kapselt den Angreifer und das Angriffziel.
	 */
	public void declareAttacker(Attack attack) {
		propertyListAttacks.add(attack);
	}

	public void declareBlocker(int attackIndex, MagicPermanent blocker) {
		propertyListAttacks.get(attackIndex).blockerAdd(blocker);
	}

	public Turn getCurrentTurn() {
		return propertyListTurns.get(propertyListTurns.getSize() - 1);
	}

	@Override
	public boolean getFlagMatchRunning() {
		return propertyMatchRunning.get();
	}

	@Override
	public List<IsAttackTarget> getListAttackTargets() {
		return propertyListAttackTargets;
	}

	/**
	 * Liefert eine Liste aller kontrollierten Karten auf dem Spielfeld.
	 *
	 * @return eine Liste aller kontrollierten Karten auf dem Spielfeld.
	 */

	public List<MagicPermanent> getListControlledCards(IsPlayer player) {
		return zoneBattlefield.getAll(player.getPlayerType());
	}

	public List<MagicPermanent> getListLegalAttackers(IsPlayer player) {
		final List<MagicPermanent> result = new ArrayList<>();
		getControlledCreatures(player).forEach(magicPermanent -> {
			final MagicPermanent attacker = magicPermanent;
			if (attacker.checkCanAttack()) {
				result.add(attacker);
			}
		});
		return result;
	}

	public List<MagicPermanent> getListLegalBlockers(IsPlayer player) {
		final List<MagicPermanent> result = new ArrayList<>();
		for (final MagicPermanent mp : getControlledCreatures(player)) {
			final MagicPermanent blocker = mp;
			if (blocker.checkCanBlock()) {
				result.add(blocker);
			}
		}
		return result;
	}

	public MagicStack getMagicStack() {
		return magicStack;
	}

	public IsPlayer getPlayer(PlayerType playerType) {
		return getCurrentTurn().getPlayer(playerType);
	}

	public IsPlayer getPlayerActive() {
		return getCurrentTurn().getPlayerActive();
	}

	@Override
	public IsPlayer getPlayerComputer() {
		return getCurrentTurn().getPlayerComputer();
	}

	@Override
	public IsPlayer getPlayerHuman() {
		return getCurrentTurn().getPlayerHuman();
	}

	public IsPlayer getPlayerNonactive() {
		return getCurrentTurn().getPlayerNonactive();
	}

	public RuleEnforcer getRuleEnforcer() {
		return ruleEnforcer;
	}

	public int getTotalAttackers() {
		return propertyListAttacks.size();
	}

	@Override
	public ZoneDefault<MagicPermanent> getZoneBattlefield() {
		return zoneBattlefield;
	}

	@Override
	public ZoneDefault<MagicCard> getZoneExile() {
		return zoneExile;
	}

	@Override
	public boolean isPhaseRunning() {
		return getCurrentPhase().getFlagPhaseRunning();
	}

	public boolean isStepRunning() {
		return getCurrentStep().getFlagStepRunning();
	}

	@Override
	public boolean isTurnRunning() {
		return getCurrentTurn().getFlagTurnRunning();
	}

	@Override
	public void matchBegin(boolean alreadyRunning) {
		if (!alreadyRunning) {
			LOGGER.debug("{} matchBegin()", this);

			ruleEnforcer.actionDraw(getPlayerHuman(), Constants.HAND_SIZE);
			ruleEnforcer.actionDraw(getPlayerComputer(), Constants.HAND_SIZE);

			setFlagIsMatchRunning(true);
		}
	}

	@Override
	public void matchEnd(boolean needPlayerInput) {
		if (!needPlayerInput) {
			LOGGER.debug("{} matchEnd()", this);
			setFlagIsMatchRunning(false);
		}
	}

	@Override
	public void phaseBegin(boolean alreadyRunning) {
		if (!alreadyRunning) {
			LOGGER.debug("{} phaseBegin()", this);
			getCurrentTurn().phaseBegin();
		}
	}

	@Override
	public void phaseEnd(boolean hasNextStep, boolean isPhaseRunning) {
		if (!hasNextStep && isPhaseRunning) {
			LOGGER.debug("{} phaseEnd()", this);
			getCurrentTurn().phaseEnd();
		}
	}

	@Override
	public BooleanProperty propertyFlagNeedPlayerInput() {
		return propertyNeedPlayerInput;
	}

	public ListProperty<Attack> propertyListAttacks() {
		return propertyListAttacks;
	}

	public void resetFlagsPassedPriority() {
		getPlayerActive().setFlagPassedPriority(false);
		getPlayerNonactive().setFlagPassedPriority(false);
	}

	/**
	 * Setzt den Spielerstatus nach dem Beschwören eines Zaubers, dem Aktivieren
	 * einer Fähigkeit, dem Ausführen einer Spezialhandlung oder dem Abgeben der
	 * Priorität zurück auf den ursprünglichen "aktiv"/"nichtaktiv"-Status.
	 *
	 * @param player
	 *            Der Spieler, dessen Status zurückgesetzt werden soll.
	 */
	public void resetPlayerState(IsPlayer player) {
		final boolean isCombatPhase = getCurrentPhase().isCombatPhase();
		if (isPlayerActive(player)) {
			player.setPlayerState(isCombatPhase ? PlayerState.ATTACKING : PlayerState.ACTIVE);
		} else {
			player.setPlayerState(isCombatPhase ? PlayerState.DEFENDING : PlayerState.NONACTIVE);
		}
	}

	public void setFlagIsMatchRunning(boolean flagIsMatchRunning) {
		LOGGER.trace("{} setFlagIsMatchRunning() -> {}", this, flagIsMatchRunning);
		this.propertyMatchRunning.set(flagIsMatchRunning);
	}

	public void setFlagNeedPlayerInput(boolean flagNeedPlayerInput) {
		LOGGER.debug("{} setFlagNeedPlayerInput() -> {}", this, flagNeedPlayerInput);
		this.propertyNeedPlayerInput.set(flagNeedPlayerInput);
	}

	@Override
	public void skipCurrentPhase() {
		LOGGER.debug("{} skipCurrentPhase()", this);
		getCurrentPhase().setFlagSkipped(false);
	}

	@Override
	public void skipCurrentStep() {
		LOGGER.debug("{} skipCurrentStep()", this);
		getCurrentStep().setFlagSkipped(false);
	}

	@Override
	public void stepBegin(boolean alreadyRunning) {
		if (!isStepRunning()) {
			LOGGER.debug("{} stepBegin()", this);
			getCurrentTurn().stepBegin();
		}
	}

	@Override
	public void stepEnd() {
		LOGGER.debug("{} stepEnd()", this);
		getCurrentTurn().stepEnd();
	}

	@Override
	public String toString() {
		return new StringBuilder("[").append("r=[").append(getTurnNumber()).append("] p=[").append(getCurrentPhase())
				.append("] s=[").append(getCurrentStep()).append("] ap=").append(getPlayerActive()).append("]")
				.toString();
	}

	@Override
	public void turnBegin(boolean alreadyRunning) {
		if (!alreadyRunning) {
			LOGGER.debug("{} turnBegin()", this);
			if (getTurnNumber() == 0) {
				setPlayerActive(determinePlayerStarting());
			} else {
				propertyListTurns.add(new Turn(getCurrentTurn()));
				setPlayerActive(determinePlayerActive());
			}
			getCurrentTurn().turnBegin();
		}
	}

	@Override
	public void turnEnd(boolean hasNextPhase, boolean hasNextStep) {
		if (!hasNextPhase && !hasNextStep) {
			LOGGER.debug("{} turnEnd()", this);
			getCurrentTurn().turnEnd();
		}

	}

	@Override
	public void update() {
		if (getFlagNeedPlayerInput()) {
			return;
		}

		// Starte Match, wenn noch nicht gestartet.
		matchBegin(getFlagMatchRunning());

		// Starte neue Runde, falls keine läuft.
		turnBegin(isTurnRunning());

		if (!isPhaseRunning()) {
			// Es läuft gerade keine Phase.

			if (getCurrentTurn().hasNextPhase()) {
				// Es gibt weitere Phasen

				setCurrentPhase();
				if (checkSkipPhase()) {
					skipCurrentPhase();
				} else {
					phaseBegin(isPhaseRunning());
				}
			} else {
				// Es gibt keine weiteren Phasen in dieser Runde.
				/**
				 * Hier darf eine Runde nicht beendet werden. Immer wenn eine
				 * neue Phase begonnen werden kann gibt es mindestens einen
				 * Schritt, der noch gespielt werdne kann. Ist das nicht der
				 * Fall, handelt es sich um eine Hauptphase. Dann muss die Phase
				 * erst ausgespielt werden.
				 */
			}
		}

		if (getCurrentPhase().isMain()) {
			// Bei der Phase handelt es sich um eine Hauptphase.
			/**
			 * Es werden keine Schritte gespielt. Spieler bekommen direkt
			 * Priorität.
			 */

			// Spiele Hauptphasen
			processStateBasedActions();
			determinePlayerPrioritised();
			if (getPlayerActive().getFlagPassedPriority() && getPlayerNonactive().getFlagPassedPriority()
					&& !getMagicStack().isEmpty()) {
				// Spieler haben gepasst, aber es liegt etwas auf dem Stack.

				processStack();
			} else if (!getPlayerActive().getFlagPassedPriority() || !getPlayerNonactive().getFlagPassedPriority()) {
				// Ein Spieler hat nicht noch gepasst.

				determinePlayerPrioritised();
				setFlagNeedPlayerInput(true);
			} else {
				// Spieler haben gespasst, es liegt nichts auf dem Stack.

				// Beende Phase
				phaseEnd(false, isPhaseRunning());
			}

		} else {
			// Bei der Phase handelt es sich nicht um eine Hauptphase.
			/**
			 * Es werden Schritte gespielt.
			 */

			// Setze neuen Schritt und prüfe, ob er übersprungen wird.
			if (!isStepRunning()) {
				// Es läuft gerade kein Schritt.

				if (getCurrentPhase().hasNextStep()) {
					// Die Phase hat noch Schritte, die gespielt werden können.

					setCurrentStep();
					if (checkSkipStep()) {
						skipCurrentStep();
					} else {
						stepBegin(isStepRunning());
					}
				} else {
					// Phase hat keine weiteren Schritte mehr.
					/**
					 * An dieser Stelle kann die aktuelle Phase beendet werden.
					 * Die Phase läuft noch, es gibt aber keine weiteren
					 * Schritte mehr.
					 */

					// Beende Phase
					phaseEnd(false, isPhaseRunning());
				}
			}

			if (checkPlayersGetPriority()) {
				// Spieler erhalten in diesem Schritt Priorität.

				// Spiele Schritt
				processStateBasedActions();
				determinePlayerPrioritised();
				if (getPlayerActive().getFlagPassedPriority() && getPlayerNonactive().getFlagPassedPriority()
						&& !getMagicStack().isEmpty()) {
					// Spieler haben gepasst, aber es liegt etwas auf dem Stack.

					processStack();
				} else if (!getPlayerActive().getFlagPassedPriority()
						|| !getPlayerNonactive().getFlagPassedPriority()) {
					// Ein Spieler hat nicht noch gepasst.

					determinePlayerPrioritised();
					setFlagNeedPlayerInput(true);
				} else {
					// Spieler haben gespasst, es liegt nichts auf dem Stack.

					// Beende Schritt
					stepEnd();
				}
			} else {
				// Spieler erhalten keine Priorität.

				// Beende Schritt
				stepEnd();

				// Beende Phase
				phaseEnd(getCurrentPhase().hasNextStep(), isPhaseRunning());
			}
		}

		// Beende Durchlauf, falls auf Spielereingabe gewartet wird.
		if (getFlagNeedPlayerInput()) {
			return;
		}

		// Beende Phase
		phaseEnd(getCurrentPhase().hasNextStep(), isPhaseRunning());

		// Beende Runde
		turnEnd(getCurrentTurn().hasNextPhase(), getCurrentPhase().hasNextStep());

		// Beende Match
		matchEnd(getFlagMatchRunning());
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

	/**
	 * Prüft, ob im aktuellen Spielschritt die Priorität an die Spieler erteilt
	 * wird.
	 *
	 * @return true, wenn Spieler im aktuellen Spielschritt die Priorität
	 *         erhalten.
	 */
	private boolean checkPlayersGetPriority() {
		return getCurrentStep().getFlagPlayersGetPriority();
	}

	/**
	 * Bestimmt den aktiven Spieler.
	 */
	private IsPlayer determinePlayerActive() {
		IsPlayer playerActive = (isPlayerActive(getPlayerHuman())) ? getPlayerComputer() : getPlayerHuman();
		LOGGER.debug("{} determinePlayerActive() -> {}", this, playerActive);
		return playerActive;
	}

	/**
	 * Bestimmt, welcher Spieler priorisiert wird. Hat ein Spieler im aktuellen
	 * Step einmal seine Priorität abgegeben, kann er nicht wieder priorisiert
	 * werden.
	 */
	private void determinePlayerPrioritised() {
		final IsPlayer playerActive = getPlayerActive();
		final IsPlayer playerNonactive = getPlayerOpponent(playerActive);

		if (!playerActive.getFlagPassedPriority() && !playerActive.isPaying()) {
			LOGGER.debug("{} determinePlayerPrioritised() -> {}", this, playerActive);
			setPlayerPrioritized(playerActive);
			return;
		} else if (!playerNonactive.getFlagPassedPriority() && playerNonactive.isPaying()) {
			LOGGER.debug("{} determinePlayerPrioritised() -> {}", this, playerNonactive);
			setPlayerPrioritized(playerNonactive);
			return;
		}

		if (!playerActive.getFlagPassedPriority() && !playerActive.isPaying()) {
			LOGGER.debug("{} determinePlayerPrioritised() -> {}", this, playerActive);
			setPlayerPrioritized(playerActive);
			return;
		} else if (!playerNonactive.getFlagPassedPriority() && !playerNonactive.isPaying()) {
			LOGGER.debug("{} determinePlayerPrioritised() -> {}", this, playerNonactive);
			setPlayerPrioritized(playerNonactive);
			return;
		}

		LOGGER.debug("{} determinePlayerPrioritised() -> Beide Spieler haben schon gepasst", this);
	}

	/**
	 * Bestimmt, welcher Spieler das Spiel beginnt.
	 */
	private IsPlayer determinePlayerStarting() {
		IsPlayer playerStarting = (new Random().nextInt(2) == 0) ? getPlayerComputer() : getPlayerHuman();
		LOGGER.debug("{} determinePlayerStarting() -> {}", this, playerStarting);
		return playerStarting;
	}

	/**
	 * Liefert eine Liste aller kontrollierten Kreaturen auf dem Spielfeld.
	 *
	 * @return eine Liste aller kontrollierten Kreaturen auf dem Spielfeld.
	 */
	private List<MagicPermanent> getControlledCreatures(IsPlayer player) {
		final List<MagicPermanent> result = new ArrayList<>();
		getListControlledCards(player).forEach(magicPermanent -> {
			if (magicPermanent.contains(ObjectType.CREATURE)) {
				result.add(magicPermanent);
			}
		});
		return result;
	}

	private Phase getCurrentPhase() {
		return getCurrentTurn().getCurrentPhase();
	}

	private Step getCurrentStep() {
		return getCurrentPhase().getCurrentStep();
	}

	private IsPlayer getPlayerOpponent(IsPlayer playerActive) {
		return getCurrentTurn().getPlayerOpponent(playerActive);
	}

	private int getTurnNumber() {
		return getCurrentTurn().getTurnNumber();
	}

	private boolean isPlayerActive(IsPlayer player) {
		return getCurrentTurn().isPlayerActive(player);
	}

	/**
	 * Durchläuft den Stack und ruft für jedes Element resolve(stackable) auf.
	 * Der Stack wird durchlaufen, jedes mal wenn beide Spieler die Priorität
	 * abgegeben haben (rule=405.5.)
	 */
	private void processStack() {
		LOGGER.debug("{} processStack()", this);
		final int sizeMagicStack = getMagicStack().getSize();
		for (int i = 0; i < sizeMagicStack; i++) {
			final IsStackable stackable = getMagicStack().peek();
			if (stackable.isPermanentSpell()) {
				getZoneBattlefield().add(magicPermanentFactory.create((MagicSpell) stackable));
			} else {
				stackable.resolve();
			}
			getMagicStack().pop();
		}
	}

	/**
	 * Arbeitet die StateBasedActions ab, die sich während der Zeit seit der
	 * letzten Prüfung angesammelt haben. Wird aufgerufen, bevor die Priorität
	 * neu bestimmt wird.
	 */
	private void processStateBasedActions() {
		LOGGER.debug("{} processStateBasedActions()", this);
		for (final StateBasedAction sba : setStateBasedActions) {
			switch (sba.getStateBasedActionType()) {
			case CREATURE_TOUGHNESS_ZERO:
				final SBACreatureToughnessZero sbactz = (SBACreatureToughnessZero) sba;
				ruleEnforcer.actionBury(getPlayer(sbactz.getPlayerControlling()), (MagicPermanent) sba.getSource());
				break;
			case PLAYER_CANT_DRAW:
				setFlagIsMatchRunning(false);
				break;
			case PLAYER_LIFE_ZERO:
				setFlagIsMatchRunning(false);
				break;
			default:
				break;
			}
		}
	}

	/**
	 * Setzt die neuen aktuelle Spielphase.
	 */
	private void setCurrentPhase() {
		getCurrentTurn().setCurrentPhase();
		LOGGER.trace("{} setCurrentPhase() -> {}", this, getCurrentPhase());
	}

	/**
	 * Setzt den neuen aktuellen Spielschritt.
	 */
	private void setCurrentStep() {
		getCurrentTurn().setCurrentStep();
		LOGGER.trace("{} setCurrentStep() -> {}", this, getCurrentStep());
	}

	private void setPlayerActive(IsPlayer playerActive) {
		LOGGER.trace("{} setPlayerActive({})", this, playerActive);
		getCurrentTurn().setPlayerActive(playerActive);
	}

	private void setPlayerPrioritized(IsPlayer playerPrioritized) {
		LOGGER.trace("{} setPlayerPrioritized({})", this, playerPrioritized);
		getCurrentTurn().setPlayerPrioritized(playerPrioritized);
	}

	boolean getFlagNeedPlayerInput() {
		return propertyFlagNeedPlayerInput().get();
	}

	@Override
	public IntegerProperty propertyTurnNumber() {
		return getCurrentTurn().propertyTurnNumber();
	}

	@Override
	public ObjectProperty<Phase> propertyCurrentPhase() {
		return getCurrentTurn().propertyCurrentPhase();
	}

	@Override
	public ObjectProperty<IsPlayer> propertyPlayerActive() {
		return getCurrentTurn().propertyPlayerActive();
	}

	@Override
	public ObjectProperty<IsPlayer> propertyPlayerPrioritized() {
		return getCurrentTurn().propertyPlayerPrioritized();
	}

	@Override
	public ObjectProperty<Step> propertyCurrentStep() {
		return getCurrentPhase().propertyCurrentStep();
	}
}
