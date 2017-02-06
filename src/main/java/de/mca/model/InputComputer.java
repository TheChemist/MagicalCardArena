package de.mca.model;

import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mca.model.enums.ObjectType;
import de.mca.model.enums.PlayerState;
import de.mca.model.interfaces.IsAttackTarget;
import de.mca.model.interfaces.IsInput;
import de.mca.model.interfaces.IsManaMap;
import de.mca.model.interfaces.IsPlayer;
import de.mca.presenter.MatchPresenter;
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
	/**
	 * Speichert den MatchPresenter.
	 */
	private MatchPresenter matchPresenter;
	/**
	 * Speichert den Spieler.
	 */
	private IsPlayer player;

	InputComputer() {
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
	public MatchPresenter getMatchPresenter() {
		return matchPresenter;
	}

	@Override
	public IsPlayer getPlayer() {
		return player;
	}

	@Override
	public RuleEnforcer getRuleEnforcer() {
		return getPlayer().getRuleEnforcer();
	}

	@Override
	public void setMatchPresenter(MatchPresenter matchPresenter) {
		this.matchPresenter = matchPresenter;
	}

	@Override
	public void setPlayer(IsPlayer player) {
		this.player = player;
		this.player.propertyPlayerState().addListener(new ChangeListener<PlayerState>() {

			@Override
			public void changed(ObservableValue<? extends PlayerState> observable, PlayerState oldValue,
					PlayerState newValue) {
				LOGGER.debug("{} changed({})", player, newValue);
				switch (newValue) {
				case SELECTING_ATTACKER:
					if (getPlayer().getFlagDeclaringAttackers()) {
						// Auswahlmodus für Angreifer.
						getPlayer().getRuleEnforcer().checkInteractable(getPlayer());

						inputEndDeclareAttackers();
					}
					break;
				case DEFENDING:
					if (getPlayer().getFlagDeclaringBlockers()) {
						// Auswahlmodus für Blocker.
						getPlayer().getRuleEnforcer().checkInteractable(getPlayer());

						inputEndDeclareBlockers();
					}
					break;
				case DISCARDING:
					inputDiscard(determineCardToDiscard(getPlayer().getZoneHand().getAll()));
					break;
				case PRIORITIZED:
					getPlayer().getRuleEnforcer().checkInteractable(getPlayer());

					// Spiele zufälliges Land
					for (final MagicCard magicCard : player.getZoneHand().getAll(ObjectType.LAND)) {
						if (matchPresenter.getMatchActive().getCurrentPhase().isMain()
								&& magicCard.getFlagIsInteractable()) {
							inputPlayLand(magicCard);
							return;
						}
					}

					for (final MagicPermanent magicPermanent : matchPresenter.getMatchActive().getZoneBattlefield()
							.getAll(ObjectType.LAND)) {
						if (matchPresenter.getMatchActive().getCurrentPhase().isMain()
								&& magicPermanent.getFlagIsInteractable()) {
							inputActivatePermanent(magicPermanent);
							return;
						}
					}

					for (final MagicCard magicCard : player.getZoneHand().getAll(ObjectType.CREATURE)) {
						if (matchPresenter.getMatchActive().getCurrentPhase().isMain()
								&& magicCard.getFlagIsInteractable()) {
							inputCastSpell(magicCard);
							return;
						}
					}

					inputPassPriority();
					break;
				default:
					break;
				}
			}

		});
	}

	@Override
	public String toString() {
		if (getPlayer() == null) {
			return "Noch kein Spieler gesetzt.";
		}
		return getPlayer().toString();
	}
}
