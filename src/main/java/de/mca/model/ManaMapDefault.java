package de.mca.model;

import java.util.Set;

import de.mca.model.enums.ColorType;
import de.mca.model.interfaces.IsManaMap;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

/**
 * Die ManaMap wird benutzt, um verschiedenartige Mana-Konstellationen im
 * Manapool der Spieler sowie Manakosten für Karten und Abilities abzubilden.
 * Farblose Manakosten können mit Mana jeder Farbe oder explizit farblosem Mana
 * bezahlt werden. Das macht es wichtig zu wissen, wie viel farblose Kosten
 * gedeckt werden müssen. Vorrangig werden farbige Kosten gedeckt, dann
 * farblose. Farblose Kosten werden vorrangig durch explizit farbloses Mana
 * gedeckt.
 *
 * @author Maximilian Werling
 *
 */
public class ManaMapDefault implements IsManaMap {

	/**
	 * Speichert das Mana.
	 */
	private final MapProperty<ColorType, Integer> propertyMapMana;

	public ManaMapDefault() {
		propertyMapMana = new SimpleMapProperty<>(FXCollections.observableHashMap());
	}

	public ManaMapDefault(ObservableMap<ColorType, Integer> manaMap) {
		propertyMapMana = new SimpleMapProperty<>(manaMap);
	}

	@Override
	public void add(ColorType color, int howMuch) {
		for (int i = 0; i < howMuch; i++) {
			manaAdd(color);
		}
	}

	@Override
	public void addAll(IsManaMap manaMap) {
		manaMap.getKeySet().forEach(key -> add(key, get(key) + manaMap.get(key)));
	}

	@Override
	public boolean contains(IsManaMap costMap) {
		// TODO MID Benötigt intensives Testen, sollten sich fehler zeigen.
		final IsManaMap difference = getDifference(costMap);

		if (difference.getTotalMana() < 0 && !hasNegativeTrueColor(difference)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean containsKey(ColorType color) {
		return propertyMapMana().containsKey(color);
	}

	@Override
	public boolean equals(IsManaMap manaMap) {
		return getDifference(manaMap).isEmpty();
	}

	@Override
	public int get(ColorType color) {
		return containsKey(color) ? propertyMapMana().get(color) : 0;
	}

	@Override
	public IsManaMap getDifference(IsManaMap manaMap) {
		final IsManaMap result = new ManaMapDefault();

		/**
		 * In dieser Art der Berechnung können sich negative Manawerte ergeben.
		 */
		result.add(ColorType.BLACK, manaMap.get(ColorType.BLACK) - get(ColorType.BLACK));
		result.add(ColorType.BLUE, manaMap.get(ColorType.BLUE) - get(ColorType.BLUE));
		result.add(ColorType.GREEN, manaMap.get(ColorType.GREEN) - get(ColorType.GREEN));
		result.add(ColorType.NONE, manaMap.get(ColorType.NONE) - get(ColorType.NONE));
		result.add(ColorType.RED, manaMap.get(ColorType.RED) - get(ColorType.RED));
		result.add(ColorType.WHITE, manaMap.get(ColorType.WHITE) - get(ColorType.WHITE));

		return result;
	}

	@Override
	public Set<ColorType> getKeySet() {
		return propertyMapMana().keySet();
	}

	@Override
	public int getTotalColoredMana() {
		int result = 0;
		for (final ColorType key : getKeySet()) {
			if (key.isTrueColor()) {
				result += get(key);
			}
		}
		return result;
	}

	@Override
	public int getTotalMana() {
		return getTotalColoredMana() + get(ColorType.NONE);
	}

	@Override
	public boolean hasColorlessMana() {
		return getTotalMana() - getTotalColoredMana() > 0;
	}

	@Override
	public boolean isEmpty() {
		return (!containsKey(ColorType.BLACK) || get(ColorType.BLACK) == 0)
				&& (!containsKey(ColorType.BLUE) || get(ColorType.BLUE) == 0)
				&& (!containsKey(ColorType.GREEN) || get(ColorType.GREEN) == 0)
				&& (!containsKey(ColorType.NONE) || get(ColorType.NONE) == 0)
				&& (!containsKey(ColorType.RED) || get(ColorType.RED) == 0)
				&& (!containsKey(ColorType.WHITE) || get(ColorType.WHITE) == 0);
	}

	@Override
	public void manaAdd(ColorType color) {
		propertyMapMana().put(color, get(color) + 1);
	}

	@Override
	public void manaRemove(ColorType color) {
		final int oldValue = get(color);
		if (oldValue <= 1) {
			propertyMapMana().remove(color);
		} else {
			propertyMapMana().put(color, oldValue - 1);
		}
	}

	@Override
	public MapProperty<ColorType, Integer> propertyManaMap() {
		return propertyMapMana;
	}

	@Override
	public void remove(ColorType color, int howMuch) {
		for (int i = 0; i < howMuch; i++) {
			manaRemove(color);
		}
	}

	@Override
	public void removeAll() {
		propertyManaMap().keySet().forEach(color -> remove(color, get(color)));
	}

	@Override
	public void setManaMap(ObservableMap<ColorType, Integer> manaMap) {
		propertyMapMana().set(manaMap);
	}

	@Override
	public String toString() {
		final StringBuilder bldr = new StringBuilder("[");
		int index = 0;
		for (final ColorType key : getKeySet()) {
			if (index != 0) {
				bldr.append(" ");
			}
			bldr.append(key).append("=[").append(get(key)).append("]");
			index++;
		}
		return bldr.append("]").toString();
	}

	/**
	 * Prüft, ob in einer ManaMap negative Werte bei den echten Farben
	 * auftauchen.
	 *
	 * @param difference
	 *            die Differenz aus zwei ManaMaps. Nur so können negative Werte
	 *            zustande kommen.
	 * @return true, wenn ein negativer Wert bei einer echten Farbe entdeckt
	 *         wurde.
	 */
	private boolean hasNegativeTrueColor(IsManaMap difference) {
		for (final ColorType key : getKeySet()) {
			if (key.isTrueColor() && get(key) < 0) {
				return true;
			}
		}
		return false;
	}

	private MapProperty<ColorType, Integer> propertyMapMana() {
		return propertyMapMana;
	}
}
