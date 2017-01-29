package de.mca.model.enums;

/**
 *
 * @author Maximilian Werling
 *
 */
public enum PlayerState {

	ACTIVATING_ABILITY("Aktiviere Fähigkeit"), ACTIVE("Aktiv"), ASSIGNING_DAMAGE_ORDER_BLOCKERS(
			"Lege Schadensreihenfolge der Verteidiger fest"), ASSINGING_DAMAGE_ORDER_ATTACKER(
					"Lege Schadensreihenfolge des Angreifers fest"), CASTING_SPELL("Beschwöre Zauberspruch"), DEFENDING("Verteidige"), DISCARDING(
							"Wirf Karte ab"), NONACTIVE("Nichtaktiv"), PAYING("Wähle Manaquelle aus"), PRIORITIZED(
									"Passe, spiele Karte oder Fähigkeit"), SELECTING_ATTACK_TARGET(
											"Wähle Angriffsziel aus"), SELECTING_ATTACKER(
													"Wähle Angreifer aus"), TAKING_SPECIAL_ACTION(
															"Führe Spezialhandlung aus");

	private final String displayName;

	private PlayerState(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String toString() {
		return displayName;
	}
}
