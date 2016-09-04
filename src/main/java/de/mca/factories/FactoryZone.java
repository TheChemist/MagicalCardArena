package de.mca.factories;

import de.mca.model.MagicCard;
import de.mca.model.ZoneDefault;
import de.mca.model.enums.PlayerType;
import de.mca.model.enums.ZoneType;

/**
 *
 * @author Maximilian Werling
 *
 */
public interface FactoryZone {

	ZoneDefault<MagicCard> create(PlayerType playerType, ZoneType zoneType);

}
