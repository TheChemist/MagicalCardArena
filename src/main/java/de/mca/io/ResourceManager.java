package de.mca.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javafx.scene.image.Image;

/**
 *
 * @author Maximilian Werling
 *
 */
public class ResourceManager {

	private static final Map<String, Image> MAP_ICONS = new HashMap<>();
	private static final String PATH_ICONS = "/icons/";
	private static final String PATH_RESOURCE_FILE = "/resources.json";

	public static Image getIcon(String iconName) throws NoSuchResourceException {
		if (availableIcons().contains(iconName)) {
			return MAP_ICONS.get(iconName);
		} else {
			throw new NoSuchResourceException(iconName);
		}
	}

	public static void loadResourceFile() {
		try {
			final StringBuilder bldr = new StringBuilder();
			final InputStream is = ResourceManager.class.getResourceAsStream(PATH_RESOURCE_FILE);
			final BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String line = null;
			while ((line = br.readLine()) != null) {
				bldr.append(line);
			}
			br.close();
			is.close();
			parseResourceFile(bldr.toString());
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public static String readFromPath(Path path) throws ResourceReadingException {
		try {
			final StringBuilder bldr = new StringBuilder();
			final BufferedReader reader = Files.newBufferedReader(path, Charset.defaultCharset());
			String line = null;
			while ((line = reader.readLine()) != null) {
				bldr.append(line);
			}
			reader.close();
			return bldr.toString();
		} catch (final IOException ioe) {
			throw new ResourceReadingException(ioe);
		}
	}

	private static Set<String> availableIcons() {
		return new HashSet<>(MAP_ICONS.keySet());
	}

	private static void parseResourceFile(String json) throws IOException {
		final JsonArray icons = new JsonParser().parse(json).getAsJsonObject().getAsJsonArray("icons");
		for (int i = 0; i < icons.size(); i++) {
			final JsonObject icon = icons.get(i).getAsJsonObject();
			final String fileName = icon.get("name").getAsString();
			final URL url = ResourceManager.class.getResource(PATH_ICONS + fileName);
			MAP_ICONS.put(fileName, new Image(url.toString()));
		}
	}
}
