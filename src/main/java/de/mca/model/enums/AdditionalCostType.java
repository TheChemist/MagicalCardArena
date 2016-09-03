package de.mca.model.enums;

/**
 *
 * @author Maximilian Werling
 *
 */
public enum AdditionalCostType {

	NO_ADDITIONAL_COST("Keine zusätzlichen Kosten"), TAP("Permanent tappen");

	private final String displayName;

	private AdditionalCostType(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String toString() {
		return displayName;
	}

}
