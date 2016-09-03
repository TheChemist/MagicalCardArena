package de.mca.model.interfaces;

import de.mca.model.CharacteristicAbility;
import de.mca.model.enums.ColorType;
import de.mca.model.enums.ObjectType;
import de.mca.model.enums.SubType;
import de.mca.model.enums.SuperType;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

/**
 * Das Interface dient der Abbildung eines Objekts im Sinne der Regel 109. im
 * offiziellen Regelbuch. Es kapselt alle Charakteristiken, die ein Object
 * definieren. Weiterhin enthält es Mehtoden zur Manipulation des Objekts.
 *
 * @author Maximilian Werling
 *
 */
public interface IsObject {

	void add(CharacteristicAbility characteristicAbilities);

	String getDisplayName();

	int getHandModifier();

	int getId();

	int getLifeModifier();

	int getLoyalty();

	int getPower();

	int getToughness();

	ObservableList<CharacteristicAbility> propertyListCharacteristicAbilities();

	ObservableSet<ColorType> propertySetColorTypes();

	ObservableSet<ObjectType> propertySetObjectTypes();

	ObservableSet<SubType> propertySetSubTypes();

	ObservableSet<SuperType> propertySetSuperTypes();

}
