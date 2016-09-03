package de.mca.model.enums;

/**
 * Sammelt die Bezeichnungen der verschiedenen Zonen. Dient der Unterscheidung
 * der verschiedenen Zonen.
 *
 * @author Maximilian Werling
 *
 */
public enum ZoneType {

	BATTLEFIELD("Battlefield", true), EXILE("Exile", true), GRAVEYARD("Graveyard", true), HAND("Hand",
			false), LIBRARY("Library", false), STACK("Stack", true);

	private final String displayName;
	private final boolean isPublicZone;

	ZoneType(String displayName, boolean isPublicZone) {
		this.displayName = displayName;
		this.isPublicZone = isPublicZone;
	}

	public final boolean isPublicZone() {
		return isPublicZone;
	}

	@Override
	public String toString() {
		return displayName;
	}
}
