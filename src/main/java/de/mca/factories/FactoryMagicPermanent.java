package de.mca.factories;

import de.mca.model.MagicCard;
import de.mca.model.MagicPermanent;

/**
 *
 * @author Maximilian Werling
 *
 */
public interface FactoryMagicPermanent {

	MagicPermanent create(MagicCard magicCard);

}
