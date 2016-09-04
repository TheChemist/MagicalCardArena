package de.mca.factories;

import java.util.List;

import de.mca.model.Phase;
import de.mca.model.Step;
import de.mca.model.enums.PhaseType;

/**
 *
 * @author Maximilian Werling
 *
 */
public interface FactoryPhase {

	Phase create(PhaseType phaseType, List<Step> stepList);

}
