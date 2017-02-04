package de.mca.model;

import java.util.ArrayList;

import com.google.common.eventbus.EventBus;
import com.google.gson.JsonArray;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import de.mca.MagicParser;
import de.mca.model.enums.AbilityType;
import de.mca.model.enums.AdditionalCostType;
import de.mca.model.enums.PlayerType;
import de.mca.model.interfaces.IsManaMap;
import de.mca.model.interfaces.IsObject;
import de.mca.model.interfaces.IsStackable;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Bildet eine Ability im Sinne von Regel 112.1a im offiziellen Regebuch ab.
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
	 * Speichert den Typ der zusätzlichen Kosten. Fallen keine zusätzlichen
	 * Kosten an wird NO_ADDITIONAL_COST gesetzt.
	 */
	private final AdditionalCostType additionalCostType;
	/**
	 * Speichert alle Informationen zum Effekt in einem JsonArray. Wird der
	 * Effekt benötigt, wird die Information in Echtzeit geparst.
	 */
	private final JsonArray effectObject;
	/**
	 * Speichert den EventBus.
	 */
	private final EventBus eventBus;
	/**
	 * Speichert den MagicParser, um den Effekt zu parsen.
	 */
	private final MagicParser magicParser;
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
	private final ObjectProperty<PlayerType> propertyPlayerControlling;
	/**
	 * Speichert das Objekt, das durch die Fähigkeit charakterisiert wird.
	 */
	private final ObjectProperty<IsObject> propertySource;

	@Inject
	ActivatedAbility(EventBus eventBus, MagicParser magicParser, @Assisted IsObject source,
			@Assisted AbilityType abilityType, @Assisted AdditionalCostType additionalCostType,
			@Assisted JsonArray effectObject, @Assisted ObservableList<IsManaMap> listCostMaps) {
		this.eventBus = eventBus;
		this.magicParser = magicParser;
		this.abilityType = abilityType;
		this.additionalCostType = additionalCostType;
		this.effectObject = effectObject;
		this.propertySource = new SimpleObjectProperty<>(source);
		this.propertyListCostMaps = new SimpleListProperty<>(listCostMaps);
		propertyListEffects = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
		propertyPlayerControlling = new SimpleObjectProperty<>(PlayerType.NONE);

		for (int i = 0; i < effectObject.size(); i++) {
			add(magicParser.parseEffect(this, effectObject.get(i).getAsJsonObject()));
		}
	}

	public void add(Effect magicEffect) {
		magicEffect.setPlayerType(getPlayerControlling());
		propertyListEffects().add(magicEffect);
	}

	public void generateEffects() {
		propertyListEffects.forEach(effect -> {
			if (effect.getPlayerType() == null || effect.getPlayerType().equals(PlayerType.NONE)) {
				throw new NullPointerException("Effekt wird von keinem Spieler kontrolliert!");
			}

			fireMagicEffect(effect);
		});
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

	public JsonArray getEffectInformation() {
		return effectObject;
	}

	@Override
	public int getId() {
		// TODO MID Nachzutragende Methodenimplementierung
		return 0;
	}

	public MagicParser getMagicParser() {
		return magicParser;
	}

	@Override
	public PlayerType getPlayerControlling() {
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
		// TODO MID Nachzutragende Methodenimplementierung
		return false;
	}

	@Override
	public ObservableList<IsManaMap> propertyListCostMaps() {
		return propertyListCostMaps;
	}

	public ListProperty<Effect> propertyListEffects() {
		return propertyListEffects;
	}

	public ObservableList<Effect> propertyListMagicEffects() {
		return propertyListEffects;
	}

	public ObjectProperty<PlayerType> propertyPlayerControlling() {
		return propertyPlayerControlling;
	}

	public ObjectProperty<IsObject> propertySource() {
		return propertySource;
	}

	public void remove(Effect magicEffect) {
		propertyListEffects().remove(magicEffect);
	}

	@Override
	public void resolve() {
		// TODO LOW Was passiert hier?
	}

	@Override
	public void setPlayerControlling(PlayerType playerControlling) {
		propertyListEffects().forEach(effect -> effect.setPlayerType(playerControlling));
		propertyPlayerControlling().set(playerControlling);
	}

	public void setSource(IsObject magicCard) {
		propertySource().set(magicCard);
	}

	@Override
	public String toString() {
		return abilityType.toString();
	}

	protected void fireMagicEffect(Effect magicEffect) {
		getEventBus().post(magicEffect);
	}

	protected EventBus getEventBus() {
		return eventBus;
	}

}