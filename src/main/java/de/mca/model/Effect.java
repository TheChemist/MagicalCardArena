package de.mca.model;

import java.util.EventObject;

import de.mca.model.enums.EffectType;
import de.mca.model.interfaces.IsPlayer;

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
	/**
	 * Speichert den Spieler, der den Effekt ausgelöst hat.
	 */
	private transient IsPlayer player;

	protected Effect(ActivatedAbility source, EffectType magicEffectType) {
		super(source);
		this.magicEffectType = magicEffectType;
	}

	public EffectType getEffectType() {
		return magicEffectType;
	}

	public IsPlayer getPlayer() {
		return player;
	}

	public void setPlayer(IsPlayer player) {
		this.player = player;
	}

	@Override
	public String toString() {
		return magicEffectType.toString();
	}
}
