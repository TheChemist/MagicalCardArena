package de.mca.model.enums;

/**
 * Sammelt die Bezeichnungen der verschiedenen rundenbedingten Aktionen.
 *
 * @author Maximilian Werling
 *
 */
public enum TurnBasedActionType {

	BEGINNING_OF_COMBAT_STEP("Spielerstatus ändern", "703.4e"), CLEANUP("Aufraeumen", "703.4n"), CLEAR_MANA_POOLS(
			"Manapools leeren", "703.4p"), COMBAT_DAMAGE_ASSIGNMENT("Kampfschaden verteilen",
					"703.4j"), COMBAT_DAMAGE_DEALING("Kampfschaden verrechnen", "703.4k"), DECLARE_ATTACKER(
							"Angreifer deklarieren", "703.4f"), DECLARE_BLOCKER("Blocker deklarieren",
									"703.4g"), DECLARE_DAMAGE_ASSIGNMENT_ORDER_ATTACKER(
											"Schadensreihefolge des Angreifers deklarieren",
											"703.4h"), DECLARE_DAMAGE_ASSIGNMENT_ORDER_BLOCKER(
													"Schadensreihenfolge des Blockers deklarieren",
													"703.4i"), DISCARD("Handkarten abwerfen", "703.4m"), DRAW("Ziehen",
															"703.4c"), PHASING("Phasing", "703.4a"), UNTAP("Enttappen",
																	"703.4b");

	private final String displayName;
	private final String rule;

	private TurnBasedActionType(String displayName, String rule) {
		this.displayName = displayName;
		this.rule = rule;
	}

	@Override
	public String toString() {
		return new StringBuilder(displayName).append(" r=[").append(rule).append("]").toString();
	}
}
