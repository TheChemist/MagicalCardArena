package de.mca.model.enums;

/**
 *
 * @author Maximilian Werling
 *
 */
public enum PlayerState {

	ACTIVATING_ABILITY("Aktiviere Fähigkeit"), ACTIVE("Aktiv"), ATTACKING("Greife an"), CASTING_SPELL(
			"Beschwöre Zauberspruch"), DEFENDING("Verteidige"), DISCARDING("Wirf Karte ab"), NONACTIVE(
					"Nichtaktiv"), PAYING("Wähle Manaquelle aus"), PRIORITIZED(
							"Passe, spiele Karte oder Fähigkeit"), TAKING_SPECIAL_ACTION("Führe Spezialhandlung aus");

	private final String displayName;

	private PlayerState(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String toString() {
		return displayName;
	}
}
