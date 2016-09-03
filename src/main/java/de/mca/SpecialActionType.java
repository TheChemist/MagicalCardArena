package de.mca;

/**
 * Hält Bezeichnungen für die fünf relevanten Special Actions nach Regel 115.2.
 * im offiziellen Regelbuch.
 *
 * @author Maximilian Werling
 *
 */
public enum SpecialActionType {

	PLAY_A_LAND("Ein Land spielen", "115.2a");

	private final String displayName;
	private final String rule;

	private SpecialActionType(String displayName, String rule) {
		this.displayName = displayName;
		this.rule = rule;
	}

	@Override
	public String toString() {
		return new StringBuilder(displayName).append(" r=[").append(rule).append("]").toString();
	}

}
