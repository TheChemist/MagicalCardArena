package de.mca.model.interfaces;

import java.util.List;

import de.mca.model.Deck;
import de.mca.model.MagicCard;
import de.mca.model.ManaMapDefault;
import de.mca.model.Match;
import de.mca.model.RuleEnforcer;
import de.mca.model.enums.ColorType;
import de.mca.model.enums.PlayerState;
import de.mca.model.enums.PlayerType;
import de.mca.model.enums.ZoneType;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;

/**
 *
 * @author Maximilian Werling
 *
 */
public interface IsPlayer extends IsAttackTarget {

	public void addAllCards(List<MagicCard> cardList, ZoneType zoneType);

	public void addCard(MagicCard magicCard, ZoneType zoneType);

	/**
	 * Erhöht den Manavorrat der Farbe x um den Betrag n.
	 *
	 * @param color
	 *            die Farbe des Manas x.
	 * @param howMany
	 *            die Anzahl n.
	 */
	public void addMana(ColorType color, int howMany);

	/**
	 * Prüft, ob sich der Spieler einen Spielertyp mit einem übergebenem teilt.
	 *
	 * @param playerType
	 *            der Spielertyp.
	 * @return true, wenn der Spielertyp des Spielers dem übergebenen
	 *         Spielertyps entspricht.
	 */
	public boolean equals(PlayerType playerType);

	public String getDisplayName();

	/**
	 * Prüft, ob der Spieler noch im Begriff ist Angreifer zu deklarieren.
	 *
	 * @return true, wenn der Spieler noch Angreifer deklariert.
	 */
	public boolean getFlagDeclaringAttackers();

	/**
	 * Prüft, ob der Spieler noch im Begriff ist Verteidiger zu deklarieren.
	 *
	 * @return true, wenn der Spieler noch Verteidiger deklariert.
	 */
	public boolean getFlagDeclaringBlockers();

	public boolean getFlagNeedInput();

	public boolean getFlagPassedPriority();

	public boolean getFlagPlayedLand();

	public IsManaMap getManaCostAlreadyPaid();

	public IsManaMap getManaCostGoal();

	public IsManaMap getManaPool();

	public Match getMatch();

	public PlayerState getPlayerState();

	public PlayerType getPlayerType();

	public RuleEnforcer getRuleEnforcer();

	public IsZone<MagicCard> getZoneGraveyard();

	public IsZone<MagicCard> getZoneHand();

	public IsZone<MagicCard> getZoneLibrary();

	/**
	 * Prüft, ob der Spieler gerade eine Fähigkeit aktiviert.
	 *
	 * @return true, wenn der Spieler eine Fähigkeit aktiviert.
	 */
	public boolean isActivatingAbility();

	/**
	 * Prüft, ob der Spieler der aktive Spieler ist.
	 *
	 * @return true, wenn der Spieler der aktive Spieler ist.
	 */
	public boolean isActive();

	/**
	 * Prüft, ob der Spieler gerade der angreifende Spieler ist.
	 *
	 * @return true, wenn der Spieler der angreifende Spieler ist.
	 */
	public boolean isAttacking();

	/**
	 * Prüft, ob der Spieler gerade einen Zauberspruch beschwört.
	 *
	 * @return true, wenn der Spieler einen Zauberspruch beschwört.
	 */
	public boolean isCastingSpell();

	/**
	 * Prüft, ob der Spieler gerade der verteidigende Spieler ist.
	 *
	 * @return true, wenn der Spieler der verteidigende Spieler ist.
	 */
	public boolean isDefending();

	public boolean isDiscarding();

	/**
	 * Prüft, ob der Spieler der nichtaktive Spieler ist.
	 *
	 * @return true, wenn der Spieler der nichtaktive Spieler ist.
	 */
	public boolean isNonactive();

	/**
	 * Prüft, ob der Spieler gerade bezahlt.
	 *
	 * @return true, wenn der Spieler bezahlt.
	 */
	public boolean isPaying();

	/**
	 * Prüft, ob der Spieler priorisiert ist.
	 *
	 * @return true, wenn der Spieler priorisiert ist.
	 */
	public boolean isPrioritised();

	/**
	 * Prüft, ob der Spieler gerade eine Spezialaktion ausführt.
	 *
	 * @return true, wenn der Spieler eine Spezialaktion ausführt.
	 */
	public boolean isTakingSpecialAction();

	public IntegerProperty propertyDeckSize();

	public BooleanProperty propertyFlagNeedInput();

	public IntegerProperty propertyGraveSize();

	public IntegerProperty propertyHandSize();

	public IntegerProperty propertyLife();

	public ObjectProperty<PlayerState> propertyPlayerState();

	public void removeAllCards(ZoneType zoneType);

	public void removeCard(MagicCard magicCard, ZoneType zoneType);

	/**
	 * Erniedrigt den Manavorrat der Farbe x um den Betrag n.
	 *
	 * @param color
	 *            die Farbe des Manas x.
	 * @param howMany
	 *            die Anzahl n.
	 */
	public void removeMana(ColorType color, int howMany);

	/**
	 * Ein Aufruf leert den Manapool des Spielers.
	 */
	public void removeManaAll();

	public void setDeck(Deck deckComputer);

	public void setDisplayName(String nameComputer);

	public void setFlagDeclareAttackers(boolean flagDeclareAttackers);

	public void setFlagDeclareBlockers(boolean flagDeclareBlockers);

	public void setFlagNeedInput(boolean flagNeedInput);

	public void setFlagPassedPriority(boolean flagPassedPriority);

	public void setFlagPlayedLand(boolean flagPlayedLand);

	public void setManaCostAlreadyPaid(ManaMapDefault manaMapDefault);

	public void setManaCostGoal(IsManaMap manaCostGoal);

	public void setMatch(Match match);

	public void setPlayerState(PlayerState ps);

}