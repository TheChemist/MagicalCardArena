package de.mca.model;

import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mca.Constants;
import de.mca.model.enums.PlayerState;
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
 * TODO: Benötigt wird ein GameStateRecorder, der nach jeder Änderung im
 * Spielzustand einen "Snapshot" des Spielfeldes, der Hand- und Deckkarten sowie
 * der Spielerzustände speichert. Benötigt wird dies zum einen für die KI, zum
 * anderen können so eine Zurück-Funktion und Illegale Handlungen abgebildet
 * werden.
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
	 * Speichert den geteilten Stack.
	 */
	private final MagicStack magicStack;
	/**
	 * Speichert den ersten Spieler.
	 */
	private final IsPlayer playerOne;
	/**
	 * Speichert den zweiten Spieler.
	 */
	private final IsPlayer playerTwo;
	/**
	 * Speichert die Kartenanzahl im Battlefield.
	 */
	private final IntegerProperty propertyBattlefieldSize;
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

	public Match(RuleEnforcer ruleEnforcer, IsPlayer playerTwo, IsPlayer playerOne) {
		this.ruleEnforcer = ruleEnforcer;
		this.ruleEnforcer.setMatch(this);

		this.playerOne = playerOne;
		this.playerTwo = playerTwo;

		magicStack = new MagicStack();
		propertyBattlefieldSize = new SimpleIntegerProperty(0);
		propertyCurrentTurn = new SimpleObjectProperty<>(new Turn(ruleEnforcer));
		propertyExileSize = new SimpleIntegerProperty(0);
		propertyMatchRunning = new SimpleBooleanProperty(false);
		propertyListAttacks = new SimpleListProperty<>(FXCollections.observableArrayList());
		propertyListAttackTargets = new SimpleListProperty<>(FXCollections.observableArrayList(playerOne, playerTwo));
		propertyListTurns = new SimpleListProperty<>(FXCollections.observableArrayList(getCurrentTurn()));
		propertyPlayerActive = new SimpleObjectProperty<>(playerOne);
		propertyPlayerPrioritized = new SimpleObjectProperty<>(playerOne);
		propertyStackSize = new SimpleIntegerProperty(0);
		zoneBattlefield = new ZoneDefault<>(ZoneType.BATTLEFIELD);
		zoneExile = new ZoneDefault<>(ZoneType.EXILE);
	}

	public RuleEnforcer getRuleEnforcer() {
		return ruleEnforcer;
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
		return getCurrentTurn().propertyCurrentPhase();
	}

	public ObjectProperty<Step> propertyCurrentStep() {
		return getCurrentTurn().getCurrentPhase().propertyCurrentStep();
	}

	public IntegerProperty propertyExileSize() {
		return propertyExileSize;
	}

	public ListProperty<Turn> propertyListTurns() {
		return propertyListTurns;
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
				.append("] s=[").append(getCurrentStep()).append("] ap=").append(getPlayerActive()).append("]")
				.toString();
	}

	public void update() {
		if (waitForInput()) {
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
					return;
				} else {
					phaseBegin(isPhaseRunning());
				}
			} else {
				// Es gibt keine weiteren Phasen in dieser Runde.
				/**
				 * Hier darf eine Runde nicht beendet werden. Immer wenn eine neue Phase
				 * begonnen werden kann gibt es mindestens einen Schritt, der noch gespielt
				 * werden kann. Ist das nicht der Fall, handelt es sich um eine Hauptphase. Dann
				 * muss die Phase erst ausgespielt werden.
				 */
			}
		} else {
			// Es läuft gerade eine Phase.

			if (getCurrentPhase().isMain()) {
				// Bei der Phase handelt es sich um eine Hauptphase.
				/**
				 * Es werden keine Schritte gespielt. Spieler bekommen direkt Priorität.
				 */

				ruleEnforcer.processStateBasedActions();
				if (checkProcessStack()) {
					// Spieler haben gepasst, aber es liegt etwas auf dem Stack.

					ruleEnforcer.processStack();
				} else if (checkContinueRound()) {
					// Ein Spieler hat nicht noch gepasst.

					determinePlayerPrioritised("update() Phase");
				} else {
					// Spieler haben gespasst, es liegt nichts auf dem Stack.

					// Beende Phase
					phaseEnd(false, isPhaseRunning(), waitForInput());
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
							// Runde wird übersprungen.

							skipCurrentStep();
							return;
						} else {
							stepBegin(isStepRunning());
						}
					} else {
						// Phase hat keine weiteren Schritte mehr.
						/**
						 * An dieser Stelle kann die aktuelle Phase beendet werden. Die Phase läuft
						 * noch, es gibt aber keine weiteren Schritte mehr.
						 */

						// Beende Phase
						phaseEnd(false, isPhaseRunning(), waitForInput());
					}
				} else {
					// Es läuft ein Schritt.

					if (checkPlayersGetPriority() && !waitForInput()) {
						// Spieler erhalten in diesem Schritt Priorität.

						// Spiele Schritt
						ruleEnforcer.processStateBasedActions();
						if (checkProcessStack()) {
							// Spieler haben gepasst, aber es liegt etwas auf dem Stack.

							ruleEnforcer.processStack();
						} else if (checkContinueRound()) {
							// Ein Spieler hat nicht noch gepasst.

							determinePlayerPrioritised("update() Step");
						} else {
							// Spieler haben gespasst, es liegt nichts auf dem Stack.

							// Beende Schritt
							stepEnd(waitForInput());

							// Beende Phase
							phaseEnd(getCurrentPhase().hasNextStep(), isPhaseRunning(), waitForInput());
						}

					} else {
						// Spieler erhalten keine Priorität.

						// Beende Schritt
						stepEnd(waitForInput());

						// Beende Phase
						phaseEnd(getCurrentPhase().hasNextStep(), isPhaseRunning(), waitForInput());
					}
				}
			}
		}

		// Beende Runde
		turnEnd(getCurrentTurn().hasNextPhase(), getCurrentPhase().hasNextStep(), getPlayerActive().isDiscarding());

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
	 * @return true, wenn Spieler im aktuellen Spielschritt die Priorität erhalten.
	 */
	private boolean checkPlayersGetPriority() {
		return getCurrentStep().getFlagPlayersGetPriority();
	}

	/**
	 * Prüft, ob der Stack abgearbeitet werden kann.
	 * 
	 * @return true, wenn beide Spieler gepasst haben und der Stack nicht leer ist.
	 */
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
		IsPlayer playerActive = (isPlayerActive(getPlayerOne())) ? getPlayerTwo() : getPlayerOne();
		LOGGER.trace("{} determinePlayerActive() -> {}", this, playerActive);
		return playerActive;
	}

	public IsPlayer getPlayerTwo() {
		return playerTwo;
	}

	public IsPlayer getPlayerOne() {
		return playerOne;
	}

	/**
	 * Bestimmt, welcher Spieler das Spiel beginnt.
	 */
	private IsPlayer determinePlayerStarting() {
		IsPlayer playerStarting = (new Random().nextInt(2) == 0) ? getPlayerOne() : getPlayerTwo();
		LOGGER.trace("{} determinePlayerStarting() -> {}", this, playerStarting);
		return playerStarting;
	}

	/**
	 * Liefert den aktuellen Spielschritt.
	 * 
	 * @return der aktuelle Spielschritt.
	 */
	private Step getCurrentStep() {
		return propertyCurrentStep().get();
	}

	/**
	 * Liefert die aktuelle Runde.
	 *
	 * @return die aktuelle Runde.
	 */
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

	/**
	 * Liefert die aktuelle Rundennummer.
	 * 
	 * @return die aktuelle Rundennummer.
	 */
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

	/**
	 * Prüft, ob ein Spielschritt läuft. Ein Schritt läuft, solange die Flag nicht
	 * gesetzt ist.
	 * 
	 * @return true, wenn der Spielschritt noch läuft.
	 */
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
	 * Startet ein neues Match, falls flagMatchRunning = false.
	 * 
	 * @param flagMatchRunning
	 *            flag, die anzeigt, ob das Match bereits läuft.
	 */
	private void matchBegin(boolean flagMatchRunning) {
		if (!flagMatchRunning) {
			LOGGER.trace("{} matchBegin()", this);

			ruleEnforcer.actionDraw(getPlayerOne(), Constants.HAND_SIZE);
			ruleEnforcer.actionDraw(getPlayerTwo(), Constants.HAND_SIZE);

			setFlagIsMatchRunning(true);
		}
	}

	/**
	 * Beendet ein Match, wenn flagMatchRunning = false.
	 * 
	 * @param flagMatchRunning
	 *            flag, die anzeigt, ob ein Match läuft.
	 */
	private void matchEnd(boolean flagMatchRunning) {
		if (!flagMatchRunning) {
			LOGGER.trace("{} matchEnd()", this);
			getRuleEnforcer().tb_endMatch();
		}
	}

	/**
	 * Beginnt eine neue Phase, wenn es eine nächste Phase gibt und keine Phase
	 * läuft (flagPhaseRunning = false).
	 * 
	 * @param flagPhaseRunning
	 *            Zeigt an, ob eine Phase bereits läuft.
	 */
	private void phaseBegin(boolean flagPhaseRunning) {
		if (!flagPhaseRunning) {
			LOGGER.trace("{} phaseBegin()", this);
			getCurrentTurn().phaseBegin();
		}
	}

	/**
	 * Beendet eine Phase. Eine Phase kann beendet werden, wenn keine weiteren
	 * Schritt mehr in der Phase gespielt werden und nicht auf Spielerinput gewartet
	 * wird.
	 * 
	 * @param hasNextStep
	 * @param flagPhaseRunning
	 * @param flagNeedPlayerInput
	 */
	private void phaseEnd(boolean hasNextStep, boolean flagPhaseRunning, boolean flagNeedPlayerInput) {
		if (!hasNextStep && flagPhaseRunning && !flagNeedPlayerInput) {
			LOGGER.trace("{} phaseEnd()", this);

			if (getCurrentPhase().isMain()) {
				getPlayerOne().setFlagPassedPriority(false);
				getPlayerTwo().setFlagPassedPriority(false);
			}

			getCurrentTurn().phaseEnd();
		}
	}

	private ObjectProperty<Turn> propertyCurrentTurn() {
		return propertyCurrentTurn;
	}

	/**
	 * Setzt die neue aktuelle Spielphase.
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

	/**
	 * Setzt die neue aktuelle Runde.
	 * 
	 * @param turn
	 *            die neue Runde.
	 */
	private void setCurrentTurn(Turn turn) {
		propertyCurrentTurn().set(turn);
		propertyListTurns().add(turn);
		LOGGER.trace("{} setCurrentTurn({})", this, turn);
	}

	/**
	 * Setzt den neuen aktiven Spieler.
	 * 
	 * @param playerActive
	 *            der neue aktive Spieler.
	 */
	private void setPlayerActive(IsPlayer playerActive) {
		LOGGER.trace("{} setPlayerActive({})", this, playerActive);
		propertyPlayerActive().set(playerActive);
		getPlayerActive().setPlayerState(PlayerState.ACTIVE);
		getPlayerNonactive().setPlayerState(PlayerState.NONACTIVE);
	}

	/**
	 * Setzt den neuen priorisierten Spieler.
	 * 
	 * @param playerPrioritized
	 *            der neue priorisierte Spieler.
	 */
	private void setPlayerPrioritized(IsPlayer playerPrioritized) {
		LOGGER.trace("{} setPlayerPrioritized({})", this, playerPrioritized);
		propertyPlayerPrioritized().set(playerPrioritized);

		// Setze Status und flag.
		playerPrioritized.setPlayerState(PlayerState.PRIORITIZED);
		playerPrioritized.setFlagNeedInput(true, "setPlayerPrioritized() in Match");
	}

	/**
	 * Überspringe eine Phase.
	 */
	private void skipCurrentPhase() {
		LOGGER.trace("{} skipCurrentPhase() -> {}", this, getCurrentPhase());
		getCurrentPhase().setFlagSkipped(false);
	}

	/**
	 * Überspringe einen Spielschritt.
	 */
	private void skipCurrentStep() {
		LOGGER.trace("{} skipCurrentStep() -> {}", this, getCurrentStep());
		getCurrentStep().setFlagSkipped(false);
	}

	/**
	 * Beginnt den neuen Spielschritt, wenn flagStepRunning = false. Es werden alle
	 * TurnBasedActions gefeuert, die für den Beginn dieses Schrittes vorgesehen
	 * sind.
	 * 
	 * @param flagStepRunning
	 *            Flag, die anzeigt, ob ein Schritt gerade läuft.
	 */
	private void stepBegin(boolean flagStepRunning) {
		if (!flagStepRunning) {
			LOGGER.trace("{} stepBegin()", this);
			getCurrentTurn().stepBegin();
		}
	}

	/**
	 * Beendet einen Spielschritt. Die verbleibenden TurnBasedActions werden
	 * gefeuert und die Prioritäts-Flags der Spieler zurück gesetzt. Ein Schritt
	 * kann beendet werden, wenn kein Spielerinput mehr benötigt wird. Das wird vor
	 * allem im letzten Schritt (Aufräumen) relevant.
	 * 
	 * @param flagNeedPlayerInput
	 *            Flag, die anzeigt, ob auf Spielerinput gewartet wird.
	 */
	private void stepEnd(boolean flagNeedPlayerInput) {
		if (!flagNeedPlayerInput) {
			LOGGER.trace("{} stepEnd()", this);

			getPlayerOne().setFlagPassedPriority(false);
			getPlayerTwo().setFlagPassedPriority(false);

			getCurrentTurn().stepEnd();
		}
	}

	/**
	 * Beginnt eine neue Runde, wenn flagTurnRunning = false. In der ersten Runde
	 * wird der Spieler, der das Spiel startet bestimmt und der Ziehschritt
	 * übersprungen. In den folgenden Runden wird der aktive Spieler bestimmt.
	 * 
	 * @param flagTurnRunning
	 *            Flag, die anzeigt, ob eine Runde läuft.
	 */
	private void turnBegin(boolean flagTurnRunning) {
		if (!flagTurnRunning) {
			LOGGER.trace("{} turnBegin()", this);
			if (getTurnNumber() == 0) {
				setPlayerActive(determinePlayerStarting());
				skipStepDraw();
			} else {
				setCurrentTurn(new Turn(getCurrentTurn()));
				setPlayerActive(determinePlayerActive());
			}
			getCurrentTurn().turnBegin();
		}
	}

	/**
	 * Beendet die aktuelle Runde. Der Phase- und Step-Iterator wird zurück gesetzt
	 * und die playedLandThisTurn-Flag für den aktiven Spieler auf false gesetzt.
	 * Eine Runde kann beendet werden, wenn keine weiteren Phasen (und in der
	 * letzten Phase keine weiteren Schritte) mehr gespielt werden können.
	 * 
	 * @param hasNextPhase
	 *            Zeigt an, ob die Runde weitere Phasen hat.
	 * @param hasNextStep
	 *            Zeigt an, ob die Phase weitere Schritte hat.
	 * @param playerDiscard
	 *            Zeigt an, ob der Spieler noch Karten abwerfen muss. TODO: Muss das
	 *            wirklich hier hin?
	 */
	private void turnEnd(boolean hasNextPhase, boolean hasNextStep, boolean playerDiscard) {
		if (!hasNextPhase && !hasNextStep && !playerDiscard) {
			LOGGER.trace("{} turnEnd()", this);

			getPlayerActive().setFlagPlayedLand(false);

			getCurrentTurn().turnEnd();
		}

	}

	/**
	 * Hilfsmethode, die bestimmt, ob auf Spielerinput gewartet wird.
	 * 
	 * @return true, wenn auf Input einer der beiden Spieler gewartet wird.
	 */
	private boolean waitForInput() {
		final IsPlayer playerActive = getPlayerActive();
		final IsPlayer playerNonactive = getPlayerNonactive();
		return playerActive.getFlagNeedInput() || playerNonactive.getFlagNeedInput()
				|| playerActive.getFlagDeclaringAttackers() || playerNonactive.getFlagDeclaringBlockers();
	}

	/**
	 * Fügt dem Match einen neuen Angriff hinzu, der zu gegebener Zeit durchgeführt
	 * wird.
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
			getZoneBattlefield().add(new MagicPermanent(magicCard));
			propertyBattlefieldSize().set(getZoneBattlefield().getSize());
		} else if (zoneType.equals(ZoneType.EXILE)) {
			getZoneExile().add(magicCard);
			propertyExileSize().set(getZoneExile().getSize());
		}
	}

	void declareBlocker(int attackIndex, MagicPermanent blocker) {
		propertyListAttacks.get(attackIndex).addBlocker(blocker);
	}

	/**
	 * Bestimmt, welcher Spieler priorisiert wird. Hat ein Spieler im aktuellen Step
	 * einmal seine Priorität abgegeben, kann er nicht wieder priorisiert werden.
	 */
	void determinePlayerPrioritised(String from) {
		final IsPlayer playerActive = getPlayerActive();
		final IsPlayer playerNonactive = getPlayerNonactive();

		if (!playerActive.getFlagPassedPriority() && !playerActive.isPaying()) {
			LOGGER.trace("{} determinePlayerPrioritised() -> {} coming from {}", this, playerActive, from);
			setPlayerPrioritized(playerActive);
			return;
		} else if (!playerNonactive.getFlagPassedPriority() && playerNonactive.isPaying()) {
			LOGGER.trace("{} determinePlayerPrioritised() -> {} coming from {}", this, playerNonactive, from);
			setPlayerPrioritized(playerNonactive);
			return;
		}

		if (!playerActive.getFlagPassedPriority() && !playerActive.isPaying()) {
			LOGGER.trace("{} determinePlayerPrioritised() -> {} coming from {}", this, playerActive, from);
			setPlayerPrioritized(playerActive);
			return;
		} else if (!playerNonactive.getFlagPassedPriority() && !playerNonactive.isPaying()) {
			LOGGER.trace("{} determinePlayerPrioritised() -> {} coming from {}", this, playerNonactive, from);
			setPlayerPrioritized(playerNonactive);
			return;
		}

		LOGGER.debug("{} determinePlayerPrioritised() -> Beide Spieler haben schon gepasst", this);
	}

	Attack getAttackByCombatant(MagicPermanent blockTarget) {
		for (Attack attack : getListAttacks()) {
			MagicPermanent attacker = (MagicPermanent) attack.getAttacker();
			if (attacker.equals(blockTarget)) {
				return attack;
			}
		}
		throw new IllegalStateException("No Attack found!");
	}

	Phase getCurrentPhase() {
		return propertyCurrentPhase().get();
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
		return getPlayerOpponent(getPlayerActive());
	}

	IsPlayer getPlayerOpponent(IsPlayer player) {
		return getPlayerOne().equals(player) ? getPlayerOne() : getPlayerTwo();
	}

	int getTotalAttackers() {
		return propertyListAttacks.size();
	}

	boolean isPlayerActive(IsPlayer player) {
		return player.equals(getPlayerActive());
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

			getZoneBattlefield().remove(new MagicPermanent(magicCard));
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

	void skipStepCombatDamage() {
		getCurrentTurn().skipStepCombatDamage();
	}

	void skipStepDeclareBlockers() {
		getCurrentTurn().skipStepDeclareBlockers();
	}

	void skipStepDraw() {
		getCurrentTurn().skipStepDraw();
	}
}
