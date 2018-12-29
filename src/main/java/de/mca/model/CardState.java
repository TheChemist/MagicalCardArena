package de.mca.model;

import java.util.List;
import java.util.Set;

import de.mca.model.enums.ColorType;
import de.mca.model.enums.ObjectType;
import de.mca.model.enums.SubType;
import de.mca.model.enums.SuperType;
import de.mca.model.enums.ZoneType;
import de.mca.model.interfaces.IsManaMap;

public class CardState {

	/**
	 * Speichert den Schaden der der Kreatur zugefügt wurden.
	 */
	private final int damage;
	/**
	 * Speichert den Namen, so wie er auf der Karte zu lesen ist.
	 */
	private final String displayName;
	/**
	 * Speichert den Filenamen der Karte (inklusive der Dateiendung).
	 */
	private final String fileName;
	/**
	 * Zeigt an, ob die Kreatur gerade angreift.
	 */
	private final boolean flagAttacking;
	/**
	 * Zeigt an, ob die Kreatur alleine angreift.
	 */
	private final boolean flagAttackingAlone;
	/**
	 * Zeigt an, ob eine Kreatur geblockt wird.
	 */
	private final boolean flagBlocked;
	/**
	 * Zeigt an, ob eine Kreatur gerade verteidigt.
	 */
	private final boolean flagBlocking;
	/**
	 * Zeigt an, ob die Kreatur alleine blockt.
	 */
	private final boolean flagBlockingAlone;
	/**
	 * Zeigt an, ob das Permanent gerade verdeckt liegt.
	 */
	private final boolean flagFaceDown;
	/**
	 * Zeigt an, ob das Permanent gerade geflippt ist.
	 */
	private final boolean flagFlipped;
	/**
	 * Zeigt an, ob mit der Objekt, in gegebenem Kontext, interagiert werden kann.
	 */
	private final boolean flagInteractable;
	/**
	 * Zeigt an, ob das Permanent gerade phased out ist.
	 */
	private final boolean flagPhasedOut;
	/**
	 * Zeigt an, ob das Permanent noch mit Einsatzverzögerung behaftet ist.
	 */
	private final boolean flagSummoningSickness;
	/**
	 * Zeigt an, ob das Permanent getappt ist.
	 */
	private final boolean flagTapped;
	/**
	 * Speichert die eindeutige Identifikationsnummer des Objekts. Wird beim
	 * erstellen der Karten vergeben. Üblicherweise von 0 bis (computerDeck.size() +
	 * humanDeck.size()).
	 */
	private final int id;
	/**
	 * Speichert die Abilities der Karte. Bleibende Karten haben Abilities.
	 */
	private final List<ActivatedAbility> listAbilities;
	/**
	 * Speichert die Kosten der Karte. Bei doppelfarbigen Karten werden alle
	 * Kombinationen gespeichert.
	 */
	private final List<IsManaMap> listCostMaps;
	/**
	 * Speichert die Liste der Effekte. Zaubersprüche haben Effekte.
	 */
	private final List<Effect> listEffects;
	/**
	 * Speichert die besuchten Zonen. Die Aktuelle ist jeweils die letzte.
	 */
	private final List<ZoneType> listZonesVisited;
	/**
	 * Speichert die Anzahl der Loyalitätspunkte.
	 */
	private final int loyalty;
	/**
	 * Speichert den Spielertyp des kontrollierenden Spielers.
	 *
	 * @see http://magiccards.info/rule/109-objects.html#rule-109-4
	 */
	private final String playerControlling;
	/**
	 * Speichert den Spielertyp des Eigentümers.
	 *
	 * @see http://magiccards.info/rule/108-cards.html#rule-108-3
	 */
	private final String playerOwning;
	/**
	 * Speichert die Angriffstärke des Objekts.
	 */
	private final int power;
	/**
	 * Speichert die Farbe der Karte. Es wird auch farblos gespeichert, allerdings
	 * handelt es sich dabei um keine echte Farbe im Sinne der Magic-Regeln. Dient
	 * auch als Color Indicator im Sinne von Regel 204. im Magic Rulebook.
	 */
	private final Set<ColorType> setColorType;
	/**
	 * Speichert die Objekttyen.
	 */
	private final Set<ObjectType> setObjectTypes;
	/**
	 * Speichert die Subtypen.
	 */
	private final Set<SubType> setSubTypes;
	/**
	 * Speichert die Supertypen.
	 */
	private final Set<SuperType> setSuperTypes;
	/**
	 * Speichert die Widerstandskraft des Objekts.
	 */
	private final int toughness;

	public CardState(int damage, String displayName, String fileName, boolean flagAttacking, boolean flagAttackingAlone,
			boolean flagBlocked, boolean flagBlocking, boolean flagBlockingAlone, boolean flagFaceDown,
			boolean flagFlipped, boolean flagInteractable, boolean flagPhasedOut, boolean flagSummoningSickness,
			boolean flagTapped, int id, List<ActivatedAbility> listAbilities, List<IsManaMap> listCostMaps,
			List<Effect> listEffects, List<ZoneType> listZonesVisited, int loyalty, String playerControlling,
			String playerOwning, int property, Set<ColorType> setColorType, Set<ObjectType> setObjectTypes,
			Set<SubType> setSubTypes, Set<SuperType> setSuperTypes, int toughness) {
		this.damage = damage;
		this.displayName = displayName;
		this.fileName = fileName;
		this.flagAttacking = flagAttacking;
		this.flagAttackingAlone = flagAttackingAlone;
		this.flagBlocked = flagBlocked;
		this.flagBlocking = flagBlocking;
		this.flagBlockingAlone = flagBlockingAlone;
		this.flagFaceDown = flagFaceDown;
		this.flagFlipped = flagFlipped;
		this.flagInteractable = flagInteractable;
		this.flagPhasedOut = flagPhasedOut;
		this.flagSummoningSickness = flagSummoningSickness;
		this.flagTapped = flagTapped;
		this.id = id;
		this.listAbilities = listAbilities;
		this.listCostMaps = listCostMaps;
		this.listEffects = listEffects;
		this.listZonesVisited = listZonesVisited;
		this.loyalty = loyalty;
		this.playerControlling = playerControlling;
		this.playerOwning = playerOwning;
		this.power = property;
		this.setColorType = setColorType;
		this.setObjectTypes = setObjectTypes;
		this.setSubTypes = setSubTypes;
		this.setSuperTypes = setSuperTypes;
		this.toughness = toughness;
	}

	int getDamage() {
		return damage;
	}

	String getDisplayName() {
		return displayName;
	}

	String getFileName() {
		return fileName;
	}

	boolean isFlagAttacking() {
		return flagAttacking;
	}

	boolean isFlagAttackingAlone() {
		return flagAttackingAlone;
	}

	boolean isFlagBlocked() {
		return flagBlocked;
	}

	boolean isFlagBlocking() {
		return flagBlocking;
	}

	boolean isFlagBlockingAlone() {
		return flagBlockingAlone;
	}

	boolean isFlagFaceDown() {
		return flagFaceDown;
	}

	boolean isFlagFlipped() {
		return flagFlipped;
	}

	boolean isFlagInteractable() {
		return flagInteractable;
	}

	boolean isFlagPhasedOut() {
		return flagPhasedOut;
	}

	boolean isFlagSummoningSickness() {
		return flagSummoningSickness;
	}

	boolean isFlagTapped() {
		return flagTapped;
	}

	int getId() {
		return id;
	}

	List<ActivatedAbility> getListAbilities() {
		return listAbilities;
	}

	List<IsManaMap> getListCostMaps() {
		return listCostMaps;
	}

	List<Effect> getListEffects() {
		return listEffects;
	}

	List<ZoneType> getListZonesVisited() {
		return listZonesVisited;
	}

	int getLoyalty() {
		return loyalty;
	}

	String getPlayerControlling() {
		return playerControlling;
	}

	String getPlayerOwning() {
		return playerOwning;
	}

	int getProperty() {
		return power;
	}

	Set<ColorType> getSetColorType() {
		return setColorType;
	}

	Set<ObjectType> getSetObjectTypes() {
		return setObjectTypes;
	}

	Set<SubType> getSetSubTypes() {
		return setSubTypes;
	}

	Set<SuperType> getSetSuperTypes() {
		return setSuperTypes;
	}

	int getToughness() {
		return toughness;
	}

}
