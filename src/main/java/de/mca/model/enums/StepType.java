package de.mca.model.enums;

/**
 * Sammelt die Bezeichnungen der verschiedenen Schritte.
 *
 * @author Maximilian Werling
 *
 */
public enum StepType {

	BEGINNING_OF_COMBAT_STEP("Beginn der Kampfphase", true), CLEANUP_STEP("Aufraeumen",
			false), COMBAT_DAMAGE_STEP("Kampfschaden", true), DECLARE_ATTACKERS("Angreifer benennen",
					true), DECLARE_BLOCKERS("Verteidiger benennen", true), DRAW_STEP("Ziehen", true), END_OF_COMBAT(
							"Ende der Kampfphase", true), END_STEP("Ende", true), NONE("Kein Spielschritt",
									false), UNTAP_STEP("Enttappen", false), UPKEEP_STEP("Unterhalt", true);

	private final String displayName;
	private final boolean playersGetPriority;

	private StepType(String displayName, boolean playersGetPriority) {
		this.displayName = displayName;
		this.playersGetPriority = playersGetPriority;
	}

	public boolean playersGetPriority() {
		return playersGetPriority;
	}

	@Override
	public String toString() {
		return displayName;
	}
}