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
import de.mca.model.enums.ColorType;
import de.mca.model.enums.PlayerType;
import de.mca.model.enums.ZoneType;
import de.mca.model.interfaces.IsMatch;
import de.mca.model.interfaces.IsObject;
import de.mca.model.interfaces.IsPlayer;
import de.mca.model.interfaces.IsZone;
import javafx.animation.AnimationTimer;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.DoubleExpression;
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

	class AdaptableImageView extends ImageView {

		public AdaptableImageView(Image image, DoubleExpression heightProperty, DoubleExpression widthProperty) {
			super(image);

			fitHeightProperty().bind(heightProperty);
			fitWidthProperty().bind(widthProperty);
			setPreserveRatio(true);
		}

	}

	class ResizableCanvas extends Canvas {

		private List<Sprite> spriteList;

		public ResizableCanvas(Pane parent, InputHuman input, List<Sprite> spriteList, ZoneType zoneType,
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
					Sprite mouseOverSprite = null;
					for (Sprite sprite : spriteList) {
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
				Sprite sprite = spriteList.get(i);
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

		private IsObject determineClickedCard(MouseEvent e, List<Sprite> listSprites) {
			int zCoordinate = -1;
			List<MagicCard> listClicked = new ArrayList<>();
			for (Sprite sprite : listSprites) {
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
	private ResizableCanvas canvasBattlefield;
	private ResizableCanvas canvasComputerGraveyard;
	private ResizableCanvas canvasComputerHand;
	private ResizableCanvas canvasExile;
	private ResizableCanvas canvasHumanGraveyard;
	private ResizableCanvas canvasHumanHand;
	@FXML
	private Label labelTurnNumber;
	@FXML
	private Label labelCurrentPhase;
	@FXML
	private Label labelCurrentStep;
	@FXML
	private Label labelPlayerActive;
	@FXML
	private Label labelPlayerPrioritized;
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
	private Label labelComputerLife;
	@FXML
	private Label labelHumanAvatar;
	@FXML
	private Label labelHumanLife;
	@FXML
	private Label labelIconComputerLife;
	@FXML
	private Label labelIconHumanLife;
	@FXML
	private Label labelIconHumanBlueMana;
	@FXML
	private Label labelIconHumanBlackMana;
	@FXML
	private Label labelIconHumanWhiteMana;
	@FXML
	private Label labelIconHumanRedMana;
	@FXML
	private Label labelIconHumanGreenMana;
	@FXML
	private Label labelIconHumanColorlessMana;
	@FXML
	private Label labelIconComputerBlueMana;
	@FXML
	private Label labelIconComputerBlackMana;
	@FXML
	private Label labelIconComputerWhiteMana;
	@FXML
	private Label labelIconComputerRedMana;
	@FXML
	private Label labelIconComputerGreenMana;
	@FXML
	private Label labelIconComputerColorlessMana;
	@FXML
	private Label labelHumanBlueMana;
	@FXML
	private Label labelHumanBlackMana;
	@FXML
	private Label labelHumanWhiteMana;
	@FXML
	private Label labelHumanRedMana;
	@FXML
	private Label labelHumanGreenMana;
	@FXML
	private Label labelHumanColorlessMana;
	@FXML
	private Label labelComputerBlueMana;
	@FXML
	private Label labelComputerBlackMana;
	@FXML
	private Label labelComputerWhiteMana;
	@FXML
	private Label labelComputerRedMana;
	@FXML
	private Label labelComputerGreenMana;
	@FXML
	private Label labelComputerColorlessMana;
	@Inject
	private MagicParser magicParser;
	private IsMatch matchActive;
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
	private Main screenController;
	private float secondsElapsedSinceLastFpsUpdate = 0f;
	private List<Sprite> spriteListBattlefield;
	private List<Sprite> spriteListComputerGraveyard;
	private List<Sprite> spriteListComputerHand;
	private List<Sprite> spriteListExile;
	private List<Sprite> spriteListHumanGraveyard;
	private List<Sprite> spriteListHumanHand;
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
		canvasBattlefield = new ResizableCanvas(paneBattlefield, inputHuman, spriteListBattlefield,
				ZoneType.BATTLEFIELD, imageViewCardZoom);
		paneBattlefield.getChildren().add(canvasBattlefield);
		tabBattlefield.setContent(paneBattlefield);

		canvasComputerGraveyard = new ResizableCanvas(paneComputerGraveyard, inputHuman, spriteListComputerGraveyard,
				ZoneType.GRAVEYARD, imageViewCardZoom);
		paneComputerGraveyard.getChildren().add(canvasComputerGraveyard);
		tabComputerGraveyard.setContent(paneComputerGraveyard);
		tabComputerGraveyard.setGraphic(new AdaptableImageView(ResourceManager.getIcon("grave.png"),
				new SimpleDoubleProperty(16.0), new SimpleDoubleProperty(16.0)));

		canvasComputerHand = new ResizableCanvas(paneComputerHand, inputHuman, spriteListComputerHand, ZoneType.HAND,
				imageViewCardZoom);
		paneComputerHand.getChildren().add(canvasComputerHand);
		tabComputerHand.setContent(paneComputerHand);
		tabComputerHand.setGraphic(new AdaptableImageView(ResourceManager.getIcon("hand.png"),
				new SimpleDoubleProperty(16.0), new SimpleDoubleProperty(16.0)));

		canvasExile = new ResizableCanvas(paneExile, inputHuman, spriteListExile, ZoneType.EXILE, imageViewCardZoom);
		paneExile.getChildren().add(canvasExile);
		tabExile.setContent(paneExile);

		canvasHumanGraveyard = new ResizableCanvas(paneHumanGraveyard, inputHuman, spriteListHumanGraveyard,
				ZoneType.GRAVEYARD, imageViewCardZoom);
		paneHumanGraveyard.getChildren().add(canvasHumanGraveyard);
		tabHumanGraveyard.setContent(paneComputerGraveyard);
		tabHumanGraveyard.setGraphic(new AdaptableImageView(ResourceManager.getIcon("grave.png"),
				new SimpleDoubleProperty(16.0), new SimpleDoubleProperty(16.0)));

		canvasHumanHand = new ResizableCanvas(paneHumanHand, inputHuman, spriteListHumanHand, ZoneType.HAND,
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
		IsMatch matchActive = createMatch(nameComputer, nameHuman, deckHuman, deckComputer, avatarComputer,
				avatarHuman);
		IsPlayer playerComputer = matchActive.getPlayerComputer();
		IsPlayer playerHuman = matchActive.getPlayerHuman();

		// Game Loop
		matchUpdater = () -> matchActive.update();
		rendererBattlefield = () -> canvasBattlefield.draw();
		rendererComputerGraveyard = () -> canvasComputerGraveyard.draw();
		rendererComputerHand = () -> canvasComputerHand.draw();
		rendererExile = () -> canvasExile.draw();
		rendererHumanGraveyard = () -> canvasHumanGraveyard.draw();
		rendererHumanHand = () -> canvasHumanHand.draw();
		fpsReporter = fps -> fpsLabel.setText(String.format("FPS: %d", fps));

		// Binden der Status-Label der Spieler an entsprechende Werte
		labelComputerAvatar.setGraphic(new AdaptableImageView(avatarComputer, labelComputerAvatar.heightProperty(),
				labelComputerAvatar.widthProperty()));
		labelIconComputerLife.setGraphic(new AdaptableImageView(ResourceManager.getIcon("heart.png"),
				labelIconComputerLife.heightProperty(), labelIconComputerLife.widthProperty()));

		labelIconComputerBlackMana.setGraphic(new AdaptableImageView(ResourceManager.getIcon("b.png"),
				labelIconComputerBlackMana.heightProperty(), labelIconComputerBlackMana.widthProperty()));
		labelIconComputerBlueMana.setGraphic(new AdaptableImageView(ResourceManager.getIcon("u.png"),
				labelIconComputerBlueMana.heightProperty(), labelIconComputerBlueMana.widthProperty()));
		labelIconComputerColorlessMana.setGraphic(new AdaptableImageView(ResourceManager.getIcon("1.png"),
				labelIconComputerColorlessMana.heightProperty(), labelIconComputerColorlessMana.widthProperty()));
		labelIconComputerGreenMana.setGraphic(new AdaptableImageView(ResourceManager.getIcon("g.png"),
				labelIconComputerGreenMana.heightProperty(), labelIconComputerGreenMana.widthProperty()));
		labelIconComputerRedMana.setGraphic(new AdaptableImageView(ResourceManager.getIcon("r.png"),
				labelIconComputerRedMana.heightProperty(), labelIconComputerRedMana.widthProperty()));
		labelIconComputerWhiteMana.setGraphic(new AdaptableImageView(ResourceManager.getIcon("w.png"),
				labelIconComputerWhiteMana.heightProperty(), labelIconComputerWhiteMana.widthProperty()));

		labelComputerLife.textProperty().bind(playerComputer.propertyLife().asString());

		labelHumanAvatar.setGraphic(new AdaptableImageView(avatarHuman, labelHumanAvatar.heightProperty(),
				labelHumanAvatar.widthProperty()));

		labelIconHumanBlackMana.setGraphic(new AdaptableImageView(ResourceManager.getIcon("b.png"),
				labelIconHumanBlackMana.heightProperty(), labelIconHumanBlackMana.widthProperty()));
		labelIconHumanBlueMana.setGraphic(new AdaptableImageView(ResourceManager.getIcon("u.png"),
				labelIconHumanBlueMana.heightProperty(), labelIconHumanBlueMana.widthProperty()));
		labelIconHumanColorlessMana.setGraphic(new AdaptableImageView(ResourceManager.getIcon("1.png"),
				labelIconHumanColorlessMana.heightProperty(), labelIconHumanColorlessMana.widthProperty()));
		labelIconHumanGreenMana.setGraphic(new AdaptableImageView(ResourceManager.getIcon("g.png"),
				labelIconHumanGreenMana.heightProperty(), labelIconHumanGreenMana.widthProperty()));
		labelIconHumanRedMana.setGraphic(new AdaptableImageView(ResourceManager.getIcon("r.png"),
				labelIconHumanRedMana.heightProperty(), labelIconHumanRedMana.widthProperty()));
		labelIconHumanWhiteMana.setGraphic(new AdaptableImageView(ResourceManager.getIcon("w.png"),
				labelIconHumanWhiteMana.heightProperty(), labelIconHumanWhiteMana.widthProperty()));

		labelIconHumanLife.setGraphic(new AdaptableImageView(ResourceManager.getIcon("heart.png"),
				labelIconHumanLife.heightProperty(), labelIconHumanLife.widthProperty()));

		labelHumanLife.textProperty().bind(playerHuman.propertyLife().asString());

		// Binden der Zonen an GUI-Elemente
		bindZone(matchActive.getZoneBattlefield(), spriteListBattlefield);
		bindZone(playerComputer.getZoneGraveyard(), spriteListComputerGraveyard);
		bindZone(playerComputer.getZoneHand(), spriteListComputerHand);
		bindZone(matchActive.getZoneExile(), spriteListExile);
		bindZone(playerHuman.getZoneGraveyard(), spriteListHumanGraveyard);
		bindZone(playerHuman.getZoneHand(), spriteListHumanHand);
		playerComputer.getManaPool().propertyManaMap().addListener(new MapChangeListener<ColorType, Integer>() {

			@Override
			public void onChanged(
					javafx.collections.MapChangeListener.Change<? extends ColorType, ? extends Integer> change) {
				if (change.getKey().equals(ColorType.BLACK)) {
					labelComputerBlackMana.setText(change.getMap().get(ColorType.BLACK).toString());
				} else if (change.getKey().equals(ColorType.BLUE)) {
					labelComputerBlueMana.setText(change.getMap().get(ColorType.BLUE).toString());
				} else if (change.getKey().equals(ColorType.GREEN)) {
					labelComputerGreenMana.setText(change.getMap().get(ColorType.GREEN).toString());
				} else if (change.getKey().equals(ColorType.NONE)) {
					labelComputerColorlessMana.setText(change.getMap().get(ColorType.NONE).toString());
				} else if (change.getKey().equals(ColorType.RED)) {
					labelComputerRedMana.setText(change.getMap().get(ColorType.RED).toString());
				} else if (change.getKey().equals(ColorType.WHITE)) {
					labelComputerWhiteMana.setText(change.getMap().get(ColorType.WHITE).toString());
				}
			}
		});
		playerHuman.getManaPool().propertyManaMap().addListener(new MapChangeListener<ColorType, Integer>() {

			@Override
			public void onChanged(
					javafx.collections.MapChangeListener.Change<? extends ColorType, ? extends Integer> change) {
				if (change.getKey().equals(ColorType.BLACK)) {
					labelComputerBlackMana.setText(change.getMap().get(ColorType.BLACK).toString());
				} else if (change.getKey().equals(ColorType.BLUE)) {
					labelComputerBlueMana.setText(change.getMap().get(ColorType.BLUE).toString());
				} else if (change.getKey().equals(ColorType.GREEN)) {
					labelComputerGreenMana.setText(change.getMap().get(ColorType.GREEN).toString());
				} else if (change.getKey().equals(ColorType.NONE)) {
					labelComputerColorlessMana.setText(change.getMap().get(ColorType.NONE).toString());
				} else if (change.getKey().equals(ColorType.RED)) {
					labelComputerRedMana.setText(change.getMap().get(ColorType.RED).toString());
				} else if (change.getKey().equals(ColorType.WHITE)) {
					labelComputerWhiteMana.setText(change.getMap().get(ColorType.WHITE).toString());
				}
			}

		});

		tabComputerGraveyard.textProperty()
				.bind(Bindings.concat("(").concat(playerComputer.propertyGraveSize().asString()).concat(") ")
						.concat(playerComputer.getDisplayName()));

		tabComputerHand.textProperty().bind(Bindings.concat("(").concat(playerComputer.propertyHandSize().asString())
				.concat(") ").concat(playerComputer.getDisplayName()));

		tabHumanGraveyard.textProperty().bind(Bindings.concat("(").concat(playerHuman.propertyGraveSize().asString())
				.concat(") ").concat(playerHuman.getDisplayName()));

		tabHumanHand.textProperty().bind(Bindings.concat("(").concat(playerHuman.propertyHandSize().asString())
				.concat(") ").concat(playerHuman.getDisplayName()));

		// Bottom Pane
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

	private void bindZone(IsZone<? extends MagicCard> zone, List<Sprite> spriteList) {
		zone.propertyListZoneCards().addListener((ListChangeListener.Change<? extends MagicCard> c) -> {
			while (c.next()) {
				if (c.wasAdded()) {
					c.getAddedSubList().forEach(addedCard -> {
						spriteList.add(new Sprite(addedCard));
					});
				} else if (c.wasRemoved()) {
					c.getRemoved().forEach(removedCard -> {
						Sprite spriteToRemove = null;
						for (Sprite sprite : spriteList) {
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

	private IsMatch createMatch(String nameComputer, String nameHuman, String deckComputer, String deckHuman,
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

	private void setMatchActive(IsMatch matchActive) {
		this.matchActive = matchActive;
	}

}
