package de.mca.model.enums;

/**
 *
 * @author Maximilian Werling
 *
 */
public enum EffectType {

	PRODUCE_MANA("Mana produzieren");

	private final String displayName;

	private EffectType(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String toString() {
		return displayName;
	}

}
