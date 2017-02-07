package de.mca.model.enums;

import java.util.EnumSet;
import java.util.Set;

/**
 * Sammelt die Bezeichnungen der verschiedenen Objekttypen.
 *
 * @author Maximilian Werling
 *
 */
public enum ObjectType {

	ARTIFACT("Artifact"), CREATURE("Creature"), ENCHANTMENT("Enchantment"), INSTANT("Instant"), LAND(
			"Land"), PLANESWALKER("Planeswalker"), SORCERY("Sorcery");

	/**
	 * Als Permanent werden Objekte bezeichnet, die auf dem Spielfeld liegen
	 * (rule=110.4a.).
	 */
	private static final Set<ObjectType> IS_PERMANENT = EnumSet.of(ARTIFACT, LAND, CREATURE, ENCHANTMENT, PLANESWALKER);
	/**
	 * Als Spell werden Objekte bezeichnet, die über den Stack laufen.
	 */
	private static final Set<ObjectType> IS_SPELL = EnumSet.of(ARTIFACT, CREATURE, ENCHANTMENT, INSTANT, PLANESWALKER,
			SORCERY);

	private final String displayName;

	private ObjectType(String displayName) {
		this.displayName = displayName;
	}

	public final boolean isPermanent() {
		return IS_PERMANENT.contains(this);
	}

	public final boolean isSpell() {
		return IS_SPELL.contains(this);
	}

	@Override
	public String toString() {
		return displayName;
	}
}
