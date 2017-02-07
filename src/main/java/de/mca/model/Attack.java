package de.mca.model;

import java.util.List;

import de.mca.model.interfaces.IsAttackTarget;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Hilfsklasse, die alle benötigten Informationen eines Angriffs kapselt.
 *
 * @author Maximilian Werling
 *
 */
public class Attack {

	/**
	 * Speichert die unsortierten Verteidiger.
	 */
	private final ListProperty<MagicPermanent> listBlockers;
	/**
	 * Speichert die vom aktiven Spieler sortierten Verteidiger.
	 */
	private final ListProperty<MagicPermanent> listBlockersSorted;
	/**
	 * Speichert die Kreatur von der der Angriff ausgeht.
	 */
	private final ObjectProperty<MagicPermanent> source;
	/**
	 * Speichert das Angriffziel.
	 */
	private final ObjectProperty<IsAttackTarget> target;

	public Attack(MagicPermanent source, IsAttackTarget target) {
		this.source = new SimpleObjectProperty<>(source);
		this.target = new SimpleObjectProperty<>(target);
		source.setFlagTapped(true);
		source.setFlagAttacking(true);
		listBlockers = new SimpleListProperty<>();
		listBlockersSorted = new SimpleListProperty<>();
	}

	public void blockerAdd(MagicPermanent blocker) {
		blocker.setFlagBlocking(true);
		getSource().setFlagBlocked(true);
		listBlockers.add(blocker);
	}

	/**
	 * Liefert eine Liste aller Kampfteilnehmer dieses Angriffs.
	 *
	 * @return Eine Liste, die Angreifer und ihm zugeteilte Blocker enthält.
	 */
	public ObservableList<MagicPermanent> getCombatants() {
		final ObservableList<MagicPermanent> result = FXCollections.observableArrayList();
		result.add(getSource());
		result.addAll(propertyListBlockers());
		return result;
	}

	public MagicPermanent getSource() {
		return source.get();
	}

	public IsAttackTarget getTarget() {
		return target.get();
	}

	public ObservableList<MagicPermanent> propertyListBlockers() {
		return listBlockers;
	}

	public ObservableList<MagicPermanent> propertyListBlockersSorted() {
		return listBlockersSorted;
	}

	public ObjectProperty<MagicPermanent> propertySource() {
		return source;
	}

	public ObjectProperty<IsAttackTarget> propertyTarget() {
		return target;
	}

	public void setBlockers(List<MagicPermanent> blockersSorted) {
		this.listBlockersSorted.addAll(blockersSorted);
	}

	@Override
	public String toString() {
		return new StringBuilder("[Attack s=[").append(source.get().toString()).append("] t=[").append(target.get().toString())
				.append("]]").toString();
	}

}
