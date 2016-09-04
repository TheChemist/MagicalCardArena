package de.mca.model.enums;

/**
 * Sammelt die Bezeichnungen der verschiedenen Supertypen.
 *
 * @author Maximilian Werling
 *
 */
public enum SuperType {

	BASIC("Basic"), LEGENDARY("Legendary"), SNOW("Snow");

	private final String displayName;

	private SuperType(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String toString() {
		return displayName;
	}

}
