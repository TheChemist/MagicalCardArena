package de.mca.model;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

/**
 *
 * @author Maximilian Werling
 *
 */
public class Deck {

	/**
	 * Speichert die Kartenliste des Decks.
	 */
	private final ListProperty<MagicCard> cardList;
	/**
	 * Speichert eine Beschreibung des Decks.
	 */
	private final StringProperty description;
	/**
	 * Speichert den Anzeigenamen.
	 */
	private final StringProperty displayName;

	public Deck() {
		this(null, null, null);
	}

	public Deck(String displayName, String description, ObservableList<MagicCard> cardList) {
		this.displayName = new SimpleStringProperty(displayName);
		this.description = new SimpleStringProperty(description);
		this.cardList = new SimpleListProperty<>(cardList);
	}

	public ObservableList<MagicCard> getCardsList() {
		return cardList;
	}

	public String getDescription() {
		return description.get();
	}

	public String getDisplayName() {
		return displayName.get();
	}

	public StringProperty propertyDescription() {
		return description;
	}

	public StringProperty propertyDisplayName() {
		return displayName;
	}

	@Override
	public String toString() {
		return displayName.get();
	}
}
