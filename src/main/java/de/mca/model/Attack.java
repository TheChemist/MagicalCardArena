package de.mca.model;

import java.util.ArrayList;
import java.util.List;

import de.mca.model.interfaces.IsAttackTarget;
import de.mca.model.interfaces.IsCombatant;

/**
 * Hilfsklasse, die alle benötigten Informationen eines Angriffs kapselt.
 *
 * @author Maximilian Werling
 *
 */
public class Attack {

	/**
	 * Speichert die Kreatur von der der Angriff ausgeht.
	 */
	private final IsCombatant attacker;
	/**
	 * Speichert das Angriffziel.
	 */
	private final IsAttackTarget attackTarget;
	/**
	 * Speichert die unsortierten Verteidiger.
	 */
	private final List<IsCombatant> listBlockers;

	public Attack(MagicPermanent source, IsAttackTarget target) {
		this.attacker = source;
		this.attackTarget = target;
		listBlockers = new ArrayList<>();

		getAttacker().setFlagTapped(true);
		getAttacker().setFlagAttacking(true);
	}

	public void addBlocker(IsCombatant combatant) {
		combatant.setFlagBlocking(true);
		getAttacker().setFlagBlocked(true);
		getListBlockers().add(combatant);
	}

	public IsCombatant getAttacker() {
		return attacker;
	}

	public IsAttackTarget getAttackTarget() {
		return attackTarget;
	}

	public List<IsCombatant> getListBlockers() {
		return listBlockers;
	}

	/**
	 * Liefert eine Liste aller Kampfteilnehmer dieses Angriffs.
	 *
	 * @return Eine Liste, die Angreifer und ihm zugeteilte Blocker enthält.
	 */
	public List<IsCombatant> getCombatants() {
		final List<IsCombatant> result = new ArrayList<>();
		result.add(getAttacker());
		result.addAll(getListBlockers());
		return result;
	}

	public void setBlockers(List<IsCombatant> listBlockers) {
		this.listBlockers.addAll(listBlockers);
	}

	@Override
	public String toString() {
		return new StringBuilder("[Attack a=[").append(getAttacker().toString()).append("] at=[")
				.append(getAttackTarget().toString()).append("]]").toString();
	}

}
