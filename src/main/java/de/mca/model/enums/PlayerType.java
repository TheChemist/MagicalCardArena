package de.mca.model.enums;

/**
 * Dient der Unterscheidung der zwei Spieler. None bekommen Zonen zugewiesen,
 * die mit keinen Spieler assoziert sind.
 *
 * @author Maximilian Werling
 *
 */
public enum PlayerType {

	COMPUTER("Computer"), HUMAN("Human"), NONE("None");

	private String displayName;

	private PlayerType(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String toString() {
		return displayName;
	}
}
