package de.mca.model;

import de.mca.model.interfaces.IsCombatant;
import de.mca.model.interfaces.IsPlayer;

/**
 *
 * @author Maximilian Werling
 *
 */
public class MagicPermanent extends MagicCard implements IsCombatant {

	/**
	 * Speichert die Grundstärke der Kreatur, so wie sie auf der Karte steht.
	 */
	private final int basePower;
	/**
	 * Speichert die Grundverteidigung der Kreatur, so wie sie auf der Karte steht.
	 */
	private final int baseToughness;
	/**
	 * Zeigt an, ob die Kreatur gerade angreift.
	 */
	private boolean flagAttacking;
	/**
	 * Zeigt an, ob die Kreatur alleine angreift.
	 */
	private boolean flagAttackingAlone;
	/**
	 * Zeigt an, ob eine Kreatur geblockt wird.
	 */
	private boolean flagBlocked;
	/**
	 * Zeigt an, ob eine Kreatur gerade verteidigt.
	 */
	private boolean flagBlocking;
	/**
	 * Zeigt an, ob die Kreatur alleine blockt.
	 */
	private boolean flagBlockingAlone;
	/**
	 * Zeigt an, ob das Permanent noch mit Einsatzverzögerung behaftet ist.
	 */
	private boolean flagSummoningSickness;
	/**
	 * Zeigt an, ob das Permanent getappt ist.
	 */
	private boolean flagTapped;
	/**
	 * Speichert den Spielertyp des kontrollierenden Spielers.
	 *
	 * @see http://magiccards.info/rule/109-objects.html#rule-109-4
	 */
	private transient IsPlayer playerControlling;
	/**
	 * Speichert den Schaden der der Kreatur zugefügt wurden.
	 */
	private int damage;

	private MagicPermanent(int id, int basePower, int baseToughness) {
		super(id);
		this.basePower = basePower;
		this.baseToughness = baseToughness;
		this.playerControlling = null;
		damage = 0;
		flagAttacking = false;
		flagAttackingAlone = false;
		flagBlocked = false;
		flagBlocking = false;
		flagBlockingAlone = false;
		flagSummoningSickness = false;
		flagTapped = false;
	}

	MagicPermanent(MagicCard magicCard) {
		this(magicCard.getId(), magicCard.getPower(), magicCard.getToughness());
		setDisplayName(magicCard.getDisplayName());
		setFileName(magicCard.getFileName());
		setListActivatedAbilities(magicCard.getListActivatedAbilities());
		setListCostMaps(magicCard.getListCostMaps());
		setListEffects(magicCard.getListEffects());
		setListZonesVisited(magicCard.getListZonesVisited());
		setLoyalty(magicCard.getLoyalty());
		setPlayerControlling(magicCard.getPlayerOwning());
		setPlayerOwning(magicCard.getPlayerOwning());
		setPower(magicCard.getPower());
		setRarity(magicCard.getRarity());
		setSetColorTypes(magicCard.getSetColorType());
		setSetObjectTypes(magicCard.getSetObjectTypes());
		setSetSubTypes(magicCard.getSetSubTypes());
		setSetSuperTypes(magicCard.getSetSuperTypes());
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

	public boolean checkCanActivate() {
		return !getListActivatedAbilities().isEmpty() && !getFlagTapped();
	}

	/**
	 * Prüft, ob das Permanent angreifen kann. Das Permanent kann angreifen, wenn es
	 * eine Kreatur ist, ungetappt ist und nicht der SummoningSickness unterliegt.
	 * (rule = 508.1a).
	 *
	 * @return true, wenn das Permanent angreifen kann.
	 */
	public boolean checkCanAttack() {
		return isCreature() && !getFlagAttacking() && !getFlagTapped() && !getFlagHasSummoningSickness();
	}

	/**
	 * Prüft, ob das Permanent geblockt werden kann. Ein Permanent kann geblockt
	 * werden, wenn es eine Kreatur ist und angreift.
	 * 
	 * @return true, wenn das Permanent geblockt werden kann.
	 */
	public boolean checkCanBeBlocked() {
		return isCreature() && getFlagAttacking();
	}

	/**
	 * Prüft, ob das Permanent blocken kann. Das Permanent kann blocken, wenn es
	 * eine Kreatur und nicht getappt ist sowie nicht gerade blockt.
	 *
	 * @return true, wenn das Permanent verteidigen kann.
	 */
	public boolean checkCanBlock() {
		return isCreature() && !getFlagBlocking() && !getFlagTapped();
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
		return damage;
	}

	public boolean getFlagAttacking() {
		return flagAttacking;
	}

	public boolean getFlagAttackingAlone() {
		return flagAttackingAlone;
	}

	@Override
	public boolean getFlagBlocked() {
		return flagBlocked;
	}

	public boolean getFlagBlocking() {
		return flagBlocking;
	}

	public boolean getFlagBlockingAlone() {
		return flagBlockingAlone;
	}

	public boolean getFlagHasSummoningSickness() {
		return flagSummoningSickness;
	}

	public boolean getFlagTapped() {
		return flagTapped;
	}

	public IsPlayer getPlayerControlling() {
		return playerControlling;
	}

	@Override
	public void resetDamage() {
		setDamage(0);
	}

	@Override
	public void setDamage(int damage) {
		LOGGER.trace("{} setDamage({})", this, damage);
		this.damage = (damage > 0 ? damage : 0);
	}

	@Override
	public void setFlagAttacking(boolean flagAttacking) {
		LOGGER.trace("{} setFlagAttacking({})", this, flagAttacking);
		this.flagAttacking = flagAttacking;
	}

	public void setFlagAttackingAlone(boolean flagAttackingAlone) {
		LOGGER.trace("{} setFlagAttackingAlone({})", this, flagAttackingAlone);
		this.flagAttackingAlone = flagAttackingAlone;
	}

	@Override
	public void setFlagBlocked(boolean flagBlocked) {
		LOGGER.trace("{} setFlagBlocked({})", this, flagBlocked);
		this.flagBlocked = flagBlocked;
	}

	@Override
	public void setFlagBlocking(boolean flagBlocking) {
		LOGGER.trace("{} setFlagBlocking({})", this, flagBlocking);
		this.flagBlocking = flagBlocking;
	}

	public void setFlagBlockingAlone(boolean flagBlockingAlone) {
		LOGGER.trace("{} setFlagBlockingAlone({})", this, flagBlockingAlone);
		this.flagBlockingAlone = flagBlockingAlone;
	}

	public void setFlagSummoningSickness(boolean flagHasSummoningSickness) {
		LOGGER.trace("{} setFlagSummoningSickness({})", this, flagHasSummoningSickness);
		this.flagSummoningSickness = flagHasSummoningSickness;
	}

	@Override
	public void setFlagTapped(boolean flagTapped) {
		LOGGER.trace("{} setFlagTapped({})", this, flagTapped);
		this.flagTapped = flagTapped;
	}

	public void setPlayerControlling(IsPlayer playerControlling) {
		LOGGER.trace("{} setPlayerControlling({})", this, playerControlling);
		this.playerControlling = playerControlling;
	}

//	public CardState getPermanentState() {
//		final List<ActivatedAbility> listAbilities = getListActivatedAbilities();
//		final List<IsManaMap> listCostMaps = getListCostMaps();
//		final List<Effect> listEffects = new ArrayList<>(propertyListEffects().get());
//		final List<ZoneType> listZonesVisited = new ArrayList<>(propertyListZonesVisited().get());
//
//		final String playerControlling = getPlayerControlling().getDisplayName();
//		final String playerOwning = getPlayerOwning().getDisplayName();
//
//		final Set<ColorType> setColorType = new HashSet<>(propertySetColorType().get());
//		final Set<ObjectType> setObjectTypes = new HashSet<>(getSetObjectTypes().get());
//		final Set<SubType> setSubTypes = new HashSet<>(getSetSubTypes().get());
//		final Set<SuperType> setSuperTypes = new HashSet<>(getSetSuperTypes().get());
//
//		return new CardState(getDamage(), getDisplayName(), getFileName(), getFlagAttacking(), getFlagAttackingAlone(),
//				getFlagBlocked(), getFlagBlocking(), getFlagBlockingAlone(), getFlagFaceDown(), getFlagFlipped(),
//				getFlagIsInteractable(), getFlagPhasedOut(), getFlagHasSummoningSickness(), getFlagTapped(), getId(),
//				listAbilities, listCostMaps, listEffects, listZonesVisited, getLoyalty(), playerControlling,
//				playerOwning, getPower(), setColorType, setObjectTypes, setSubTypes, setSuperTypes, getToughness());
//	}

}
