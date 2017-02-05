package de.mca;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mca.model.MagicCard;
import de.mca.model.MagicPermanent;
import de.mca.model.enums.PlayerState;
import de.mca.model.enums.ZoneType;
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
	 * Speichert den Spieler.
	 */
	private IsPlayer player;
	/**
	 * Speichert den MatchPresenter
	 */
	private MatchPresenter matchPresenter;

	InputHuman() {

	}

	@Override
	public IsPlayer getPlayer() {
		return player;
	}

	public void input(MagicCard object, ZoneType zoneType) {
		if (zoneType.equals(ZoneType.HAND)) {
			// Karte in Hand geklickt
			MagicCard magicCard = object;
			if (getPlayer().isDiscarding()) {
				getPlayer().fireDiscard(magicCard);
			} else {
				if (magicCard.isLand()) {
					getPlayer().firePlayLand(magicCard);
				} else {
					getPlayer().fireCastSpell(magicCard);
				}
			}
		} else if (zoneType.equals(ZoneType.BATTLEFIELD)) {
			// Karte auf Spielfeld geklickt
			MagicPermanent magicPermanent = (MagicPermanent) object;
			if (getPlayer().isAttacking()) {
				// Angreifer deklarieren

				getPlayer().fireDeclareAttacker(magicPermanent);
			} else {
				// Permanent aktivieren

				getPlayer().fireActivatePermanent(magicPermanent);
			}
		}
	}

	public void progress() {
		switch (getPlayer().getPlayerState()) {
		case SELECTING_ATTACKER:
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
	public void setPlayer(IsPlayer player) {
		this.player = player;
		this.player.propertyPlayerState().addListener(new ChangeListener<PlayerState>() {

			@Override
			public void changed(ObservableValue<? extends PlayerState> observable, PlayerState oldValue,
					PlayerState newValue) {
				switch (newValue) {
				case SELECTING_ATTACKER:
					if (getPlayer().getFlagDeclaringAttackers()) {
						// Auswahlmodus für Angreifer.
						matchPresenter.getMatchActive().getRuleEnforcer().checkInteractable(getPlayer());
					}
					break;
				case DEFENDING:
					if (getPlayer().getFlagDeclaringBlockers()) {
						// Auswahlmodus für Blocker.
						matchPresenter.getMatchActive().getRuleEnforcer().checkInteractable(getPlayer());
					}
					break;
				case PRIORITIZED:
					matchPresenter.getMatchActive().getRuleEnforcer().checkInteractable(getPlayer());
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

	@Override
	public void setParent(MatchPresenter matchPresenter) {
		this.matchPresenter = matchPresenter;
	}
}
