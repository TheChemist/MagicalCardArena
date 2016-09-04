package de.mca.factories;

import de.mca.model.EffectProduceMana;
import de.mca.model.interfaces.IsAbility;
import de.mca.model.interfaces.IsManaMap;

/**
 *
 * @author Maximilian Werling
 *
 */
public interface FactoryEffect {

	EffectProduceMana create(IsAbility source, IsManaMap produceMap);

}
