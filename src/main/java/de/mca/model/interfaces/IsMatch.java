package de.mca.model.interfaces;

import java.util.List;

import de.mca.model.MagicCard;
import de.mca.model.MagicPermanent;
import de.mca.model.Phase;
import de.mca.model.Step;
import de.mca.model.ZoneDefault;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;

/**
 * Deklariert alle Methoden, die für die Steuerung des Ablaufs eines Matches
 * notwenig sind.
 *
 * @author Maximilian Werling
 *
 */
public interface IsMatch {

	/**
	 * Prüft, ob die anstehende Phase übersprungen wird.
	 *
	 * @return true, wenn die Phase übersprungen wird.
	 */
	public boolean checkSkipPhase();

	/**
	 * Prüft, ob der anstehende Spielschritt übersprungen wird.
	 *
	 * @return true, wenn der Spielschritt übersprungen wird.
	 */
	public boolean checkSkipStep();

	/**
	 * Prüft, ob die anstehende Runde übersprungen wird.
	 *
	 * @return true, wenn die Runde übersprungen wird.
	 */
	public boolean checkSkipTurn();

	/**
	 * Prüft, ob das Match zuende ist.
	 *
	 * @return true, wenn das Match zuende ist.
	 */
	public boolean getFlagMatchRunning();

	public List<IsAttackTarget> getListAttackTargets();

	public IsPlayer getPlayerComputer();

	public IsPlayer getPlayerHuman();

	public ZoneDefault<MagicPermanent> getZoneBattlefield();

	public ZoneDefault<MagicCard> getZoneExile();

	/**
	 * Prüft, ob eine Phase noch läuft.
	 *
	 * @return true, wenn die Phase noch läuft.
	 */
	public boolean isPhaseRunning();

	/**
	 * Prüft, ob eine Runde noch läuft.
	 *
	 * @return true, wenn die Runde noch läuft.
	 */
	public boolean isTurnRunning();

	/**
	 * Startet ein neues Spiel (rule = 103.).
	 */
	public void matchBegin(boolean alreadyRunning);

	/**
	 * Beendet das Spiel (rule = 104.).
	 */
	public void matchEnd(boolean isMatchRunning);

	/**
	 * Beginn eine neue Phase.
	 */
	public void phaseBegin(boolean alreadyRunning);

	/**
	 * Beendet eine Phase. Eine Phase kann beendet werden, wenn keine weiteren
	 * Schritt mehr in der Phase gespielt werden und die Runde gerade läuft.
	 */
	public void phaseEnd(boolean hasNextStep, boolean isPhaseRunning);

	public BooleanProperty propertyFlagNeedPlayerInput();

	/**
	 * Überspringe eine Phase.
	 */
	public void skipCurrentPhase();

	/**
	 * Überspringe einen Spielschritt.
	 */
	public void skipCurrentStep();

	/**
	 * Beginnt den neuen Spielschritt. Es werden alle TurnBasedActions gefeuert,
	 * die für den Beginn dieses Schrittes vorgesehen sind.
	 */
	public void stepBegin(boolean alreadyRunning);

	/**
	 * Beendet einen Spielschritt. Die verbleibenden TurnBasedActions werden
	 * gefeuert und die Prioritäts-Flags der Spieler zurück gesetzt.
	 */
	public void stepEnd();

	/**
	 * Beginnt eine neue Runde.
	 */
	public void turnBegin(boolean alreadyRunning);

	/**
	 * Beendet die aktuelle Runde. Der Phase- und Step-Iterator wird zurück
	 * gesetzt und die playedLandThisTurn-Flag für den aktiven Spieler auf false
	 * gesetzt. Eine Runde kann beendet werden, wenn keine weiteren Phasen (und
	 * in der letzten Phase keine weiteren Schritte) mehr gespielt werden
	 * können.
	 */
	public void turnEnd(boolean hasNextPhase, boolean hasNextStepp);

	public void update();

	public IntegerProperty propertyTurnNumber();

	public ObjectProperty<Phase> propertyCurrentPhase();

	public ObjectProperty<IsPlayer> propertyPlayerActive();

	public ObjectProperty<IsPlayer> propertyPlayerPrioritized();

	public ObjectProperty<Step> propertyCurrentStep();
}
