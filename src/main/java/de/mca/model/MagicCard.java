package de.mca.model;

import java.util.ArrayList;
import java.util.Set;

import de.mca.model.enums.ColorType;
import de.mca.model.enums.ObjectType;
import de.mca.model.enums.PlayerType;
import de.mca.model.enums.RarityType;
import de.mca.model.enums.SubType;
import de.mca.model.enums.SuperType;
import de.mca.model.enums.ZoneType;
import de.mca.model.interfaces.IsManaMap;
import de.mca.model.interfaces.IsObject;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

/**
 * Bildet eine Karte im Sinne der Regel 108. im offiziellen Regelwerk ab. Sie
 * kapselt weitere Charakterisitiken, wie die Seltenheit und den Eigentümer.
 *
 * @author Maximilian Werling
 *
 */
public class MagicCard implements IsObject {

	/**
	 * Höhe eines Kartenscans. Angegeben in Double, um das Seitenverhältnis
	 * exakt errechnen zu können.
	 */
	public static final double CARD_HEIGHT = 445.0;
	/**
	 * Breite eines Kartenscans. Angegeben in Double, um das Seitenverhältnis
	 * exakt errechnen zu können.
	 */
	public static final double CARD_WIDTH = 312.0;
	/**
	 * Speichert den Namen, so wie er auf der Karte zu lesen ist.
	 */
	private final StringProperty displayName;
	/**
	 * Speichert den Filenamen der Karte (inklusive der Dateiendung).
	 */
	private final StringProperty fileName;
	/**
	 * Speichert die eindeutige Identifikationsnummer des Objekts. Wird beim
	 * erstellen der Karten vergeben. Üblicherweise von 0 bis
	 * (computerDeck.size() + humanDeck.size()).
	 */
	private final int id;
	/**
	 * Speichert die Abilities der Karte. Bleibende Karten haben Abilities.
	 */
	private final ListProperty<CharacteristicAbility> listCharacteristicAbilities;
	/**
	 * Speichert die Kosten der Karte. Bei doppelfarbigen Karten werden alle
	 * Kombinationen gespeichert.
	 */
	private final ListProperty<IsManaMap> listCostMaps;
	/**
	 * Speichert die Liste der Effekte. Zaubersprüche haben Effekte.
	 */
	private final ListProperty<Effect> listEffects;
	/**
	 * Speichert die besuchten Zonen. Die Aktuelle ist jeweils die letzte.
	 */
	private final ListProperty<ZoneType> listZonesVisited;
	/**
	 * Speichert die Anzahl der Loyalitätspunkte.
	 */
	private final IntegerProperty loyalty;
	/**
	 * Speichert den Spielertyp des Eigentümers.
	 */
	private final ObjectProperty<PlayerType> playerOwning;
	/**
	 * Speichert die Angriffstärke des Objekts.
	 */
	private final IntegerProperty power;
	/**
	 * Speichert die Seltenheit der Karte.
	 */
	private final ObjectProperty<RarityType> rarity;
	/**
	 * Speichert die Farbe der Karte. Es wird auch farblos gespeichert,
	 * allerdings handelt es sich dabei um keine echte Farbe im Sinne der
	 * Magic-Regeln. Dient auch als Color Indicator im Sinne von Regel 204. im
	 * Magic Rulebook.
	 */
	private final SetProperty<ColorType> setColorType;
	/**
	 * Speichert die Objekttyen.
	 */
	private final SetProperty<ObjectType> setObjectTypes;
	/**
	 * Speichert die Subtypen.
	 */
	private final SetProperty<SubType> setSubTypes;
	/**
	 * Speichert die Supertypen.
	 */
	private final SetProperty<SuperType> setSuperTypes;
	/**
	 * Speichert die Widerstandskraft des Objekts.
	 */
	private final IntegerProperty toughness;

	public MagicCard(int id) {
		this.id = id;
		displayName = new SimpleStringProperty();
		fileName = new SimpleStringProperty();
		listCharacteristicAbilities = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
		listCostMaps = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
		listEffects = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
		listZonesVisited = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
		loyalty = new SimpleIntegerProperty();
		playerOwning = new SimpleObjectProperty<>();
		power = new SimpleIntegerProperty();
		rarity = new SimpleObjectProperty<>();
		setColorType = new SimpleSetProperty<>();
		setObjectTypes = new SimpleSetProperty<>();
		setSubTypes = new SimpleSetProperty<>();
		setSuperTypes = new SimpleSetProperty<>();
		toughness = new SimpleIntegerProperty();
	}

	public MagicCard(MagicPermanent magicPermanent) {
		this(magicPermanent.getId());
		setDisplayName(magicPermanent.getDisplayName());
		setFileName(magicPermanent.getFileName());
		setListCharacteristicAbilities(magicPermanent.propertyListCharacteristicAbilities());
		setListCostMaps(magicPermanent.propertyListCostMaps());
		setListEffects(magicPermanent.propertyListEffects());
		setListZonesVisited(magicPermanent.propertyListZonesVisited());
		setLoyalty(magicPermanent.getLoyalty());
		setPlayerOwning(magicPermanent.getPlayerOwning());
		setPower(magicPermanent.getPower());
		setSetColorTypes(magicPermanent.propertySetColorTypes());
		setSetObjectTypes(magicPermanent.propertySetObjectTypes());
		setSetSubTypes(magicPermanent.propertySetSubTypes());
		setSetSuperTypes(magicPermanent.propertySetSuperTypes());
		setToughness(magicPermanent.getToughness());
	}

	@Override
	public void add(CharacteristicAbility ability) {
		listCharacteristicAbilities.add(ability);
	}

	public void add(ColorType color) {
		setColorType.add(color);
	}

	public void add(Effect magicEffect) {
		listEffects.add(magicEffect);
	}

	public void add(ObjectType objectType) {
		setObjectTypes.add(objectType);
	}

	public void add(SubType subType) {
		setSubTypes.add(subType);
	}

	public void add(SuperType superType) {
		setSuperTypes.add(superType);
	}

	public void addZone(ZoneType currentZone) {
		listZonesVisited.add(currentZone);
	}

	public boolean contains(ColorType color) {
		return setColorType.contains(color);
	}

	public boolean contains(ObjectType coreType) {
		return setObjectTypes.contains(coreType);
	}

	public boolean contains(SubType subType) {
		return setSubTypes.contains(subType);
	}

	public boolean contains(SuperType superType) {
		return setSuperTypes.contains(superType);
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
		if (listCostMaps.isEmpty()) {
			return 0;
		} else {
			return listCostMaps.get(0).getTotalMana();
		}
	}

	public ZoneType getCurrentZone() {
		if (listZonesVisited.size() == 0) {
			return null;
		} else {
			return listZonesVisited.get(listZonesVisited.size() - 1);
		}
	}

	@Override
	public String getDisplayName() {
		return displayName.get();
	}

	public String getFileName() {
		return fileName.get();
	}

	@Override
	public int getHandModifier() {
		// TODO: Bisher keine Verwendung
		return 0;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public int getLifeModifier() {
		// TODO: Bisher keine Verwendung
		return 0;
	}

	@Override
	public int getLoyalty() {
		return loyalty.get();
	}

	public PlayerType getPlayerOwning() {
		return playerOwning.get();
	}

	@Override
	public int getPower() {
		return power.get();
	}

	public RarityType getRarity() {
		return rarity.get();
	}

	@Override
	public int getToughness() {
		return toughness.get();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.getId();
		return result;
	}

	public boolean isArtifact() {
		return setObjectTypes.contains(ObjectType.ARTIFACT);
	}

	public boolean isBlack() {
		return setColorType.contains(ColorType.BLACK);
	}

	public boolean isBlue() {
		return setColorType.contains(ColorType.BLUE);
	}

	public boolean isColorless() {
		return setColorType.size() == 0;
	}

	public boolean isCreature() {
		return setObjectTypes.contains(ObjectType.CREATURE);
	}

	public boolean isEnchantment() {
		return setObjectTypes.contains(ObjectType.ENCHANTMENT);
	}

	public boolean isEveryColor() {
		return isBlack() && isBlue() && isGreen() && isRed() && isWhite();
	}

	public boolean isGreen() {
		return setColorType.contains(ColorType.GREEN);
	}

	public boolean isInstant() {
		return setObjectTypes.contains(ObjectType.INSTANT);
	}

	public boolean isLand() {
		return setObjectTypes.contains(ObjectType.LAND);
	}

	public boolean isMonocolored() {
		return setColorType.size() == 1;
	}

	public boolean isMulticolored() {
		return setColorType.size() > 1;
	}

	public boolean isPermanent() {
		return isArtifact() || isCreature() || isEnchantment() || isLand() || isPlaneswalker();
	}

	public boolean isPermanentSpell() {
		for (final ObjectType ot : setObjectTypes) {
			if (ot.isPermanentSpell()) {
				return true;
			}
		}
		return false;
	}

	public boolean isPlaneswalker() {
		return setObjectTypes.contains(ObjectType.PLANESWALKER);
	}

	public boolean isRed() {
		return contains(ColorType.RED);
	}

	public boolean isSorcery() {
		return setObjectTypes.contains(ObjectType.SORCERY);
	}

	public boolean isSpell() {
		return isInstant() || isSorcery();
	}

	public boolean isWhite() {
		return contains(ColorType.WHITE);
	}

	@Override
	public ObservableList<CharacteristicAbility> propertyListCharacteristicAbilities() {
		return listCharacteristicAbilities;
	}

	public ListProperty<IsManaMap> propertyListCostMaps() {
		return listCostMaps;
	}

	public ListProperty<Effect> propertyListEffects() {
		return listEffects;
	}

	public ListProperty<ZoneType> propertyListZonesVisited() {
		return listZonesVisited;
	}

	public ObjectProperty<PlayerType> propertyPlayerOwning() {
		return playerOwning;
	}

	public ObjectProperty<RarityType> propertyRarityType() {
		return rarity;
	}

	@Override
	public SetProperty<ColorType> propertySetColorTypes() {
		return setColorType;
	}

	@Override
	public SetProperty<ObjectType> propertySetObjectTypes() {
		return setObjectTypes;
	}

	@Override
	public SetProperty<SubType> propertySetSubTypes() {
		return setSubTypes;
	}

	@Override
	public SetProperty<SuperType> propertySetSuperTypes() {
		return setSuperTypes;
	}

	public void remove(ColorType color) {
		this.setColorType.remove(color);
	}

	public void remove(ObjectType objectType) {
		this.setObjectTypes.remove(objectType);
	}

	public void remove(SubType subType) {
		this.setSubTypes.remove(subType);
	}

	public void remove(SuperType superType) {
		this.setSuperTypes.remove(superType);
	}

	public void setDisplayName(String displayName) {
		this.displayName.set(displayName);
	}

	public void setFileName(String fileName) {
		this.fileName.set(fileName);
	}

	public void setListCharacteristicAbilities(ObservableList<CharacteristicAbility> listAbilities) {
		this.listCharacteristicAbilities.set(listAbilities);
		this.listCharacteristicAbilities.forEach(element -> {
			element.setSource(this);
		});
	}

	public void setListCostMaps(ObservableList<IsManaMap> listCostMaps) {
		this.listCostMaps.set(listCostMaps);
	}

	public void setListEffects(ObservableList<Effect> listEffects) {
		this.listEffects.set(listEffects);
	}

	public void setListZonesVisited(ObservableList<ZoneType> listZonesVisited) {
		this.listZonesVisited.set(listZonesVisited);
	}

	public void setLoyalty(int loyalty) {
		this.loyalty.set(loyalty);
	}

	public void setPlayerOwning(PlayerType playerOwning) {
		this.playerOwning.set(playerOwning);
	}

	public void setPower(int power) {
		this.power.set(power);
	}

	public void setRarity(RarityType rarity) {
		this.rarity.set(rarity);
	}

	public void setSetColorTypes(ObservableSet<ColorType> setColorTypes) {
		this.setColorType.set(setColorTypes);
	}

	public void setSetObjectTypes(ObservableSet<ObjectType> setObjectTypes) {
		this.setObjectTypes.set(setObjectTypes);
	}

	public void setSetSubTypes(ObservableSet<SubType> setSubTypes) {
		this.setSubTypes.set(setSubTypes);
	}

	public void setSetSuperTypes(ObservableSet<SuperType> setSuperTypes) {
		this.setSuperTypes.set(setSuperTypes);
	}

	public void setStrength(int power, int toughness) {
		setPower(power);
		setToughness(toughness);
	}

	public void setToughness(int toughness) {
		this.toughness.set(toughness);
	}

	@Override
	public String toString() {
		return new StringBuilder("[").append(displayName).append(" id=[").append(id).append("] z=[")
				.append(getCurrentZone()).append("] cm=[").append(listCostMaps.size()).append("]]").toString();
	}

}
