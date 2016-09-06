package de.mca.presenter;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import com.google.inject.Inject;

import de.mca.Constants;
import de.mca.InputComputer;
import de.mca.InputHuman;
import de.mca.MagicParser;
import de.mca.Main;
import de.mca.factories.FactoryMatch;
import de.mca.factories.FactoryPlayer;
import de.mca.io.FileManager;
import de.mca.io.ResourceManager;
import de.mca.model.MagicCard;
import de.mca.model.Match;
import de.mca.model.enums.ColorType;
import de.mca.model.enums.PlayerType;
import de.mca.model.enums.ZoneType;
import de.mca.model.interfaces.IsObject;
import de.mca.model.interfaces.IsPlayer;
import de.mca.model.interfaces.IsZone;
import javafx.animation.AnimationTimer;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

/**
 * 
 * @author Maximilian Werling
 *
 */
public class MatchPresenter extends AnimationTimer implements Initializable, IsStackableScreen {

	/**
	 * Hilfsklasse zur schnelleren Erstellung der Icon-Labels.
	 * 
	 * @author Maximilian Werling
	 *
	 */
	class AdaptableImageView extends ImageView {

		public AdaptableImageView(Image image, DoubleExpression heightProperty, DoubleExpression widthProperty) {
			super(image);

			fitHeightProperty().bind(heightProperty);
			fitWidthProperty().bind(widthProperty);
			setPreserveRatio(true);
		}

	}

	/**
	 * Hilfsklasse zur schnelleren Erstellung der Zeichenflächen für die Zonen
	 * der Spieler.
	 * 
	 * @author Maximilian Werling
	 *
	 */
	class CanvasZone extends Canvas {

		private List<CardSprite> spriteList;

		public CanvasZone(Pane parent, InputHuman input, List<CardSprite> spriteList, ZoneType zoneType,
				ImageView zoomView) {
			this.spriteList = spriteList;
			widthProperty().bind(parent.widthProperty());
			heightProperty().bind(parent.heightProperty());

			addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent e) {
					input.input(determineClickedCard(e, spriteList), zoneType);
				}
			});

			addEventHandler(MouseEvent.MOUSE_MOVED, new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					CardSprite mouseOverSprite = null;
					for (CardSprite sprite : spriteList) {
						if (sprite.getBoundary().contains(new Point2D(event.getX(), event.getY()))) {
							mouseOverSprite = sprite;
							break;
						}
					}
					if (mouseOverSprite != null) {
						zoomView.setImage(mouseOverSprite.getImage());
						zoomView.setPreserveRatio(true);
					} else {
						zoomView.setImage(null);
					}
				}

			});
		}

		public void draw() {
			GraphicsContext gc = getGraphicsContext2D();

			gc.clearRect(0, 0, getWidth(), getHeight());

			for (int i = 0; i < spriteList.size(); i++) {
				CardSprite sprite = spriteList.get(i);
				double positionX = 0;
				double positionY = 0;

				DoubleProperty heightBoundSpriteHeight = heightProperty();
				DoubleBinding heightBoundSpriteWidth = heightBoundSpriteHeight.multiply(Constants.CARD_RATIO);

				DoubleBinding widthBoundSpriteWidth = widthProperty().divide(spriteList.size());
				DoubleBinding widthBoundSpriteHeight = widthBoundSpriteWidth.divide(Constants.CARD_RATIO);

				if (heightBoundSpriteWidth.greaterThan(widthBoundSpriteWidth).get()) {
					sprite.propertyHeight().bind(widthBoundSpriteHeight);
					sprite.propertyWidth().bind(widthBoundSpriteWidth);
				} else {
					sprite.propertyHeight().bind(heightBoundSpriteHeight);
					sprite.propertyWidth().bind(heightBoundSpriteWidth);
				}

				positionX = sprite.getWidth() * i;
				positionY = 0;

				sprite.setPosition(positionX, positionY);

				sprite.render(gc);
			}
		}

		@Override
		public boolean isResizable() {
			return true;
		}

		private IsObject determineClickedCard(MouseEvent e, List<CardSprite> listSprites) {
			int zCoordinate = -1;
			List<MagicCard> listClicked = new ArrayList<>();
			for (CardSprite sprite : listSprites) {
				if (sprite.getBoundary().contains(new Point2D(e.getX(), e.getY()))) {
					listClicked.add(sprite.getCard());
					zCoordinate++;
				}
			}
			return listClicked.get(zCoordinate);
		}
	}

	@FXML
	private Button buttonPassPriority;
	private CanvasZone canvasBattlefield;
	private CanvasZone canvasComputerGraveyard;
	private CanvasZone canvasComputerHand;
	private CanvasZone canvasExile;
	private CanvasZone canvasHumanGraveyard;
	private CanvasZone canvasHumanHand;
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
	private Label labelPlayerPrioritized;
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
	private long previousTime = 0;
	private Runnable rendererBattlefield;
	private Runnable rendererComputerGraveyard;
	private Runnable rendererComputerHand;
	private Runnable rendererExile;
	private Runnable rendererHumanGraveyard;
	private Runnable rendererHumanHand;
	@SuppressWarnings("unused")
	private Main screenController;
	private float secondsElapsedSinceLastFpsUpdate = 0f;
	private List<CardSprite> spriteListBattlefield;
	private List<CardSprite> spriteListComputerGraveyard;
	private List<CardSprite> spriteListComputerHand;
	private List<CardSprite> spriteListExile;
	private List<CardSprite> spriteListHumanGraveyard;
	private List<CardSprite> spriteListHumanHand;
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

	public MatchPresenter() {
		// TODO: Threading?
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
		spriteListBattlefield = new ArrayList<>();
		spriteListComputerGraveyard = new ArrayList<>();
		spriteListComputerHand = new ArrayList<>();
		spriteListExile = new ArrayList<>();
		spriteListHumanGraveyard = new ArrayList<>();
		spriteListHumanHand = new ArrayList<>();

		// Left Pane
		imageViewCardZoom = new AdaptableImageView(null, paneCardZoom.heightProperty(), paneCardZoom.widthProperty());
		AnchorPane.setLeftAnchor(imageViewCardZoom, 5.0);
		AnchorPane.setRightAnchor(imageViewCardZoom, 5.0);
		paneCardZoom.getChildren().add(imageViewCardZoom);
		tabCardZoom.setContent(paneCardZoom);

		buttonPassPriority.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				inputHuman.inputPassPriority();
			}
		});

		// Center Pane
		canvasBattlefield = new CanvasZone(paneBattlefield, inputHuman, spriteListBattlefield, ZoneType.BATTLEFIELD,
				imageViewCardZoom);
		paneBattlefield.getChildren().add(canvasBattlefield);
		tabBattlefield.setContent(paneBattlefield);

		canvasComputerGraveyard = new CanvasZone(paneComputerGraveyard, inputHuman, spriteListComputerGraveyard,
				ZoneType.GRAVEYARD, imageViewCardZoom);
		paneComputerGraveyard.getChildren().add(canvasComputerGraveyard);
		tabComputerGraveyard.setContent(paneComputerGraveyard);
		tabComputerGraveyard.setGraphic(new AdaptableImageView(ResourceManager.getIcon("grave.png"),
				new SimpleDoubleProperty(16.0), new SimpleDoubleProperty(16.0)));

		canvasComputerHand = new CanvasZone(paneComputerHand, inputHuman, spriteListComputerHand, ZoneType.HAND,
				imageViewCardZoom);
		paneComputerHand.getChildren().add(canvasComputerHand);
		tabComputerHand.setContent(paneComputerHand);
		tabComputerHand.setGraphic(new AdaptableImageView(ResourceManager.getIcon("hand.png"),
				new SimpleDoubleProperty(16.0), new SimpleDoubleProperty(16.0)));

		canvasExile = new CanvasZone(paneExile, inputHuman, spriteListExile, ZoneType.EXILE, imageViewCardZoom);
		paneExile.getChildren().add(canvasExile);
		tabExile.setContent(paneExile);

		canvasHumanGraveyard = new CanvasZone(paneHumanGraveyard, inputHuman, spriteListHumanGraveyard,
				ZoneType.GRAVEYARD, imageViewCardZoom);
		paneHumanGraveyard.getChildren().add(canvasHumanGraveyard);
		tabHumanGraveyard.setContent(paneComputerGraveyard);
		tabHumanGraveyard.setGraphic(new AdaptableImageView(ResourceManager.getIcon("grave.png"),
				new SimpleDoubleProperty(16.0), new SimpleDoubleProperty(16.0)));

		canvasHumanHand = new CanvasZone(paneHumanHand, inputHuman, spriteListHumanHand, ZoneType.HAND,
				imageViewCardZoom);
		paneHumanHand.getChildren().add(canvasHumanHand);
		tabHumanHand.setContent(paneHumanHand);
		tabHumanHand.setGraphic(new AdaptableImageView(ResourceManager.getIcon("hand.png"),
				new SimpleDoubleProperty(16.0), new SimpleDoubleProperty(16.0)));
	}

	@FXML
	public void play() {
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

		// Game Loop
		matchUpdater = () -> matchActive.update();
		rendererBattlefield = () -> canvasBattlefield.draw();
		rendererComputerGraveyard = () -> canvasComputerGraveyard.draw();
		rendererComputerHand = () -> canvasComputerHand.draw();
		rendererExile = () -> canvasExile.draw();
		rendererHumanGraveyard = () -> canvasHumanGraveyard.draw();
		rendererHumanHand = () -> canvasHumanHand.draw();
		fpsReporter = fps -> fpsLabel.setText(String.format("FPS: %d", fps));

		/**
		 * Linkes Panel
		 */
		// Elemente erstellen
		setIconLabel(avatarComputer, labelComputerAvatar);
		setIconLabel(ResourceManager.getIcon("heart.png"), labelIconComputerLife);

		setIconLabel(ResourceManager.getIcon("b.png"), labelIconComputerBlackMana);
		setIconLabel(ResourceManager.getIcon("u.png"), labelIconComputerBlueMana);
		setIconLabel(ResourceManager.getIcon("1.png"), labelIconComputerColorlessMana);
		setIconLabel(ResourceManager.getIcon("g.png"), labelIconComputerGreenMana);
		setIconLabel(ResourceManager.getIcon("r.png"), labelIconComputerRedMana);
		setIconLabel(ResourceManager.getIcon("w.png"), labelIconComputerWhiteMana);

		setIconLabel(avatarHuman, labelHumanAvatar);
		setIconLabel(ResourceManager.getIcon("heart.png"), labelIconHumanLife);

		setIconLabel(ResourceManager.getIcon("b.png"), labelIconHumanBlackMana);
		setIconLabel(ResourceManager.getIcon("u.png"), labelIconHumanBlueMana);
		setIconLabel(ResourceManager.getIcon("1.png"), labelIconHumanColorlessMana);
		setIconLabel(ResourceManager.getIcon("g.png"), labelIconHumanGreenMana);
		setIconLabel(ResourceManager.getIcon("r.png"), labelIconHumanRedMana);
		setIconLabel(ResourceManager.getIcon("w.png"), labelIconHumanWhiteMana);

		// Elemente binden
		labelComputerLife.textProperty().bind(playerComputer.propertyLife().asString());

		labelHumanLife.textProperty().bind(playerHuman.propertyLife().asString());

		/**
		 * Mittleres Panel
		 */
		// Elemente binden
		tabBattlefield.textProperty().bind(createMatchTabBinding(ZoneType.BATTLEFIELD));

		tabComputerGraveyard.textProperty().bind(createPlayerTabBinding(playerComputer, ZoneType.GRAVEYARD));
		tabComputerHand.textProperty().bind(createPlayerTabBinding(playerComputer, ZoneType.HAND));

		tabExile.textProperty().bind(createMatchTabBinding(ZoneType.EXILE));

		tabHumanGraveyard.textProperty().bind(createPlayerTabBinding(playerHuman, ZoneType.GRAVEYARD));
		tabHumanHand.textProperty().bind(createPlayerTabBinding(playerHuman, ZoneType.HAND));

		bindZone(matchActive.getZoneBattlefield(), spriteListBattlefield);
		bindZone(playerComputer.getZoneGraveyard(), spriteListComputerGraveyard);
		bindZone(playerComputer.getZoneHand(), spriteListComputerHand);
		bindZone(matchActive.getZoneExile(), spriteListExile);
		bindZone(playerHuman.getZoneGraveyard(), spriteListHumanGraveyard);
		bindZone(playerHuman.getZoneHand(), spriteListHumanHand);

		bindManaPool(playerComputer);
		bindManaPool(playerHuman);

		/**
		 * Unteres Panel
		 */
		// Elemente binden
		labelTurnNumber.textProperty().bind(matchActive.propertyTurnNumber().asString());
		labelCurrentPhase.textProperty().bind(matchActive.propertyCurrentPhase().asString());
		labelCurrentStep.textProperty().bind(matchActive.propertyCurrentStep().asString());
		labelPlayerActive.textProperty().bind(matchActive.propertyPlayerActive().asString());
		labelPlayerPrioritized.textProperty().bind(matchActive.propertyPlayerPrioritized().asString());

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

	private void bindManaPool(IsPlayer player) {
		player.getManaPool().propertyManaMap().addListener(new MapChangeListener<ColorType, Integer>() {

			@Override
			public void onChanged(
					javafx.collections.MapChangeListener.Change<? extends ColorType, ? extends Integer> change) {
				if (change.getKey().equals(ColorType.BLACK)) {
					if (player.equals(PlayerType.COMPUTER)) {
						labelComputerBlackMana.setText(change.getMap().get(ColorType.BLACK).toString());
					} else {
						labelHumanBlackMana.setText(change.getMap().get(ColorType.BLACK).toString());
					}
				} else if (change.getKey().equals(ColorType.BLUE)) {
					if (player.equals(PlayerType.COMPUTER)) {
						labelComputerBlueMana.setText(change.getMap().get(ColorType.BLUE).toString());
					} else {
						labelHumanBlueMana.setText(change.getMap().get(ColorType.BLUE).toString());
					}
				} else if (change.getKey().equals(ColorType.GREEN)) {
					if (player.equals(PlayerType.COMPUTER)) {
						labelComputerGreenMana.setText(change.getMap().get(ColorType.GREEN).toString());
					} else {
						labelHumanGreenMana.setText(change.getMap().get(ColorType.GREEN).toString());
					}
				} else if (change.getKey().equals(ColorType.NONE)) {
					if (player.equals(PlayerType.COMPUTER)) {
						labelComputerColorlessMana.setText(change.getMap().get(ColorType.NONE).toString());
					} else {
						labelHumanColorlessMana.setText(change.getMap().get(ColorType.NONE).toString());
					}
				} else if (change.getKey().equals(ColorType.RED)) {
					if (player.equals(PlayerType.COMPUTER)) {
						labelComputerRedMana.setText(change.getMap().get(ColorType.RED).toString());
					} else {
						labelHumanRedMana.setText(change.getMap().get(ColorType.RED).toString());
					}
				} else if (change.getKey().equals(ColorType.WHITE)) {
					if (player.equals(PlayerType.COMPUTER)) {
						labelComputerWhiteMana.setText(change.getMap().get(ColorType.WHITE).toString());
					} else {
						labelHumanWhiteMana.setText(change.getMap().get(ColorType.WHITE).toString());
					}
				}
			}
		});
	}

	private void bindZone(IsZone<? extends MagicCard> zone, List<CardSprite> spriteList) {
		zone.propertyListZoneCards().addListener((ListChangeListener.Change<? extends MagicCard> c) -> {
			while (c.next()) {
				if (c.wasAdded()) {
					c.getAddedSubList().forEach(addedCard -> {
						spriteList.add(new CardSprite(addedCard));
					});
				} else if (c.wasRemoved()) {
					c.getRemoved().forEach(removedCard -> {
						CardSprite spriteToRemove = null;
						for (CardSprite sprite : spriteList) {
							if (sprite.getCard().equals(removedCard)) {
								spriteToRemove = sprite;
							}
						}
						spriteList.remove(spriteToRemove);
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

	private StringExpression createPlayerTabBinding(IsPlayer player, ZoneType zoneType) {
		if (zoneType.equals(ZoneType.HAND)) {
			return Bindings.concat("(").concat(player.propertyHandSize().asString()).concat(") ")
					.concat(player.getDisplayName());
		} else if (zoneType.equals(ZoneType.GRAVEYARD)) {
			return Bindings.concat("(").concat(player.propertyGraveSize().asString()).concat(") ")
					.concat(player.getDisplayName());
		} else {
			throw new IllegalArgumentException();
		}
	}

	private StringExpression createMatchTabBinding(ZoneType zoneType) {
		if (zoneType.equals(ZoneType.BATTLEFIELD)) {
			return Bindings.concat("(").concat(matchActive.propertyBattlefieldSize().asString())
					.concat(") Battlefield");
		} else if (zoneType.equals(ZoneType.EXILE)) {
			return Bindings.concat("(").concat(matchActive.propertyExileSize().asString()).concat(") Exile");
		} else {
			throw new IllegalArgumentException();
		}
	}

	private void setIconLabel(Image icon, Label label) {
		label.setGraphic(new AdaptableImageView(icon, label.heightProperty(), label.widthProperty()));
	}

	private void setMatchActive(Match matchActive) {
		this.matchActive = matchActive;
	}

}
