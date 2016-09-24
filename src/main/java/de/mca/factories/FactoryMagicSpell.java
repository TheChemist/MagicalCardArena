package de.mca.factories;

import de.mca.model.MagicCard;
import de.mca.model.MagicSpell;
import de.mca.model.enums.PlayerType;

/**
 * 
 * @author Maximilian Werling
 *
 */
public interface FactoryMagicSpell {

	MagicSpell create(MagicCard magicCard, PlayerType controllingPlayer);

}
