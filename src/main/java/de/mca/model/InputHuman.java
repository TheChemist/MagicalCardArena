package de.mca.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mca.model.enums.PlayerState;
import de.mca.model.enums.ZoneType;
import de.mca.model.interfaces.IsInput;
import de.mca.model.interfaces.IsPlayer;
import de.mca.presenter.MatchPresenter;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author Maximilian Werling
 *
 */
public class InputHuman implements IsInput {

	/**
	 * Speichert den Logger.
	 */
	private final static Logger LOGGER = LoggerFactory.getLogger("Input");
	private Match match;
	/**
	 * Speichert den MatchPresenter
	 */
	private MatchPresenter matchPresenter;
	/**
	 * Speichert den Spieler.
	 */
	private IsPlayer player;

	public InputHuman(MatchPresenter matchPresenter, Match match, IsPlayer player) {
		this.matchPresenter = matchPresenter;
		this.match = match;
		this.player = player;

		// Erstelle Binding zur flagNeedInput.
		// Setze hier die Trigger zum Öffnen der Dialoge
		this.player.propertyFlagNeedInput().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				LOGGER.trace("{} changed({}, {})", player, oldValue, newValue);

				if (newValue) {
					if (player.getPlayerState().equals(PlayerState.DISCARDING)) {

					} else {
						getPlayer().getRuleEnforcer().i_deriveInteractionStatus(getPlayer());
					}
				}
			}

		});
	}

	@Override
	public Match getMatch() {
		return match;
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

	public void input(MagicCard magicCard, ZoneType zoneType) {
		if (zoneType.equals(ZoneType.HAND)) {
			// Karte in Hand geklickt

			if (getPlayer().isDiscarding()) {
				// Spieler muss abwerfen.

				inputDiscard(magicCard);
			} else {
				// Spieler ist in beliebigem anderen Status.

				if (magicCard.isLand()) {
					inputPlayLand(magicCard);
				} else {
					inputCastSpell(magicCard);
				}
			}
		} else if (zoneType.equals(ZoneType.BATTLEFIELD)) {
			// Karte auf Spielfeld geklickt

			MagicPermanent magicPermanent = (MagicPermanent) magicCard;
			if (getPlayer().isAttacking() && getPlayer().getFlagDeclaringAttackers()) {
				// Angreifer deklarieren.

				inputDeclareAttacker(magicPermanent);
			} else if (getPlayer().isDefending() && getPlayer().getFlagDeclaringBlockers()) {
				// Blocker deklarieren.

				inputDeclareBlocker(magicPermanent);
			} else {
				// Permanent aktivieren

				inputActivatePermanent(magicPermanent);
			}
		}
	}

	public void progress() {
		switch (getPlayer().getPlayerState()) {
		case ATTACKING:
			if (getPlayer().getFlagDeclaringAttackers()) {
				// Spieler befindet sich im Auswahlmodus für Angreifer.

				inputEndDeclareAttackers();
			} else {
				// Spieler befindet sich in Prioritätsphase.

				inputPassPriority();
			}
			break;
		case DEFENDING:
			if (getPlayer().getFlagDeclaringBlockers()) {
				// Spieler befindet sich im Auswahlmodus für Verteidiger.

				inputEndDeclareBlockers();
			} else {
				// Spieler befindet sich in Prioritätsphase.

				inputPassPriority();
			}
			break;
		default:
			LOGGER.trace("{} progress() -> Pass priority!", this);
			inputPassPriority();
			break;
		}
	}

	@Override
	public String toString() {
		if (getPlayer() == null) {
			return "Noch kein Spieler gesetzt.";
		}
		return getPlayer().toString();
	}

}
