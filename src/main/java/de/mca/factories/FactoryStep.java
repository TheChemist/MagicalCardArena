package de.mca.factories;

import java.util.Set;

import de.mca.model.RuleEnforcer;
import de.mca.model.Step;
import de.mca.model.enums.StepType;
import de.mca.model.enums.TurnBasedActionType;

/**
 *
 * @author Maximilian Werling
 *
 */
public interface FactoryStep {

	Step create(RuleEnforcer ruleEnforcer, StepType stepType, Set<TurnBasedActionType> startTBAs);

}
