// package de.mca.model;
//
// import java.util.Set;
//
// import javafx.beans.property.IntegerProperty;
// import javafx.beans.property.ListProperty;
// import javafx.beans.property.SetProperty;
// import javafx.beans.property.SimpleIntegerProperty;
// import javafx.beans.property.SimpleListProperty;
// import javafx.beans.property.SimpleSetProperty;
// import javafx.beans.property.SimpleStringProperty;
// import javafx.beans.property.StringProperty;
// import javafx.collections.ObservableList;
// import javafx.collections.ObservableSet;
//
/// **
// * Bildet ein Objekt im Sinne der Regel 109. im offiziellen Regelbuch ab.
// * Speichert die einzelnen Charakteristiken eines Objekts. Nicht alle
// * Charakteristiken sind bei jedem Objekt zwingend notwenig. Der Color
// Indicator
// * wird durch das Attribut color hinreichend abgebildet.
// *
// * @author Maximilian Werling
// *
// */
//// TODO: Entferne, Inhalte in MagicCard einfügen
//// TODO: Weitere Interfaces erstellen, die elementare Eigenschaften kapseln
// public abstract class MagicObject implements IsObject {
//
// /**
// * Speichert den Namen, so wie er auf der Karte zu lesen ist.
// */
// private final StringProperty displayName;
// /**
// * Speichert den Filenamen der Karte.
// */
// private final StringProperty fileName;
// /**
// * Speichert die eindeutige Identifikationsnummer des Objekts. Wird beim
// * erstellen der Karten vergeben. Üblicherweise von 0 bis
// * (computerDeck.size() + humanDeck.size()).
// */
// private final int id;
// /**
// * Speichert die Abilities der Karte. Bleibende Karten haben Abilities.
// */
// private final ListProperty<Ability> listAbilities;
// /**
// * Speichert die Kosten der Karte. Bei doppelfarbigen Karten werden alle
// * Kombinationen gespeichert.
// */
// private final ListProperty<IsManaMap> listCostMaps;
// /**
// * Speichert die Liste der Effekte. Zaubersprüche haben Effekte.
// */
// private final ListProperty<MEffect> listEffects;
// /**
// * Speichert die besuchten Zonen. Die Aktuelle ist jeweils die letzte.
// */
// private final ListProperty<ZoneType> listZonesVisited;
// /**
// * Speichert die Anzahl der Loyalitätspunkte.
// */
// private final IntegerProperty loyalty;
// /**
// * Speichert die Angriffstärke des Objekts.
// */
// private final IntegerProperty power;
// /**
// * Speichert die Farbe der Karte. Es wird auch farblos gespeichert,
// * allerdings handelt es sich dabei um keine echte Farbe im Sinne der
// * Magic-Regeln. Dient auch als Color Indicator im Sinne von Regel 204. im
// * Magic Rulebook.
// */
// private final SetProperty<ColorType> setColorType;
// /**
// * Speichert die Objekttyen.
// */
// private final SetProperty<ObjectType> setObjectTypes;
// /**
// * Speichert die Subtypen.
// */
// private final SetProperty<SubType> setSubTypes;
// /**
// * Speichert die Supertypen.
// */
// private final SetProperty<SuperType> setSuperTypes;
// /**
// * Speichert die Widerstandskraft des Objekts.
// */
// private final IntegerProperty toughness;
//
// public MagicObject(int id) {
// this.id = id;
// displayName = new SimpleStringProperty();
// fileName = new SimpleStringProperty();
// listAbilities = new SimpleListProperty<>();
// listCostMaps = new SimpleListProperty<>();
// listEffects = new SimpleListProperty<>();
// listZonesVisited = new SimpleListProperty<>();
// loyalty = new SimpleIntegerProperty();
// power = new SimpleIntegerProperty();
// setColorType = new SimpleSetProperty<>();
// setObjectTypes = new SimpleSetProperty<>();
// setSubTypes = new SimpleSetProperty<>();
// setSuperTypes = new SimpleSetProperty<>();
// toughness = new SimpleIntegerProperty();
// }
//
// public void add(Ability ability) {
// listAbilities.add(ability);
// }
//
// public void add(ColorType color) {
// setColorType.add(color);
// }
//
// public void add(MEffect magicEffect) {
// listEffects.add(magicEffect);
// }
//
// public void add(ObjectType objectType) {
// setObjectTypes.add(objectType);
// }
//
// public void add(SubType subType) {
// setSubTypes.add(subType);
// }
//
// public void add(SuperType superType) {
// setSuperTypes.add(superType);
// }
//
// public void addZone(ZoneType currentZone) {
// listZonesVisited.add(currentZone);
// }
//
// public boolean contains(ColorType color) {
// return setColorType.contains(color);
// }
//
// public boolean contains(ObjectType coreType) {
// return setObjectTypes.contains(coreType);
// }
//
// public boolean contains(SubType subType) {
// return setSubTypes.contains(subType);
// }
//
// public boolean contains(SuperType superType) {
// return setSuperTypes.contains(superType);
// }
//
// @Override
// public boolean equals(Object obj) {
// return super.equals(obj) && id == ((MagicObject) obj).getId();
// }
//
// public boolean equalsColor(Set<ColorType> colors) {
// boolean result = true;
// for (final ColorType color : colors) {
// result = result && contains(color);
// }
// return result;
// }
//
// public boolean equalsCoreType(Set<ObjectType> coreTypes) {
// boolean result = true;
// for (final ObjectType coreType : coreTypes) {
// result = result && contains(coreType);
// }
// return result;
// }
//
// public boolean equalsSubType(Set<SubType> subTypes) {
// boolean result = true;
// for (final SubType subType : subTypes) {
// result = result && contains(subType);
// }
// return result;
// }
//
// public int getConvertedManaCost() {
// return listCostMaps.get(0).getTotalMana();
// }
//
// public ZoneType getCurrentZone() {
// if (listZonesVisited.size() == 0) {
// return null;
// } else {
// return listZonesVisited.get(listZonesVisited.size() - 1);
// }
// }
//
// @Override
// public String getDisplayName() {
// return displayName.get();
// }
//
// public String getFileName() {
// return fileName.get();
// }
//
// @Override
// public int getId() {
// return id;
// }
//
// @Override
// public int getLoyalty() {
// return loyalty.get();
// }
//
// @Override
// public int getPower() {
// return power.get();
// }
//
// @Override
// public int getToughness() {
// return toughness.get();
// }
//
// @Override
// public int hashCode() {
// return id;
// }
//
// public boolean isArtifact() {
// return contains(ObjectType.ARTIFACT);
// }
//
// public boolean isBlack() {
// return contains(ColorType.BLACK);
// }
//
// public boolean isBlue() {
// return contains(ColorType.BLUE);
// }
//
// public boolean isColorless() {
// return setColorType.size() == 0;
// }
//
// public boolean isCreature() {
// return contains(ObjectType.CREATURE);
// }
//
// public boolean isEnchantment() {
// return setObjectTypes.contains(ObjectType.ENCHANTMENT);
// }
//
// public boolean isEveryColor() {
// return isBlack() && isBlue() && isGreen() && isRed() && isWhite();
// }
//
// public boolean isGreen() {
// return contains(ColorType.GREEN);
// }
//
// public boolean isInstant() {
// return setObjectTypes.contains(ObjectType.INSTANT);
// }
//
// public boolean isLand() {
// return contains(ObjectType.LAND);
// }
//
// public boolean isMonocolored() {
// return setColorType.size() == 1;
// }
//
// public boolean isMulticolored() {
// return setColorType.size() > 1;
// }
//
// public boolean isPermanent() {
// return isArtifact() || isCreature() || isEnchantment() || isLand() ||
// isPlaneswalker();
// }
//
// public boolean isPermanentSpell() {
// for (final ObjectType ot : setObjectTypes) {
// if (ot.isPermanentSpell()) {
// return true;
// }
// }
// return false;
// }
//
// public boolean isPlaneswalker() {
// return setObjectTypes.contains(ObjectType.PLANESWALKER);
// }
//
// public boolean isRed() {
// return contains(ColorType.RED);
// }
//
// public boolean isSorcery() {
// return setObjectTypes.contains(ObjectType.SORCERY);
// }
//
// public boolean isSpell() {
// return isInstant() || isSorcery();
// }
//
// public boolean isWhite() {
// return contains(ColorType.WHITE);
// }
//
// public ListProperty<Ability> propertyListAbilities() {
// return listAbilities;
// }
//
// @Override
// public ObservableList<IsAbility> propertyListCharacteristicAbilities() {
// return listAbilities;
// }
//
// public ListProperty<IsManaMap> propertyListCostMaps() {
// return listCostMaps;
// }
//
// public ListProperty<MEffect> propertyListEffects() {
// return listEffects;
// }
//
// public ListProperty<ZoneType> propertyListZonesVisited() {
// return listZonesVisited;
// }
//
// public SetProperty<ColorType> propertySetColorTypes() {
// return setColorType;
// }
//
// public SetProperty<ObjectType> propertySetObjectTypes() {
// return setObjectTypes;
// }
//
// public SetProperty<SubType> propertySetSubTypes() {
// return setSubTypes;
// }
//
// public SetProperty<SuperType> propertySetSuperTypes() {
// return setSuperTypes;
// }
//
// public void remove(ColorType color) {
// this.setColorType.remove(color);
// }
//
// public void remove(ObjectType objectType) {
// this.setObjectTypes.remove(objectType);
// }
//
// public void remove(SubType subType) {
// this.setSubTypes.remove(subType);
// }
//
// public void remove(SuperType superType) {
// this.setSuperTypes.remove(superType);
// }
//
// public void setDisplayName(String displayName) {
// this.displayName.set(displayName);
// }
//
// public void setFileName(String fileName) {
// this.fileName.set(fileName);
// }
//
// public void setListAbilities(ObservableList<Ability> listAbilities) {
// this.listAbilities.set(listAbilities);
// }
//
// public void setListCostMaps(ObservableList<IsManaMap> listCostMaps) {
// this.listCostMaps.set(listCostMaps);
// }
//
// public void setListEffects(ObservableList<MEffect> listEffects) {
// this.listEffects.set(listEffects);
// }
//
// public void setListZonesVisited(ObservableList<ZoneType> listZonesVisited) {
// this.listZonesVisited.set(listZonesVisited);
// }
//
// public void setLoyalty(int loyalty) {
// this.loyalty.set(loyalty);
// }
//
// public void setPower(int power) {
// this.power.set(power);
// }
//
// public void setSetColorTypes(ObservableSet<ColorType> setColorTypes) {
// this.setColorType.set(setColorTypes);
// }
//
// public void setSetObjectTypes(ObservableSet<ObjectType> setObjectTypes) {
// this.setObjectTypes.set(setObjectTypes);
// }
//
// public void setSetSubTypes(ObservableSet<SubType> setSubTypes) {
// this.setSubTypes.set(setSubTypes);
// }
//
// public void setSetSuperTypes(ObservableSet<SuperType> setSuperTypes) {
// this.setSuperTypes.set(setSuperTypes);
// }
//
// public void setStrength(int power, int toughness) {
// setPower(power);
// setToughness(toughness);
// }
//
// public void setToughness(int toughness) {
// this.toughness.set(toughness);
// }
//
// @Override
// public String toString() {
// return new StringBuilder("[").append(displayName)
// // .append(" id=[").append(id)
// .append("] z=[").append(getCurrentZone()).append("]
// cm=[").append(listCostMaps.size()).append("]]")
// .toString();
// }
//
// }
