package de.mca.model;

import java.util.ArrayList;
import java.util.List;

import de.mca.model.enums.AdditionalCostType;
import de.mca.model.enums.ColorType;
import de.mca.model.interfaces.IsManaMap;

public class TotalCostInformation {

	private IsManaMap initialCost;
	private final List<IsManaMap> costIncreases;
	private final List<IsManaMap> costReductions;
	private AdditionalCostType additionalCostType;

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

	public void setInitalCost(IsManaMap initialCost) {
		this.initialCost = initialCost;
	}

	public AdditionalCostType getAdditionalCostType() {
		return additionalCostType;
	}

	public void setAdditionalCostType(AdditionalCostType additionalCostType) {
		this.additionalCostType = additionalCostType;
	}

	public IsManaMap getInitialCost() {
		return initialCost;
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

	public int getTotalConvertedCost() {
		return getTotalCost().getTotalMana();
	}

	public boolean hasAdditionalCostType() {
		return getAdditionalCostType() != null;
	}

}
