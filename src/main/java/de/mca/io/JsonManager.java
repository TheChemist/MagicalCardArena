package de.mca.io;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;

import de.mca.model.Attack;
import de.mca.model.BoardState;
import de.mca.model.CardState;
import de.mca.model.MagicCard;
import de.mca.model.MagicPermanent;
import de.mca.model.Match;
import de.mca.model.Phase;
import de.mca.model.RuleEnforcer;
import de.mca.model.StateBasedAction;
import de.mca.model.Step;
import de.mca.model.Turn;
import de.mca.model.enums.ColorType;
import de.mca.model.enums.PhaseType;
import de.mca.model.enums.PlayerState;
import de.mca.model.enums.StepType;
import de.mca.model.interfaces.IsAttackTarget;
import de.mca.model.interfaces.IsPlayer;
import de.mca.model.interfaces.IsStackable;

public class JsonManager {

	public static void writeBoardState(Match match) {
		final IsPlayer playerOne = match.getPlayerOne();
		final String playerOneDisplayName = playerOne.getDisplayName();
		final int playerOneInteractionCount = playerOne.getInteractionCount();
		final int playerOneDamage = playerOne.getDamage();
		final boolean playerOneFlagDeclaringAttackers = playerOne.getFlagDeclaringAttackers();
		final boolean playerOneFlagDeclaringBlockers = playerOne.getFlagDeclaringBlockers();
		final boolean playerOneFlagNeedInput = playerOne.getFlagNeedInput();
		final boolean playerOneFlagPassedPriority = playerOne.getFlagPassedPriority();
		final boolean playerOneFlagPlayedLand = playerOne.getFlagPlayedLand();
		final int playerOneLife = playerOne.getLife();
		final PlayerState playerOnePlayerState = playerOne.getPlayerState();
		final List<MagicCard> playerOnezoneGraveyard = playerOne.getZoneGraveyard().getAll();
		final List<MagicCard> playerOneZoneHand = playerOne.getZoneHand().getAll();
		final List<MagicCard> playerOneZoneLibrary = playerOne.getZoneLibrary().getAll();
		final Map<ColorType, Integer> playerOneManaCostGoal = new HashMap<>(
				playerOne.getManaCostGoal().propertyMapMana().get());
		final Map<ColorType, Integer> playerOneManaMapPaid = new HashMap<>(
				playerOne.getManaCostAlreadyPaid().propertyMapMana().get());
		final Map<ColorType, Integer> playerOneManaPool = new HashMap<>(
				playerOne.getManaPool().propertyMapMana().get());

		final IsPlayer playerTwo = match.getPlayerTwo();
		final String playerTwoDisplayName = playerTwo.getDisplayName();
		final int playerTwoInteractionCount = playerTwo.getInteractionCount();
		final int playerTwoDamage = playerTwo.getDamage();
		final boolean playerTwoFlagDeclaringAttackers = playerTwo.getFlagDeclaringAttackers();
		final boolean playerTwoFlagDeclaringBlockers = playerTwo.getFlagDeclaringBlockers();
		final boolean playerTwoFlagNeedInput = playerTwo.getFlagNeedInput();
		final boolean playerTwoFlagPassedPriority = playerTwo.getFlagPassedPriority();
		final boolean playerTwoflagPlayedLand = playerTwo.getFlagPlayedLand();
		final int playerTwoLife = playerTwo.getLife();
		final PlayerState playerTwoPlayerState = playerTwo.getPlayerState();
		final List<MagicCard> playerTwoZoneHand = playerTwo.getZoneHand().getAll();
		final List<MagicCard> playerTwoZoneLibrary = playerTwo.getZoneLibrary().getAll();
		final List<MagicCard> playerTwoZoneGraveyard = playerTwo.getZoneGraveyard().getAll();
		final Map<ColorType, Integer> playerTwoManaCostGoal = new HashMap<>(
				playerTwo.getManaCostGoal().propertyMapMana().get());
		final Map<ColorType, Integer> playerTwoManaMapPaid = new HashMap<>(
				playerTwo.getManaCostAlreadyPaid().propertyMapMana().get());
		final Map<ColorType, Integer> playerTwoManaPool = new HashMap<>(
				playerTwo.getManaPool().propertyMapMana().get());

		final Turn currentTurn = match.getCurrentTurn();
		final boolean flagTurnRunning = currentTurn.getFlagTurnRunning();
		final boolean flagSkipTurn = currentTurn.getFlagTurnSkipped();
		final int turnNumber = currentTurn.getTurnNumber();

		final Phase currentPhase = currentTurn.getCurrentPhase();
		final PhaseType phaseType = currentPhase.getPhaseType();
		final boolean flagPhaseRunning = currentPhase.getFlagPhaseRunning();
		final boolean flagPhaseSkipped = currentPhase.getFlagPhaseSkipped();
		final boolean flagPhaseRepeated = currentPhase.getFlagPhaseRepeated();

		final Step currentStep = currentPhase.getCurrentStep();
		final StepType stepType = currentStep.getStepType();
		final boolean flagStepRepeated = currentStep.getFlagStepRepeated();
		final boolean flagStepRunning = currentStep.getFlagStepRunning();
		final boolean flagStepSkipped = currentStep.getFlagStepSkipped();

		final int actionCount = match.getActionCount();
		final boolean flagMatchRunning = match.getFlagMatchRunning();
		final List<Attack> listAttacks = new ArrayList<>(match.getListAttacks());
		final List<IsAttackTarget> listAttackTargets = new ArrayList<>(match.getListAttackTargets());
		final List<IsStackable> listStack = new ArrayList<>(match.getZoneStack().getList());
		final List<CardState> zoneBattlefield = new ArrayList<>();
		final List<MagicCard> zoneExile = new ArrayList<>(match.getZoneExile().getAll());

		final RuleEnforcer ruleEnforcer = match.getRuleEnforcer();
		final MagicPermanent temporaryBlocker = ruleEnforcer.getTemporaryBlocker();
		final Set<StateBasedAction> setStateBasedActions = new HashSet<>(ruleEnforcer.getpropertyStateBasedActions());

		final BoardState boardState = new BoardState(actionCount, currentPhase, currentStep, playerOneDamage,
				playerTwoDamage, playerOneDisplayName, playerTwoDisplayName, playerOneFlagDeclaringAttackers,
				playerTwoFlagDeclaringAttackers, playerOneFlagDeclaringBlockers, playerTwoFlagDeclaringBlockers,
				playerOneFlagNeedInput, playerTwoFlagNeedInput, playerOneFlagPassedPriority,
				playerTwoFlagPassedPriority, flagPhaseRepeated, flagPhaseRunning, flagPhaseSkipped,
				playerOneFlagPlayedLand, playerTwoflagPlayedLand, flagStepRepeated, flagStepRunning, flagStepSkipped,
				flagTurnRunning, flagSkipTurn, playerOneInteractionCount, playerTwoInteractionCount, playerOneLife,
				playerTwoLife, listAttacks, listAttackTargets, listStack, playerOneManaCostGoal, playerTwoManaCostGoal,
				playerOneManaMapPaid, playerTwoManaMapPaid, playerOneManaPool, playerTwoManaPool, flagMatchRunning,
				phaseType, playerOnePlayerState, playerTwoPlayerState, setStateBasedActions, stepType, temporaryBlocker,
				turnNumber, zoneBattlefield, zoneExile, playerOnezoneGraveyard, playerTwoZoneGraveyard,
				playerOneZoneHand, playerTwoZoneHand, playerOneZoneLibrary, playerTwoZoneLibrary);

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		try {
			gson.toJson(playerOneZoneHand.get(0), new FileWriter("./file.f"));
		} catch (JsonIOException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
