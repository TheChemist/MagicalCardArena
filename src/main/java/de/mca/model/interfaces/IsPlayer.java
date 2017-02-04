package de.mca.model.interfaces;

import java.util.List;

import com.google.common.eventbus.EventBus;

import de.mca.PACastSpell;
import de.mca.PADiscard;
import de.mca.PASelectCostMap;
import de.mca.PASelectPermanent;
import de.mca.PlayerAction;
import de.mca.PlayerActionType;
import de.mca.SAPlayLand;
import de.mca.model.Deck;
import de.mca.model.MagicCard;
import de.mca.model.MagicPermanent;
import de.mca.model.MagicSpell;
import de.mca.model.ManaMapDefault;
import de.mca.model.StateBasedAction;
import de.mca.model.enums.ColorType;
import de.mca.model.enums.PlayerState;
import de.mca.model.enums.PlayerType;
import de.mca.model.enums.ZoneType;
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
	 * Die Lebenspunkte des Spielers werden um n erhöht.
	 *
	 * @param lifepoints
	 *            Anzahl, um welche die Lebenspunkte des Spielers erhöht werden.
	 */
	public void addLife(int lifepoints);

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

	/**
	 * Wird durch Spielerinput ausgelöst und legt eine Spieleraktion auf den
	 * Eventbus: Der Spieler hat ein Permanent aktiviert.
	 *
	 * @param magicPermanent
	 *            das aktivierte Permanent.
	 */
	public default void fireActivatePermanent(MagicPermanent magicPermanent) {
		getEventBus().post(new PASelectPermanent(this, magicPermanent, PlayerActionType.ACTIVATE_PERMANENT));
	}

	/**
	 * Wird durch Spielerinput ausgelöst und legt eine Spieleraktion auf den
	 * Eventbus: Der Spieler beschwört einen Zauberspruch.
	 *
	 * @param magicCard
	 *            der Zauberspruch.
	 */
	public default void fireCastSpell(MagicCard magicCard) {
		getEventBus().post(new PACastSpell(this, magicCard));
	}

	/**
	 * Wird durch Spielerinput ausgelöst und legt eine Spieleraktion auf den
	 * Eventbus: Der Spieler gibt auf.
	 */
	public default void fireConcede() {
		getEventBus().post(new PlayerAction(this, PlayerActionType.CONCEDE));
	}

	/**
	 * Wird durch Spielerinput ausgelöst und legt eine Spieleraktion auf den
	 * Eventbus: Der Spieler deklariert einen Angreifer.
	 *
	 * @param attacker
	 *            der Angreifer.
	 */
	public default void fireDeclareAttacker(MagicPermanent magicPermanent) {
		getEventBus().post(new PASelectPermanent(this, magicPermanent, PlayerActionType.DECLARE_ATTACKER));
	}

	/**
	 * Wird durch Spielerinput ausgelöst und legt eine Spieleraktion auf den
	 * Eventbus: Der Spieler deklariert einen Blocker.
	 *
	 * @param blocker
	 *            der Blocker.
	 */
	public default void fireDeclareBlocker(MagicPermanent blocker) {
		getEventBus().post(new PASelectPermanent(this, blocker, PlayerActionType.DECLARE_BLOCKER));
	}

	/**
	 * Wird durch Spielerinput ausgelöst und legt eine Spieleraktion auf den
	 * Eventbus: Der Spieler wirft eine Karte ab.
	 *
	 * @param magicCard
	 *            die abgeworfene Karte.
	 */
	public default void fireDiscard(MagicCard magicCard) {
		getEventBus().post(new PADiscard(this, magicCard));
	}

	/**
	 * Wird durch Spielerinput ausgelöst und legt eine Spieleraktion auf den
	 * Eventbus: Der Spieler hat möchte keine weiteren Angreifer deklarieren.
	 */
	public default void fireEndDeclareAttackers() {
		getEventBus().post(new PlayerAction(this, PlayerActionType.END_DECLARE_ATTACKERS));
	}

	/**
	 * Wird durch Spielerinput ausgelöst und legt eine Spieleraktion auf den
	 * Eventbus: Der Spieler möchte keine weiteren Blocker deklarieren.
	 */
	public default void fireEndDeclareBlockers() {
		getEventBus().post(new PlayerAction(this, PlayerActionType.END_DECLARE_BLOCKERS));
	}

	/**
	 * Wird durch Spielerinput ausgelöst und legt eine Spieleraktion auf den
	 * Eventbus: Der Spieler gibt die Priorität ab.
	 */
	public default void firePassPriority() {
		getEventBus().post(new PlayerAction(this, PlayerActionType.PASS_PRIORITY));
	}

	/**
	 * Wird durch Spielerinput ausgelöst und legt eine Spieleraktion auf den
	 * Eventbus: Der Spieler möchte ein Land spielen.
	 *
	 * @param magicCard
	 *            das Land.
	 */
	public default void firePlayLand(MagicCard magicCard) {
		getEventBus().post(new SAPlayLand(this, magicCard));
	}

	public default void fireSelectCostMap(MagicSpell spell) {
		getEventBus().post(new PASelectCostMap(this, spell));
	}

	/**
	 * Wird durch Spielerinput ausgelöst und legt eine Spieleraktion auf den
	 * Eventbus:
	 */
	public default void fireStateBasedAction(StateBasedAction sba) {
		getEventBus().post(sba);
	}

	public String getDisplayName();

	public EventBus getEventBus();

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

	public boolean getFlagPassedPriority();

	public boolean getFlagPlayedLand();

	public IsManaMap getManaCostAlreadyPaid();

	public IsManaMap getManaCostGoal();

	public IsManaMap getManaPool();

	public PlayerState getPlayerState();

	public PlayerType getPlayerType();

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

	public IntegerProperty propertyGraveSize();

	public IntegerProperty propertyHandSize();

	public IntegerProperty propertyLife();

	public ObjectProperty<PlayerState> propertyPlayerState();

	public void removeAllCards(ZoneType zoneType);

	public void removeCard(MagicCard magicCard, ZoneType zoneType);

	/**
	 * Die Lebenspunkte des Spielers werden um n erniedrigt.
	 *
	 * @param lifepoints
	 *            Anzahl, um welche die Lebenspunkte des Spielers erniedrigt
	 *            werden.
	 */
	public void removeLife(int life);

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

	public void setFlagPassedPriority(boolean flagPassedPriority);

	public void setFlagPlayedLand(boolean flagPlayedLand);

	public void setManaCostAlreadyPaid(ManaMapDefault manaMapDefault);

	public void setManaCostGoal(IsManaMap manaCostGoal);

	public void setPlayerState(PlayerState ps);

}