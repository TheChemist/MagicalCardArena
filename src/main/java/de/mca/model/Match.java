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
import de.mca.model.enums.ObjectType;
import de.mca.model.enums.PlayerState;
import de.mca.model.enums.PlayerType;
import de.mca.model.enums.ZoneType;
import de.mca.model.interfaces.IsAttackTarget;
import de.mca.model.interfaces.IsPlayer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
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
	private final FactoryMagicPermanent factoryMagicPermanent;
	/**
	 * Speichert den geteilten Stack.
	 */
	private final MagicStack magicStack;
	/**
	 * Speichert die Kartenanzahl im Battlefield.
	 */
	private final IntegerProperty propertyBattlefieldSize;
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
	 * Speichert die Kartenanzahl im Exil.
	 */
	private final IntegerProperty propertyExileSize;
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
	 * Speichert die Anzahl der Elemente auf dem Stack.
	 */
	private IntegerProperty propertyStackSize;
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

		this.factoryMagicPermanent = magicPermanentFactory;
		this.magicStack = magicStack;
		this.ruleEnforcer = ruleEnforcer;
		this.zoneBattlefield = zoneBattlefield;
		this.zoneExile = zoneFactory.create(PlayerType.NONE, ZoneType.EXILE);

		ruleEnforcer.setMatch(this);

		propertyBattlefieldSize = new SimpleIntegerProperty(0);
		propertyCurrentPhase = new SimpleObjectProperty<>(null);
		propertyCurrentStep = new SimpleObjectProperty<>(null);
		propertyCurrentTurn = new SimpleObjectProperty<>(null);
		propertyExileSize = new SimpleIntegerProperty(0);
		propertyMatchRunning = new SimpleBooleanProperty(false);
		propertyNeedPlayerInput = new SimpleBooleanProperty(false);
		propertyListAttacks = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
		propertyListAttackTargets = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
		propertyListTurns = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
		propertyPlayerActive = new SimpleObjectProperty<>(playerComputer);
		propertyPlayerPrioritized = new SimpleObjectProperty<>(playerComputer);
		propertyStackSize = new SimpleIntegerProperty(0);

		propertyListAttackTargets.add(playerComputer);
		propertyListAttackTargets.add(playerHuman);

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

	public MagicStack getZoneStack() {
		return magicStack;
	}

	public IntegerProperty propertyBattlefieldSize() {
		return propertyBattlefieldSize;
	}

	public ObjectProperty<Phase> propertyCurrentPhase() {
		return propertyCurrentPhase;
	}

	public ObjectProperty<Step> propertyCurrentStep() {
		return propertyCurrentStep;
	}

	public IntegerProperty propertyExileSize() {
		return propertyExileSize;
	}

	public BooleanProperty propertyFlagNeedPlayerInput() {
		return propertyNeedPlayerInput;
	}

	public ObjectProperty<IsPlayer> propertyPlayerActive() {
		return propertyPlayerActive;
	}

	public ObjectProperty<IsPlayer> propertyPlayerPrioritized() {
		return propertyPlayerPrioritized;
	}

	public IntegerProperty propertyStackSize() {
		return propertyStackSize;
	}

	public IntegerProperty propertyTurnNumber() {
		return getCurrentTurn().propertyTurnNumber();
	}

	@Override
	public String toString() {
		return new StringBuilder("[").append("r=[").append(getTurnNumber()).append("] p=[").append(getCurrentPhase())
				.append("] s=[").append(getCurrentStep()).append("] ap=[").append(getPlayerActive()).append("]]")
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
				 * Schritt, der noch gespielt werden kann. Ist das nicht der
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
			if (checkProcessStack()) {
				// Spieler haben gepasst, aber es liegt etwas auf dem Stack.

				ruleEnforcer.processStack();
			} else if (checkContinueRound()) {
				// Ein Spieler hat nicht noch gepasst.

				determinePlayerPrioritised();
			} else {
				// Spieler haben gespasst, es liegt nichts auf dem Stack.

				// Beende Phase
				phaseEnd(false, isPhaseRunning(), getFlagNeedPlayerInput());
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
						return;
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
					phaseEnd(false, isPhaseRunning(), getFlagNeedPlayerInput());
				}
			}

			if (checkPlayersGetPriority() && !getFlagNeedPlayerInput()) {
				// Spieler erhalten in diesem Schritt Priorität.

				// Spiele Schritt
				ruleEnforcer.processStateBasedActions();
				if (checkProcessStack()) {
					// Spieler haben gepasst, aber es liegt etwas auf dem Stack.

					ruleEnforcer.processStack();
				} else if (checkContinueRound()) {
					// Ein Spieler hat nicht noch gepasst.

					determinePlayerPrioritised();
				} else {
					// Spieler haben gespasst, es liegt nichts auf dem Stack.

					// Beende Schritt
					stepEnd(getFlagNeedPlayerInput());

					// Beende Phase
					phaseEnd(getCurrentPhase().hasNextStep(), isPhaseRunning(), getFlagNeedPlayerInput());
				}
			} else {
				// Spieler erhalten keine Priorität.

				// Beende Schritt
				stepEnd(getFlagNeedPlayerInput());

				// Beende Phase
				phaseEnd(getCurrentPhase().hasNextStep(), isPhaseRunning(), getFlagNeedPlayerInput());
			}
		}

		// Beende Runde
		turnEnd(getCurrentTurn().hasNextPhase(), getCurrentPhase().hasNextStep());

		// Beende Match
		matchEnd(getFlagMatchRunning());
	}

	private boolean checkContinueRound() {
		final boolean playerActivePassed = getPlayerActive().getFlagPassedPriority();
		final boolean playerNonactivePassed = getPlayerNonactive().getFlagPassedPriority();

		final boolean result = !playerActivePassed || !playerNonactivePassed;
		LOGGER.trace("{} checkContinueRound() -> {}", this, result);
		return result;
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

	private boolean checkProcessStack() {
		final boolean playerActivePassed = getPlayerActive().getFlagPassedPriority();
		final boolean playerNonactivePassed = getPlayerNonactive().getFlagPassedPriority();
		final boolean isStackEmpty = getZoneStack().isEmpty();

		final boolean result = playerActivePassed && playerNonactivePassed && !isStackEmpty;
		LOGGER.trace("{} checkProcessStack() -> {}", this, result);
		return result;
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
				: getPlayer(PlayerType.HUMAN);
		LOGGER.debug("{} determinePlayerActive() -> {}", this, playerActive);
		return playerActive;
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
		getZoneBattlefield().getAll(player.getPlayerType()).forEach(magicPermanent -> {
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

	private IsPlayer getPlayerPrioritized() {
		return propertyPlayerPrioritized().get();
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
		// TODO HIGH Was passiert, wenn ein Match aus ist?
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
	private void phaseEnd(boolean hasNextStep, boolean isPhaseRunning, boolean needPlayerInput) {
		if (!hasNextStep && isPhaseRunning && !needPlayerInput) {
			LOGGER.debug("{} phaseEnd()", this);
			getCurrentTurn().phaseEnd();
		}
	}

	private ObjectProperty<Turn> propertyCurrentTurn() {
		return propertyCurrentTurn;
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

	private void setCurrentTurn(Turn turn) {
		propertyCurrentTurn.set(turn);
		propertyListTurns.add(turn);
	}

	private void setPlayerActive(IsPlayer playerActive) {
		LOGGER.trace("{} setPlayerActive({})", this, playerActive);
		propertyPlayerActive().set(playerActive);
		getPlayerActive().setPlayerState(PlayerState.ACTIVE);
		getPlayerNonactive().setPlayerState(PlayerState.NONACTIVE);
	}

	private void setPlayerPrioritized(IsPlayer playerPrioritized) {
		LOGGER.trace("{} setPlayerPrioritized({})", this, playerPrioritized);
		propertyPlayerPrioritized().set(playerPrioritized);

		// Setze Input Flag.
		setFlagNeedPlayerInput(true, "setPlayerPrioritized()");

		getPlayerPrioritized().setPlayerState(PlayerState.PRIORITIZED);
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
	 * gefeuert und die Prioritäts-Flags der Spieler zurück gesetzt. Ein Schritt
	 * kann beendet werden, wenn kein Spielerinput mehr benötigt wird. Das wird
	 * vor allem im letzten Schritt (Aufräumen) relevant.
	 */

	private void stepEnd(boolean needPlayerInput) {
		if (!needPlayerInput) {
			LOGGER.debug("{} stepEnd()", this);
			getCurrentTurn().stepEnd();
		}
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

	/**
	 * Fügt dem Match einen neuen Angriff hinzu, der zu gegebener Zeit
	 * durchgeführt wird.
	 *
	 * @param attack
	 *            ein Hilfsobjekt, das alle relevanten Informationen zu einen
	 *            Angriff kapselt.
	 */
	void addAttack(Attack attack) {
		LOGGER.debug("{} addAttack({})", this, attack);
		propertyListAttacks.add(attack);
	}

	void addCard(MagicCard magicCard, ZoneType zoneType) {
		if (zoneType.equals(ZoneType.BATTLEFIELD)) {
			getZoneBattlefield().add(factoryMagicPermanent.create(magicCard));
			propertyBattlefieldSize().set(getZoneBattlefield().getSize());
		} else if (zoneType.equals(ZoneType.EXILE)) {
			getZoneExile().add(magicCard);
			propertyExileSize().set(getZoneExile().getSize());
		}
	}

	void declareBlocker(int attackIndex, MagicPermanent blocker) {
		propertyListAttacks.get(attackIndex).blockerAdd(blocker);
	}

	/**
	 * Bestimmt, welcher Spieler priorisiert wird. Hat ein Spieler im aktuellen
	 * Step einmal seine Priorität abgegeben, kann er nicht wieder priorisiert
	 * werden.
	 */
	void determinePlayerPrioritised() {
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

	Phase getCurrentPhase() {
		return propertyCurrentPhase().get();
	}

	boolean getFlagNeedPlayerInput() {
		return propertyFlagNeedPlayerInput().get();
	}

	List<Attack> getListAttacks() {
		return propertyListAttacks.get();
	}

	List<IsAttackTarget> getListAttackTargets() {
		return propertyListAttackTargets.get();
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
		return getCurrentTurn().getPlayerOpponent(getPlayerActive().getPlayerType());
	}

	int getTotalAttackers() {
		return propertyListAttacks.size();
	}

	boolean isPlayerActive(IsPlayer player) {
		return getCurrentTurn().isPlayerActive(player);
	}

	void popSpell() {
		getZoneStack().pop();
		propertyStackSize().set(getZoneStack().getSize());
	}

	void pushSpell(MagicSpell magicSpell) {
		magicSpell.addZone(ZoneType.STACK);
		getZoneStack().push(magicSpell);
		propertyStackSize().set(getZoneStack().getSize());
	}

	void removeCard(MagicCard magicCard, ZoneType zoneType) {
		if (zoneType.equals(ZoneType.BATTLEFIELD)) {
			// Karte vom Spielfeld entfernen.

			getZoneBattlefield().remove(factoryMagicPermanent.create(magicCard));
			propertyBattlefieldSize().set(getZoneBattlefield().getSize());

		} else if (zoneType.equals(ZoneType.EXILE)) {
			// Karte aus dem Exil entfernen

			getZoneExile().remove(magicCard);
			propertyExileSize().set(getZoneExile().getSize());

		} else {
			// Sollte nicht vorkommen

			throw new IllegalArgumentException("Kann Karte nicht aus " + zoneType.toString() + " entfernen!");
		}
	}

	void resetFlagsPassedPriority() {
		getPlayerActive().setFlagPassedPriority(false);
		getPlayerNonactive().setFlagPassedPriority(false);
	}

	void resetListAttacks() {
		propertyListAttacks.clear();
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
			player.setPlayerState(isCombatPhase ? PlayerState.SELECTING_ATTACKER : PlayerState.ACTIVE);
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

	void setFlagNeedPlayerInput(boolean flagNeedPlayerInput, String caller) {
		LOGGER.trace("{} setFlagNeedPlayerInput({}, {})", this, flagNeedPlayerInput, caller);
		this.propertyNeedPlayerInput.set(flagNeedPlayerInput);
	}

	void skipStepCombatDamage() {
		getCurrentTurn().skipStepCombatDamage();
	}

	void skipStepDeclareBlockers() {
		getCurrentTurn().skipStepDeclareBlockers();
	}

	public RuleEnforcer getRuleEnforcer() {
		return ruleEnforcer;
	}
}
