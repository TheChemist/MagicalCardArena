package de.mca;

import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mca.model.Attack;
import de.mca.model.ActivatedAbility;
import de.mca.model.MagicCard;
import de.mca.model.MagicPermanent;
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
	private IsPlayer player;

	InputComputer() {
	}

	@Override
	public ActivatedAbility determineAbility(List<ActivatedAbility> listLegalAbilities) {
		// TODO: KI-Entscheidung
		throw new UnsupportedOperationException("Computer muss Entscheidung treffen, die er nicht treffen kann");
	}

	@Override
	public MagicPermanent determineAttacker(List<MagicPermanent> legalAttackers) throws UnsupportedOperationException {
		// TODO: KI-Entscheidung
		throw new UnsupportedOperationException("Computer muss Entscheidung treffen, die er nicht treffen kann");
	}

	@Override
	public IsAttackTarget determineAttackTarget(List<IsAttackTarget> legalAttackTargets)
			throws UnsupportedOperationException {
		// TODO: KI-Entscheidung
		throw new UnsupportedOperationException("Computer muss Entscheidung treffen, die er nicht treffen kann");
	}

	@Override
	public MagicPermanent determineBlocker(List<MagicPermanent> legalBlockers) throws UnsupportedOperationException {
		// TODO: KI-Entscheidung
		throw new UnsupportedOperationException("Computer muss Entscheidung treffen, die er nicht treffen kann");
	}

	@Override
	public int determineBlockTarget(List<Attack> listAttacks) throws UnsupportedOperationException {
		// TODO: KI-Entscheidung
		throw new UnsupportedOperationException("Computer muss Entscheidung treffen, die er nicht treffen kann");
	}

	@Override
	public MagicPermanent determineCardToActivate(List<MagicPermanent> legalPermanents)
			throws UnsupportedOperationException {
		// TODO: KI-Entscheidung
		throw new UnsupportedOperationException("Computer muss Entscheidung treffen, die er nicht treffen kann");
	}

	@Override
	public MagicCard determineCardToCast(List<MagicCard> legalCards) throws UnsupportedOperationException {
		// TODO: KI-Entscheidung
		throw new UnsupportedOperationException("Computer muss Entscheidung treffen, die er nicht treffen kann");
	}

	@Override
	public MagicCard determineCardToDiscard(List<MagicCard> handCards) {
		return handCards.get(new Random().nextInt(handCards.size()));
	}

	@Override
	public IsManaMap determineCostGoal(List<IsManaMap> costMaps) throws UnsupportedOperationException {
		// TODO: KI-Entscheidung
		throw new UnsupportedOperationException("Computer muss Entscheidung treffen, die er nicht treffen kann");
	}

	@Override
	public List<MagicPermanent> determineDamageAssignmentOrderAttacker(List<MagicPermanent> blockers)
			throws UnsupportedOperationException {
		// TODO: KI-Entscheidung
		throw new UnsupportedOperationException("Computer muss Entscheidung treffen, die er nicht treffen kann");
	}

	@Override
	public void determineInput(PlayerState playerState) {
		if (getPlayer().isAttacking()) {
			LOGGER.debug("{} determineInput({})", this, playerState);
			inputEndDeclareAttackers();
		} else if (getPlayer().isDefending()) {
			LOGGER.debug("{} determineInput({})", this, playerState);
			inputEndDeclareBlockers();
		} else if (getPlayer().isPrioritised()) {
			LOGGER.debug("{} determineInput({})", this, playerState);
			inputPassPriority();
		} else if (getPlayer().isDiscarding()) {
			LOGGER.debug("{} determineInput({})", this, playerState);
			// Computer wirft zufällige Karte ab.
			inputDiscard(determineCardToDiscard(getPlayer().getCardsHand()));
		}
	}

	@Override
	public IsPlayer getPlayer() {
		return player;
	}

	@Override
	public void setPlayer(IsPlayer player) {
		this.player = player;

		player.propertyPlayerState().addListener(new ChangeListener<PlayerState>() {

			@Override
			public void changed(ObservableValue<? extends PlayerState> observable, PlayerState oldValue,
					PlayerState newValue) {
				determineInput(newValue);
			}

		});
	}

	@Override
	public String toString() {
		return "Computer";
	}
}
