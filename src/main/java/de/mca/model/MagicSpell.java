package de.mca.model;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import de.mca.model.enums.ObjectType;
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
	 *
	 * @see http://magiccards.info/rule/109-objects.html#rule-109-4
	 */
	private final ObjectProperty<PlayerType> playerControlling;

	@Inject
	MagicSpell(EventBus eventBus, @Assisted MagicCard magicCard, @Assisted PlayerType playerControlling) {
		super(magicCard.getId());
		this.eventBus = eventBus;
		this.playerControlling = new SimpleObjectProperty<>(playerControlling);
		setDisplayName(magicCard.getDisplayName());
		setFileName(magicCard.getFileName());
		setListCharacteristicAbilities(magicCard.propertyListAbilities());
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

	public boolean canSplice() {
		// TODO: Feature "Splice"-Fähigkeit.
		return false;
	}

	@Override
	public PlayerType getPlayerControlling() {
		return playerControlling.get();
	}

	public boolean hasAdditionalCost() {
		// TODO: Feature zusätzliche Kosten.
		return false;
	}

	public boolean hasBuyback() {
		// TODO: Feature "Kicker"-Fähigkeit.
		return false;
	}

	/**
	 * Prüft, ob der Zauberspruch hybride Kosten enthält. Ein Zauberspruch mit
	 * hybriden Kosten hat mehr als eine CostMap.
	 *
	 * @return true, wenn die Anzahl der CostMaps größer 1 ist.
	 */
	public boolean hasHybridCost() {
		return propertyListCostMaps().getSize() > 1;
	}

	public boolean hasKicker() {
		// TODO: Feature "Buyback"-Fähigkeit.
		return false;
	}

	public boolean hasPhyrexianCost() {
		// TODO: Feature Phyrexanische Kosten.
		return false;
	}

	public boolean hasVariableCost() {
		// TODO: Feature variable Kosten.
		return false;
	}

	public boolean isModal() {
		// TODO: Feature modale Zaubersprüche.
		return false;
	}

	@Override
	public boolean isPermanentSpell() {
		for (final ObjectType ot : propertySetObjectTypes()) {
			if (ot.isPermanentSpell()) {
				return true;
			}
		}
		return false;
	}

	public boolean requiresTarget() {
		// TODO: Benötigt der Zauberspruch ein Ziel?
		return false;
	}

	@Override
	public void resolve() {
		propertyListEffects().forEach(effect -> getEventBus().post(effect));
	}

	@Override
	public void setPlayerControlling(PlayerType playerControlling) {
		this.playerControlling.set(playerControlling);
	}

	private EventBus getEventBus() {
		return eventBus;
	}
}
