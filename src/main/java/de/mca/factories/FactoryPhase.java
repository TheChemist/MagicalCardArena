package de.mca.factories;

import java.util.List;

import com.google.inject.assistedinject.Assisted;

import de.mca.model.Match;
import de.mca.model.Phase;
import de.mca.model.Step;
import de.mca.model.enums.PhaseType;

/**
 *
 * @author Maximilian Werling
 *
 */
public interface FactoryPhase {

	Phase create(@Assisted PhaseType phaseType, @Assisted List<Step> stepList, @Assisted Match parent);

}
