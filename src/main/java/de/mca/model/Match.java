package de.mca.model;

import java.util.ArrayList;
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
import de.mca.model.interfaces.IsPlayer;
import de.mca.model.interfaces.IsStackable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;

/**
 * Bildet ein Match ab, an dessen Ende ein Ergebnis feststeht. Ein Match läuft
 * so lange, bis ein Spieler gewinnt oder unentschieden gespielt wird. Die
 * Rundenstruktur wird in verschiedene Hilfsklassen ausgelagert und vollständig
 * gekapselt (Turn, Phase, Step).
 *
 * @author Maximilian Werling
 *
 */
public final class Match {

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
	 * Speichert die aktuelle Spielphase.
	 */
	private final ObjectProperty<Phase> propertyCurrentPhase;
	/**
	 * Speichert den aktuellen Spielschritt.
	 */
	private final ObjectProperty<Step> propertyCurrentStep;
	/**
	 * Speichert die aktuelle Runde.
	 */
	private final ObjectProperty<Turn> propertyCurrentTurn;
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
	 * Speichert den aktiven Spieler.
	 */
	private final ObjectProperty<IsPlayer> propertyPlayerActive;
	/**
	 * Speichert den priorisierten Spieler.
	 */
	private final ObjectProperty<IsPlayer> propertyPlayerPrioritized;
	/**
	 * Speichert den RuleEnforcer.
	 */
	private final RuleEnforcer ruleEnforcer;
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

		propertyListAttackTargets.add(playerComputer);
		propertyListAttackTargets.add(playerHuman);

		propertyCurrentTurn = new SimpleObjectProperty<>(null);
		propertyCurrentPhase = new SimpleObjectProperty<>(null);
		propertyCurrentStep = new SimpleObjectProperty<Step>(null);

		propertyPlayerActive = new SimpleObjectProperty<>(playerComputer);
		propertyPlayerPrioritized = new SimpleObjectProperty<>(playerComputer);

		setCurrentTurn(turnFactory.create(playerComputer, playerHuman, this));
	}

	public IsPlayer getPlayer(PlayerType playerType) {
		return getCurrentTurn().getPlayer(playerType);
	}

	public ZoneDefault<MagicPermanent> getZoneBattlefield() {
		return zoneBattlefield;
	}

	public ZoneDefault<MagicCard> getZoneExile() {
		return zoneExile;
	}

	public ObjectProperty<Phase> propertyCurrentPhase() {
		return propertyCurrentPhase;
	}

	public ObjectProperty<Step> propertyCurrentStep() {
		return propertyCurrentStep;
	}

	public ObjectProperty<IsPlayer> propertyPlayerActive() {
		return propertyPlayerActive;
	}

	public ObjectProperty<IsPlayer> propertyPlayerPrioritized() {
		return propertyPlayerPrioritized;
	}

	public IntegerProperty propertyTurnNumber() {
		return getCurrentTurn().propertyTurnNumber();
	}

	@Override
	public String toString() {
		return new StringBuilder("[").append("r=[").append(getTurnNumber()).append("] p=[").append(getCurrentPhase())
				.append("] s=[").append(getCurrentStep()).append("] ap=").append(getPlayerActive()).append("]")
				.toString();
	}

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
			ruleEnforcer.processStateBasedActions();
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
				ruleEnforcer.processStateBasedActions();
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
	 * Prüft, ob die anstehende Phase übersprungen wird.
	 *
	 * @return true, wenn die Phase übersprungen wird.
	 */
	private boolean checkSkipPhase() {
		return getCurrentPhase().getFlagSkipped();
	}

	/**
	 * Prüft, ob der anstehende Spielschritt übersprungen wird.
	 *
	 * @return true, wenn der Spielschritt übersprungen wird.
	 */
	private boolean checkSkipStep() {
		return getCurrentStep().getFlagStepSkipped();
	}

	/**
	 * Prüft, ob die anstehende Runde übersprungen wird.
	 *
	 * @return true, wenn die Runde übersprungen wird.
	 */
	private boolean checkSkipTurn() {
		return getCurrentTurn().getFlagTurnSkipped();
	}

	/**
	 * Bestimmt den aktiven Spieler.
	 */
	private IsPlayer determinePlayerActive() {
		IsPlayer playerActive = (isPlayerActive(getPlayer(PlayerType.HUMAN))) ? getPlayer(PlayerType.COMPUTER)
				: getPlayer(PlayerType.NONE);
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
		final IsPlayer playerNonactive = getPlayerNonactive();

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
		IsPlayer playerStarting = (new Random().nextInt(2) == 0) ? getPlayer(PlayerType.COMPUTER)
				: getPlayer(PlayerType.HUMAN);
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

	private Step getCurrentStep() {
		return propertyCurrentStep().get();
	}

	private Turn getCurrentTurn() {
		return propertyCurrentTurn().get();
	}

	/**
	 * Prüft, ob das Match zuende ist.
	 *
	 * @return true, wenn das Match zuende ist.
	 */
	private boolean getFlagMatchRunning() {
		return propertyMatchRunning.get();
	}

	private List<IsAttackTarget> getListAttackTargets() {
		return propertyListAttackTargets.get();
	}

	private List<MagicPermanent> getListLegalAttackers(IsPlayer player) {
		final List<MagicPermanent> result = new ArrayList<>();
		getControlledCreatures(player).forEach(magicPermanent -> {
			final MagicPermanent attacker = magicPermanent;
			if (attacker.checkCanAttack()) {
				result.add(attacker);
			}
		});
		return result;
	}

	private List<MagicPermanent> getListLegalBlockers(IsPlayer player) {
		final List<MagicPermanent> result = new ArrayList<>();
		for (final MagicPermanent mp : getControlledCreatures(player)) {
			final MagicPermanent blocker = mp;
			if (blocker.checkCanBlock()) {
				result.add(blocker);
			}
		}
		return result;
	}

	private MagicStack getMagicStack() {
		return magicStack;
	}

	private int getTurnNumber() {
		return getCurrentTurn().propertyTurnNumber().get();
	}

	/**
	 * Prüft, ob eine Phase noch läuft.
	 *
	 * @return true, wenn die Phase noch läuft.
	 */
	private boolean isPhaseRunning() {
		return getCurrentPhase().getFlagPhaseRunning();
	}

	private boolean isPlayerActive(IsPlayer player) {
		return getCurrentTurn().isPlayerActive(player);
	}

	private boolean isStepRunning() {
		return getCurrentStep().getFlagStepRunning();
	}

	/**
	 * Prüft, ob eine Runde noch läuft.
	 *
	 * @return true, wenn die Runde noch läuft.
	 */

	private boolean isTurnRunning() {
		return getCurrentTurn().getFlagTurnRunning();
	}

	/**
	 * Startet ein neues Spiel (rule = 103.).
	 */

	private void matchBegin(boolean alreadyRunning) {
		if (!alreadyRunning) {
			LOGGER.debug("{} matchBegin()", this);

			ruleEnforcer.actionDraw(getPlayer(PlayerType.COMPUTER), Constants.HAND_SIZE);
			ruleEnforcer.actionDraw(getPlayer(PlayerType.HUMAN), Constants.HAND_SIZE);

			setFlagIsMatchRunning(true);
		}
	}

	/**
	 * Beendet das Spiel (rule = 104.).
	 */

	private void matchEnd(boolean needPlayerInput) {
		if (!needPlayerInput) {
			LOGGER.debug("{} matchEnd()", this);
			setFlagIsMatchRunning(false);
		}
	}

	/**
	 * Beginn eine neue Phase.
	 */

	private void phaseBegin(boolean alreadyRunning) {
		if (!alreadyRunning) {
			LOGGER.debug("{} phaseBegin()", this);
			getCurrentTurn().phaseBegin();
		}
	}

	/**
	 * Beendet eine Phase. Eine Phase kann beendet werden, wenn keine weiteren
	 * Schritt mehr in der Phase gespielt werden und die Runde gerade läuft.
	 */

	private void phaseEnd(boolean hasNextStep, boolean isPhaseRunning) {
		if (!hasNextStep && isPhaseRunning) {
			LOGGER.debug("{} phaseEnd()", this);
			getCurrentTurn().phaseEnd();
		}
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

	private ObjectProperty<Turn> propertyCurrentTurn() {
		return propertyCurrentTurn;
	}

	private BooleanProperty propertyFlagNeedPlayerInput() {
		return propertyNeedPlayerInput;
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
		propertyPlayerActive().set(playerActive);
		playerActive.setPlayerState(PlayerState.ACTIVE);
		getCurrentTurn().getPlayerOpponent(playerActive).setPlayerState(PlayerState.NONACTIVE);
	}

	private void setPlayerPrioritized(IsPlayer playerPrioritized) {
		LOGGER.trace("{} setPlayerPrioritized({})", this, playerPrioritized);
		propertyPlayerPrioritized().set(playerPrioritized);
		playerPrioritized.setPlayerState(PlayerState.PRIORITIZED);
	}

	/**
	 * Überspringe eine Phase.
	 */

	private void skipCurrentPhase() {
		LOGGER.debug("{} skipCurrentPhase()", this);
		getCurrentPhase().setFlagSkipped(false);
	}

	/**
	 * Überspringe einen Spielschritt.
	 */

	private void skipCurrentStep() {
		LOGGER.debug("{} skipCurrentStep()", this);
		getCurrentStep().setFlagSkipped(false);
	}

	/**
	 * Beginnt den neuen Spielschritt. Es werden alle TurnBasedActions gefeuert,
	 * die für den Beginn dieses Schrittes vorgesehen sind.
	 */

	private void stepBegin(boolean alreadyRunning) {
		if (!isStepRunning()) {
			LOGGER.debug("{} stepBegin()", this);
			getCurrentTurn().stepBegin();
		}
	}

	/**
	 * Beendet einen Spielschritt. Die verbleibenden TurnBasedActions werden
	 * gefeuert und die Prioritäts-Flags der Spieler zurück gesetzt.
	 */

	private void stepEnd() {
		LOGGER.debug("{} stepEnd()", this);
		getCurrentTurn().stepEnd();
	}

	/**
	 * Beginnt eine neue Runde.
	 */

	private void turnBegin(boolean alreadyRunning) {
		if (!alreadyRunning) {
			LOGGER.debug("{} turnBegin()", this);
			if (getTurnNumber() == 0) {
				setPlayerActive(determinePlayerStarting());
			} else {
				setCurrentTurn(new Turn(getCurrentTurn(), this));
				setPlayerActive(determinePlayerActive());
			}
			getCurrentTurn().turnBegin();
		}
	}

	private void setCurrentTurn(Turn turn) {
		propertyCurrentTurn.set(turn);
		propertyListTurns.add(turn);
	}

	/**
	 * Beendet die aktuelle Runde. Der Phase- und Step-Iterator wird zurück
	 * gesetzt und die playedLandThisTurn-Flag für den aktiven Spieler auf false
	 * gesetzt. Eine Runde kann beendet werden, wenn keine weiteren Phasen (und
	 * in der letzten Phase keine weiteren Schritte) mehr gespielt werden
	 * können.
	 */

	private void turnEnd(boolean hasNextPhase, boolean hasNextStep) {
		if (!hasNextPhase && !hasNextStep) {
			LOGGER.debug("{} turnEnd()", this);
			getCurrentTurn().turnEnd();
		}

	}

	void addCard(MagicCard magicCard, ZoneType zoneType) {
		// TODO: Implementieren
	}

	boolean checkCanActivate(IsPlayer p, ActivatedAbility aa) {
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

	boolean checkCanCast(IsPlayer p, MagicSpell ms) {
		final boolean isActivePlayer = isPlayerActive(p);
		final boolean currentStepIsMain = getCurrentPhase().isMain();
		final boolean stackEmpty = magicStack.isEmpty();
		LOGGER.debug("{} checkCanCast({}, {}) = {}", this, p, ms, (isActivePlayer && currentStepIsMain && stackEmpty));
		return isActivePlayer && currentStepIsMain && stackEmpty;
	}

	boolean checkCanPlayLandCard(IsPlayer p) {
		final boolean isActivePlayer = isPlayerActive(p);
		final boolean currentStepIsMain = getCurrentPhase().isMain();
		final boolean stackEmpty = magicStack.isEmpty();
		final boolean landFlag = !getPlayerActive().getFlagPlayedLand();
		LOGGER.debug("{} checkCanPlayLandCard({}) = {}", this, p,
				(isActivePlayer && currentStepIsMain && landFlag && stackEmpty));
		return isActivePlayer && currentStepIsMain && landFlag && stackEmpty;
	}

	/**
	 * Fügt dem Spieler einen neuen Angriff auf ihn oder einen von ihm
	 * kontrollierten Planeswalker hinzu.
	 *
	 * @param attack
	 *            ein Angriff kapselt den Angreifer und das Angriffziel.
	 */
	void declareAttacker(Attack attack) {
		propertyListAttacks.add(attack);
	}

	void declareBlocker(int attackIndex, MagicPermanent blocker) {
		propertyListAttacks.get(attackIndex).blockerAdd(blocker);
	}

	List<MagicPermanent> getCardsBattlefield() {
		return zoneBattlefield.getAll();
	}

	Phase getCurrentPhase() {
		return propertyCurrentPhase().get();
	}

	boolean getFlagNeedPlayerInput() {
		return propertyFlagNeedPlayerInput().get();
	}

	List<Attack> getListAttacks() {
		return propertyListAttacks.get();
	}

	/**
	 * Liefert eine Liste aller kontrollierten Karten auf dem Spielfeld.
	 *
	 * @return eine Liste aller kontrollierten Karten auf dem Spielfeld.
	 */

	List<MagicPermanent> getListControlledCards(IsPlayer player) {
		return zoneBattlefield.getAll(player.getPlayerType());
	}

	/**
	 * Liefert den aktiven Spieler.
	 *
	 * @return den aktiven Spieler.
	 */
	IsPlayer getPlayerActive() {
		return propertyPlayerActive().get();
	}

	/**
	 * Liefert den nichtaktiven Spieler.
	 *
	 * @return den nichtaktiven Spieler.
	 */
	IsPlayer getPlayerNonactive() {
		return getCurrentTurn().getPlayerOpponent(getPlayerActive());
	}

	int getTotalAttackers() {
		return propertyListAttacks.size();
	}

	void pushSpell(MagicSpell magicSpell) {
		magicSpell.addZone(ZoneType.STACK);
		magicStack.push(magicSpell);
	}

	void removeCard(MagicPermanent magicPermanent, ZoneType zoneType) {
		// TODO: Implementieren
	}

	void resetFlagsPassedPriority() {
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
	void resetPlayerState(IsPlayer player) {
		final boolean isCombatPhase = getCurrentPhase().isCombatPhase();
		if (isPlayerActive(player)) {
			player.setPlayerState(isCombatPhase ? PlayerState.ATTACKING : PlayerState.ACTIVE);
		} else {
			player.setPlayerState(isCombatPhase ? PlayerState.DEFENDING : PlayerState.NONACTIVE);
		}
	}

	void setCurrentStep(Step step) {
		propertyCurrentStep().set(step);
	}

	void setFlagIsMatchRunning(boolean flagIsMatchRunning) {
		LOGGER.trace("{} setFlagIsMatchRunning() -> {}", this, flagIsMatchRunning);
		this.propertyMatchRunning.set(flagIsMatchRunning);
	}

	void setFlagNeedPlayerInput(boolean flagNeedPlayerInput) {
		LOGGER.debug("{} setFlagNeedPlayerInput() -> {}", this, flagNeedPlayerInput);
		this.propertyNeedPlayerInput.set(flagNeedPlayerInput);
	}

	void skipStepCombatDamage() {
		getCurrentTurn().skipStepCombatDamage();
	}

	void skipStepDeclareBlockers() {
		getCurrentTurn().skipStepDeclareBlockers();
	}
}
