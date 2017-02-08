package de.mca;

import java.nio.file.Path;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Inject;

import de.mca.factories.FactoryAbility;
import de.mca.factories.FactoryEffect;
import de.mca.io.FileManager;
import de.mca.io.ResourceManager;
import de.mca.io.ResourceReadingException;
import de.mca.model.ActivatedAbility;
import de.mca.model.Deck;
import de.mca.model.Effect;
import de.mca.model.MagicCard;
import de.mca.model.ManaMapDefault;
import de.mca.model.enums.AbilityType;
import de.mca.model.enums.AdditionalCostType;
import de.mca.model.enums.ColorType;
import de.mca.model.enums.EffectType;
import de.mca.model.enums.ObjectType;
import de.mca.model.enums.RarityType;
import de.mca.model.enums.SubType;
import de.mca.model.enums.SuperType;
import de.mca.model.interfaces.IsManaMap;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;

/**
 *
 * @author Maximilian Werling
 *
 */
public class MagicParser {

	private static int ID = 0;
	private static final Logger LOGGER = LoggerFactory.getLogger("MagicParser");
	private final FactoryAbility abilityFactory;
	private final FactoryEffect factoryEffect;

	@Inject
	private MagicParser(FactoryAbility abilityFactory, FactoryEffect mEffectFactory) {
		this.abilityFactory = abilityFactory;
		this.factoryEffect = mEffectFactory;
	}

	public MagicCard parseCardFromPath(Path cardPath) throws ResourceReadingException {
		final JsonObject cardObject = new JsonParser().parse(ResourceManager.readFromPath(cardPath)).getAsJsonObject();
		final JsonElement elementColors = cardObject.get("colors");
		final JsonElement elementSupertypes = cardObject.get("supertypes");
		final JsonArray cardCoretypes = cardObject.get("coretypes").getAsJsonArray();
		final JsonElement elementSubtypes = cardObject.get("subtypes");
		final JsonElement elementCost = cardObject.get("cost");
		final JsonElement elementPower = cardObject.get("power");
		final JsonElement elementToughness = cardObject.get("toughness");
		final JsonElement elementAbilities = cardObject.get("abilities");

		// card
		final MagicCard card = new MagicCard(ID++);

		// parse display name
		final String displayName = cardObject.get("displayName").getAsString();
		card.setDisplayName(displayName);
		card.setFileName(cardPath.getFileName().toString().split("[.]")[0]);

		// parse rarity
		final RarityType rarity = RarityType.valueOf(cardObject.get("rarity").getAsString());
		card.setRarity(rarity);

		// parse power
		if (elementPower != null) {
			final int power = cardObject.get("power").getAsInt();
			card.setPower(power);
		}

		// parse toughness
		if (elementToughness != null) {
			final int toughness = cardObject.get("toughness").getAsInt();
			card.setToughness(toughness);
		}

		// parse colors
		if (elementColors != null) {
			final ObservableSet<ColorType> setColorTypes = FXCollections.observableSet();
			final JsonArray cardColors = elementColors.getAsJsonArray();
			for (int i = 0; i < cardColors.size(); i++) {
				setColorTypes.add(ColorType.valueOf(cardColors.get(i).getAsJsonObject().get("color").getAsString()));
			}
			card.setSetColorTypes(setColorTypes);
		}

		// parse types
		if (elementSupertypes != null) {
			final ObservableSet<SuperType> setSuperTypes = FXCollections.observableSet();
			final JsonArray cardSupertypes = elementSupertypes.getAsJsonArray();
			for (int i = 0; i < cardSupertypes.size(); i++) {
				setSuperTypes
						.add(SuperType.valueOf(cardSupertypes.get(i).getAsJsonObject().get("supertype").getAsString()));
			}
			card.setSetSuperTypes(setSuperTypes);
		}
		final ObservableSet<ObjectType> setObjectTypes = FXCollections.observableSet();
		for (int i = 0; i < cardCoretypes.size(); i++) {
			setObjectTypes
					.add(ObjectType.valueOf(cardCoretypes.get(i).getAsJsonObject().get("coretype").getAsString()));
		}
		card.setSetObjectTypes(setObjectTypes);
		if (elementSubtypes != null) {
			final ObservableSet<SubType> setSubTypes = FXCollections.observableSet();
			final JsonArray cardSubtypes = elementSubtypes.getAsJsonArray();
			for (int i = 0; i < cardSubtypes.size(); i++) {
				setSubTypes.add(SubType.valueOf(cardSubtypes.get(i).getAsJsonObject().get("subtype").getAsString()));
			}
			card.setSetSubTypes(setSubTypes);
		}

		// parse cost and color
		if (elementCost != null) {
			final ObservableList<IsManaMap> costMaps = FXCollections.observableArrayList();
			final ObservableSet<ColorType> color = FXCollections.observableSet();
			final ObservableMap<ColorType, Integer> costMap = FXCollections.observableHashMap();
			final JsonArray cardCost = elementCost.getAsJsonArray();
			for (int i = 0; i < cardCost.size(); i++) {
				final JsonArray cardCostMap = cardCost.get(i).getAsJsonArray();
				for (int j = 0; j < cardCostMap.size(); j++) {
					final JsonObject costObject = cardCostMap.get(j).getAsJsonObject();
					final ColorType manaColor = ColorType.valueOf(costObject.get("color").getAsString());
					final int howMuch = costObject.get("value").getAsInt();
					costMap.put(manaColor, howMuch);
					if (manaColor.isTrueColor()) {
						color.add(manaColor);
					}
				}
				costMaps.add(new ManaMapDefault(costMap));
			}
			card.setListCostMaps(costMaps);
			card.setSetColorTypes(color);
		} else {
			// Füge leere CostMap hinzu
			card.setListCostMaps(FXCollections.observableArrayList(new ManaMapDefault()));
		}

		// parse abilities
		if (elementAbilities != null) {
			final ObservableList<ActivatedAbility> listAbilities = FXCollections.observableArrayList();
			final JsonArray cardAbilities = elementAbilities.getAsJsonArray();
			for (int i = 0; i < cardAbilities.size(); i++) {
				listAbilities.add(parseAbility(card, cardAbilities.get(i).getAsJsonObject()));
			}
			card.setListActivatedAbilities(listAbilities);
		}
		return card;
	}

	public Deck parseDeckFromPath(Path deckPath) throws ResourceReadingException {
		final JsonObject deckObject = new JsonParser().parse(ResourceManager.readFromPath(deckPath)).getAsJsonObject();
		final String deckName = deckObject.get("name").getAsString();
		final String deckDescription = deckObject.get("description").getAsString();
		final JsonArray deckCards = deckObject.get("cards").getAsJsonArray();
		final ObservableList<MagicCard> cardList = FXCollections.observableArrayList();
		for (int i = 0; i < deckCards.size(); i++) {
			final JsonObject cardObject = deckCards.get(i).getAsJsonObject();
			final String cardName = cardObject.get("name").getAsString();
			final int cardCount = cardObject.get("count").getAsInt();
			FileManager.loadCardImages(cardName);
			for (int j = 0; j < cardCount; j++) {
				final MagicCard card = parseCardFromPath(FileManager.getCardPath(cardName));
				cardList.add(card);
				LOGGER.trace("Card parsed: {}", card.toString());
			}
		}
		final Deck deck = new Deck(deckName, deckDescription, cardList);
		LOGGER.trace("Deck parsed: {}", deck.toString());
		return deck;
	}

	public Effect parseEffect(ActivatedAbility source, JsonObject effectObject) {
		switch (EffectType.valueOf(effectObject.get("effecttype").getAsString())) {
		case PRODUCE_MANA:
			final JsonArray produceArray = effectObject.get("produce").getAsJsonArray();
			final ObservableMap<ColorType, Integer> tempMap = FXCollections.observableMap(new HashMap<>());
			for (int i = 0; i < produceArray.size(); i++) {
				final JsonObject costObject = produceArray.get(i).getAsJsonObject();
				final ColorType manaColor = ColorType.valueOf(costObject.get("color").getAsString());
				final int howMuch = costObject.get("value").getAsInt();
				tempMap.put(manaColor, howMuch);
			}

			return factoryEffect.create(source, new ManaMapDefault(tempMap));
		}
		return null;
	}

	// TODO HIGH Karten überprüfen, Abilities haben auch CostMaps
	private ActivatedAbility parseAbility(MagicCard card, JsonObject abilityObject) {
		final AbilityType abilityType = AbilityType.valueOf(abilityObject.get("abilitytype").getAsString());
		final ObservableList<IsManaMap> listCostMaps = new SimpleListProperty<>(FXCollections.emptyObservableList());
		final JsonElement additionalCostElement = abilityObject.get("additionalcost");
		AdditionalCostType additionalCostType = AdditionalCostType.NO_ADDITIONAL_COST;
		if (additionalCostElement != null) {
			additionalCostType = AdditionalCostType.valueOf(additionalCostElement.getAsString());
		}
		final JsonArray effectArray = abilityObject.get("effects").getAsJsonArray();

		return abilityFactory.create(card, abilityType, additionalCostType, effectArray, listCostMaps);
	}

}
