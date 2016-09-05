package de.mca.factories;

import com.google.inject.assistedinject.Assisted;

import de.mca.model.Deck;
import de.mca.model.Match;
import de.mca.model.interfaces.IsPlayer;

/**
 *
 * @author Maximilian Werling
 *
 */
public interface FactoryMatch {

	public Match create(@Assisted("playerComputer") IsPlayer playerComputer,
			@Assisted("playerHuman") IsPlayer playerHuman, @Assisted("nameHuman") String nameHuman,
			@Assisted("nameComputer") String nameComputer, @Assisted("deckHuman") Deck deckHuman,
			@Assisted("deckComputer") Deck deckComputer);

}
