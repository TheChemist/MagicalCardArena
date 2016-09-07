package de.mca.model.interfaces;

import java.util.List;

import com.google.common.eventbus.EventBus;

import de.mca.PAActivateAbility;
import de.mca.PACastSpell;
import de.mca.PADeclareAttacker;
import de.mca.PADeclareBlocker;
import de.mca.PADiscard;
import de.mca.PlayerAction;
import de.mca.PlayerActionType;
import de.mca.SAPlayLand;
import de.mca.model.Attack;
import de.mca.model.CharacteristicAbility;
import de.mca.model.Deck;
import de.mca.model.MagicCard;
import de.mca.model.MagicPermanent;
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

	public void actionDraw();

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
	 * Liefert, ob der Spieler sich einen Spielertyp mit übergebenen Typ teilt.
	 *
	 * @param playerType
	 *            ein Spielertyp, der zum Vergleich herangezogen wird.
	 * @return true, wenn der Spielertyp des Spielers dem übergebenen
	 *         Spielertyps entspricht.
	 */
	public boolean equals(PlayerType playerType);

	public default void fireActivateActivatedAbility(CharacteristicAbility characteristicAbility) {
		getEventBus().post(new PAActivateAbility(this, characteristicAbility));
	}

	public default void fireCastSpell(MagicCard magicCard) {
		getEventBus().post(new PACastSpell(this, magicCard));
	}

	/**
	 * Lässt den Spieler das Spiel aufgeben. Der Spieler verliert dadurch das
	 * Spiel (rule = 104.3a).
	 */
	public default void fireConcede() {
		getEventBus().post(new PlayerAction(this, PlayerActionType.CONCEDE));
	}

	public default void fireDeclareAttacker(Attack attack) {
		getEventBus().post(new PADeclareAttacker(this, attack));
	}

	public default void fireDeclareBlocker(int attackerIndex, MagicPermanent blocker) {
		getEventBus().post(new PADeclareBlocker(this, attackerIndex, blocker));
	}

	public default void fireDiscard(MagicCard magicCard) {
		getEventBus().post(new PADiscard(this, magicCard));
	}

	/**
	 * Wird aufgerufen, wenn der Spieler alle Angreifer deklariert hat.
	 */
	public default void fireEndDeclareAttackers() {
		getEventBus().post(new PlayerAction(this, PlayerActionType.END_DECLARE_ATTACKERS));
	}

	/**
	 * Wird aufgerufen, wenn der Spieler alle Blocker deklariert hat.
	 */
	public default void fireEndDeclareBlockers() {
		getEventBus().post(new PlayerAction(this, PlayerActionType.END_DECLARE_BLOCKERS));
	}

	/**
	 * Lässt den Spieler seine Priorität abgeben.
	 */
	public default void firePassPriority() {
		getEventBus().post(new PlayerAction(this, PlayerActionType.PASS_PRIORITY));
	}

	public default void firePlayLand(MagicCard magicCard) {
		getEventBus().post(new SAPlayLand(this, magicCard));
	}

	public default void fireSelectBlockTarget(MagicPermanent blockTarget) {
		// TODO: Neue PA benötigt.
	}

	public default void fireStateBasedAction(StateBasedAction sba) {
		getEventBus().post(sba);
	}

	public List<MagicCard> getCardsHand();

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

	public List<MagicCard> getLibraryCards();

	public IsManaMap getManaCostAlreadyPaid();

	public IsManaMap getManaCostGoal();

	public IsManaMap getManaPool();

	public PlayerState getPlayerState();

	public PlayerType getPlayerType();

	public IsZone<MagicCard> getZoneGraveyard();

	public IsZone<MagicCard> getZoneHand();

	/**
	 * Liefert, ob der Spieler gerade eine Fähigkeit aktiviert.
	 *
	 * @return true, wenn der Spieler eine Fähigkeit aktiviert.
	 */
	public boolean isActivatingAbility();

	/**
	 * Liefert, ob der Spieler der aktive Spieler ist.
	 *
	 * @return true, wenn der Spieler der aktive Spieler ist.
	 */
	public boolean isActive();

	/**
	 * Liefert, ob der Spieler gerade der angreifende Spieler ist.
	 *
	 * @return true, wenn der Spieler der angreifende Spieler ist.
	 */
	public boolean isAttacking();

	/**
	 * Liefert, ob der Spieler gerade einen Zauberspruch beschwört.
	 *
	 * @return true, wenn der Spieler einen Zauberspruch beschwört.
	 */
	public boolean isCastingSpell();

	/**
	 * Liefert, ob der Spieler gerade der verteidigende Spieler ist.
	 *
	 * @return true, wenn der Spieler der verteidigende Spieler ist.
	 */
	public boolean isDefending();

	public boolean isDiscarding();

	/**
	 * Liefert, ob der Spieler der nichtaktive Spieler ist.
	 *
	 * @return true, wenn der Spieler der nichtaktive Spieler ist.
	 */
	public boolean isNonactive();

	/**
	 * Liefert, ob der Spieler gerade bezahlt.
	 *
	 * @return true, wenn der Spieler bezahlt.
	 */
	public boolean isPaying();

	/**
	 * Liefert, ob der Spieler priorisiert ist.
	 *
	 * @return true, wenn der Spieler priorisiert ist.
	 */
	public boolean isPrioritised();

	/**
	 * Liefert, ob der Spieler gerade eine Spezialaktion ausführt.
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