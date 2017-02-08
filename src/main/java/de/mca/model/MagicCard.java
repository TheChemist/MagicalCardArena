package de.mca.model;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mca.model.enums.ColorType;
import de.mca.model.enums.ObjectType;
import de.mca.model.enums.PlayerType;
import de.mca.model.enums.RarityType;
import de.mca.model.enums.SubType;
import de.mca.model.enums.SuperType;
import de.mca.model.enums.ZoneType;
import de.mca.model.interfaces.IsInteractable;
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
public class MagicCard implements IsObject, IsInteractable {

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
	 * Zeigt an, ob mit der Objekt, in gegebenem Kontext, interagiert werden
	 * kann.
	 */
	private boolean flagIsInteractable;
	/**
	 * Speichert die eindeutige Identifikationsnummer des Objekts. Wird beim
	 * erstellen der Karten vergeben. Üblicherweise von 0 bis
	 * (computerDeck.size() + humanDeck.size()).
	 */
	private final int id;
	/**
	 * Speichert die Abilities der Karte. Bleibende Karten haben Abilities.
	 */
	private final ListProperty<ActivatedAbility> propertyListAbilities;
	/**
	 * Speichert die Kosten der Karte. Bei doppelfarbigen Karten werden alle
	 * Kombinationen gespeichert.
	 */
	private final ListProperty<IsManaMap> propertyListCostMaps;
	/**
	 * Speichert die Liste der Effekte. Zaubersprüche haben Effekte.
	 */
	private final ListProperty<Effect> propertyListEffects;
	/**
	 * Speichert die besuchten Zonen. Die Aktuelle ist jeweils die letzte.
	 */
	private final ListProperty<ZoneType> propertyListZonesVisited;
	/**
	 * Speichert die Anzahl der Loyalitätspunkte.
	 */
	private final IntegerProperty propertyLoyalty;
	/**
	 * Speichert den Spielertyp des Eigentümers.
	 *
	 * @see http://magiccards.info/rule/108-cards.html#rule-108-3
	 */
	private final ObjectProperty<PlayerType> propertyPlayerOwning;
	/**
	 * Speichert die Angriffstärke des Objekts.
	 */
	private final IntegerProperty propertyPower;
	/**
	 * Speichert die Farbe der Karte. Es wird auch farblos gespeichert,
	 * allerdings handelt es sich dabei um keine echte Farbe im Sinne der
	 * Magic-Regeln. Dient auch als Color Indicator im Sinne von Regel 204. im
	 * Magic Rulebook.
	 */
	private final SetProperty<ColorType> propertySetColorType;
	/**
	 * Speichert die Objekttyen.
	 */
	private final SetProperty<ObjectType> propertySetObjectTypes;
	/**
	 * Speichert die Subtypen.
	 */
	private final SetProperty<SubType> propertySetSubTypes;
	/**
	 * Speichert die Supertypen.
	 */
	private final SetProperty<SuperType> propertySetSuperTypes;
	/**
	 * Speichert die Widerstandskraft des Objekts.
	 */
	private final IntegerProperty propertyToughness;
	/**
	 * Speichert die Seltenheit der Karte.
	 */
	private RarityType rarity;

	public MagicCard(int id) {
		this.id = id;
		displayName = "";
		fileName = "";
		propertyListAbilities = new SimpleListProperty<>(FXCollections.observableArrayList());
		propertyListCostMaps = new SimpleListProperty<>(FXCollections.observableArrayList());
		propertyListEffects = new SimpleListProperty<>(FXCollections.observableArrayList());
		propertyListZonesVisited = new SimpleListProperty<>(FXCollections.observableArrayList());
		propertyLoyalty = new SimpleIntegerProperty(0);
		propertyPlayerOwning = new SimpleObjectProperty<>(PlayerType.NONE);
		propertyPower = new SimpleIntegerProperty(0);
		propertySetColorType = new SimpleSetProperty<>(FXCollections.observableSet());
		propertySetObjectTypes = new SimpleSetProperty<>(FXCollections.observableSet());
		propertySetSubTypes = new SimpleSetProperty<>(FXCollections.observableSet());
		propertySetSuperTypes = new SimpleSetProperty<>(FXCollections.observableSet());
		propertyToughness = new SimpleIntegerProperty(0);
		rarity = RarityType.BASIC;
	}

	public MagicCard(MagicPermanent magicPermanent) {
		this(magicPermanent.getId());
		setDisplayName(magicPermanent.getDisplayName());
		setFileName(magicPermanent.getFileName());
		setListActivatedAbilities(magicPermanent.propertyListAbilities());
		setListCostMaps(magicPermanent.propertyListCostMaps());
		setListEffects(magicPermanent.propertyListEffects());
		setListZonesVisited(magicPermanent.propertyListZonesVisited());
		setLoyalty(magicPermanent.getLoyalty());
		setPlayerOwning(magicPermanent.getPlayerOwning());
		setPower(magicPermanent.getBasePower());
		setSetColorTypes(magicPermanent.propertySetColorTypes());
		setSetObjectTypes(magicPermanent.propertySetObjectTypes());
		setSetSubTypes(magicPermanent.propertySetSubTypes());
		setSetSuperTypes(magicPermanent.propertySetSuperTypes());
		setToughness(magicPermanent.getBaseToughness());
	}

	@Override
	public void add(ActivatedAbility ability) {
		ability.setPlayerControlling(getPlayerOwning());
		propertyListAbilities().add(ability);
	}

	public void add(ColorType color) {
		propertySetColorType().add(color);
	}

	public void add(Effect magicEffect) {
		propertyListEffects().add(magicEffect);
	}

	public void add(ObjectType objectType) {
		propertySetObjectTypes().add(objectType);
	}

	public void add(SubType subType) {
		propertySetSubTypes().add(subType);
	}

	public void add(SuperType superType) {
		propertySetSuperTypes().add(superType);
	}

	public void addZone(ZoneType currentZone) {
		propertyListZonesVisited().add(currentZone);
	}

	public boolean contains(ColorType color) {
		return propertySetColorType().contains(color);
	}

	public boolean contains(ObjectType coreType) {
		return propertySetObjectTypes().contains(coreType);
	}

	public boolean contains(SubType subType) {
		return propertySetSubTypes().contains(subType);
	}

	public boolean contains(SuperType superType) {
		return propertySetSuperTypes().contains(superType);
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
		if (propertyListCostMaps().isEmpty()) {
			return 0;
		} else {
			return propertyListCostMaps().get(0).getTotalMana();
		}
	}

	public ZoneType getCurrentZone() {
		if (propertyListZonesVisited().size() == 0) {
			return null;
		} else {
			return propertyListZonesVisited().get(propertyListZonesVisited().size() - 1);
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
		return flagIsInteractable;
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

	public List<ActivatedAbility> getListAbilities() {
		return propertyListAbilities().get();
	}

	public List<IsManaMap> getListCostMaps() {
		return propertyListCostMaps().get();
	}

	@Override
	public int getLoyalty() {
		return propertyLoyalty().get();
	}

	public PlayerType getPlayerOwning() {
		return propertyPlayerOwning().get();
	}

	@Override
	public int getPower() {
		return propertyPower().get();
	}

	public RarityType getRarity() {
		return rarity;
	}

	@Override
	public int getToughness() {
		return propertyToughness().get();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.getId();
		return result;
	}

	public boolean isArtifact() {
		return propertySetObjectTypes().contains(ObjectType.ARTIFACT);
	}

	public boolean isBlack() {
		return propertySetColorType().contains(ColorType.BLACK);
	}

	public boolean isBlue() {
		return propertySetColorType().contains(ColorType.BLUE);
	}

	public boolean isColorless() {
		return propertySetColorType().size() == 0;
	}

	public boolean isCreature() {
		return propertySetObjectTypes().contains(ObjectType.CREATURE);
	}

	public boolean isEnchantment() {
		return propertySetObjectTypes().contains(ObjectType.ENCHANTMENT);
	}

	public boolean isEveryColor() {
		return isBlack() && isBlue() && isGreen() && isRed() && isWhite();
	}

	public boolean isGreen() {
		return propertySetColorType().contains(ColorType.GREEN);
	}

	public boolean isInstant() {
		return propertySetObjectTypes().contains(ObjectType.INSTANT);
	}

	public boolean isLand() {
		return propertySetObjectTypes().contains(ObjectType.LAND);
	}

	public boolean isMonocolored() {
		return propertySetColorType().size() == 1;
	}

	public boolean isMulticolored() {
		return propertySetColorType().size() > 1;
	}

	public boolean isPermanent() {
		return isArtifact() || isCreature() || isEnchantment() || isLand() || isPlaneswalker();
	}

	public boolean isPermanentSpell() {
		return isArtifact() || isCreature() || isEnchantment() || isPlaneswalker();
	}

	public boolean isPlaneswalker() {
		return propertySetObjectTypes().contains(ObjectType.PLANESWALKER);
	}

	public boolean isRed() {
		return propertySetColorType().contains(ColorType.RED);
	}

	public boolean isSorcery() {
		return propertySetObjectTypes().contains(ObjectType.SORCERY);
	}

	public boolean isSpell() {
		return isInstant() || isSorcery();
	}

	public boolean isWhite() {
		return propertySetColorType.contains(ColorType.WHITE);
	}

	@Override
	public ListProperty<ActivatedAbility> propertyListAbilities() {
		return propertyListAbilities;
	}

	public ListProperty<IsManaMap> propertyListCostMaps() {
		return propertyListCostMaps;
	}

	public ListProperty<Effect> propertyListEffects() {
		return propertyListEffects;
	}

	public ListProperty<ZoneType> propertyListZonesVisited() {
		return propertyListZonesVisited;
	}

	public IntegerProperty propertyLoyalty() {
		return propertyLoyalty;
	}

	public ObjectProperty<PlayerType> propertyPlayerOwning() {
		return propertyPlayerOwning;
	}

	public IntegerProperty propertyPower() {
		return propertyPower;
	}

	public SetProperty<ColorType> propertySetColorType() {
		return propertySetColorType;
	}

	@Override
	public SetProperty<ColorType> propertySetColorTypes() {
		return propertySetColorType;
	}

	@Override
	public SetProperty<ObjectType> propertySetObjectTypes() {
		return propertySetObjectTypes;
	}

	@Override
	public SetProperty<SubType> propertySetSubTypes() {
		return propertySetSubTypes;
	}

	@Override
	public SetProperty<SuperType> propertySetSuperTypes() {
		return propertySetSuperTypes;
	}

	public IntegerProperty propertyToughness() {
		return propertyToughness;
	}

	public void remove(ColorType color) {
		propertySetColorType.remove(color);
	}

	public void remove(ObjectType objectType) {
		propertySetObjectTypes.remove(objectType);
	}

	public void remove(SubType subType) {
		propertySetSubTypes.remove(subType);
	}

	public void remove(SuperType superType) {
		propertySetSuperTypes.remove(superType);
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
		this.flagIsInteractable = flagInteractable;
	}

	public void setListActivatedAbilities(ObservableList<ActivatedAbility> listAbilities) {
		propertyListAbilities.set(listAbilities);
		propertyListAbilities().forEach(element -> {
			element.setSource(this);
		});
	}

	public void setListCostMaps(ObservableList<IsManaMap> listCostMaps) {
		propertyListCostMaps().set(listCostMaps);
	}

	public void setListEffects(ObservableList<Effect> listEffects) {
		propertyListEffects().set(listEffects);
	}

	public void setListZonesVisited(ObservableList<ZoneType> listZonesVisited) {
		propertyListZonesVisited().set(listZonesVisited);
	}

	public void setLoyalty(int loyalty) {
		propertyLoyalty().set(loyalty);
	}

	public void setPlayerOwning(PlayerType playerOwning) {
		propertyListAbilities().forEach(ability -> ability.setPlayerControlling(playerOwning));
		propertyPlayerOwning().set(playerOwning);
	}

	public void setPower(int power) {
		propertyPower().set(power);
	}

	public void setRarity(RarityType rarity) {
		this.rarity = rarity;
	}

	public void setSetColorTypes(ObservableSet<ColorType> setColorTypes) {
		propertySetColorType().set(setColorTypes);
	}

	public void setSetObjectTypes(ObservableSet<ObjectType> setObjectTypes) {
		propertySetObjectTypes().set(setObjectTypes);
	}

	public void setSetSubTypes(ObservableSet<SubType> setSubTypes) {
		propertySetSubTypes().set(setSubTypes);
	}

	public void setSetSuperTypes(ObservableSet<SuperType> setSuperTypes) {
		propertySetSuperTypes().set(setSuperTypes);
	}

	public void setStrength(int power, int toughness) {
		setPower(power);
		setToughness(toughness);
	}

	public void setToughness(int toughness) {
		LOGGER.debug("{} setToughness({})", this, toughness);
		propertyToughness().set(toughness);
	}

	@Override
	public String toString() {
		return new StringBuilder("[").append(displayName).append(" id=[").append(id).append("] z=[")
				.append(getCurrentZone()).append("] cm=[").append(getConvertedManaCost()).append("]]").toString();
	}

}
