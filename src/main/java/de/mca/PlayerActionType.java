package de.mca;

/**
 *
 * @author Maximilian Werling
 *
 */
public enum PlayerActionType {

	ACTIVATE_MANA_ABILITY("Manafähigkeit aktivieren"), ACTIVATE_PERMANENT(
			"Aktivierte Fähigkeit aktivieren"), CAST_SPELL("Zauber beschwören"), CONCEDE(
					"Spiel aufgeben"), DECLARE_ATTACKER("Angreifer deklarieren"), DECLARE_BLOCKER(
							"Verteidiger deklarieren"), DISCARD("Karte abwerfen"), END_DECLARE_ATTACKERS(
									"Beende Angreifer deklarieren"), END_DECLARE_BLOCKERS(
											"Beenden Blocker deklarieren"), PASS_PRIORITY(
													"Priorität abgeben"), SELECT_ATTACK_TARGET(
															"Angriffziel auswählen"), SELECT_COST_MAP(
																	"Bezahlziel auswählen");

	private final String displayName;

	private PlayerActionType(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String toString() {
		return new StringBuilder(displayName).toString();
	}

}
