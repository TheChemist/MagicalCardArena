package de.mca.model;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import de.mca.model.enums.PlayerType;
import de.mca.model.interfaces.IsStackable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Bildet einen Zauberspruch im Sinne von Abschnitt 111. des offiziellen
 * Regelbuchs ab.
 *
 * @author Maximilian Werling
 *
 */
public class MagicSpell extends MagicCard implements IsStackable {

	/**
	 * Speichert den EventBus.
	 */
	private final EventBus eventBus;
	/**
	 * Speichert den Spielertyp des kontrollierenden Spielers.
	 */
	private final ObjectProperty<PlayerType> playerControlling;

	@Inject
	MagicSpell(EventBus eventBus, @Assisted MagicCard magicCard) {
		super(magicCard.getId());
		this.eventBus = eventBus;
		this.playerControlling = new SimpleObjectProperty<>(magicCard.getPlayerOwning());
		setDisplayName(magicCard.getDisplayName());
		setFileName(magicCard.getFileName());
		setListCharacteristicAbilities(magicCard.propertyListCharacteristicAbilities());
		setListCostMaps(magicCard.propertyListCostMaps());
		setListEffects(magicCard.propertyListEffects());
		setListZonesVisited(magicCard.propertyListZonesVisited());
		setLoyalty(magicCard.getLoyalty());
		setPlayerOwning(magicCard.getPlayerOwning());
		setPower(magicCard.getPower());
		setRarity(getRarity());
		setSetColorTypes(magicCard.propertySetColorTypes());
		setSetObjectTypes(magicCard.propertySetObjectTypes());
		setSetSubTypes(magicCard.propertySetSubTypes());
		setSetSuperTypes(magicCard.propertySetSuperTypes());
		setToughness(magicCard.getToughness());
	}

	@Override
	public PlayerType getPlayerControlling() {
		return playerControlling.get();
	}

	@Override
	public void resolve() {
		for (final Effect effect : propertyListEffects()) {
			eventBus.post(effect);
		}
	}

	@Override
	public void setPlayerControlling(PlayerType playerControlling) {
		this.playerControlling.set(playerControlling);
	}

}
