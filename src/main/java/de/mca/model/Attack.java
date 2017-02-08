package de.mca.model;

import java.util.List;

import de.mca.model.interfaces.IsAttackTarget;
import de.mca.model.interfaces.IsCombatant;
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

	// /**
	// * Speichert die vom aktiven Spieler sortierten Verteidiger.
	// */
	// TODO MID Wieder verfügbar machen.
	// private final ListProperty<IsCombatant> listBlockersSorted;
	/**
	 * Speichert die Kreatur von der der Angriff ausgeht.
	 */
	private final ObjectProperty<IsCombatant> attacker;
	/**
	 * Speichert das Angriffziel.
	 */
	private final ObjectProperty<IsAttackTarget> attackTarget;
	/**
	 * Speichert die unsortierten Verteidiger.
	 */
	private final ListProperty<IsCombatant> listBlockers;

	public Attack(MagicPermanent source, IsAttackTarget target) {
		this.attacker = new SimpleObjectProperty<>(source);
		this.attackTarget = new SimpleObjectProperty<>(target);
		listBlockers = new SimpleListProperty<>(FXCollections.observableArrayList());
		// listBlockersSorted = new
		// SimpleListProperty<>(FXCollections.observableArrayList());

		// TODO HIGH Besseren Ort für die flags suchen?
		getAttacker().setFlagTapped(true);
		getAttacker().setFlagAttacking(true);
	}

	public void addBlocker(IsCombatant combatant) {
		combatant.setFlagBlocking(true);
		getAttacker().setFlagBlocked(true);
		propertyListBlockers().add(combatant);
	}

	public IsCombatant getAttacker() {
		return propertyAttacker().get();
	}

	public IsAttackTarget getAttackTarget() {
		return attackTarget.get();
	}

	/**
	 * Liefert eine Liste aller Kampfteilnehmer dieses Angriffs.
	 *
	 * @return Eine Liste, die Angreifer und ihm zugeteilte Blocker enthält.
	 */
	public ObservableList<IsCombatant> getCombatants() {
		final ObservableList<IsCombatant> result = FXCollections.observableArrayList();
		result.add(getAttacker());
		result.addAll(propertyListBlockers());
		return result;
	}

	public ObjectProperty<IsCombatant> propertyAttacker() {
		return attacker;
	}

	// public ObservableList<IsCombatant> propertyListBlockersSorted() {
	// return listBlockersSorted;
	// }

	public ObjectProperty<IsAttackTarget> propertyAttackTarget() {
		return attackTarget;
	}

	public ObservableList<IsCombatant> propertyListBlockers() {
		return listBlockers;
	}

	public void setBlockers(List<IsCombatant> listBlocker) {
		propertyListBlockers().addAll(listBlocker);
	}

	@Override
	public String toString() {
		return new StringBuilder("[Attack a=[").append(attacker.get().toString()).append("] at=[")
				.append(attackTarget.get().toString()).append("]]").toString();
	}

}
