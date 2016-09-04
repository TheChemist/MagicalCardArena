package de.mca.model.enums;

/**
 * Sammelt die Bezeichnungen der verschiedenen Subtypen.
 *
 * @author Maximilian Werling
 *
 */
public enum SubType {

	ADVISOR("Advisor"), BARBARIAN("Barbarian"), BEAR("Bear"), BEAST("Beast"), CAT("Cat"), CONSTRUCT("Construct"), CRAB(
			"Crab"), DEMON("Demon"), EEL("Eel"), ELEMENTAL("Elemental"), ELF("Elf"), ELK("Elk"), FOREST(
					"Forest"), GOBLIN("Goblin"), GOLEM("Golem"), HORSE("Horse"), HOUND("Hound"), HUMAN("Human"), ISLAND(
							"Island"), KITHKIN("Kithkin"), KNIGHT("Knight"), MERFOLK("Merfolk"), MINOTAUR(
									"Minotaur"), MOUNTAIN("Mountain"), MYR("Myr"), PLAINS("Plains"), PLANT(
											"Plant"), RHINO("Rhino"), ROGUE("Rogue"), SALAMANDER("Salamander"), SCOUT(
													"Scout"), SLUG("Slug"), SOLDIER("Soldier"), SWAMP(
															"Swamp"), TREEFOLK("Treefolk"), VAMPIRE("Vampire"), WARRIOR(
																	"Warrior"), WIZARD("Wizard"), WOLF("Wolf"), ZOMBIE(
																			"Zombie");

	private final String displayName;

	private SubType(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String toString() {
		return displayName;
	}
}
