package de.mca.model.enums;

/**
 * Sammelt die Bezeichnung der verschiedenen Farben. Farblos, obwohl keine echte
 * Farbe, wird durch ColorType.NONE repräsentiert.
 *
 * @author Maximilian Werling
 *
 */
public enum ColorType {

	BLACK("schwarz", true), BLUE("blau", true), GREEN("grün", true), NONE("farblos", false), RED("rot",
			true), WHITE("weiß", true);

	private final String displayName;
	private final boolean isTrueColor;

	private ColorType(String displayName, boolean isTrueColor) {
		this.displayName = displayName;
		this.isTrueColor = isTrueColor;
	}

	public boolean isTrueColor() {
		return isTrueColor;
	}

	@Override
	public String toString() {
		return displayName;
	}
}
