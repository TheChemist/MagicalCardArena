package de.mca.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import de.mca.model.enums.AbilityType;
import de.mca.model.enums.ColorType;
import de.mca.model.enums.EffectType;
import de.mca.model.enums.ObjectType;
import de.mca.model.enums.PlayerType;
import de.mca.model.enums.SubType;
import de.mca.model.enums.SuperType;
import de.mca.model.enums.ZoneType;
import de.mca.model.interfaces.IsZone;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Maximilian Werling
 *
 */
public final class ZoneDefault<E extends MagicCard> implements IsZone<E> {

	/**
	 * Speichert den Spielertyp des Spielers.
	 */
	private final PlayerType playerType;
	/**
	 * Speichert die Karten der Zone.
	 */
	private final ListProperty<E> propertyCardList;
	/**
	 * Speichert den Zonentyp.
	 */
	private final ZoneType zoneType;

	ZoneDefault(PlayerType playerType, ZoneType zoneType) {
		this.playerType = playerType;
		this.zoneType = zoneType;
		propertyCardList = new SimpleListProperty<>(FXCollections.observableArrayList());
	}

	@Override
	public void add(E card) {
		card.addZone(zoneType);
		propertyCardList.add(card);
	}

	@Override
	public void addAll(List<? extends E> cardList) {
		final Iterator<? extends E> iterator = cardList.listIterator();
		while (iterator.hasNext()) {
			add(iterator.next());
		}
	}

	@Override
	public E get(int index) {
		return propertyCardList.get(index);
	}

	@Override
	public List<E> getAll() {
		return new ArrayList<>(propertyCardList);
	}

	@Override
	public List<E> getAll(AbilityType abilityType) {
		final List<E> result = new ArrayList<>();
		for (final E card : propertyCardList) {
			final List<ActivatedAbility> listAbilities = card.getListAbilities();
			for (final ActivatedAbility ability : listAbilities) {
				if (ability.getAbilityType().equals(abilityType)) {
					result.add(card);
				}
			}
		}
		return result;
	}

	@Override
	public List<E> getAll(ColorType color) {
		final List<E> result = new ArrayList<>();
		for (final E card : propertyCardList) {
			if (card.contains(color)) {
				result.add(card);
			}
		}
		return result;
	}

	@Override
	public List<E> getAll(EffectType effectType) {
		final List<E> result = new ArrayList<>();
		for (final E card : propertyCardList) {
			final List<ActivatedAbility> listAbilities = card.getListAbilities();
			for (final ActivatedAbility ability : listAbilities) {
				final List<Effect> listEffects = ability.getListEffects();
				for (final Effect effect : listEffects) {
					if (effect.getEffectType().equals(effectType)) {
						result.add(card);
					}
				}
			}
		}
		return result;
	}

	@Override
	public List<E> getAll(int convertedManaCost) {
		final List<E> result = new ArrayList<>();
		for (final E card : propertyCardList) {
			if (card.getConvertedManaCost() == convertedManaCost) {
				result.add(card);
			}
		}
		return result;
	}

	@Override
	public List<E> getAll(ObjectType coreType) {
		final List<E> result = new ArrayList<>();
		for (final E card : propertyCardList) {
			if (card.contains(coreType)) {
				result.add(card);
			}
		}
		return result;
	}

	@Override
	public List<E> getAll(PlayerType playerOwning) {
		final List<E> result = new ArrayList<>();
		for (final E card : propertyCardList) {
			if (card.getPlayerOwning().equals(playerOwning)) {
				result.add(card);
			}
		}
		return result;
	}

	@Override
	public List<E> getAll(SubType subType) {
		final List<E> result = new ArrayList<>();
		for (final E card : propertyCardList) {
			if (card.contains(subType)) {
				result.add(card);
			}
		}
		return result;
	}

	@Override
	public List<E> getAll(SuperType superType) {
		final List<E> result = new ArrayList<>();
		for (final E card : propertyCardList) {
			if (card.contains(superType)) {
				result.add(card);
			}
		}
		return result;
	}

	@Override
	public E getBottom() {
		return propertyCardList.get(0);
	}

	@Override
	public PlayerType getPlayerType() {
		return playerType;
	}

	@Override
	public int getSize() {
		return propertyCardList.size();
	}

	@Override
	public E getTop() {
		return propertyCardList.get(getSize() - 1);
	}

	@Override
	public ZoneType getZoneType() {
		return zoneType;
	}

	@Override
	public ObservableList<E> propertyListZoneCards() {
		return propertyCardList;
	}

	@Override
	public boolean remove(E card) {
		return propertyCardList.remove(card);
	}

	@Override
	public void removeAll() {
		propertyCardList.clear();
	}

	@Override
	public boolean searchZone(ColorType color) {
		for (final MagicCard card : propertyCardList) {
			if (card.contains(color)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean searchZone(int convertedManaCost) {
		for (final MagicCard card : propertyCardList) {
			if (card.getConvertedManaCost() == convertedManaCost) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean searchZone(MagicCard card) {
		for (final MagicCard c : propertyCardList) {
			if (c.equals(card)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean searchZone(ObjectType coreType) {
		for (final MagicCard card : propertyCardList) {
			if (card.contains(coreType)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean searchZone(PlayerType playerOwning) {
		for (final MagicCard card : propertyCardList) {
			if (card.getPlayerOwning().equals(playerOwning)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean searchZone(SubType subType) {
		for (final MagicCard card : propertyCardList) {
			if (card.contains(subType)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean searchZone(SuperType superType) {
		for (final MagicCard card : propertyCardList) {
			if (card.contains(superType)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void shuffle() {
		Collections.shuffle(propertyCardList);
	}

	@Override
	public String toString() {
		return new StringBuilder(zoneType.toString()).append(" ").append(Arrays.toString(propertyCardList.toArray()))
				.toString();
	}

}
