package de.mca.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mca.Constants;
import de.mca.model.enums.ObjectType;
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
public class InputComputer implements IsInput {

	/**
	 * Speichert den Logger.
	 */
	private final static Logger LOGGER = LoggerFactory.getLogger("Input");
	/**
	 * Speichert das Match.
	 */
	private final Match match;
	/**
	 * Speichert den MatchPresenter.
	 */
	private MatchPresenter matchPresenter;
	/**
	 * Speichert den Spieler.
	 */
	private IsPlayer player;

	public InputComputer(MatchPresenter matchPresenter, Match match, IsPlayer player) {
		this.matchPresenter = matchPresenter;
		this.match = match;
		this.player = player;

		this.player.propertyFlagNeedInput().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				LOGGER.trace("{} changed({}, {})", player, oldValue, newValue);

				if (newValue) {
					getRuleEnforcer().i_deriveInteractionStatus(getPlayer(), "InputComputer");

					switch (getPlayer().getPlayerState()) {
					case ATTACKING:
						if (getPlayer().getInteractionCount() < 1) {
							// Kein Interaktion möglich, breche ab.

							inputEndDeclareAttackers();
							break;
						}

						if (getPlayer().getFlagDeclaringAttackers()) {
							// Auswahlmodus für Angreifer.

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
					case CHOOSING_BLOCK_TARGET:
						// Blocke ersten Angreifer

						inputDeclareBlockTarget((MagicPermanent) getMatch().getListAttacks().get(0).getAttacker());
						break;
					case DEFENDING:
						if (getPlayer().getInteractionCount() < 1) {
							// Kein Interaktion möglich, breche ab.

							inputEndDeclareBlockers();
							break;
						}

						if (getPlayer().getFlagDeclaringBlockers()) {
							// Auswahlmodus für Blocker

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
						final int originalHandSize = player.propertyHandSize().get();
						inputDiscardRandom(originalHandSize - Constants.HAND_SIZE);
						break;
					case PRIORITIZED:
						if (getPlayer().getInteractionCount() < 1) {
							// Kein Interaktion möglich, breche ab.

							inputPassPriority();
							break;
						}

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

	@Override
	public String toString() {
		if (getPlayer() == null) {
			return "Noch kein Spieler gesetzt.";
		}
		return getPlayer().toString();
	}
}
