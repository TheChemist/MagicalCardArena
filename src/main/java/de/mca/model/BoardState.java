package de.mca.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

import de.mca.model.enums.ColorType;
import de.mca.model.enums.PhaseType;
import de.mca.model.enums.PlayerState;
import de.mca.model.enums.StepType;
import de.mca.model.interfaces.IsAttackTarget;
import de.mca.model.interfaces.IsStackable;

public class BoardState {

	/**
	 * Speichert die Anzahl der Aktionen innerhalb eines Schrittes, um jede einzelne
	 * Aktion die zu einer Änderung des BoardStates führt identifiziertbar zu
	 * machen.
	 */
	private final int actionCount;
	/**
	 * Speichert die aktuelle Spielphase.
	 */
	private final Phase currentPhase;
	/**
	 * Speichert den aktuellen Spielschritt.
	 */
	private final Step currentStep;
	/**
	 * Speichert den zugefügten Kampfschaden. Wird jede Runde zurück gesetzt.
	 */
	private final int damagePlayerOne;
	/**
	 * Speichert den zugefügten Kampfschaden. Wird jede Runde zurück gesetzt.
	 */
	private final int damagePlayerTwo;
	/**
	 * Speichert den Anzeigenamen des Spielers.
	 */
	private final String displayNamePlayerOne;

	/**
	 * Speichert den Anzeigenamen des Spielers.
	 */
	private final String displayNamePlayerTwo;
	/**
	 * Zeigt an, ob der Spieler gerade Angreifer deklariert.
	 */
	private final boolean flagDeclaringAttackersPlayerOne;
	/**
	 * Zeigt an, ob der Spieler gerade Angreifer deklariert.
	 */
	private final boolean flagDeclaringAttackersPlayerTwo;
	/**
	 * Zeigt an, ob der Spieler gerade Verteidiger deklariert.
	 */
	private final boolean flagDeclaringBlockersPlayerOne;
	/**
	 * Zeigt an, ob der Spieler gerade Verteidiger deklariert.
	 */
	private final boolean flagDeclaringBlockersPlayerTwo;
	/**
	 * Zeigt an, ob von dem Spieler eine Eingabe erwartet wird.
	 */
	private final boolean flagNeedInputPlayerOne;
	/**
	 * Zeigt an, ob von dem Spieler eine Eingabe erwartet wird.
	 */
	private final boolean flagNeedInputPlayerTwo;
	/**
	 * Zeigt an, ob der Spieler bereits die Priorität abgegeben hat. Wird am Ende
	 * jeden Schrittes wieder zurück gesetzt.
	 */
	private final boolean flagPassedPriorityPlayerOne;
	/**
	 * Zeigt an, ob der Spieler bereits die Priorität abgegeben hat. Wird am Ende
	 * jeden Schrittes wieder zurück gesetzt.
	 */
	private final boolean flagPassedPriorityPlayerTwo;
	/**
	 * Zeigt an, ob die Phase wiederholt wird.
	 */
	private final boolean flagPhaseRepeated;
	/**
	 * Zeigt an, ob die Phase gerade läuft.
	 */
	private final boolean flagPhaseRunning;
	/**
	 * Zeigt an, ob die Phase übersprungen wird.
	 */
	private final boolean flagPhaseSkipped;
	/**
	 * Zeigt an, ob der Spieler in der aktuellen Runde bereits ein Land gespielt
	 * hat. Wird am Ende jeder Runde wieder zurück gesetzt.
	 */
	private final boolean flagPlayedLandPlayerOne;
	/**
	 * Zeigt an, ob der Spieler in der aktuellen Runde bereits ein Land gespielt
	 * hat. Wird am Ende jeder Runde wieder zurück gesetzt.
	 */
	private final boolean flagPlayedLandPlayerTwo;
	/**
	 * Zeigt an, ob der Spielschritt wiederholt wird.
	 */
	private final boolean flagStepRepeated;
	/**
	 * Zeigt an, ob der Schritt gerade läuft.
	 */
	private final boolean flagStepRunning;
	/**
	 * Zeigt an, ob der Spielschritt übersprungen wird.
	 */
	private final boolean flagStepSkipped;
	/**
	 * Zeigt an, ob die Runde gerade läuft.
	 */
	private final boolean flagTurnRunning;
	/**
	 * Zeigt an, ob die nächste Runde übersprungen wird.
	 */
	private final boolean flagTurnSkipped;
	/**
	 * Speichert die Anzahl an Aktionen, die der Spieler derzeit durchführen kann.
	 */
	private final int interactionCountPlayerOne;
	/**
	 * Speichert die Anzahl an Aktionen, die der Spieler derzeit durchführen kann.
	 */
	private final int interactionCountPlayerTwo;
	/**
	 * Speichert die aktuellen Lebenspunkte des Spielers.
	 */
	private final int lifePlayerOne;
	/**
	 * Speichert die aktuellen Lebenspunkte des Spielers.
	 */
	private final int lifePlayerTwo;
	/**
	 * Speichert alle Angriffe.
	 */
	private final List<Attack> listAttacks;
	/**
	 * Speichert die Liste aller angreifbaren Ziele. Diese Liste wird auch an
	 * Spieler weitergegeben.
	 */
	private final List<IsAttackTarget> listAttackTargets;
	/**
	 * Speichert die Karten des Stacks.
	 */
	private final List<IsStackable> listStack;
	/**
	 * Speichert das aktuelle Bezahlziel.
	 */
	private final Map<ColorType, Integer> manaCostGoalPlayerOne;
	/**
	 * Speichert das aktuelle Bezahlziel.
	 */
	private final Map<ColorType, Integer> manaCostGoalPlayerTwo;
	/**
	 * Speichert das Mana.
	 */
	private final Map<ColorType, Integer> manaMapPaidPlayerOne;
	/**
	 * Speichert das Mana.
	 */
	private final Map<ColorType, Integer> manaMapPaidPlayerTwo;
	/**
	 * Speichert den Manapool des Spielers.
	 */
	private final Map<ColorType, Integer> manaPoolPlayerOne;
	/**
	 * Speichert den Manapool des Spielers.
	 */
	private final Map<ColorType, Integer> manaPoolPlayerTwo;
	/**
	 * Zeigt an, ob das Match gerade läuft.
	 */
	private final boolean flagMatchRunning;
	/**
	 * Speichert den Phasentyp.
	 */
	private final PhaseType phaseType;
	/**
	 * Speichert den aktuellen Spielerstatus.
	 */
	private final PlayerState playerStatePlayerOne;
	/**
	 * Speichert den aktuellen Spielerstatus.
	 */
	private final PlayerState playerStatePlayerTwo;
	/**
	 * Sammelts StateBasedActions. Diese werden zu bestimmten Zeitpunkten
	 * abgearbeitet.
	 */
	private final Set<StateBasedAction> setStateBasedActions;
	/**
	 * Speichert den Typ des Spielschrittes.
	 */
	private final StepType stepType;
	/**
	 * Speichert vorübergehend den Blocker, während der Spieler das Blockziel
	 * auswählt.
	 */
	private final MagicPermanent temporaryBlocker;
	/**
	 * Speichert die Rundennummer.
	 */
	private final int turnNumber;
	/**
	 * Speichert die geteilte Spieldfeld-Zone.
	 */
	private final List<CardState> zoneBattlefield;
	/**
	 * Speichert das geteilte Exil.
	 */
	private final List<MagicCard> zoneExile;
	/**
	 * Speichert den Friedhof des Spielers.
	 */
	private final List<MagicCard> zoneGraveyardPlayerOne;

	/**
	 * Speichert den Friedhof des Spielers.
	 */
	private final List<MagicCard> zoneGraveyardPlayerTwo;
	/**
	 * Speichert die Hand des Spielers.
	 */
	private final List<MagicCard> zoneHandPlayerOne;
	/**
	 * Speichert die Hand des Spielers.
	 */
	private final List<MagicCard> zoneHandPlayerTwo;
	/**
	 * Speichert die Bibliothek des Spielers.
	 */
	private final List<MagicCard> zoneLibraryPlayerOne;
	/**
	 * Speichert die Bibliothek des Spielers.
	 */
	private final List<MagicCard> zoneLibraryPlayerTwo;

	public BoardState(int actionCount, Phase currentPhase, Step currentStep, int damagePlayerOne, int damagePlayerTwo,
			String displayNamePlayerOne, String displayNamePlayerTwo, boolean flagDeclaringAttackersPlayerOne,
			boolean flagDeclaringAttackersPlayerTwo, boolean flagDeclaringBlockersPlayerOne,
			boolean flagDeclaringBlockersPlayerTwo, boolean flagNeedInputPlayerOne, boolean flagNeedInputPlayerTwo,
			boolean flagPassedPriorityPlayerOne, boolean flagPassedPriorityPlayerTwo, boolean flagPhaseRepeated,
			boolean flagPhaseRunning, boolean flagPhaseSkipped, boolean flagPlayedLandPlayerOne,
			boolean flagPlayedLandPlayerTwo, boolean flagStepRepeated, boolean flagStepRunning, boolean flagStepSkipped,
			boolean flagTurnRunning, boolean flagTurnSkipped, int interactionCountPlayerOne,
			int interactionCountPlayerTwo, int lifePlayerOne, int lifePlayerTwo, List<Attack> listAttacks,
			List<IsAttackTarget> listAttackTargets, List<IsStackable> listStack,
			Map<ColorType, Integer> manaCostGoalPlayerOne, Map<ColorType, Integer> manaCostGoalPlayerTwo,
			Map<ColorType, Integer> manaMapPaidPlayerOne, Map<ColorType, Integer> manaMapPaidPlayerTwo,
			Map<ColorType, Integer> manaPoolPlayerOne, Map<ColorType, Integer> manaPoolPlayerTwo,
			boolean flagMatchRunning, PhaseType phaseType, PlayerState playerStatePlayerOne,
			PlayerState playerStatePlayerTwo, Set<StateBasedAction> setStateBasedActions, StepType stepType,
			MagicPermanent temporaryBlocker, int turnNumber, List<CardState> zoneBattlefield, List<MagicCard> zoneExile,
			List<MagicCard> zoneGraveyardPlayerOne, List<MagicCard> zoneGraveyardPlayerTwo,
			List<MagicCard> zoneHandPlayerOne, List<MagicCard> zoneHandPlayerTwo, List<MagicCard> zoneLibraryPlayerOne,
			List<MagicCard> zoneLibraryPlayerTwo) {
		super();
		this.actionCount = actionCount;
		this.currentPhase = currentPhase;
		this.currentStep = currentStep;
		this.damagePlayerOne = damagePlayerOne;
		this.damagePlayerTwo = damagePlayerTwo;
		this.displayNamePlayerOne = displayNamePlayerOne;
		this.displayNamePlayerTwo = displayNamePlayerTwo;
		this.flagDeclaringAttackersPlayerOne = flagDeclaringAttackersPlayerOne;
		this.flagDeclaringAttackersPlayerTwo = flagDeclaringAttackersPlayerTwo;
		this.flagDeclaringBlockersPlayerOne = flagDeclaringBlockersPlayerOne;
		this.flagDeclaringBlockersPlayerTwo = flagDeclaringBlockersPlayerTwo;
		this.flagNeedInputPlayerOne = flagNeedInputPlayerOne;
		this.flagNeedInputPlayerTwo = flagNeedInputPlayerTwo;
		this.flagPassedPriorityPlayerOne = flagPassedPriorityPlayerOne;
		this.flagPassedPriorityPlayerTwo = flagPassedPriorityPlayerTwo;
		this.flagPhaseRepeated = flagPhaseRepeated;
		this.flagPhaseRunning = flagPhaseRunning;
		this.flagPhaseSkipped = flagPhaseSkipped;
		this.flagPlayedLandPlayerOne = flagPlayedLandPlayerOne;
		this.flagPlayedLandPlayerTwo = flagPlayedLandPlayerTwo;
		this.flagStepRepeated = flagStepRepeated;
		this.flagStepRunning = flagStepRunning;
		this.flagStepSkipped = flagStepSkipped;
		this.flagTurnRunning = flagTurnRunning;
		this.flagTurnSkipped = flagTurnSkipped;
		this.interactionCountPlayerOne = interactionCountPlayerOne;
		this.interactionCountPlayerTwo = interactionCountPlayerTwo;
		this.lifePlayerOne = lifePlayerOne;
		this.lifePlayerTwo = lifePlayerTwo;
		this.listAttacks = listAttacks;
		this.listAttackTargets = listAttackTargets;
		this.listStack = listStack;
		this.manaCostGoalPlayerOne = manaCostGoalPlayerOne;
		this.manaCostGoalPlayerTwo = manaCostGoalPlayerTwo;
		this.manaMapPaidPlayerOne = manaMapPaidPlayerOne;
		this.manaMapPaidPlayerTwo = manaMapPaidPlayerTwo;
		this.manaPoolPlayerOne = manaPoolPlayerOne;
		this.manaPoolPlayerTwo = manaPoolPlayerTwo;
		this.flagMatchRunning = flagMatchRunning;
		this.phaseType = phaseType;
		this.playerStatePlayerOne = playerStatePlayerOne;
		this.playerStatePlayerTwo = playerStatePlayerTwo;
		this.setStateBasedActions = setStateBasedActions;
		this.stepType = stepType;
		this.temporaryBlocker = temporaryBlocker;
		this.turnNumber = turnNumber;
		this.zoneBattlefield = zoneBattlefield;
		this.zoneExile = zoneExile;
		this.zoneGraveyardPlayerOne = zoneGraveyardPlayerOne;
		this.zoneGraveyardPlayerTwo = zoneGraveyardPlayerTwo;
		this.zoneHandPlayerOne = zoneHandPlayerOne;
		this.zoneHandPlayerTwo = zoneHandPlayerTwo;
		this.zoneLibraryPlayerOne = zoneLibraryPlayerOne;
		this.zoneLibraryPlayerTwo = zoneLibraryPlayerTwo;
	}

	int getActionCount() {
		return actionCount;
	}

	Phase getCurrentPhase() {
		return currentPhase;
	}

	Step getCurrentStep() {
		return currentStep;
	}

	int getDamagePlayerOne() {
		return damagePlayerOne;
	}

	int getDamagePlayerTwo() {
		return damagePlayerTwo;
	}

	String getDisplayNamePlayerOne() {
		return displayNamePlayerOne;
	}

	String getDisplayNamePlayerTwo() {
		return displayNamePlayerTwo;
	}

	boolean isFlagDeclaringAttackersPlayerOne() {
		return flagDeclaringAttackersPlayerOne;
	}

	boolean isFlagDeclaringAttackersPlayerTwo() {
		return flagDeclaringAttackersPlayerTwo;
	}

	boolean isFlagDeclaringBlockersPlayerOne() {
		return flagDeclaringBlockersPlayerOne;
	}

	boolean isFlagDeclaringBlockersPlayerTwo() {
		return flagDeclaringBlockersPlayerTwo;
	}

	boolean isFlagNeedInputPlayerOne() {
		return flagNeedInputPlayerOne;
	}

	boolean isFlagNeedInputPlayerTwo() {
		return flagNeedInputPlayerTwo;
	}

	boolean isFlagPassedPriorityPlayerOne() {
		return flagPassedPriorityPlayerOne;
	}

	boolean isFlagPassedPriorityPlayerTwo() {
		return flagPassedPriorityPlayerTwo;
	}

	boolean isFlagPhaseRepeated() {
		return flagPhaseRepeated;
	}

	boolean isFlagPhaseRunning() {
		return flagPhaseRunning;
	}

	boolean isFlagPhaseSkipped() {
		return flagPhaseSkipped;
	}

	boolean isFlagPlayedLandPlayerOne() {
		return flagPlayedLandPlayerOne;
	}

	boolean isFlagPlayedLandPlayerTwo() {
		return flagPlayedLandPlayerTwo;
	}

	boolean isFlagStepRepeated() {
		return flagStepRepeated;
	}

	boolean isFlagStepRunning() {
		return flagStepRunning;
	}

	boolean isFlagStepSkipped() {
		return flagStepSkipped;
	}

	boolean isFlagTurnRunning() {
		return flagTurnRunning;
	}

	boolean isFlagTurnSkipped() {
		return flagTurnSkipped;
	}

	int getInteractionCountPlayerOne() {
		return interactionCountPlayerOne;
	}

	int getInteractionCountPlayerTwo() {
		return interactionCountPlayerTwo;
	}

	int getLifePlayerOne() {
		return lifePlayerOne;
	}

	int getLifePlayerTwo() {
		return lifePlayerTwo;
	}

	List<Attack> getListAttacks() {
		return listAttacks;
	}

	List<IsAttackTarget> getListAttackTargets() {
		return listAttackTargets;
	}

	List<IsStackable> getListStack() {
		return listStack;
	}

	Map<ColorType, Integer> getManaCostGoalPlayerOne() {
		return manaCostGoalPlayerOne;
	}

	Map<ColorType, Integer> getManaCostGoalPlayerTwo() {
		return manaCostGoalPlayerTwo;
	}

	Map<ColorType, Integer> getManaMapPaidPlayerOne() {
		return manaMapPaidPlayerOne;
	}

	Map<ColorType, Integer> getManaMapPaidPlayerTwo() {
		return manaMapPaidPlayerTwo;
	}

	Map<ColorType, Integer> getManaPoolPlayerOne() {
		return manaPoolPlayerOne;
	}

	Map<ColorType, Integer> getManaPoolPlayerTwo() {
		return manaPoolPlayerTwo;
	}

	boolean isFlagMatchRunning() {
		return flagMatchRunning;
	}

	PhaseType getPhaseType() {
		return phaseType;
	}

	PlayerState getPlayerStatePlayerOne() {
		return playerStatePlayerOne;
	}

	PlayerState getPlayerStatePlayerTwo() {
		return playerStatePlayerTwo;
	}

	Set<StateBasedAction> getSetStateBasedActions() {
		return setStateBasedActions;
	}

	StepType getStepType() {
		return stepType;
	}

	MagicPermanent getTemporaryBlocker() {
		return temporaryBlocker;
	}

	int getTurnNumber() {
		return turnNumber;
	}

	List<CardState> getZoneBattlefield() {
		return zoneBattlefield;
	}

	List<MagicCard> getZoneExile() {
		return zoneExile;
	}

	List<MagicCard> getZoneGraveyardPlayerOne() {
		return zoneGraveyardPlayerOne;
	}

	List<MagicCard> getZoneGraveyardPlayerTwo() {
		return zoneGraveyardPlayerTwo;
	}

	List<MagicCard> getZoneHandPlayerOne() {
		return zoneHandPlayerOne;
	}

	List<MagicCard> getZoneHandPlayerTwo() {
		return zoneHandPlayerTwo;
	}

	List<MagicCard> getZoneLibraryPlayerOne() {
		return zoneLibraryPlayerOne;
	}

	List<MagicCard> getZoneLibraryPlayerTwo() {
		return zoneLibraryPlayerTwo;
	}

}
