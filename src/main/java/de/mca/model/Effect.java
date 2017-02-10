package de.mca.model;

import java.util.EventObject;

import de.mca.model.enums.EffectType;
import de.mca.model.enums.PlayerType;

/**
 *
 * @author Maximilian Werling
 *
 */
@SuppressWarnings("serial")
public abstract class Effect extends EventObject {

	/**
	 * Speichert den Effekttyp.
	 */
	private final EffectType magicEffectType;
	private PlayerType playerType;

	protected Effect(ActivatedAbility source, EffectType magicEffectType) {
		super(source);
		this.magicEffectType = magicEffectType;
	}

	public EffectType getEffectType() {
		return magicEffectType;
	}

	public PlayerType getPlayerType() {
		return playerType;
	}

	public void setPlayerType(PlayerType playerType) {
		this.playerType = playerType;
	}

	@Override
	public String toString() {
		return magicEffectType.toString();
	}
}
