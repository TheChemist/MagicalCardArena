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
	private final List<IsManaMap> listCostMaps;
	/**
	 * Speichert die Liste der Effekte, die die Fähigkeit hervorruft.
	 */
	private final List<Effect> listEffects;
	/**
	 * Speichert den Spielertyp des kontrollierenden Spielers.
	 */
	private IsPlayer playerControlling;
	/**
	 * Speichert das Objekt, das durch die Fähigkeit charakterisiert wird.
	 */
	private IsObject source;

	public ActivatedAbility(IsObject source, AbilityType abilityType, AdditionalCostType additionalCostType,
			JsonArray effectObject, List<IsManaMap> listCostMaps) {
		this.abilityType = abilityType;
		this.additionalCostType = additionalCostType;
		this.source = source;
		this.listCostMaps = listCostMaps;
		listEffects = new ArrayList<>();
		playerControlling = null;

		for (int i = 0; i < effectObject.size(); i++) {
			add(MagicParser.parseEffect(this, effectObject.get(i).getAsJsonObject()));
		}
	}

	public void add(Effect magicEffect) {
		magicEffect.setPlayer(getPlayerControlling());
		getListEffects().add(magicEffect);
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
		return listEffects;
	}

	@Override
	public IsPlayer getPlayerControlling() {
		return playerControlling;
	}

	public IsObject getSource() {
		return source;
	}

	public boolean isManaAbility() {
		return getAbilityType().isManaAbility();
	}

	@Override
	public boolean isPermanentSpell() {
		return false;
	}

	@Override
	public List<IsManaMap> getListCostMaps() {
		return listCostMaps;
	}

	public void remove(Effect magicEffect) {
		getListEffects().remove(magicEffect);
	}

	@Override
	public void setPlayerControlling(IsPlayer playerControlling) {
		getListEffects().forEach(effect -> effect.setPlayer(playerControlling));
		this.playerControlling = playerControlling;
	}

	public void setSource(IsObject source) {
		this.source = source;
	}

	@Override
	public String toString() {
		return abilityType.toString();
	}

}