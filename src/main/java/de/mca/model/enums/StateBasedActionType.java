package de.mca.model.enums;

/**
 * Dient zur Unterscheidung verschiedener StateBasedActions.
 *
 * @author Maximilian Werling
 *
 */
public enum StateBasedActionType {

	CREATURE_TOUGHNESS_ZERO("Widerstand der Kreatur < 0", "704.5f"), PLAYER_CANT_DRAW("Spieler kann nicht ziehen",
			"704.5b"), PLAYER_LIFE_ZERO("Spieler hat keine Lebenspunkte mehr", "704.5a");

	private final String displayName;
	private final String rule;

	private StateBasedActionType(String displayName, String rule) {
		this.displayName = displayName;
		this.rule = rule;
	}

	@Override
	public String toString() {
		return new StringBuilder(displayName).append(" r=[").append(rule).append("]").toString();
	}

}
