package de.mca.model;

import de.mca.model.enums.PlayerType;
import de.mca.model.interfaces.IsCombatant;
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
public class MagicPermanent extends MagicCard implements IsCombatant {

	private final int basePower;
	/**
	 * Speichert die Grundverteidigung der Kreatur, so wie sie auf der Karte
	 * steht.
	 */
	private final int baseToughness;
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
	/**
	 * Speichert den Schaden der der Kreatur zugefügt wurden.
	 */
	private final IntegerProperty propertyDamage;

	private MagicPermanent(int id, int basePower, int baseToughness) {
		super(id);
		this.basePower = basePower;
		this.baseToughness = baseToughness;
		this.playerControlling = new SimpleObjectProperty<>();
		propertyDamage = new SimpleIntegerProperty(0);
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

	MagicPermanent(MagicCard magicCard) {
		this(magicCard.getId(), magicCard.getPower(), magicCard.getToughness());
		setDisplayName(magicCard.getDisplayName());
		setFileName(magicCard.getFileName());
		setListActivatedAbilities(magicCard.propertyListAbilities());
		setListCostMaps(magicCard.propertyListCostMaps());
		setListEffects(magicCard.propertyListEffects());
		setListZonesVisited(magicCard.propertyListZonesVisited());
		setLoyalty(magicCard.getLoyalty());
		setPlayerControlling(magicCard.getPlayerOwning());
		setPlayerOwning(magicCard.getPlayerOwning());
		setPower(magicCard.getPower());
		setRarity(magicCard.getRarity());
		setSetColorTypes(magicCard.propertySetColorTypes());
		setSetObjectTypes(magicCard.propertySetObjectTypes());
		setSetSubTypes(magicCard.propertySetSubTypes());
		setSetSuperTypes(magicCard.propertySetSuperTypes());
		setToughness(magicCard.getToughness());
	}

	@Override
	public void applyCombatDamage() {
		setToughness(getToughness() - getDamage());
	}

	@Override
	public void assignCombatDamage(int combatDamage) {
		setDamage(getDamage() + combatDamage);
	}

	/**
	 * Prüft, ob die Kreatur angreifen kann. Die Kreatur kann angreifen, wenn
	 * sie ungetappt ist und nicht der SummoningSickness unterliegt. (rule =
	 * 508.1a).
	 *
	 * @return true, wenn die Kreatur angreifen kann.
	 */
	public boolean checkCanAttack() {
		return !getFlagTapped() && !getFlagHasSummoningSickness();
	}

	/**
	 * Prüft, ob die Kreatur verteidigen kann. Die Kreatur kann blocken, wenn
	 * sie nicht getappt ist.
	 *
	 * @return true, wenn die Kreatur verteidigen kann.
	 */
	public boolean checkCanBlock() {
		return !getFlagTapped();
	}

	@Override
	public int getBasePower() {
		return basePower;
	}

	@Override
	public int getBaseToughness() {
		return baseToughness;
	}

	@Override
	public int getDamage() {
		return propertyDamage().get();
	}

	@Override
	public boolean getFlagBlocked() {
		return flagBlocked.get();
	}

	public boolean getFlagHasSummoningSickness() {
		return flagSummoningSickness.get();
	}

	public boolean getFlagTapped() {
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

	@Override
	public IntegerProperty propertyDamage() {
		return propertyDamage;
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
	public void setFlagAttacking(boolean flagAttacking) {
		this.flagAttacking.set(flagAttacking);
	}

	public void setFlagAttackingAlone(boolean flagAttackingAlone) {
		this.flagAttackingAlone.set(flagAttackingAlone);
	}

	@Override
	public void setFlagBlocked(boolean flagBlocked) {
		this.flagBlocked.set(flagBlocked);
	}

	@Override
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

	@Override
	public void setFlagTapped(boolean flagTapped) {
		this.flagTapped.set(flagTapped);
	}

	public void setPlayerControlling(PlayerType playerControlling) {
		LOGGER.trace("{} setPlayerControlling({})", this, playerControlling);
		this.playerControlling.set(playerControlling);
	}

}
