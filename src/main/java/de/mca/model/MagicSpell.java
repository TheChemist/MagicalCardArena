package de.mca.model;

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
	 * Speichert den Spielertyp des kontrollierenden Spielers.
	 *
	 * @see http://magiccards.info/rule/109-objects.html#rule-109-4
	 */
	private final ObjectProperty<PlayerType> playerControlling;

	@Inject
	MagicSpell(@Assisted MagicCard magicCard, @Assisted PlayerType playerControlling) {
		super(magicCard.getId());
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
		// TODO LOW Feature "Splice"-Fähigkeit.
		return false;
	}

	@Override
	public PlayerType getPlayerControlling() {
		return playerControlling.get();
	}

	public boolean hasAdditionalCost() {
		// TODO MID Feature zusätzliche Kosten.
		return false;
	}

	public boolean hasBuyback() {
		// TODO LOW Feature "Kicker"-Fähigkeit.
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
		// TODO LOW Feature "Buyback"-Fähigkeit.
		return false;
	}

	public boolean hasPhyrexianCost() {
		// TODO LOW Feature Phyrexanische Kosten.
		return false;
	}

	public boolean hasVariableCost() {
		// TODO LOW Feature variable Kosten.
		return false;
	}

	public boolean isModal() {
		// TODO LOW Feature modale Zaubersprüche.
		return false;
	}

	public boolean requiresTarget() {
		// TODO LOW Benötigt der Zauberspruch ein Ziel?
		return false;
	}

	@Override
	public void setPlayerControlling(PlayerType playerControlling) {
		this.playerControlling.set(playerControlling);
	}

}
