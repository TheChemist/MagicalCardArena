package de.mca.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	/**
	 * Speichert den MatchPresenter
	 */
	private MatchPresenter matchPresenter;
	/**
	 * Speichert den Spieler.
	 */
	private IsPlayer player;

	InputHuman() {

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

	public void input(MagicCard object, ZoneType zoneType) {
		if (zoneType.equals(ZoneType.HAND)) {
			// Karte in Hand geklickt
			MagicCard magicCard = object;
			if (getPlayer().isDiscarding()) {
				inputDiscard(magicCard);
			} else {
				if (magicCard.isLand()) {
					inputPlayLand(magicCard);
				} else {
					inputCastSpell(magicCard);
				}
			}
		} else if (zoneType.equals(ZoneType.BATTLEFIELD)) {
			// Karte auf Spielfeld geklickt
			MagicPermanent magicPermanent = (MagicPermanent) object;
			if (getPlayer().isAttacking()) {
				// Angreifer deklarieren

				inputDeclareAttacker(magicPermanent);
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
	public void setMatchPresenter(MatchPresenter matchPresenter) {
		this.matchPresenter = matchPresenter;
	}

	@Override
	public void setPlayer(IsPlayer player) {
		this.player = player;

		// Erstelle Binding zur flagNeedInput.
		this.player.propertyFlagNeedInput().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				LOGGER.trace("{} changed({}, {})", player, oldValue, newValue);

				if (newValue) {
					getPlayer().getRuleEnforcer().i_deriveInteractionStatus(getPlayer());
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
