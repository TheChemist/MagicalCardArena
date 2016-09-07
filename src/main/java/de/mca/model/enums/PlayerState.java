package de.mca.model.enums;

/**
 *
 * @author Maximilian Werling
 *
 */
public enum PlayerState {

	ACTIVATING_ABILITY("Aktiviert Fähigkeit"), ACTIVE("Aktiv"), ATTACKING("Greift an"), CASTING_SPELL(
			"Beschwört Zauberspruch"), DEFENDING("Verteidigt"), DISCARDING("Wirft Karte ab"), NONACTIVE("Nichtaktiv"), PAYING("Bezahlt"), PRIORITIZED(
							"Priorisiert"), TAKING_SPECIAL_ACTION("Führt Spezialhandlung aus");

	private final String displayName;

	private PlayerState(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String toString() {
		return displayName;
	}
}
