package de.mca.model.enums;

/**
 * Bildet die Seltenheiten nach Regel 206.2 im offiziellen Regelbuch ab.
 *
 * @author Maximilian Werling
 *
 */
public enum RarityType {

	BASIC("basic"), COMMON("common"), MYTHIC_RARE("mytic rare"), RARE("rare"), SPECIAL("special"), UNCOMMON("uncommon");

	private final String displayName;

	RarityType(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String toString() {
		return displayName;
	}
}
