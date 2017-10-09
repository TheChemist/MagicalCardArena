package de.mca.model.enums;

/**
 *
 * @author Maximilian Werling
 *
 */
public enum PlayerState {

	ACTIVATING_ABILITY("Aktiviere Fähigkeit"), ACTIVE("Aktiv"), ASSIGNING_DAMAGE_ORDER_BLOCKERS(
			"Lege Schadensreihenfolge der Verteidiger fest"), ASSINGING_DAMAGE_ORDER_ATTACKER(
					"Lege Schadensreihenfolge des Angreifers fest"), ATTACKING("Angriff"), CASTING_SPELL(
							"Beschwöre Zauberspruch"), CHOOSING_BLOCK_TARGET(
									"Wähle Blockziel aus"), DEFENDING("Blocke"), DISCARDING("Wirf Karte ab"), NONACTIVE(
											"Nichtaktiv"), PAYING("Wähle Manaquelle aus"), PRIORITIZED(
													"Passe, spiele Karte oder Fähigkeit"), TAKING_SPECIAL_ACTION(
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
