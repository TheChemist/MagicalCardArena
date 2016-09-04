package de.mca.model;

import java.util.Arrays;

import de.mca.model.enums.ZoneType;
import de.mca.model.interfaces.IsStackable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Maximilian Werling
 *
 */
public class MagicStack {

	/**
	 * Speichert die Karten des Stacks.
	 */
	private final ObservableList<IsStackable> stack;
	/**
	 * Speichert den Zonentyp.
	 */
	private final ZoneType zoneType;

	public MagicStack() {
		this.zoneType = ZoneType.STACK;
		stack = FXCollections.observableArrayList();
	}

	public IsStackable get(int i) {
		return stack.get(i);
	}

	public ObservableList<IsStackable> getList() {
		return stack;
	}

	public int getSize() {
		return stack.size();
	}

	public boolean isEmpty() {
		return stack.isEmpty();
	}

	public IsStackable peek() {
		return stack.get(0);
	}

	public void pop() {
		stack.remove(0);
	}

	public void push(IsStackable stackable) {
		stack.add(0, stackable);
	}

	@Override
	public String toString() {
		return new StringBuilder(zoneType.toString()).append(" ").append(Arrays.toString(stack.toArray())).toString();
	}
}
