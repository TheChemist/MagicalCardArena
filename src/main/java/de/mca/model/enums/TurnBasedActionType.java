package de.mca.model.enums;

/**
 * Sammelt die Bezeichnungen der verschiedenen rundenbedingten Aktionen.
 *
 * @author Maximilian Werling
 *
 */
public enum TurnBasedActionType {

	BEGINNING_OF_COMBAT_STEP("Beginn der Kampfphase"), CLEANUP("Aufräumen"), CLEAR_MANA_POOLS(
			"Manapools leeren"), COMBAT_DAMAGE_ASSIGNMENT("Kampfschaden verteilen"), COMBAT_DAMAGE_DEALING(
					"Kampfschaden verrechnen"), DECLARE_ATTACKER("Angreifer deklarieren"), DECLARE_BLOCKER(
							"Blocker deklarieren"), DECLARE_DAMAGE_ASSIGNMENT_ORDER_ATTACKER(
									"Schadensreihefolge des Angreifers deklarieren"), DECLARE_DAMAGE_ASSIGNMENT_ORDER_BLOCKER(
											"Schadensreihenfolge des Blockers deklarieren"), DISCARD(
													"Handkarten abwerfen"), DRAW(
															"Ziehen"), PHASING("Phasing"), UNTAP("Enttappen");

	private final String displayName;

	private TurnBasedActionType(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String toString() {
		return displayName;
	}
}
