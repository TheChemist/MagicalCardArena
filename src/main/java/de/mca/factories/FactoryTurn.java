package de.mca.factories;

import com.google.inject.assistedinject.Assisted;

import de.mca.model.Match;
import de.mca.model.Turn;
import de.mca.model.interfaces.IsPlayer;

/**
 * 
 * @author Maximilian Werling
 *
 */
public interface FactoryTurn {

	Turn create(@Assisted("playerComputer") IsPlayer playerComputer, @Assisted("playerHuman") IsPlayer human,
			@Assisted Match parent);

}
