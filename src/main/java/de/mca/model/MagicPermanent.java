package de.mca.model;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import de.mca.model.enums.PlayerType;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 *
 * @author Maximilian Werling
 *
 */
public class MagicPermanent extends MagicCard {

	/**
	 * Speichert den Schaden der der Kreatur zugefügt wurden.
	 */
	private final IntegerProperty damage;
	/**
	 * Speichert den EventBus.
	 */
	private final EventBus eventBus;
	/**
	 * Zeigt an, ob die Kreatur gerade angreift.
	 */
	private final BooleanProperty flagAttacking;
	/**
	 * Zeigt an, ob die Kreatur alleine angreift.
	 */
	private final BooleanProperty flagAttackingAlone;
	/**
	 * Zeigt an, ob eine Kreatur geblockt wird.
	 */
	private final BooleanProperty flagBlocked;
	/**
	 * Zeigt an, ob eine Kreatur gerade verteidigt.
	 */
	private final BooleanProperty flagBlocking;
	/**
	 * Zeigt an, ob die Kreatur alleine blockt.
	 */
	private final BooleanProperty flagBlockingAlone;
	/**
	 * Zeigt an, ob das Permanent gerade verdeckt liegt.
	 */
	private final BooleanProperty flagFaceDown;
	/**
	 * Zeigt an, ob das Permanent gerade geflippt ist.
	 */
	private final BooleanProperty flagFlipped;
	/**
	 * Zeigt an, ob das Permanent gerade phased out ist.
	 */
	private final BooleanProperty flagPhasedOut;
	/**
	 * Zeigt an, ob das Permanent noch mit Einsatzverzögerung behaftet ist.
	 */
	private final BooleanProperty flagSummoningSickness;
	/**
	 * Zeigt an, ob das Permanent getappt ist.
	 */
	private final BooleanProperty flagTapped;
	/**
	 * Speichert den Spielertyp des kontrollierenden Spielers.
	 * 
	 * @see http://magiccards.info/rule/109-objects.html#rule-109-4
	 */
	private final ObjectProperty<PlayerType> playerControlling;

	private MagicPermanent(int id, EventBus eventBus, PlayerType playerControlling) {
		super(id);
		this.eventBus = eventBus;
		this.playerControlling = new SimpleObjectProperty<>(playerControlling);
		damage = new SimpleIntegerProperty(0);
		flagAttacking = new SimpleBooleanProperty(false);
		flagAttackingAlone = new SimpleBooleanProperty(false);
		flagBlocked = new SimpleBooleanProperty(false);
		flagBlocking = new SimpleBooleanProperty(false);
		flagBlockingAlone = new SimpleBooleanProperty(false);
		flagFaceDown = new SimpleBooleanProperty(false);
		flagFlipped = new SimpleBooleanProperty(false);
		flagPhasedOut = new SimpleBooleanProperty(false);
		flagSummoningSickness = new SimpleBooleanProperty(true);
		flagTapped = new SimpleBooleanProperty(false);
	}

	@Inject
	MagicPermanent(EventBus eventBus, @Assisted MagicCard magicCard) {
		this(magicCard.getId(), eventBus, magicCard.getPlayerOwning());
		setDisplayName(magicCard.getDisplayName());
		setFileName(magicCard.getFileName());
		setListCharacteristicAbilities(magicCard.propertyListCharacteristicAbilities());
		setListCostMaps(magicCard.propertyListCostMaps());
		setListEffects(magicCard.propertyListEffects());
		setListZonesVisited(magicCard.propertyListZonesVisited());
		setLoyalty(magicCard.getLoyalty());
		setPlayerOwning(magicCard.getPlayerOwning());
		setPower(magicCard.getPower());
		setRarity(magicCard.getRarity());
		setSetColorTypes(magicCard.propertySetColorTypes());
		setSetObjectTypes(magicCard.propertySetObjectTypes());
		setSetSubTypes(magicCard.propertySetSubTypes());
		setSetSuperTypes(magicCard.propertySetSuperTypes());
		setToughness(magicCard.getToughness());
	}

	/**
	 * Prüft, ob die Kreatur angreifen kann. Die Kreatur kann angreifen, wenn
	 * sie ungetappt ist und nicht der SummoningSickness unterliegt. (rule =
	 * 508.1a).
	 *
	 * @return true, wenn die Kreatur angreifen kann.
	 */
	public boolean checkCanAttack() {
		return !isFlagTapped() && !isFlagSummoningSickness();
	}

	/**
	 * Prüft, ob die Kreatur verteidigen kann. Die Kreatur kann blocken, wenn
	 * sie nicht getappt ist.
	 *
	 * @return true, wenn die Kreatur verteidigen kann.
	 */
	public boolean checkCanBlock() {
		return !isFlagTapped();
	}

	public int getDamage() {
		return damage.get();
	}

	public PlayerType getPlayerControlling() {
		return playerControlling.get();
	}

	public boolean isFlagAttacking() {
		return flagAttacking.get();
	}

	public boolean isFlagAttackingAlone() {
		return flagAttackingAlone.get();
	}

	public boolean isFlagBlocked() {
		return flagBlocked.get();
	}

	public boolean isFlagBlocking() {
		return flagBlocking.get();
	}

	public boolean isFlagBlockingAlone() {
		return flagBlockingAlone.get();
	}

	public boolean isFlagFaceDown() {
		return flagFaceDown.get();
	}

	public boolean isFlagFlipped() {
		return flagFlipped.get();
	}

	public boolean isFlagPhasedOut() {
		return flagPhasedOut.get();
	}

	public boolean isFlagSummoningSickness() {
		return flagSummoningSickness.get();
	}

	public boolean isFlagTapped() {
		return flagTapped.get();
	}

	public void setDamage(int damage) {
		this.damage.set(damage);
	}

	public void setFlagAttacking(boolean flagAttacking) {
		this.flagAttacking.set(flagAttacking);
	}

	public void setFlagAttackingAlone(boolean flagAttackingAlone) {
		this.flagAttackingAlone.set(flagAttackingAlone);
	}

	public void setFlagBlocked(boolean flagBlocked) {
		this.flagBlocked.set(flagBlocked);
	}

	public void setFlagBlocking(boolean flagBlocking) {
		this.flagBlocking.set(flagBlocking);
	}

	public void setFlagBlockingAlone(boolean flagBlockingAlone) {
		this.flagBlockingAlone.set(flagBlockingAlone);
	}

	public void setFlagFaceDown(boolean flagFaceDown) {
		this.flagFaceDown.set(flagFaceDown);
		;
	}

	public void setFlagFlipped(boolean flagFlipped) {
		this.flagFlipped.set(flagFlipped);
	}

	public void setFlagPhasedOut(boolean flagPhasedOut) {
		this.flagPhasedOut.set(flagPhasedOut);
	}

	public void setFlagSummoningSickness(boolean flagHasSummoningSickness) {
		this.flagSummoningSickness.set(flagHasSummoningSickness);
	}

	public void setFlagTapped(boolean flagTapped) {
		this.flagTapped.set(flagTapped);
	}

	public void setPlayerControlling(PlayerType playerControlling) {
		this.playerControlling.set(playerControlling);
		;
	}

	/**
	 * Prüft, ob die Kreatur tödlich verwundet wurde.
	 *
	 * @return true, wenn der Schaden größer oder gleich groß ist wie die
	 *         Zähigkeit der Kreatur.
	 */
	private boolean checkLethalDamage() {
		return getDamage() >= getToughness();
	}

	/**
	 * Löst eine statusbasierte Aktion aus, wenn die Verteidigung der bleibenden
	 * Karte unter 0 fällt.
	 */
	private void fireCreatureKill() {
		getEventBus().post(new SBACreatureToughnessZero(this, getPlayerControlling()));
	}

	private EventBus getEventBus() {
		return eventBus;
	}

}
