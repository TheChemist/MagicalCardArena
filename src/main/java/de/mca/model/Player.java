package de.mca.model;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mca.model.enums.ColorType;
import de.mca.model.enums.PlayerState;
import de.mca.model.enums.PlayerType;
import de.mca.model.enums.StateBasedActionType;
import de.mca.model.enums.ZoneType;
import de.mca.model.interfaces.IsManaMap;
import de.mca.model.interfaces.IsPlayer;
import de.mca.model.interfaces.IsZone;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Maximilian Werling
 *
 */
public final class Player implements IsPlayer {

	/**
	 * Speichert den Logger.
	 */
	private final static Logger LOGGER = LoggerFactory.getLogger("Player");
	/**
	 * Speichert den Bezahlfortschritt des aktuellen Spruchs oder Fähigkeit.
	 */
	private IsManaMap manaCostAlreadyPaid;
	/**
	 * Speichert das aktuelle Bezahlziel.
	 */
	private IsManaMap manaCostGoal;
	/**
	 * Speichert den Manapool des Spielers.
	 */
	private final IsManaMap manaPool;
	/**
	 * Speichert den Spielertyp. Dient zur Identifikation.
	 */
	private final PlayerType playerType;
	/**
	 * Speichert den zugefügten Kampfschaden. Wird jede Runde zurück gesetzt.
	 */
	private final IntegerProperty propertyDamage;
	/**
	 * Speichert seperat noch Anzahl Karten in der Bibliothek.
	 */
	private final IntegerProperty propertyDeckSize;
	/**
	 * Speichert den Anzeigenamen des Spielers.
	 */
	private final StringProperty propertyDisplayName;
	/**
	 * Zeigt an, ob der Spieler gerade Angreifer deklariert.
	 */
	private final BooleanProperty propertyFlagDeclaringAttackers;
	/**
	 * Zeigt an, ob der Spieler gerade Verteidiger deklariert.
	 */
	private final BooleanProperty propertyFlagDeclaringBlockers;
	/**
	 * Zeigt an, ob von dem Spieler eine Eingabe erwartet wird.
	 */
	private final BooleanProperty propertyFlagNeedInput;
	/**
	 * Zeigt an, ob der Spieler bereits die Priorität abgegeben hat. Wird am
	 * Ende jeden Schrittes wieder zurück gesetzt.
	 */
	private final BooleanProperty propertyFlagPassedPriority;
	/**
	 * Zeigt an, ob der Spieler in der aktuellen Runde bereits ein Land gespielt
	 * hat. Wird am Ende jeder Runde wieder zurück gesetzt.
	 */
	private final BooleanProperty propertyFlagPlayedLand;
	/**
	 * Speichert separat die Anzahl Karten im Friedhof.
	 */
	private final IntegerProperty propertyGraveSize;
	/**
	 * Speichert separat die Anzahl Karten in der Hand.
	 */
	private final IntegerProperty propertyHandSize;
	/**
	 * Speichert die aktuellen Lebenspunkte des Spielers.
	 */
	private final IntegerProperty propertyLife;
	/**
	 * Speichert den aktuellen Spielerstatus.
	 */
	private final ObjectProperty<PlayerState> propertyPlayerState;
	private final RuleEnforcer ruleEnforcer;
	/**
	 * Speichert den Friedhof des Spielers.
	 */
	private final IsZone<MagicCard> zoneGraveyard;
	/**
	 * Speichert die Hand des Spielers.
	 */
	private final IsZone<MagicCard> zoneHand;
	/**
	 * Speichert die Bibliothek des Spielers.
	 */
	private final IsZone<MagicCard> zoneLibrary;

	public Player(RuleEnforcer ruleEnforcer, PlayerType playerType, String displayName, Deck deck) {
		this.playerType = playerType;
		this.ruleEnforcer = ruleEnforcer;

		propertyDamage = new SimpleIntegerProperty(0);
		propertyDeckSize = new SimpleIntegerProperty(0);
		propertyDisplayName = new SimpleStringProperty(displayName);
		propertyFlagDeclaringAttackers = new SimpleBooleanProperty(false);
		propertyFlagDeclaringBlockers = new SimpleBooleanProperty(false);
		propertyFlagPassedPriority = new SimpleBooleanProperty(false);
		propertyFlagNeedInput = new SimpleBooleanProperty(false);
		propertyFlagPlayedLand = new SimpleBooleanProperty(false);
		propertyGraveSize = new SimpleIntegerProperty(0);
		propertyHandSize = new SimpleIntegerProperty(0);
		propertyLife = new SimpleIntegerProperty(20);
		propertyPlayerState = new SimpleObjectProperty<>(PlayerState.NONACTIVE);
		zoneGraveyard = new ZoneDefault<>(playerType, ZoneType.GRAVEYARD);
		zoneHand = new ZoneDefault<>(playerType, ZoneType.HAND);
		zoneLibrary = new ZoneDefault<>(playerType, ZoneType.LIBRARY);

		manaPool = new ManaMapDefault();
		manaCostAlreadyPaid = new ManaMapDefault();
		manaCostGoal = new ManaMapDefault();

		addAllCards(deck.getCardsList(), ZoneType.LIBRARY);

		// Setze den Eigentümer jeder Karte
		getZoneLibrary().propertyListZoneCards().forEach(card -> card.setPlayerOwning(getPlayerType()));

		// Mische
		getZoneLibrary().shuffle();
	}

	@Override
	public void addAllCards(List<MagicCard> cardList, ZoneType zoneType) {
		switch (zoneType) {
		case GRAVEYARD:
			getZoneGraveyard().addAll(cardList);
			setGraveSize(getZoneGraveyard().getSize());
			break;
		case HAND:
			getZoneHand().addAll(cardList);
			setHandSize(getZoneHand().getSize());
			break;
		case LIBRARY:
			getZoneLibrary().addAll(cardList);
			setDeckSize(getZoneLibrary().getSize());
			break;
		default:
			break;
		}
	}

	@Override
	public void addCard(MagicCard magicCard, ZoneType zoneType) {
		switch (zoneType) {
		case GRAVEYARD:
			getZoneGraveyard().add(magicCard);
			setGraveSize(getZoneGraveyard().getSize());
			break;
		case HAND:
			getZoneHand().add(magicCard);
			setHandSize(getZoneHand().getSize());
			break;
		case LIBRARY:
			getZoneLibrary().add(magicCard);
			setDeckSize(getZoneLibrary().getSize());
			break;
		default:
			break;
		}
	}

	@Override
	public void addLife(int life) {
		setLife(getLife() + life);
	}

	@Override
	public void addMana(ColorType colorType, int howMuch) {
		for (int i = 0; i < howMuch; i++) {
			final IsManaMap manaLeftToPay = manaCostGoal.getDifference(manaCostAlreadyPaid);
			if (manaLeftToPay.get(colorType) > 0) {
				LOGGER.trace("{} manaAdd({}) -> Direkt eingezahlt ({})!", this, colorType, colorType);
				manaCostAlreadyPaid.manaAdd(colorType);
			} else if (manaLeftToPay.hasColorlessMana()) {
				LOGGER.trace("{} manaAdd({}) -> Direkt eingezahlt (farblos)!", this, colorType);
				manaCostAlreadyPaid.manaAdd(ColorType.NONE);
			} else {
				LOGGER.trace("{} manaAdd({}) -> Gelagert im Manapool!", this, colorType);
				manaPool.manaAdd(colorType);
			}
		}
	}

	@Override
	public boolean chechIsValidAttackTarget(MagicPermanent attacker) {
		return !equals(attacker.getPlayerControlling());
	}

	@Override
	public boolean equals(PlayerType playerType) {
		return getPlayerType().equals(playerType);
	}

	@Override
	public int getDamage() {
		return propertyDamage().get();
	}

	@Override
	public String getDisplayName() {
		return propertyDisplayName.get();
	}

	@Override
	public boolean getFlagDeclaringAttackers() {
		return propertyFlagDeclaringAttackers.get();
	}

	@Override
	public boolean getFlagDeclaringBlockers() {
		return propertyFlagDeclaringBlockers.get();
	}

	@Override
	public boolean getFlagNeedInput() {
		return propertyFlagNeedInput.get();
	}

	@Override
	public boolean getFlagPassedPriority() {
		return propertyFlagPassedPriority.get();
	}

	@Override
	public boolean getFlagPlayedLand() {
		return propertyFlagPlayedLand.get();
	}

	@Override
	public int getLife() {
		return propertyLife.get();
	}

	@Override
	public IsManaMap getManaCostAlreadyPaid() {
		return manaCostAlreadyPaid;
	}

	@Override
	public IsManaMap getManaCostGoal() {
		return manaCostGoal;
	}

	@Override
	public IsManaMap getManaPool() {
		return manaPool;
	}

	// @Override
	// public Match getMatch() {
	// return match;
	// }

	@Override
	public PlayerState getPlayerState() {
		return propertyPlayerState().get();
	}

	@Override
	public PlayerType getPlayerType() {
		return playerType;
	}

	@Override
	public IntegerProperty getPropertyLife() {
		return propertyLife;
	}

	@Override
	public RuleEnforcer getRuleEnforcer() {
		return ruleEnforcer;
	}

	@Override
	public IsZone<MagicCard> getZoneGraveyard() {
		return zoneGraveyard;
	}

	@Override
	public IsZone<MagicCard> getZoneHand() {
		return zoneHand;
	}

	@Override
	public IsZone<MagicCard> getZoneLibrary() {
		return zoneLibrary;
	}

	@Override
	public boolean isActivatingAbility() {
		return getPlayerState().equals(PlayerState.ACTIVATING_ABILITY);
	}

	@Override
	public boolean isActive() {
		return getPlayerState().equals(PlayerState.ACTIVE) || isAttacking();
	}

	@Override
	public boolean isAttacking() {
		return getPlayerState().equals(PlayerState.ATTACKING);
	}

	@Override
	public boolean isCastingSpell() {
		return getPlayerState().equals(PlayerState.CASTING_SPELL);
	}

	@Override
	public boolean isDefending() {
		return getPlayerState().equals(PlayerState.DEFENDING);
	}

	@Override
	public boolean isDiscarding() {
		return getPlayerState().equals(PlayerState.DISCARDING);
	}

	@Override
	public boolean isNonactive() {
		return getPlayerState().equals(PlayerState.NONACTIVE) || isDefending();
	}

	@Override
	public boolean isPaying() {
		return getPlayerState().equals(PlayerState.PAYING);
	}

	@Override
	public boolean isPrioritised() {
		return getPlayerState().equals(PlayerState.PRIORITIZED);
	}

	@Override
	public boolean isTakingSpecialAction() {
		return getPlayerState().equals(PlayerState.TAKING_SPECIAL_ACTION);
	}

	@Override
	public IntegerProperty propertyDamage() {
		return propertyDamage;
	}

	@Override
	public IntegerProperty propertyDeckSize() {
		return propertyDeckSize;
	}

	@Override
	public BooleanProperty propertyFlagNeedInput() {
		return propertyFlagNeedInput;
	}

	@Override
	public IntegerProperty propertyGraveSize() {
		return propertyGraveSize;
	}

	@Override
	public IntegerProperty propertyHandSize() {
		return propertyHandSize;
	}

	@Override
	public IntegerProperty propertyLife() {
		return propertyLife;
	}

	@Override
	public ObjectProperty<PlayerState> propertyPlayerState() {
		return propertyPlayerState;
	}

	@Override
	public void removeAllCards(ZoneType zoneType) {
		switch (zoneType) {
		case GRAVEYARD:
			getZoneGraveyard().removeAll();
			setGraveSize(getZoneGraveyard().getSize());
			break;
		case HAND:
			getZoneHand().removeAll();
			setHandSize(getZoneHand().getSize());
			break;
		case LIBRARY:
			getZoneLibrary().removeAll();
			setDeckSize(getZoneLibrary().getSize());
			break;
		default:
			break;
		}
	}

	@Override
	public void removeCard(MagicCard magicCard, ZoneType zoneType) {
		switch (zoneType) {
		case GRAVEYARD:
			getZoneGraveyard().remove(magicCard);
			setGraveSize(getZoneGraveyard().getSize());
			break;
		case HAND:
			getZoneHand().remove(magicCard);
			setHandSize(getZoneHand().getSize());
			break;
		case LIBRARY:
			getZoneLibrary().remove(magicCard);
			setDeckSize(getZoneLibrary().getSize());
			break;
		default:
			break;
		}
	}

	@Override
	public void removeMana(ColorType colorType, int howMuch) {
		LOGGER.trace("{} removeMana({}, {})", this, colorType, howMuch);
		for (int i = 0; i < howMuch; i++) {
			manaPool.manaRemove(colorType);
		}
	}

	@Override
	public void removeManaAll() {
		LOGGER.trace("{} removeAll()");
		manaPool.removeAll();
	}

	@Override
	public void resetDamage() {
		setDamage(0);
	}

	@Override
	public void setDamage(int damage) {
		LOGGER.debug("{} setDamage({})", this, damage);
		propertyDamage().set(damage > 0 ? damage : 0);
	}

	@Override
	public void setFlagDeclareAttackers(boolean flagDeclareAttackers) {
		LOGGER.trace("{} setFlagDeclareAttackers({})", this, flagDeclareAttackers);
		this.propertyFlagDeclaringAttackers.set(flagDeclareAttackers);
	}

	@Override
	public void setFlagDeclareBlockers(boolean flagDeclareBlockers) {
		LOGGER.trace("{} setFlagDeclareBlockers({})", this, flagDeclareBlockers);
		this.propertyFlagDeclaringBlockers.set(flagDeclareBlockers);
	}

	@Override
	public void setFlagNeedInput(boolean flagNeedInput) {
		LOGGER.debug("{} setFlagNeedInput({})", this, flagNeedInput);
		propertyFlagNeedInput().set(flagNeedInput);
	}

	@Override
	public void setFlagPassedPriority(boolean flagPassedPriority) {
		LOGGER.trace("{} setFlagPassedPriority({})", this, flagPassedPriority);
		this.propertyFlagPassedPriority.set(flagPassedPriority);
	}

	@Override
	public void setFlagPlayedLand(boolean flagPlayedLand) {
		LOGGER.trace("{} setFlagPlayedLand({})", this, flagPlayedLand);
		this.propertyFlagPlayedLand.set(flagPlayedLand);
	}

	@Override
	public void setLife(int life) {
		LOGGER.trace("{} setLife({})", this, life);
		propertyLife().set(life);
		if (life < 1) {
			getRuleEnforcer().addStateBasedAction(new StateBasedAction(this, StateBasedActionType.PLAYER_LIFE_ZERO));
		}
	}

	@Override
	public void setManaCostAlreadyPaid(ManaMapDefault manaCostAlreadyPaid) {
		LOGGER.trace("{} setManaCostAlreadyPaid({})", this, manaCostAlreadyPaid);
		this.manaCostAlreadyPaid = manaCostAlreadyPaid;
	}

	@Override
	public void setManaCostGoal(IsManaMap manaCostGoal) {
		LOGGER.trace("{} setManaCostGoal({})", this, manaCostGoal);
		this.manaCostGoal = manaCostGoal;
	}

	// @Override
	// public void setMatch(Match match) {
	// this.match = match;
	// }

	@Override
	public void setPlayerState(PlayerState playerState) {
		LOGGER.debug("{} setPlayerState({})", this, playerState);
		propertyPlayerState().set(playerState);
	}

	@Override
	public void substractLife(int life) {
		setLife(getLife() - life);
	}

	@Override
	public String toString() {
		return getDisplayName();
		// TODO LOW Detaillierte Status-Ausgabe
		// new StringBuilder("[pt=[").append(getPlayerType()).append("]
		// ps=[").append(getPlayerState())
		// .append("] l=[").append(getLife()).append("]]").toString();
	}

	private void setDeckSize(int deckSize) {
		propertyDeckSize().set(deckSize);
	}

	private void setGraveSize(int graveSize) {
		propertyGraveSize().set(graveSize);
	}

	private void setHandSize(int handSize) {
		propertyHandSize().set(handSize);
	}

}
