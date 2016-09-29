package de.mca.model;

import java.util.ArrayList;
import java.util.List;

import de.mca.model.enums.AdditionalCostType;
import de.mca.model.enums.ColorType;
import de.mca.model.interfaces.IsManaMap;

public class TotalCostInformation {

	private AdditionalCostType additionalCostType;
	private final List<IsManaMap> costIncreases;
	private final List<IsManaMap> costReductions;
	private IsManaMap initialCost;

	public TotalCostInformation() {
		costIncreases = new ArrayList<>();
		costReductions = new ArrayList<>();
	}

	public void addCostIncrease(IsManaMap costIncrease) {
		costIncreases.add(costIncrease);
	}

	public void addCostReductions(IsManaMap costReduction) {
		costReductions.add(costReduction);
	}

	public AdditionalCostType getAdditionalCostType() {
		return additionalCostType;
	}

	public IsManaMap getInitialCost() {
		return initialCost;
	}

	public int getTotalConvertedCost() {
		return getTotalCost().getTotalMana();
	}

	public IsManaMap getTotalCost() {
		IsManaMap totalCost = initialCost;
		for (IsManaMap costIncrease : costIncreases) {
			for (ColorType color : costIncrease.getKeySet()) {
				totalCost.add(color, costIncrease.get(color));
			}
		}
		for (IsManaMap costReduction : costReductions) {
			for (ColorType color : costReduction.getKeySet()) {
				totalCost.remove(color, costReduction.get(color));
			}
		}
		return totalCost;
	}

	public boolean hasAdditionalCostType() {
		if (getAdditionalCostType() == null) {
			return true;
		}
		if (getAdditionalCostType().equals(AdditionalCostType.NO_ADDITIONAL_COST)) {
			return true;
		} else {
			return false;
		}
	}

	public void setAdditionalCostType(AdditionalCostType additionalCostType) {
		this.additionalCostType = additionalCostType;
	}

	public void setInitalCost(IsManaMap initialCost) {
		this.initialCost = initialCost;
	}

}
