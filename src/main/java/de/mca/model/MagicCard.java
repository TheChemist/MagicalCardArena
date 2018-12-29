package de.mca.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mca.model.enums.ColorType;
import de.mca.model.enums.ObjectType;
import de.mca.model.enums.RarityType;
import de.mca.model.enums.SubType;
import de.mca.model.enums.SuperType;
import de.mca.model.enums.ZoneType;
import de.mca.model.interfaces.IsInteractable;
import de.mca.model.interfaces.IsManaMap;
import de.mca.model.interfaces.IsObject;
import de.mca.model.interfaces.IsPlayer;

/**
 * Bildet eine Karte im Sinne der Regel 108. im offiziellen Regelwerk ab. Sie
 * kapselt weitere Charakterisitiken, wie die Seltenheit und den Eigentümer.
 *
 * @author Maximilian Werling
 *
 */
public class MagicCard implements IsObject, IsInteractable {

	/**
	 * Höhe eines Kartenscans. Angegeben in Double, um das Seitenverhältnis exakt
	 * errechnen zu können.
	 */
	public static final double CARD_HEIGHT = 445.0;
	/**
	 * Breite eines Kartenscans. Angegeben in Double, um das Seitenverhältnis exakt
	 * errechnen zu können.
	 */
	public static final double CARD_WIDTH = 312.0;
	/**
	 * Speichert den Logger.
	 */
	protected final static Logger LOGGER = LoggerFactory.getLogger("MagicCard");
	/**
	 * Speichert den Namen, so wie er auf der Karte zu lesen ist.
	 */
	private String displayName;
	/**
	 * Speichert den Filenamen der Karte (inklusive der Dateiendung).
	 */
	private String fileName;
	/**
	 * Zeigt an, ob mit der Objekt, in gegebenem Kontext, interagiert werden kann.
	 */
	private boolean flagInteractable;
	/**
	 * Speichert die eindeutige Identifikationsnummer des Objekts. Wird beim
	 * erstellen der Karten vergeben. Üblicherweise von 0 bis (computerDeck.size() +
	 * humanDeck.size()).
	 */
	private final int id;
	/**
	 * Speichert die Abilities der Karte. Bleibende Karten haben Abilities.
	 */
	private List<ActivatedAbility> listAbilities;
	/**
	 * Speichert die Kosten der Karte. Bei doppelfarbigen Karten werden alle
	 * Kombinationen gespeichert.
	 */
	private List<IsManaMap> listCostMaps;
	/**
	 * Speichert die Liste der Effekte. Zaubersprüche haben Effekte.
	 */
	private List<Effect> listEffects;
	/**
	 * Speichert die besuchten Zonen. Die Aktuelle ist jeweils die letzte.
	 */
	private List<ZoneType> listZonesVisited;
	/**
	 * Speichert die Anzahl der Loyalitätspunkte.
	 */
	private int loyalty;
	/**
	 * Speichert den Spielertyp des Eigentümers.
	 *
	 * @see http://magiccards.info/rule/108-cards.html#rule-108-3
	 */
	private transient IsPlayer playerOwning;
	/**
	 * Speichert die Angriffstärke des Objekts.
	 */
	private int power;
	/**
	 * Speichert die Seltenheit der Karte.
	 */
	private RarityType rarity;
	/**
	 * Speichert die Farbe der Karte. Es wird auch farblos gespeichert, allerdings
	 * handelt es sich dabei um keine echte Farbe im Sinne der Magic-Regeln. Dient
	 * auch als Color Indicator im Sinne von Regel 204. im Magic Rulebook.
	 */
	private Set<ColorType> setColorType;
	/**
	 * Speichert die Objekttyen.
	 */
	private Set<ObjectType> setObjectTypes;
	/**
	 * Speichert die Subtypen.
	 */
	private Set<SubType> setSubTypes;
	/**
	 * Speichert die Supertypen.
	 */
	private Set<SuperType> setSuperTypes;
	/**
	 * Speichert die Widerstandskraft des Objekts.
	 */
	private int toughness;

	public MagicCard(int id) {
		this.id = id;
		displayName = "";
		fileName = "";
		listAbilities = new ArrayList<>();
		listCostMaps = new ArrayList<>();
		listEffects = new ArrayList<>();
		listZonesVisited = new ArrayList<>();
		loyalty = 0;
		playerOwning = null;
		power = 0;
		setColorType = new HashSet<>();
		setObjectTypes = new HashSet<>();
		setSubTypes = new HashSet<>();
		setSuperTypes = new HashSet<>();
		toughness = 0;
		rarity = RarityType.BASIC;
	}

	MagicCard(MagicPermanent magicPermanent) {
		this(magicPermanent.getId());
		setDisplayName(magicPermanent.getDisplayName());
		setFileName(magicPermanent.getFileName());
		setListActivatedAbilities(magicPermanent.getListActivatedAbilities());
		setListCostMaps(magicPermanent.getListCostMaps());
		setListEffects(magicPermanent.getListEffects());
		setListZonesVisited(magicPermanent.getListZonesVisited());
		setLoyalty(magicPermanent.getLoyalty());
		setPlayerOwning(magicPermanent.getPlayerOwning());
		setPower(magicPermanent.getBasePower());
		setSetColorTypes(magicPermanent.getSetColorTypes());
		setSetObjectTypes(magicPermanent.getSetObjectTypes());
		setSetSubTypes(magicPermanent.getSetSubTypes());
		setSetSuperTypes(magicPermanent.getSetSuperTypes());
		setToughness(magicPermanent.getBaseToughness());
	}

	@Override
	public void add(ActivatedAbility ability) {
		ability.setPlayerControlling(getPlayerOwning());
		getListActivatedAbilities().add(ability);
	}

	public void add(ColorType color) {
		getSetColorType().add(color);
	}

	public void add(Effect magicEffect) {
		getListEffects().add(magicEffect);
	}

	public void add(ObjectType objectType) {
		getSetObjectTypes().add(objectType);
	}

	public void add(SubType subType) {
		getSetSubTypes().add(subType);
	}

	public void add(SuperType superType) {
		getSetSuperTypes().add(superType);
	}

	public void addZone(ZoneType currentZone) {
		getListZonesVisited().add(currentZone);
	}

	public boolean contains(ColorType color) {
		return getSetColorType().contains(color);
	}

	public boolean contains(ObjectType coreType) {
		return getSetObjectTypes().contains(coreType);
	}

	public boolean contains(SubType subType) {
		return getSetSubTypes().contains(subType);
	}

	public boolean contains(SuperType superType) {
		return getSetSuperTypes().contains(superType);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		MagicCard other = (MagicCard) obj;
		if (this.getId() != other.getId()) {
			return false;
		}

		return true;
	}

	public boolean equalsColor(Set<ColorType> colors) {
		boolean result = true;
		for (final ColorType color : colors) {
			result = result && contains(color);
		}
		return result;
	}

	public boolean equalsCoreType(Set<ObjectType> coreTypes) {
		boolean result = true;
		for (final ObjectType coreType : coreTypes) {
			result = result && contains(coreType);
		}
		return result;
	}

	public boolean equalsSubType(Set<SubType> subTypes) {
		boolean result = true;
		for (final SubType subType : subTypes) {
			result = result && contains(subType);
		}
		return result;
	}

	public int getConvertedManaCost() {
		if (getListCostMaps().isEmpty()) {
			return 0;
		} else {
			return getListCostMaps().get(0).getTotalMana();
		}
	}

	public ZoneType getCurrentZone() {
		final List<ZoneType> list = getListZonesVisited();
		if (list.size() == 0) {
			return null;
		} else {
			return list.get(list.size() - 1);
		}
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	public String getFileName() {
		return fileName;
	}

	@Override
	public boolean getFlagIsInteractable() {
		return flagInteractable;
	}

	@Override
	public int getHandModifier() {
		// TODO LOW Bisher keine Verwendung
		return 0;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public int getLifeModifier() {
		// TODO LOW Bisher keine Verwendung
		return 0;
	}

	public List<ActivatedAbility> getListActivatedAbilities() {
		return listAbilities;
	}

	public List<IsManaMap> getListCostMaps() {
		return listCostMaps;
	}

	public List<Effect> getListEffects() {
		return listEffects;
	}

	public List<ZoneType> getListZonesVisited() {
		return listZonesVisited;
	}

	@Override
	public int getLoyalty() {
		return loyalty;
	}

	public ActivatedAbility getManaAbility() throws NullPointerException {
		for (ActivatedAbility ability : getListActivatedAbilities()) {
			return ability;
		}
		throw new NullPointerException("No Mana Ability!");
	}

	public IsPlayer getPlayerOwning() {
		return playerOwning;
	}

	@Override
	public int getPower() {
		return power;
	}

	public RarityType getRarity() {
		return rarity;
	}

	public Set<ColorType> getSetColorType() {
		return setColorType;
	}

	@Override
	public Set<ColorType> getSetColorTypes() {
		return setColorType;
	}

	@Override
	public Set<ObjectType> getSetObjectTypes() {
		return setObjectTypes;
	}

	@Override
	public Set<SubType> getSetSubTypes() {
		return setSubTypes;
	}

	@Override
	public Set<SuperType> getSetSuperTypes() {
		return setSuperTypes;
	}

	@Override
	public int getToughness() {
		return toughness;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.getId();
		return result;
	}

	public boolean isArtifact() {
		return getSetObjectTypes().contains(ObjectType.ARTIFACT);
	}

	public boolean isBlack() {
		return getSetColorType().contains(ColorType.BLACK);
	}

	public boolean isBlue() {
		return getSetColorType().contains(ColorType.BLUE);
	}

	public boolean isColorless() {
		return getSetColorType().size() == 0;
	}

	public boolean isCreature() {
		return getSetObjectTypes().contains(ObjectType.CREATURE);
	}

	public boolean isEnchantment() {
		return getSetObjectTypes().contains(ObjectType.ENCHANTMENT);
	}

	public boolean isEveryColor() {
		return isBlack() && isBlue() && isGreen() && isRed() && isWhite();
	}

	public boolean isGreen() {
		return getSetColorType().contains(ColorType.GREEN);
	}

	public boolean isInstant() {
		return getSetObjectTypes().contains(ObjectType.INSTANT);
	}

	public boolean isLand() {
		return getSetObjectTypes().contains(ObjectType.LAND);
	}

	public boolean isManaSource() {
		try {
			return getManaAbility() != null;
		} catch (NullPointerException e) {
			return false;
		}
	}

	public boolean isMonocolored() {
		return getSetColorType().size() == 1;
	}

	public boolean isMulticolored() {
		return getSetColorType().size() > 1;
	}

	public boolean isPermanent() {
		return isArtifact() || isCreature() || isEnchantment() || isLand() || isPlaneswalker();
	}

	public boolean isPermanentSpell() {
		return isArtifact() || isCreature() || isEnchantment() || isPlaneswalker();
	}

	public boolean isPlaneswalker() {
		return getSetObjectTypes().contains(ObjectType.PLANESWALKER);
	}

	public boolean isRed() {
		return getSetColorType().contains(ColorType.RED);
	}

	public boolean isSorcery() {
		return getSetObjectTypes().contains(ObjectType.SORCERY);
	}

	public boolean isSpell() {
		return isInstant() || isSorcery();
	}

	public boolean isWhite() {
		return setColorType.contains(ColorType.WHITE);
	}

	public void remove(ColorType color) {
		setColorType.remove(color);
	}

	public void remove(ObjectType objectType) {
		setObjectTypes.remove(objectType);
	}

	public void remove(SubType subType) {
		setSubTypes.remove(subType);
	}

	public void remove(SuperType superType) {
		setSuperTypes.remove(superType);
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public void setFlagInteractable(boolean flagInteractable) {
		LOGGER.trace("{} setFlagInteractable({})", this, flagInteractable);
		this.flagInteractable = flagInteractable;
	}

	public void setListActivatedAbilities(List<ActivatedAbility> listAbilities) {
		this.listAbilities = listAbilities;
		getListActivatedAbilities().forEach(element -> {
			element.setSource(this);
		});
	}

	public void setListCostMaps(List<IsManaMap> listCostMaps) {
		this.listCostMaps = listCostMaps;
	}

	public void setListEffects(List<Effect> listEffects) {
		this.listEffects = listEffects;
	}

	public void setListZonesVisited(List<ZoneType> listZonesVisited) {
		this.listZonesVisited = listZonesVisited;
	}

	public void setLoyalty(int loyalty) {
		this.loyalty = loyalty;
	}

	public void setPlayerOwning(IsPlayer playerOwning) {
		getListActivatedAbilities().forEach(ability -> ability.setPlayerControlling(playerOwning));
		this.playerOwning = playerOwning;
	}

	public void setPower(int power) {
		this.power = power;
	}

	public void setRarity(RarityType rarity) {
		this.rarity = rarity;
	}

	public void setSetColorTypes(Set<ColorType> setColorTypes) {
		this.setColorType = setColorTypes;
	}

	public void setSetObjectTypes(Set<ObjectType> setObjectTypes) {
		this.setObjectTypes = setObjectTypes;
	}

	public void setSetSubTypes(Set<SubType> setSubTypes) {
		this.setSubTypes = setSubTypes;
	}

	public void setSetSuperTypes(Set<SuperType> setSuperTypes) {
		this.setSuperTypes = setSuperTypes;
	}

	public void setStrength(int power, int toughness) {
		LOGGER.trace("{} setStrength({}, {})", this, power, toughness);
		setPower(power);
		setToughness(toughness);
	}

	public void setToughness(int toughness) {
		LOGGER.trace("{} setToughness({})", this, toughness);
		this.toughness = toughness;
	}

	@Override
	public String toString() {
		return new StringBuilder("[").append(displayName).append(" id=[").append(id).append("] z=[")
				.append(getCurrentZone()).append("] cm=[").append(getConvertedManaCost()).append("]]").toString();
	}

}
