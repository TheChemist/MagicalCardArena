package de.mca;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import de.mca.model.MagicCard;

/**
 *
 * @author Maximilian Werling
 *
 */
public class Constants {

	public static final boolean AI_ONLY = true;
	public static final double CARD_RATIO = MagicCard.CARD_WIDTH / MagicCard.CARD_HEIGHT;

	public static final boolean DEBUG = true;
	public static final int HAND_SIZE = 7;
	public static final int THRESHOLD = 7;
	
	private static final String PATH_AVATAR_IMAGES = "avatarImages/";
	private static final String PATH_CARD_FILES = "cardFiles/";
	private static final String PATH_CARD_IMAGES = "cardImages/";
	private static final String PATH_DECK_FILES = "deckFiles/";

}
