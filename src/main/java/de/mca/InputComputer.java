package de.mca;

import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mca.model.ActivatedAbility;
import de.mca.model.Attack;
import de.mca.model.MagicCard;
import de.mca.model.MagicPermanent;
import de.mca.model.Match;
import de.mca.model.enums.PlayerState;
import de.mca.model.interfaces.IsAttackTarget;
import de.mca.model.interfaces.IsManaMap;
import de.mca.model.interfaces.IsPlayer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author Maximilian Werling
 *
 */
public class InputComputer implements IsInput {

	/**
	 * Speichert den Logger.
	 */
	private final static Logger LOGGER = LoggerFactory.getLogger("Input");
	private Match match;
	private IsPlayer player;

	InputComputer() {
	}

	@Override
	public void buttonProgressClicked(boolean flagNeedPlayerInput) {
		final PlayerState playerState = getPlayer().getPlayerState();

		switch (playerState) {
		case ACTIVE:
		case NONACTIVE:
			LOGGER.trace("{} buttonProgressClicked({}) -> Nothing to do!", this, flagNeedPlayerInput);
			break;
		case SELECTING_ATTACKER:
			if (getPlayer().getFlagDeclaringAttackers()) {
				// Spieler befindet sich im Auswahlmodus für Angreifer.

				inputEndDeclareAttackers();
			}
			break;
		case DEFENDING:
			if (getPlayer().getFlagDeclaringBlockers()) {
				// Spieler befindet sich im Auswahlmodus für Verteidiger.

				inputEndDeclareBlockers();
			}
			break;
		case DISCARDING:
			LOGGER.trace("{} buttonProgressClicked({}) -> Discard!", this, flagNeedPlayerInput);
			inputDiscard(determineCardToDiscard(getPlayer().getZoneHand().getAll()));
			break;
		default:
			LOGGER.trace("{} buttonProgressClicked({}) -> Pass priority!", this, flagNeedPlayerInput);
			inputPassPriority();
			break;
		}
	}

	public ActivatedAbility determineAbility(List<ActivatedAbility> listLegalAbilities) {
		// TODO MID KI-Entscheidung
		throw new UnsupportedOperationException("Computer muss Entscheidung treffen, die er nicht treffen kann");
	}

	public MagicPermanent determineAttacker(List<MagicPermanent> legalAttackers) throws UnsupportedOperationException {
		// TODO MID KI-Entscheidung
		throw new UnsupportedOperationException("Computer muss Entscheidung treffen, die er nicht treffen kann");
	}

	public IsAttackTarget determineAttackTarget(List<IsAttackTarget> legalAttackTargets)
			throws UnsupportedOperationException {
		// TODO MID KI-Entscheidung
		throw new UnsupportedOperationException("Computer muss Entscheidung treffen, die er nicht treffen kann");
	}

	public MagicPermanent determineBlocker(List<MagicPermanent> legalBlockers) throws UnsupportedOperationException {
		// TODO MID KI-Entscheidung
		throw new UnsupportedOperationException("Computer muss Entscheidung treffen, die er nicht treffen kann");
	}

	public int determineBlockTarget(List<Attack> listAttacks) throws UnsupportedOperationException {
		// TODO MID KI-Entscheidung
		throw new UnsupportedOperationException("Computer muss Entscheidung treffen, die er nicht treffen kann");
	}

	public MagicPermanent determineCardToActivate(List<MagicPermanent> legalPermanents)
			throws UnsupportedOperationException {
		// TODO MID KI-Entscheidung
		throw new UnsupportedOperationException("Computer muss Entscheidung treffen, die er nicht treffen kann");
	}

	public MagicCard determineCardToCast(List<MagicCard> legalCards) throws UnsupportedOperationException {
		// TODO MID KI-Entscheidung
		throw new UnsupportedOperationException("Computer muss Entscheidung treffen, die er nicht treffen kann");
	}

	public MagicCard determineCardToDiscard(List<MagicCard> handCards) {
		return handCards.get(new Random().nextInt(handCards.size()));
	}

	public IsManaMap determineCostGoal(List<IsManaMap> costMaps) throws UnsupportedOperationException {
		// TODO MID KI-Entscheidung
		throw new UnsupportedOperationException("Computer muss Entscheidung treffen, die er nicht treffen kann");
	}

	public List<MagicPermanent> determineDamageAssignmentOrderAttacker(List<MagicPermanent> blockers)
			throws UnsupportedOperationException {
		// TODO MID KI-Entscheidung
		throw new UnsupportedOperationException("Computer muss Entscheidung treffen, die er nicht treffen kann");
	}

	@Override
	public IsPlayer getPlayer() {
		return player;
	}

	public void setMatch(Match match) {
		this.match = match;
		/**
		 * Stattdessen sollte der Spieler immer dann zu einer Handlung bewogen
		 * werden, wenn das Match einen Input benötigt (flagNeedPlayerInput).
		 *
		 * Eine Kombination des PS und verschiedener Flags sollten ausreichen,
		 * um zu bestimmen, ob und welche Art von Input vom Spieler benötigt
		 * werden.
		 */
		this.match.propertyFlagNeedPlayerInput().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue) {
					buttonProgressClicked(newValue);
				}
			}

		});
	}

	@Override
	public void setPlayer(IsPlayer player) {
		this.player = player;
	}

	@Override
	public String toString() {
		if (getPlayer() == null) {
			return "Noch kein Spieler gesetzt.";
		}
		return getPlayer().toString();
	}
}
