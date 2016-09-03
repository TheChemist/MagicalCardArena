package de.mca.io;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.image.Image;

/**
 *
 * @author Maximilian Werling
 *
 */
public class FileManager {

	private static final Logger LOGGER = LoggerFactory.getLogger("FileManager");
	private static final Map<String, Image> MAP_AVATAR_IMAGES = new HashMap<>();
	private static final Map<String, Path> MAP_CARD_FILES = new HashMap<>();
	private static final Map<String, Image> MAP_CARD_IMAGES = new HashMap<>();
	private static final Map<String, Path> MAP_DECK_FILES = new HashMap<>();
	private static final String PATH_AVATAR_IMAGES = "avatarImages/";
	private static final String PATH_CARD_FILES = "cardFiles/";
	private static final String PATH_CARD_IMAGES = "cardImages/";
	private static final String PATH_DECK_FILES = "deckFiles/";

	public static List<Image> getAvatarImages() {
		return new ArrayList<>(MAP_AVATAR_IMAGES.values());
	}

	public static Image getCardImage(String cardName) throws NoSuchResourceException {
		final String fileName = cardName.split("[.]")[0] + ".jpg";
		if (availableCardImages().contains(fileName)) {
			LOGGER.trace("CardImage loaded: {}", fileName);
			return MAP_CARD_IMAGES.get(fileName);
		} else {
			throw new NoSuchResourceException(fileName);
		}
	}

	public static Path getCardPath(String cardName) throws NoSuchResourceException {
		final String fileName = cardName + ".json";
		if (availableCards().contains(fileName)) {
			return MAP_CARD_FILES.get(fileName);
		} else {
			throw new NoSuchResourceException(fileName);
		}
	}

	public static List<String> getDeckNames() {
		return new ArrayList<>(MAP_DECK_FILES.keySet());
	}

	public static Path getDeckPath(String deckName) throws NoSuchResourceException {
		final String fileName = deckName;
		if (availableDecks().contains(fileName)) {
			return MAP_DECK_FILES.get(fileName);
		} else {
			throw new NoSuchResourceException(fileName);
		}
	}

	public static void loadAvatarImages() {
		mapImageDirectory(MAP_AVATAR_IMAGES, PATH_AVATAR_IMAGES);
	}

	public static void loadCardData() {
		mapFileDirectory(MAP_CARD_FILES, PATH_CARD_FILES);
	}

	public static void loadCardImages(String cardName) {
		final String fileName = cardName + ".jpg";
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(PATH_CARD_IMAGES))) {
			for (final Path path : directoryStream) {
				final Path currentFileName = path.getFileName();
				if (fileName.equals(currentFileName.toString())) {
					MAP_CARD_IMAGES.put(currentFileName.toString(), new Image(path.toFile().toURI().toString()));
					LOGGER.trace("CardImage added: {}", currentFileName);
				}
			}
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
	}

	public static void loadDeckData() {
		mapFileDirectory(MAP_DECK_FILES, PATH_DECK_FILES);
	}

	private static Set<String> availableCardImages() {
		return new HashSet<String>(MAP_CARD_IMAGES.keySet());
	}

	private static Set<String> availableCards() {
		return new HashSet<String>(MAP_CARD_FILES.keySet());
	}

	private static Set<String> availableDecks() {
		return new HashSet<String>(MAP_DECK_FILES.keySet());
	}

	private static void mapFileDirectory(Map<String, Path> map, String directory) {
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(directory))) {
			for (final Path path : directoryStream) {
				final String fileName = path.getFileName().toString();
				map.put(fileName, path);
				LOGGER.trace("Path added: {} -> {}", fileName, path.toString());
			}
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
	}

	private static void mapImageDirectory(Map<String, Image> map, String directory) {
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(directory))) {
			for (final Path path : directoryStream) {
				final String pathString = path.toFile().toURI().toString();
				map.put(path.getFileName().toString(), new Image(pathString));
				LOGGER.trace("Image added: {}", pathString);
			}
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
	}
}
