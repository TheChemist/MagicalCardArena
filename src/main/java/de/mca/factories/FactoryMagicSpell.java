package de.mca.factories;

import de.mca.model.MagicCard;
import de.mca.model.MagicSpell;

public interface FactoryMagicSpell {

	MagicSpell create(MagicCard mc);

}
