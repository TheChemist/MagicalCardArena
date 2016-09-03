package de.mca.factories;

import de.mca.model.enums.PlayerType;
import de.mca.model.interfaces.IsPlayer;

/**
 *
 * @author Maximilian Werling
 *
 */
public interface FactoryPlayer {

	IsPlayer create(PlayerType playerType);

}
