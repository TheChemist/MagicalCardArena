package de.mca.model.enums;

import java.util.EnumSet;
import java.util.Set;

/**
 * Sammelt die Bezeichnungen der unterschiedlichen Arten von Fähigkeiten. Da
 * Manafähigkeiten in den Regeln getrennt behandelt werden, werden sie hier als
 * eigene Bezeichnung geführt.
 *
 * @author Maximilian Werling
 *
 */
public enum AbilityType {

	ACTIVATED_ABILITY("Aktivierte Fähigkeit", "112.3b"), ACTIVATED_MANA_ABILITY("Manafähigkeit",
			"605.1a"), SPELL_ABILITY("Zauberspruch-Fähigkeit", "112.3a"), STATIC_ABILITY("Statische Fähigkeit",
					"112.3d."), TRIGGERED_ABILITY("Getriggerte Fähigkeit",
							"112.3c"), TRIGGERED_MANA_ABILITY("Getriggerte Manafähigkeit", "112.3d");

	private static final Set<AbilityType> IS_ACTIVATED = EnumSet.of(ACTIVATED_ABILITY, ACTIVATED_MANA_ABILITY);
	private final String displayName;
	private final String rule;

	private AbilityType(String displayName, String rule) {
		this.displayName = displayName;
		this.rule = rule;
	}

	public boolean isActivatedAbility() {
		return IS_ACTIVATED.contains(this);
	}

	public boolean isManaAbility() {
		return equals(ACTIVATED_MANA_ABILITY) || equals(TRIGGERED_MANA_ABILITY);
	}

	@Override
	public String toString() {
		return new StringBuilder(displayName).append(" r=[").append(rule).append("]").toString();
	}
}
