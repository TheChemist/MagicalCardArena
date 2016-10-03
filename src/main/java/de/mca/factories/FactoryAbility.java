package de.mca.factories;

import com.google.gson.JsonArray;

import de.mca.model.ActivatedAbility;
import de.mca.model.enums.AbilityType;
import de.mca.model.enums.AdditionalCostType;
import de.mca.model.interfaces.IsManaMap;
import de.mca.model.interfaces.IsObject;
import javafx.collections.ObservableList;

/**
 *
 * @author Maximilian Werling
 *
 */
public interface FactoryAbility {

	ActivatedAbility create(IsObject source, AbilityType abilityType, AdditionalCostType additionalCostType,
			JsonArray effectObject, ObservableList<IsManaMap> listCostMaps);

}
