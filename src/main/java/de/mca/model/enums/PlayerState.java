package de.mca.model.enums;

/**
 *
 * @author Maximilian Werling
 *
 */
public enum PlayerState {

	ACTIVATING_ABILITY("Aktiviert Fähigkeit"), ACTIVE("Aktiv"), ATTACKING("Greift an"), CASTING_SPELL(
			"Beschwört Zauberspruch"), DEFENDING("Verteidigt"), NONACTIVE("Nichtaktiv"), PAYING("Bezahlt"), PRIORITIZED(
					"Priorisiert"), TAKING_SPECIAL_ACTION("Führt Spezialhandlung aus"), DISCARDING("Wirft Karte ab");

	private final String displayName;

	private PlayerState(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String toString() {
		return displayName;
	}
}
