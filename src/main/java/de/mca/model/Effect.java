package de.mca.model;

import java.util.EventObject;

import de.mca.model.enums.EffectType;
import de.mca.model.interfaces.IsAbility;

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

	Effect(IsAbility source, EffectType magicEffectType) {
		super(source);
		this.magicEffectType = magicEffectType;
	}

	public EffectType getMagicEffectType() {
		return magicEffectType;
	}

	@Override
	public String toString() {
		return magicEffectType.toString();
	}
}
