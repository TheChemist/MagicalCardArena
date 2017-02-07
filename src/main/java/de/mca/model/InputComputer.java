package de.mca.model;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mca.model.enums.ObjectType;
import de.mca.model.enums.PlayerState;
import de.mca.model.interfaces.IsInput;
import de.mca.model.interfaces.IsPlayer;
import de.mca.model.interfaces.IsZone;
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

	@Override
	public Match getMatch() {
		return getPlayer().getMatch();
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
				case ATTACKING:
					if (getPlayer().getFlagDeclaringAttackers()) {
						// Auswahlmodus für Angreifer.
						getRuleEnforcer().i_deriveInteractionStatus(getPlayer());

						// Greife an mit allen Kreaturen
						for (final MagicPermanent magicPermanent : getMatch().getZoneBattlefield()
								.getAll(ObjectType.CREATURE)) {
							if (magicPermanent.getFlagIsInteractable()) {
								inputDeclareAttacker(magicPermanent);
								return;
							}
						}

						inputEndDeclareAttackers();
					}
					break;
				case DEFENDING:
					if (getPlayer().getFlagDeclaringBlockers()) {
						// Auswahlmodus für Blocker.
						getRuleEnforcer().i_deriveInteractionStatus(getPlayer());

						// Blocke mit allen Kreaturen die erste Kreatur
						for (final MagicPermanent magicPermanent : getMatch().getZoneBattlefield()
								.getAll(ObjectType.CREATURE)) {
							if (magicPermanent.getFlagIsInteractable()) {
								inputDeclareBlocker(magicPermanent);
								return;
							}
						}

						inputEndDeclareBlockers();
					}
					break;
				case DISCARDING:
					IsZone<MagicCard> zoneHand = getPlayer().getZoneHand();
					inputDiscard(zoneHand.get(new Random().nextInt(zoneHand.getSize())));
					break;
				case PRIORITIZED:
					getRuleEnforcer().i_deriveInteractionStatus(getPlayer());

					// Spiele zufälliges Land
					for (final MagicCard magicCard : player.getZoneHand().getAll(ObjectType.LAND)) {
						if (magicCard.getFlagIsInteractable() && getMatch().getCurrentPhase().isMain()) {
							inputPlayLand(magicCard);
							return;
						}
					}

					// Aktiviere erstes aktivierbares Permanent
					for (final MagicPermanent magicPermanent : getMatch().getZoneBattlefield()
							.getAll(ObjectType.LAND)) {
						if (magicPermanent.getFlagIsInteractable() && getMatch().getCurrentPhase().isMain()) {
							inputActivatePermanent(magicPermanent);
							return;
						}
					}

					// Beschwöre erste beschwörbaren Zauberspruch.
					for (final MagicCard magicCard : player.getZoneHand().getAll(ObjectType.CREATURE)) {
						if (magicCard.getFlagIsInteractable() && getMatch().getCurrentPhase().isMain()) {
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
