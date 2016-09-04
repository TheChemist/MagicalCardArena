package de.mca.model.interfaces;

import java.util.List;

import de.mca.model.enums.ColorType;
import de.mca.model.enums.ObjectType;
import de.mca.model.enums.PlayerType;
import de.mca.model.enums.SubType;
import de.mca.model.enums.SuperType;
import de.mca.model.enums.ZoneType;
import javafx.collections.ObservableList;

/**
 *
 * @author Maximilian Werling
 *
 */
public interface IsZone<T> {

	/**
	 * Fügt der Zone eine Karte hinzu.
	 *
	 * @param card
	 *            die Karte, die der Zone hinzugefügt werden soll.
	 */
	public void add(T card);

	/**
	 * Fügt der Zone eine Liste an Karten hinzu.
	 *
	 * @param cardList
	 *            die Kartenliste, die der Zone hinzugefügt werden soll.
	 */
	public void addAll(List<? extends T> cardList);

	public T get(int index);

	/**
	 * Liefert alle Karten der Zone.
	 *
	 * @return eine Liste mit allen Karten der Zone.
	 */
	public List<? extends T> getAll();

	/**
	 * Liefert eine Liste mit allen Karten einer bestimmten Farbe der Zone.
	 *
	 * @param color
	 *            Die Farbe, der die Karten entsprechen sollen.
	 * @return eine Liste mit allen Karten einer bestimmten Farbe der Zone.
	 */
	public List<? extends T> getAll(ColorType color);

	/**
	 * Liefert eine Liste mit allen Karten einer bestimmten Menge umgewandelten
	 * Manas der Zone.
	 *
	 * @param convertedManaCost
	 *            Die umgewandelten Manakosten, denen die Karten entsprechen
	 *            sollen.
	 * @return eine Liste mit allen Karten einer bestimmten Menge umgewandelten
	 *         Manas der Zone.
	 */
	public List<? extends T> getAll(int convertedManaCost);

	/**
	 * Liefert eine Liste mit allen Karten eines bestimmten Kerntyps der Zone.
	 *
	 * @param coreType
	 *            Der Kerntyp, dem die Karten entsprechen sollen.
	 * @return eine Liste mit allen Karten eines bestimmten Kerntyps der Zone.
	 */
	public List<? extends T> getAll(ObjectType coreType);

	/**
	 * Liefert eine Liste mit allen Karten eines bestimmten Spielers der Zone.
	 *
	 * @param playerOwning
	 *            Der Spieler, dem die Karten entsprechen sollen.
	 * @return eine Liste mit allen Karten eines bestimmten Spielers der Zone.
	 */
	public List<? extends T> getAll(PlayerType playerOwning);

	/**
	 * Liefert eine Liste mit allen Karten eines bestimmten Subtyps der Zone.
	 *
	 * @param subTyp
	 *            Der Subtype, dem die Karten entsprechen sollen.
	 * @return eine Liste mit allen Karten eines bestimmten Subtyps der Zone.
	 */
	public List<? extends T> getAll(SubType subTyp);

	/**
	 * Liefert eine Liste mit allen Karten eines bestimmten Supertyps der Zone.
	 *
	 * @param superType
	 *            Der Supertyp, dem die Karten entsprechen sollen.
	 * @return eine Liste mit allen Karten eines bestimmten Supertyps der Zone.
	 */
	public List<? extends T> getAll(SuperType superType);

	/**
	 * Liefert eine Liste mit allen Artefaktkarten der Zone.
	 *
	 * @return eine Liste mit allen Artefaktkarten der Zone.
	 */
	public List<? extends T> getArtifacts();

	/**
	 * Liefert die unterste Karte der Zone (index = 0).
	 *
	 * @return die unterste Karte der Zone.
	 */
	public T getBottom();

	/**
	 * Liefert eine Liste mit allen Monsterkarten der Zone.
	 *
	 * @return eine Liste mit allen Monsterkarten der Zone.
	 */
	public List<? extends T> getCreatures();

	/**
	 * Liefert eine Liste mit allen Länderkarten der Zone.
	 *
	 * @return eine Liste mit allen Länderkarten der Zone.
	 */
	public List<? extends T> getLands();

	/**
	 * Liefert den Spielertyp des Spielers, mit dem die Zone assoziert ist.
	 * 
	 * @return Spielertyp des assozierten Spielers.
	 */
	public PlayerType getPlayerType();

	/**
	 * Liefert die Größe der Zone.
	 *
	 * @return die Größe der Zone.
	 */
	public int getSize();

	/**
	 * Liefert die oberste Karte des Zone (index = getSize() - 1).
	 *
	 * @return Die oberste Karte der Zone.
	 */
	public T getTop();

	/**
	 * Liefert den Zonentyp der Zone.
	 *
	 * @return der Zonentyp der Zone.
	 */
	public ZoneType getZoneType();

	public ObservableList<? extends T> propertyListZoneCards();

	/**
	 * Entfernt die Karte mit einer bestimmten id aus der Zone.
	 *
	 * @param card
	 *            Die zu entfernende Karte.
	 */
	public void remove(T card);

	/**
	 * Entfernt alle Karten aus der Zone.
	 */
	public void removeAll();

	/**
	 * Durchsuchte die Zone nach einer Karte einer bestimmten Farbe.
	 *
	 * @param color
	 *            Die Farbe, nach der die Zone durchsucht werden soll.
	 * @return true, wenn eine Karte gefunden wurde, die der Farbe entspricht.
	 */
	public boolean searchZone(ColorType color);

	/**
	 * Durchsuchte die Zone nach einer Karte mit bestimmten umgewandelten
	 * Manakosten.
	 *
	 * @param convertedManaCost
	 *            Die umgewandelten Manakosten, nach denen die Zone durchsucht
	 *            werden soll.
	 * @return true, wenn eine Karte gefunden wurde, die den umgewandelten
	 *         Manakosten entspricht.
	 */
	public boolean searchZone(int convertedManaCost);

	/**
	 * Durchsuchte die Zone nach einer Karte eines bestimmten Kerntyps.
	 *
	 * @param coreType
	 *            Der Kerntyp, nach dem die Zone durchsucht werden soll.
	 * @return true, wenn eine Karte gefunden wurde, die dem Kerntyp entspricht.
	 */
	public boolean searchZone(ObjectType coreType);

	/**
	 * Durchsuchte die Zone nach einer Karte eines bestimmten Spielers.
	 *
	 * @param playerOwning
	 *            Der Spieler, nach dem die Zone durchsucht werden soll.
	 * @return true, wenn eine Karte gefunden wurde, die dem Spieler entspricht.
	 */
	public boolean searchZone(PlayerType playerOwning);

	/**
	 * Durchsuchte die Zone nach einer Karte eines bestimmten Subtyps.
	 *
	 * @param subType
	 *            Der Subtyp, nach dem die Zone durchsucht werden soll.
	 * @return true, wenn eine Karte gefunden wurde, die dem Subtyp entspricht.
	 */
	public boolean searchZone(SubType subType);

	/**
	 * Durchsuchte die Zone nach einer Karte eines bestimmten Supertyps.
	 *
	 * @param superType
	 *            Der Supertype, nach dem die Zone durchsucht werden soll.
	 * @return true, wenn eine Karte gefunden wurde, die dem Supertyp
	 *         entspricht.
	 */
	public boolean searchZone(SuperType superType);

	/**
	 * Durchsuchte die Zone nach einer bestimmten Karte.
	 *
	 * @param card
	 *            Der Karte, nach der die Zone durchsucht werden soll.
	 * @return true, wenn eine Karte gefunden wurde.
	 */
	public boolean searchZone(T card);

	/**
	 * Ordnet die Karten in der Zone zufällig an.
	 */
	public void shuffle();

	/**
	 * Zeigt den Zonentyp sowie die in der Zone bedindlichen Karten an
	 *
	 * @return die Zone als String.
	 */
	@Override
	public String toString();
}
