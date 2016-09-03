package de.mca;

/**
 *
 * @author Maximilian Werling
 *
 */
public enum PlayerActionType {

	ACTIVATE_ACTIVATED_ABILITY("Aktivierte Fähigkeit aktivieren", "602.2"), ACTIVATE_MANA_ABILITY(
			"Manafähigkeit aktivieren", "605.3"), CAST_SPELL("Zauber beschwören", "601.2"), CONCEDE("Spiel aufgeben",
					"104.3a"), DECLARE_ATTACKER("Angreifer deklarieren", "508.1"), DECLARE_BLOCKER(
							"Verteidiger deklarieren", ""), END_DECLARE_ATTACKERS("Beende Angreifer deklarieren",
									""), END_DECLARE_BLOCKERS("Beenden Blocker deklarieren", ""), PASS_PRIORITY(
											"Priorität abgeben", "116.3d"), SELECT_COST_MAP("Bezahlziel auswählen", "");

	private final String displayName;
	private final String rule;

	private PlayerActionType(String displayName, String rule) {
		this.displayName = displayName;
		this.rule = rule;
	}

	@Override
	public String toString() {
		return new StringBuilder(displayName).append(" r=[").append(rule).append("]").toString();
	}

}
