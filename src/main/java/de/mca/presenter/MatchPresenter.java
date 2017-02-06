package de.mca.presenter;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import de.mca.MagicParser;
import de.mca.Main;
import de.mca.factories.FactoryMatch;
import de.mca.factories.FactoryPlayer;
import de.mca.io.FileManager;
import de.mca.io.ResourceManager;
import de.mca.model.InputComputer;
import de.mca.model.InputHuman;
import de.mca.model.MagicCard;
import de.mca.model.MagicPermanent;
import de.mca.model.MagicStack;
import de.mca.model.Match;
import de.mca.model.enums.ColorType;
import de.mca.model.enums.PlayerType;
import de.mca.model.enums.ZoneType;
import de.mca.model.interfaces.IsPlayer;
import de.mca.model.interfaces.IsStackable;
import de.mca.model.interfaces.IsZone;
import javafx.animation.AnimationTimer;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

/**
 *
 * @author Maximilian Werling
 *
 */
public class MatchPresenter extends AnimationTimer implements Initializable, IsStackableScreen {

	@FXML
	private Button buttonProgress;
	private CanvasZoneBattlefield canvasBattlefield;
	private CanvasZoneDefault canvasComputerGraveyard;
	private CanvasZoneDefault canvasComputerHand;
	private CanvasZoneDefault canvasExile;
	private CanvasZoneDefault canvasHumanGraveyard;
	private CanvasZoneDefault canvasHumanHand;
	private CanvasZoneStack canvasStack;
	@Inject
	private FactoryMatch factoryMatch;
	@Inject
	private FactoryPlayer factoryPlayer;
	@FXML
	private Label fpsLabel;
	private Consumer<Integer> fpsReporter;
	private int framesSinceLastFpsUpdate = 0;
	@FXML
	private GridPane gridPaneCenter;
	private ImageView imageViewCardZoom;
	@Inject
	private InputComputer inputComputer;
	@Inject
	private InputHuman inputHuman;
	@FXML
	private Label labelComputerAvatar;
	@FXML
	private Label labelComputerBlackMana;
	@FXML
	private Label labelComputerBlueMana;
	@FXML
	private Label labelComputerColorlessMana;
	@FXML
	private Label labelComputerGreenMana;
	@FXML
	private Label labelComputerLife;
	@FXML
	private Label labelComputerRedMana;
	@FXML
	private Label labelComputerWhiteMana;
	@FXML
	private Label labelCurrentPhase;
	@FXML
	private Label labelCurrentStep;
	@FXML
	private Label labelHint;
	@FXML
	private Label labelHumanAvatar;
	@FXML
	private Label labelHumanBlackMana;
	@FXML
	private Label labelHumanBlueMana;
	@FXML
	private Label labelHumanColorlessMana;
	@FXML
	private Label labelHumanGreenMana;
	@FXML
	private Label labelHumanLife;
	@FXML
	private Label labelHumanRedMana;
	@FXML
	private Label labelHumanWhiteMana;
	@FXML
	private Label labelIconComputerBlackMana;
	@FXML
	private Label labelIconComputerBlueMana;
	@FXML
	private Label labelIconComputerColorlessMana;
	@FXML
	private Label labelIconComputerGreenMana;
	@FXML
	private Label labelIconComputerLife;
	@FXML
	private Label labelIconComputerRedMana;
	@FXML
	private Label labelIconComputerWhiteMana;
	@FXML
	private Label labelIconHumanBlackMana;
	@FXML
	private Label labelIconHumanBlueMana;
	@FXML
	private Label labelIconHumanColorlessMana;
	@FXML
	private Label labelIconHumanGreenMana;
	@FXML
	private Label labelIconHumanLife;
	@FXML
	private Label labelIconHumanRedMana;
	@FXML
	private Label labelIconHumanWhiteMana;
	@FXML
	private Label labelPlayerActive;
	@FXML
	private Label labelTurnNumber;
	@Inject
	private MagicParser magicParser;
	private Match matchActive;
	private Runnable matchUpdater;
	@FXML
	private MenuBar menuBarTop;
	@FXML
	private AnchorPane paneBattlefield;
	@FXML
	private AnchorPane paneCardZoom;
	@FXML
	private AnchorPane paneComputerGraveyard;
	@FXML
	private AnchorPane paneComputerHand;
	@FXML
	private AnchorPane paneExile;
	@FXML
	private AnchorPane paneHumanGraveyard;
	@FXML
	private AnchorPane paneHumanHand;
	@FXML
	private AnchorPane paneLeft;
	@FXML
	private AnchorPane paneRight;
	@FXML
	private AnchorPane paneStack;
	private long previousTime = 0;
	private Runnable rendererBattlefield;
	private Runnable rendererComputerGraveyard;
	private Runnable rendererComputerHand;
	private Runnable rendererExile;
	private Runnable rendererHumanGraveyard;
	private Runnable rendererHumanHand;
	private Runnable rendererStack;
	@SuppressWarnings("unused")
	private Main screenController;
	private float secondsElapsedSinceLastFpsUpdate = 0f;
	private List<SpriteMagicPermanent> spriteListBattlefield;
	private final List<SpriteMagicCard> spriteListComputerGraveyard;
	private final List<SpriteMagicCard> spriteListComputerHand;
	private final List<SpriteMagicCard> spriteListExile;
	private final List<SpriteMagicCard> spriteListHumanGraveyard;
	private final List<SpriteMagicCard> spriteListHumanHand;
	private final List<SpriteStackable> spriteListStack;
	@FXML
	private Tab tabBattlefield;
	@FXML
	private Tab tabCardZoom;
	@FXML
	private Tab tabComputerGraveyard;
	@FXML
	private Tab tabComputerHand;
	@FXML
	private Tab tabExile;
	@FXML
	private Tab tabHumanGraveyard;
	@FXML
	private Tab tabHumanHand;
	@FXML
	private Tab tabStack;

	@Inject
	public MatchPresenter(EventBus eventBus) {
		// TODO LOW Threading?
		eventBus.register(this);

		spriteListBattlefield = new ArrayList<>();
		spriteListComputerGraveyard = new ArrayList<>();
		spriteListComputerHand = new ArrayList<>();
		spriteListExile = new ArrayList<>();
		spriteListHumanGraveyard = new ArrayList<>();
		spriteListHumanHand = new ArrayList<>();
		spriteListStack = new ArrayList<>();
	}

	@Subscribe
	public void examineButtonChange(ProgressNameChange progressNameChange) {
		buttonProgress.setText(progressNameChange.getName());
	}

	public Match getMatchActive() {
		return matchActive;
	}

	@Override
	public void handle(long currentTime) {
		if (matchUpdater == null) {
			return;
		}

		if (previousTime == 0) {
			previousTime = currentTime;
			return;
		}

		float secondsElapsed = (currentTime - previousTime) / 1e9f;

		previousTime = currentTime;

		matchUpdater.run();

		rendererBattlefield.run();
		rendererComputerGraveyard.run();
		rendererComputerHand.run();
		rendererExile.run();
		rendererHumanGraveyard.run();
		rendererHumanHand.run();
		rendererStack.run();

		secondsElapsedSinceLastFpsUpdate += secondsElapsed;
		framesSinceLastFpsUpdate++;
		if (secondsElapsedSinceLastFpsUpdate >= 0.5f) {
			int fps = Math.round(framesSinceLastFpsUpdate / secondsElapsedSinceLastFpsUpdate);
			fpsReporter.accept(fps);
			secondsElapsedSinceLastFpsUpdate = 0;
			framesSinceLastFpsUpdate = 0;
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Left Pane
		canvasStack = new CanvasZoneStack(paneStack, spriteListStack);
		paneStack.getChildren().add(canvasStack);
		tabStack.setContent(paneStack);

		imageViewCardZoom = new AdaptableImageView(null, paneCardZoom.heightProperty(), paneCardZoom.widthProperty());
		AnchorPane.setLeftAnchor(imageViewCardZoom, 5.0);
		AnchorPane.setRightAnchor(imageViewCardZoom, 5.0);
		paneCardZoom.getChildren().add(imageViewCardZoom);
		tabCardZoom.setContent(paneCardZoom);

		buttonProgress.setOnAction(actionEvent -> {
			inputHuman.progress();
		});

		// Center Pane
		canvasBattlefield = new CanvasZoneBattlefield(paneBattlefield, inputHuman, spriteListBattlefield,
				ZoneType.BATTLEFIELD, imageViewCardZoom);
		paneBattlefield.getChildren().add(canvasBattlefield);
		tabBattlefield.setContent(paneBattlefield);

		canvasComputerGraveyard = new CanvasZoneDefault(paneComputerGraveyard, inputHuman, spriteListComputerGraveyard,
				ZoneType.GRAVEYARD, imageViewCardZoom);
		paneComputerGraveyard.getChildren().add(canvasComputerGraveyard);
		tabComputerGraveyard.setContent(paneComputerGraveyard);
		tabComputerGraveyard.setGraphic(new AdaptableImageView(ResourceManager.getIcon("grave.png"),
				new SimpleDoubleProperty(16.0), new SimpleDoubleProperty(16.0)));

		canvasComputerHand = new CanvasZoneDefault(paneComputerHand, inputHuman, spriteListComputerHand, ZoneType.HAND,
				imageViewCardZoom);
		paneComputerHand.getChildren().add(canvasComputerHand);
		tabComputerHand.setContent(paneComputerHand);
		tabComputerHand.setGraphic(new AdaptableImageView(ResourceManager.getIcon("hand.png"),
				new SimpleDoubleProperty(16.0), new SimpleDoubleProperty(16.0)));

		canvasExile = new CanvasZoneDefault(paneExile, inputHuman, spriteListExile, ZoneType.EXILE, imageViewCardZoom);
		paneExile.getChildren().add(canvasExile);
		tabExile.setContent(paneExile);

		canvasHumanGraveyard = new CanvasZoneDefault(paneHumanGraveyard, inputHuman, spriteListHumanGraveyard,
				ZoneType.GRAVEYARD, imageViewCardZoom);
		paneHumanGraveyard.getChildren().add(canvasHumanGraveyard);
		tabHumanGraveyard.setContent(paneComputerGraveyard);
		tabHumanGraveyard.setGraphic(new AdaptableImageView(ResourceManager.getIcon("grave.png"),
				new SimpleDoubleProperty(16.0), new SimpleDoubleProperty(16.0)));

		canvasHumanHand = new CanvasZoneDefault(paneHumanHand, inputHuman, spriteListHumanHand, ZoneType.HAND,
				imageViewCardZoom);
		paneHumanHand.getChildren().add(canvasHumanHand);
		tabHumanHand.setContent(paneHumanHand);
		tabHumanHand.setGraphic(new AdaptableImageView(ResourceManager.getIcon("hand.png"),
				new SimpleDoubleProperty(16.0), new SimpleDoubleProperty(16.0)));
	}

	@FXML
	public void play() {
		if (getMatchActive() != null) {
			stop();
		}

		// Parameter für Testmatch
		String nameHuman = "Human";
		String nameComputer = "Computer";
		String deckComputer = "vanillaartifact.json";
		String deckHuman = "vanillaartifact.json";
		Image avatarComputer = FileManager.getAvatarImages().get(0);
		Image avatarHuman = FileManager.getAvatarImages().get(1);

		// Match erstellen
		Match matchActive = createMatch(nameComputer, nameHuman, deckHuman, deckComputer, avatarComputer, avatarHuman);
		IsPlayer playerComputer = matchActive.getPlayer(PlayerType.COMPUTER);
		IsPlayer playerHuman = matchActive.getPlayer(PlayerType.HUMAN);
		setMatchActive(matchActive);

		inputComputer.setMatchPresenter(this);
		inputHuman.setMatchPresenter(this);

		// Game Loop
		matchUpdater = () -> matchActive.update();
		rendererBattlefield = () -> canvasBattlefield.draw();
		rendererComputerGraveyard = () -> canvasComputerGraveyard.draw();
		rendererComputerHand = () -> canvasComputerHand.draw();
		rendererExile = () -> canvasExile.draw();
		rendererHumanGraveyard = () -> canvasHumanGraveyard.draw();
		rendererHumanHand = () -> canvasHumanHand.draw();
		rendererStack = () -> canvasStack.draw();
		fpsReporter = fps -> fpsLabel.setText(String.format("FPS: %d", fps));

		/**
		 * Linkes Panel
		 */
		// Elemente erstellen
		initializeIconLabel(avatarComputer, labelComputerAvatar);
		initializeIconLabel(ResourceManager.getIcon("heart.png"), labelIconComputerLife);

		initializeIconLabel(ResourceManager.getIcon("b.png"), labelIconComputerBlackMana);
		initializeIconLabel(ResourceManager.getIcon("u.png"), labelIconComputerBlueMana);
		initializeIconLabel(ResourceManager.getIcon("1.png"), labelIconComputerColorlessMana);
		initializeIconLabel(ResourceManager.getIcon("g.png"), labelIconComputerGreenMana);
		initializeIconLabel(ResourceManager.getIcon("r.png"), labelIconComputerRedMana);
		initializeIconLabel(ResourceManager.getIcon("w.png"), labelIconComputerWhiteMana);

		initializeIconLabel(avatarHuman, labelHumanAvatar);
		initializeIconLabel(ResourceManager.getIcon("heart.png"), labelIconHumanLife);

		initializeIconLabel(ResourceManager.getIcon("b.png"), labelIconHumanBlackMana);
		initializeIconLabel(ResourceManager.getIcon("u.png"), labelIconHumanBlueMana);
		initializeIconLabel(ResourceManager.getIcon("1.png"), labelIconHumanColorlessMana);
		initializeIconLabel(ResourceManager.getIcon("g.png"), labelIconHumanGreenMana);
		initializeIconLabel(ResourceManager.getIcon("r.png"), labelIconHumanRedMana);
		initializeIconLabel(ResourceManager.getIcon("w.png"), labelIconHumanWhiteMana);

		// Elemente binden
		bindLifeLabel(labelComputerLife, playerComputer);
		bindManaPool(playerComputer);

		bindSizeTab(tabStack, PlayerType.NONE, ZoneType.STACK);
		bindZoneStack(matchActive.getZoneStack(), spriteListStack);

		bindLifeLabel(labelHumanLife, playerHuman);
		bindManaPool(playerHuman);

		/**
		 * Mittleres Panel
		 */
		// Elemente binden
		bindSizeTab(tabBattlefield, PlayerType.NONE, ZoneType.BATTLEFIELD);
		bindZoneBattlefield(matchActive.getZoneBattlefield(), spriteListBattlefield);

		bindSizeTab(tabComputerGraveyard, PlayerType.COMPUTER, ZoneType.GRAVEYARD);
		bindSizeTab(tabComputerHand, PlayerType.COMPUTER, ZoneType.HAND);
		bindZoneDefault(playerComputer.getZoneGraveyard(), spriteListComputerGraveyard);
		bindZoneDefault(playerComputer.getZoneHand(), spriteListComputerHand);

		bindSizeTab(tabExile, PlayerType.NONE, ZoneType.EXILE);
		bindZoneDefault(matchActive.getZoneExile(), spriteListExile);

		bindSizeTab(tabHumanGraveyard, PlayerType.HUMAN, ZoneType.GRAVEYARD);
		bindSizeTab(tabHumanHand, PlayerType.HUMAN, ZoneType.HAND);
		bindZoneDefault(playerHuman.getZoneGraveyard(), spriteListHumanGraveyard);
		bindZoneDefault(playerHuman.getZoneHand(), spriteListHumanHand);

		/**
		 * Unteres Panel
		 */
		// Elemente binden
		labelTurnNumber.textProperty().bind(matchActive.propertyTurnNumber().asString());
		labelCurrentPhase.textProperty().bind(matchActive.propertyCurrentPhase().asString());
		labelCurrentStep.textProperty().bind(matchActive.propertyCurrentStep().asString());
		labelPlayerActive.textProperty().bind(matchActive.propertyPlayerActive().asString());

		labelHint.textProperty().bind(playerHuman.propertyPlayerState().asString());

		// Starten
		setMatchActive(matchActive);
		this.start();
	}

	@Override
	public void setScreenController(Main screenController) {
		this.screenController = screenController;
	}

	@Override
	public void stop() {
		previousTime = 0;
		secondsElapsedSinceLastFpsUpdate = 0f;
		framesSinceLastFpsUpdate = 0;
		super.stop();
	}

	private void bindLifeLabel(Label statusLabel, IsPlayer player) {
		statusLabel.textProperty().bind(player.propertyLife().asString());
	}

	private void bindManaPool(IsPlayer player) {
		player.getManaPool().propertyMapMana().addListener(new MapChangeListener<ColorType, Integer>() {

			@Override
			public void onChanged(
					javafx.collections.MapChangeListener.Change<? extends ColorType, ? extends Integer> change) {
				ColorType colorType = change.getKey();
				String stringValue = change.getValueAdded() == null ? "0" : change.getValueAdded().toString();

				if (colorType.equals(ColorType.BLACK)) {
					if (player.equals(PlayerType.COMPUTER)) {
						labelComputerBlackMana.setText(stringValue);
					} else {
						labelHumanBlackMana.setText(stringValue);
					}
				} else if (change.getKey().equals(ColorType.BLUE)) {
					if (player.equals(PlayerType.COMPUTER)) {
						labelComputerBlueMana.setText(stringValue);
					} else {
						labelHumanBlueMana.setText(stringValue);
					}
				} else if (change.getKey().equals(ColorType.GREEN)) {
					if (player.equals(PlayerType.COMPUTER)) {
						labelComputerGreenMana.setText(stringValue);
					} else {
						labelHumanGreenMana.setText(stringValue);
					}
				} else if (change.getKey().equals(ColorType.NONE)) {
					if (player.equals(PlayerType.COMPUTER)) {
						labelComputerColorlessMana.setText(stringValue);
					} else {
						labelHumanColorlessMana.setText(stringValue);
					}
				} else if (change.getKey().equals(ColorType.RED)) {
					if (player.equals(PlayerType.COMPUTER)) {
						labelComputerRedMana.setText(stringValue);
					} else {
						labelHumanRedMana.setText(stringValue);
					}
				} else if (change.getKey().equals(ColorType.WHITE)) {
					if (player.equals(PlayerType.COMPUTER)) {
						labelComputerWhiteMana.setText(stringValue);
					} else {
						labelHumanWhiteMana.setText(stringValue);
					}
				}
			}
		});
	}

	private void bindSizeTab(Tab sizeTab, PlayerType playerType, ZoneType zoneType) {
		sizeTab.textProperty().bind(createTabBinding(playerType, zoneType));
	}

	private void bindZoneBattlefield(IsZone<? extends MagicPermanent> zone, List<SpriteMagicPermanent> listSprites) {
		zone.propertyListZoneCards().addListener((ListChangeListener.Change<? extends MagicPermanent> listChanges) -> {
			while (listChanges.next()) {
				if (listChanges.wasAdded()) {
					listChanges.getAddedSubList().forEach(magicCard -> {
						listSprites.add(new SpriteMagicPermanent(magicCard));
					});
				} else if (listChanges.wasRemoved()) {
					listChanges.getRemoved().forEach(magicCard -> {
						SpriteMagicPermanent spriteToRemove = null;
						for (SpriteMagicPermanent sprite : listSprites) {
							if (sprite.getMagicPermanent().equals(magicCard)) {
								spriteToRemove = sprite;
							}
						}
						listSprites.remove(spriteToRemove);
					});
				}
			}
		});
	}

	private void bindZoneDefault(IsZone<? extends MagicCard> zone, List<SpriteMagicCard> listSprites) {
		zone.propertyListZoneCards().addListener((ListChangeListener.Change<? extends MagicCard> listChanges) -> {
			while (listChanges.next()) {
				if (listChanges.wasAdded()) {
					listChanges.getAddedSubList().forEach(magicCard -> {
						listSprites.add(new SpriteMagicCard(magicCard));
					});
				} else if (listChanges.wasRemoved()) {
					listChanges.getRemoved().forEach(magicCard -> {
						Sprite spriteToRemove = null;
						for (SpriteMagicCard sprite : listSprites) {
							if (sprite.getMagicObject().equals(magicCard)) {
								spriteToRemove = sprite;
							}
						}
						listSprites.remove(spriteToRemove);
					});
				}
			}
		});
	}

	private void bindZoneStack(MagicStack stack, List<SpriteStackable> listSprites) {
		stack.getList().addListener((ListChangeListener.Change<? extends IsStackable> listChanges) -> {
			while (listChanges.next()) {
				if (listChanges.wasAdded()) {
					listChanges.getAddedSubList().forEach(stackable -> {
						listSprites.add(new SpriteStackable(stackable));
					});
				} else if (listChanges.wasRemoved()) {
					listChanges.getRemoved().forEach(stackable -> {
						Sprite spriteToRemove = null;
						for (SpriteStackable sprite : listSprites) {
							if (sprite.getMagicObject().equals(stackable)) {
								spriteToRemove = sprite;
							}
						}
						listSprites.remove(spriteToRemove);
					});
				}
			}
		});
	}

	private Match createMatch(String nameComputer, String nameHuman, String deckComputer, String deckHuman,
			Image avatarComputer, Image avatarHuman) {
		// Erstelle Spieler
		IsPlayer playerComputer = factoryPlayer.create(PlayerType.COMPUTER);
		IsPlayer playerHuman = factoryPlayer.create(PlayerType.HUMAN);

		// Binde Spieler an ihren Input
		inputComputer.setPlayer(playerComputer);
		inputHuman.setPlayer(playerHuman);

		return factoryMatch.create(playerComputer, playerHuman, nameHuman, nameComputer,
				magicParser.parseDeckFromPath(FileManager.getDeckPath(deckHuman)),
				magicParser.parseDeckFromPath(FileManager.getDeckPath(deckComputer)));
	}

	private StringExpression createTabBinding(PlayerType playerType, ZoneType zoneType) {
		if (zoneType.equals(ZoneType.BATTLEFIELD)) {
			return Bindings.concat("(").concat(matchActive.propertyBattlefieldSize().asString())
					.concat(") Battlefield");
		} else if (zoneType.equals(ZoneType.EXILE)) {
			return Bindings.concat("(").concat(matchActive.propertyExileSize().asString()).concat(") Exile");
		} else if (zoneType.equals(ZoneType.STACK)) {
			return Bindings.concat("(").concat(matchActive.propertyStackSize().asString()).concat(") Stack");
		} else if (zoneType.equals(ZoneType.HAND)) {
			IsPlayer player = getMatchActive().getPlayer(playerType);
			return Bindings.concat("(").concat(player.propertyHandSize().asString()).concat(") ")
					.concat(player.getDisplayName());
		} else if (zoneType.equals(ZoneType.GRAVEYARD)) {
			IsPlayer player = getMatchActive().getPlayer(playerType);
			return Bindings.concat("(").concat(player.propertyGraveSize().asString()).concat(") ")
					.concat(player.getDisplayName());
		} else {
			throw new IllegalArgumentException();
		}
	}

	private void initializeIconLabel(Image icon, Label label) {
		label.setGraphic(new AdaptableImageView(icon, label.heightProperty(), label.widthProperty()));
	}

	private void setMatchActive(Match matchActive) {
		this.matchActive = matchActive;
	}

}
