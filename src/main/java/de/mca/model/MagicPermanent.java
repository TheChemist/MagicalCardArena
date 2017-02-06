package de.mca.model;

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
	 * Speichert die Grundverteidigung der Kreatur, so wie sie auf der Karte
	 * steht.
	 */
	private final int baseToughness;
	/**
	 * Speichert den Schaden der der Kreatur zugefügt wurden.
	 */
	private final IntegerProperty damage;
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

	private MagicPermanent(int id, PlayerType playerControlling) {
		super(id);
		this.playerControlling = new SimpleObjectProperty<>(playerControlling);
		baseToughness = getToughness();
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
	MagicPermanent(@Assisted MagicCard magicCard) {
		this(magicCard.getId(), magicCard.getPlayerOwning());
		setDisplayName(magicCard.getDisplayName());
		setFileName(magicCard.getFileName());
		setListCharacteristicAbilities(magicCard.propertyListAbilities());
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

	public void applyCombatDamage() {
		setToughness(getToughness() - getDamage());
	}

	/**
	 * Prüft, ob die Kreatur angreifen kann. Die Kreatur kann angreifen, wenn
	 * sie ungetappt ist und nicht der SummoningSickness unterliegt. (rule =
	 * 508.1a).
	 *
	 * @return true, wenn die Kreatur angreifen kann.
	 */
	public boolean checkCanAttack() {
		return !getFlagIsTapped() && !getFlagHasSummoningSickness();
	}

	/**
	 * Prüft, ob die Kreatur verteidigen kann. Die Kreatur kann blocken, wenn
	 * sie nicht getappt ist.
	 *
	 * @return true, wenn die Kreatur verteidigen kann.
	 */
	public boolean checkCanBlock() {
		return !getFlagIsTapped();
	}

	public int getBaseToughness() {
		return baseToughness;
	}

	public int getDamage() {
		return damage.get();
	}

	public boolean getFlagHasSummoningSickness() {
		return flagSummoningSickness.get();
	}

	public boolean getFlagIsTapped() {
		return flagTapped.get();
	}

	public PlayerType getPlayerControlling() {
		return playerControlling.get();
	}

	public boolean isActivatable() {
		return !getListAbilities().isEmpty();
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
	}

	@Override
	public void setToughness(int toughness) {
		super.setToughness(toughness);
	}

	// private EventBus getEventBus() {
	// return eventBus;
	// }

}
