package de.mca;

/**
 * Hält Bezeichnungen für die fünf relevanten Special Actions nach Regel 115.2.
 * im offiziellen Regelbuch.
 *
 * @author Maximilian Werling
 *
 */
public enum SpecialActionType {

	PLAY_A_LAND("Ein Land spielen");

	private final String displayName;

	private SpecialActionType(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String toString() {
		return displayName;
	}

}
