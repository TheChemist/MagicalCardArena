package de.mca.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;

import de.mca.MagicParser;
import de.mca.model.enums.AbilityType;
import de.mca.model.enums.AdditionalCostType;
import de.mca.model.interfaces.IsManaMap;
import de.mca.model.interfaces.IsObject;
import de.mca.model.interfaces.IsPlayer;
import de.mca.model.interfaces.IsStackable;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Bildet eine Ability im Sinne von Regel 112.1a im offiziellen Regebuch ab.
 * TODO MID Bisher gibt es nur aktivierte Fähigkeiten.
 *
 * @author Maximilian Werling
 *
 */
public class ActivatedAbility implements IsStackable {

	/**
	 * Speichert den Fähigkeitstyp.
	 */
	private final AbilityType abilityType;
	/**
	 * Speichert den Typ der zusätzlichen Kosten. Fallen keine zusätzlichen Kosten
	 * an wird NO_ADDITIONAL_COST gesetzt.
	 */
	private final AdditionalCostType additionalCostType;
	/**
	 * Speichert die verschiedenen Darstellungen der Kosten.
	 */
	private final ListProperty<IsManaMap> propertyListCostMaps;
	/**
	 * Speichert die Liste der Effekte, die die Fähigkeit hervorruft.
	 */
	private final ListProperty<Effect> propertyListEffects;
	/**
	 * Speichert den Spielertyp des kontrollierenden Spielers.
	 */
	private final ObjectProperty<IsPlayer> propertyPlayerControlling;
	/**
	 * Speichert das Objekt, das durch die Fähigkeit charakterisiert wird.
	 */
	private final ObjectProperty<IsObject> propertySource;

	public ActivatedAbility(IsObject source, AbilityType abilityType, AdditionalCostType additionalCostType,
			JsonArray effectObject, ObservableList<IsManaMap> listCostMaps) {
		this.abilityType = abilityType;
		this.additionalCostType = additionalCostType;
		this.propertySource = new SimpleObjectProperty<>(source);
		this.propertyListCostMaps = new SimpleListProperty<>(listCostMaps);
		propertyListEffects = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
		propertyPlayerControlling = new SimpleObjectProperty<>();

		for (int i = 0; i < effectObject.size(); i++) {
			add(MagicParser.parseEffect(this, effectObject.get(i).getAsJsonObject()));
		}
	}

	public void add(Effect magicEffect) {
		magicEffect.setPlayer(getPlayerControlling());
		propertyListEffects().add(magicEffect);
	}

	public AbilityType getAbilityType() {
		return abilityType;
	}

	public AdditionalCostType getAdditionalCostType() {
		return additionalCostType;
	}

	@Override
	public String getDisplayName() {
		return abilityType.toString();
	}

	@Override
	public int getId() {
		// TODO MID Nachzutragende Methodenimplementierung
		return 0;
	}

	public List<Effect> getListEffects() {
		return propertyListEffects().get();
	}

	@Override
	public IsPlayer getPlayerControlling() {
		return propertyPlayerControlling().get();
	}

	public IsObject getSource() {
		return propertySource().get();
	}

	public boolean isManaAbility() {
		return getAbilityType().isManaAbility();
	}

	@Override
	public boolean isPermanentSpell() {
		return false;
	}

	@Override
	public ObservableList<IsManaMap> propertyListCostMaps() {
		return propertyListCostMaps;
	}

	@Override
	public ListProperty<Effect> propertyListEffects() {
		return propertyListEffects;
	}

	public ObjectProperty<IsPlayer> propertyPlayerControlling() {
		return propertyPlayerControlling;
	}

	public ObjectProperty<IsObject> propertySource() {
		return propertySource;
	}

	public void remove(Effect magicEffect) {
		propertyListEffects().remove(magicEffect);
	}

	@Override
	public void setPlayerControlling(IsPlayer playerControlling) {
		propertyListEffects().forEach(effect -> effect.setPlayer(playerControlling));
		propertyPlayerControlling().set(playerControlling);
	}

	public void setSource(IsObject magicCard) {
		propertySource().set(magicCard);
	}

	@Override
	public String toString() {
		return abilityType.toString();
	}

}