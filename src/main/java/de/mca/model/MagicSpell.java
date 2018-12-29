package de.mca.model;

import de.mca.model.interfaces.IsPlayer;
import de.mca.model.interfaces.IsStackable;

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
	private transient IsPlayer playerControlling;

	MagicSpell(MagicCard magicCard, IsPlayer playerControlling) {
		super(magicCard.getId());
		this.playerControlling = playerControlling;
		setDisplayName(magicCard.getDisplayName());
		setFileName(magicCard.getFileName());
		setListActivatedAbilities(magicCard.getListActivatedAbilities());
		setListCostMaps(magicCard.getListCostMaps());
		setListEffects(magicCard.getListEffects());
		setListZonesVisited(magicCard.getListZonesVisited());
		setLoyalty(magicCard.getLoyalty());
		setPlayerOwning(magicCard.getPlayerOwning());
		setPower(magicCard.getPower());
		setRarity(getRarity());
		setSetColorTypes(magicCard.getSetColorType());
		setSetObjectTypes(magicCard.getSetObjectTypes());
		setSetSubTypes(magicCard.getSetSubTypes());
		setSetSuperTypes(magicCard.getSetSuperTypes());
		setToughness(magicCard.getToughness());
	}

	@Override
	public IsPlayer getPlayerControlling() {
		return playerControlling;
	}

	/**
	 * Prüft, ob der Zauberspruch hybride Kosten enthält. Ein Zauberspruch mit
	 * hybriden Kosten hat mehr als eine CostMap.
	 *
	 * @return true, wenn die Anzahl der CostMaps größer 1 ist.
	 */
	public boolean hasHybridCost() {
		return getListCostMaps().size() > 1;
	}

	@Override
	public void setPlayerControlling(IsPlayer playerControlling) {

		LOGGER.trace("{} setPlayerControlling({})", this, playerControlling);
		this.playerControlling = playerControlling;
	}

}
