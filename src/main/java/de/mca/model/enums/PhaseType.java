package de.mca.model.enums;

/**
 *
 * @author Maximilian Werling
 *
 */
public enum PhaseType {

	BEGINNING_PHASE("Anfangsphase"), COMBAT_PHASE("Kampfphase"), ENDING_PHASE("Ending phase"), POSTCOMBAT_MAIN_PHASE(
			"Zweite Hauptphase"), PRECOMBAT_MAIN_PHASE("Erste Hauptphase");

	private final String displayName;

	private PhaseType(String displayName) {
		this.displayName = displayName;
	}

	public boolean isCombatPhase() {
		return this.equals(COMBAT_PHASE);
	}

	public boolean isMain() {
		return this.equals(PRECOMBAT_MAIN_PHASE) || this.equals(POSTCOMBAT_MAIN_PHASE);
	}

	@Override
	public String toString() {
		return displayName;
	}

}
