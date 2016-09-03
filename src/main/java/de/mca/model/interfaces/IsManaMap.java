package de.mca.model.interfaces;

import java.util.Set;

import de.mca.model.enums.ColorType;
import javafx.beans.property.MapProperty;
import javafx.collections.ObservableMap;

/**
 *
 * @author Maximilian Werling
 *
 */
public interface IsManaMap {

	public void add(ColorType color, int howMuch);

	public void addAll(IsManaMap manaMap);

	public boolean containsKey(ColorType color);

	public boolean equals(IsManaMap manaMap);

	public int get(ColorType color);

	/**
	 * Liefert eine neue ManaMap zurück in der manaMap von this abgezogen wird.
	 *
	 * @param manaMap
	 *            die manaMap, die von this abgezogen wird.
	 * @return eine ManaMap, die die Differenz von this - manaMap darstellt.
	 */
	public IsManaMap getDifference(IsManaMap manaMap);

	public Set<ColorType> getKeySet();

	public int getTotalColoredMana();

	public int getTotalMana();

	public boolean hasColorlessMana();

	/**
	 * Prüft, ob die Map leer ist.
	 *
	 * @return true, wenn die Map leer ist.
	 */
	public boolean isEmpty();

	public void manaAdd(ColorType color);

	public void manaRemove(ColorType color);

	public MapProperty<ColorType, Integer> propertyManaMap();

	public void remove(ColorType color, int howMuch);

	public void removeAll();

	public void setManaMap(ObservableMap<ColorType, Integer> manaMap);

	@Override
	public String toString();

}
