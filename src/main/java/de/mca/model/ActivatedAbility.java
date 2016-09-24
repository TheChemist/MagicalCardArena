package de.mca.model;

import de.mca.model.enums.ColorType;
import de.mca.model.enums.ObjectType;
import de.mca.model.enums.SubType;
import de.mca.model.enums.SuperType;
import de.mca.model.interfaces.IsObject;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

/**
 *
 * @author Maximilian Werling
 *
 */
// TODO: Klasse ist sehr panne
public final class ActivatedAbility extends Ability implements IsObject {

	public ActivatedAbility(Ability characteristicAbility) {
		super(characteristicAbility.getEventBus(), characteristicAbility.getMagicParser(),
				characteristicAbility.getSource(), characteristicAbility.getAbilityType(),
				characteristicAbility.getAdditionalCostType(), characteristicAbility.getEffectInformation(), characteristicAbility.propertyListCostMaps());
		// TODO: Keine gute Lösung
		setListEffects(characteristicAbility.propertyListMagicEffects());
	}

	@Override
	public void add(Ability characteristicAbilities) {
		// Nicht relevant hier...
	}

	@Override
	public String getDisplayName() {
		return toString();
	}

	@Override
	public int getHandModifier() {
		// Nicht relevant
		return 0;
	}

	@Override
	public int getId() {
		return -1;
	}

	@Override
	public int getLifeModifier() {
		// Nicht relevant
		return 0;
	}

	@Override
	public int getLoyalty() {
		// Nicht relevant
		return 0;
	}

	@Override
	public int getPower() {
		// Nicht relevant
		return 0;
	}

	@Override
	public int getToughness() {
		// Nicht relevant
		return 0;
	}

	public boolean isPermanentSpell() {
		return false;
	}

	@Override
	public ObservableList<Ability> propertyListCharacteristicAbilities() {
		// Nicht relevant
		return null;
	}

	@Override
	public ObservableSet<ColorType> propertySetColorTypes() {
		// Nicht relevant
		return null;
	}

	@Override
	public ObservableSet<ObjectType> propertySetObjectTypes() {
		// Nicht relevant
		return null;
	}

	@Override
	public ObservableSet<SubType> propertySetSubTypes() {
		// Nicht relevant
		return null;
	}

	@Override
	public ObservableSet<SuperType> propertySetSuperTypes() {
		// Nicht relevant
		return null;
	}

	@Override
	public String toString() {
		return new StringBuilder("[").append(super.toString()).append(" act=[").append(getAdditionalCostType())
				.append("]]").toString();
	}

	private void setListEffects(ObservableList<Effect> propertyListMagicEffects) {
		// TODO Auto-generated method stub
	}

}
