package de.mca;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mca.model.MagicCard;
import de.mca.model.MagicPermanent;
import de.mca.model.enums.PlayerState;
import de.mca.model.enums.ZoneType;
import de.mca.model.interfaces.IsPlayer;

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
	private IsPlayer player;

	InputHuman() {

	}

	@Override
	public void buttonProgressClicked(boolean flagNeedPlayerInput) {
		final PlayerState playerState = getPlayer().getPlayerState();

		switch (playerState) {
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
			LOGGER.debug("{} buttonProgressClicked({}) -> Pass priority!", this, flagNeedPlayerInput);
			inputPassPriority();
			break;
		}
	}

	@Override
	public IsPlayer getPlayer() {
		return player;
	}

	// TODO: Konzept der Interactables einführen?
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
				// Angreifer auswählen

				getPlayer().fireDeclareAttacker(magicPermanent);
			} else {
				// TODO: Fähigkeit spielen

				getPlayer().fireActivatePermanent(magicPermanent);
			}
		}
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
