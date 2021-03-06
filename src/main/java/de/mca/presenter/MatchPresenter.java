package de.mca.presenter;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mca.Constants;
import de.mca.MagicParser;
import de.mca.Main;
import de.mca.io.FileManager;
import de.mca.io.ResourceManager;
import de.mca.model.InputComputer;
import de.mca.model.InputHuman;
import de.mca.model.MagicCard;
import de.mca.model.MagicPermanent;
import de.mca.model.MagicStack;
import de.mca.model.Match;
import de.mca.model.Player;
import de.mca.model.RuleEnforcer;
import de.mca.model.enums.ZoneType;
import de.mca.model.interfaces.IsPlayer;
import de.mca.model.interfaces.IsStackable;
import de.mca.model.interfaces.IsZone;
import javafx.animation.AnimationTimer;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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

	/**
	 * Speichert den Logger.
	 */
	private final static Logger LOGGER = LoggerFactory.getLogger("MatchPresenter");
	private CanvasZoneBattlefield canvasBattlefield;
	private CanvasZoneDefault canvasComputerGraveyard;
	private CanvasZoneDefault canvasComputerHand;
	private CanvasZoneDefault canvasExile;
	private CanvasZoneDefault canvasHumanGraveyard;
	private CanvasZoneDefault canvasHumanHand;
	private CanvasZoneStack canvasStack;
	@FXML
	private Label fpsLabel;
	private Consumer<Integer> fpsReporter;
	private int framesSinceLastFpsUpdate = 0;
	@FXML
	private GridPane gridPaneCenter;
	private ImageView imageViewCardZoom;
	@FXML
	private Label labelCurrentPhase;
	@FXML
	private Label labelCurrentStep;
	@FXML
	private Label labelHint;
	@FXML
	private Label labelPlayerActive;
	@FXML
	private Label labelTurnNumber;
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
	private AnchorPane panePlayerStatusOne;
	@FXML
	private AnchorPane panePlayerStatusTwo;
	@FXML
	private AnchorPane paneRight;
	@FXML
	private AnchorPane paneStack;
	private PlayerStatusComputerPresenter playerStatusComputerOne;
	private PlayerStatusComputerPresenter playerStatusComputerTwo;
	private PlayerStatusHumanPresenter playerStatusHuman;
	private long previousTime = 0;
	private Runnable rendererBattlefield;
	private Runnable rendererComputerGraveyard;
	private Runnable rendererComputerHand;
	private Runnable rendererExile;
	private Runnable rendererHumanGraveyard;
	private Runnable rendererHumanHand;
	private Runnable rendererStack;
	private Main screenController;
	private float secondsElapsedSinceLastFpsUpdate = 0f;
	private float secondsElapsedSinceUpdate;
	private final List<SpriteMagicPermanent> spriteListBattlefield;
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

	public MatchPresenter() {
		// TODO LOW Threading?

		spriteListBattlefield = new ArrayList<>();
		spriteListComputerGraveyard = new ArrayList<>();
		spriteListComputerHand = new ArrayList<>();
		spriteListExile = new ArrayList<>();
		spriteListHumanGraveyard = new ArrayList<>();
		spriteListHumanHand = new ArrayList<>();
		spriteListStack = new ArrayList<>();
	}

	@FXML
	public void concede() {
		stopMatch();
	}

	public Match getMatchActive() {
		return matchActive;
	}

	/**
	 * @see http://svanimpe.be/blog/game-loops-fx.html
	 */
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

		// Update das Match jede Sekunde
		if (secondsElapsedSinceUpdate >= 0.1f) {
			matchUpdater.run();
			secondsElapsedSinceUpdate = 0;
		}

		// Die Renderer
		rendererBattlefield.run();
		rendererComputerGraveyard.run();
		rendererComputerHand.run();
		rendererExile.run();
		rendererHumanGraveyard.run();
		rendererHumanHand.run();
		rendererStack.run();

		secondsElapsedSinceLastFpsUpdate += secondsElapsed;
		secondsElapsedSinceUpdate += secondsElapsed;
		framesSinceLastFpsUpdate++;

		// Update FPS-Anzeige alle halbe Sekunde
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
		canvasStack = new CanvasZoneStack(paneStack);
		paneStack.getChildren().add(canvasStack);
		tabStack.setContent(paneStack);

		imageViewCardZoom = new AdaptableImageView(null, paneCardZoom.heightProperty(), paneCardZoom.widthProperty());
		AnchorPane.setLeftAnchor(imageViewCardZoom, 5.0);
		AnchorPane.setRightAnchor(imageViewCardZoom, 5.0);
		paneCardZoom.getChildren().add(imageViewCardZoom);
		tabCardZoom.setContent(paneCardZoom);

		PlayerStatusHumanView playerStatusHumanView = new PlayerStatusHumanView();
		playerStatusHuman = (PlayerStatusHumanPresenter) playerStatusHumanView.getPresenter();

		if (Constants.AI_ONLY) {
			PlayerStatusComputerView playerStatusComputerViewOne = new PlayerStatusComputerView();
			playerStatusComputerOne = (PlayerStatusComputerPresenter) playerStatusComputerViewOne.getPresenter();
			panePlayerStatusOne.getChildren().add(playerStatusComputerViewOne.getView());

			PlayerStatusComputerView playerStatusComputerViewTwo = new PlayerStatusComputerView();
			playerStatusComputerTwo = (PlayerStatusComputerPresenter) playerStatusComputerViewTwo.getPresenter();
			panePlayerStatusTwo.getChildren().add(playerStatusComputerViewTwo.getView());
		} else {
			PlayerStatusComputerView playerStatusComputerView = new PlayerStatusComputerView();
			playerStatusComputerOne = (PlayerStatusComputerPresenter) playerStatusComputerView.getPresenter();
			panePlayerStatusOne.getChildren().add(playerStatusComputerView.getView());

			panePlayerStatusTwo.getChildren().add(playerStatusHumanView.getView());
		}

		// Center Pane
		canvasBattlefield = new CanvasZoneBattlefield(paneBattlefield, imageViewCardZoom);
		paneBattlefield.getChildren().add(canvasBattlefield);
		tabBattlefield.setContent(paneBattlefield);

		canvasComputerGraveyard = new CanvasZoneDefault(paneComputerGraveyard, ZoneType.GRAVEYARD, imageViewCardZoom);
		paneComputerGraveyard.getChildren().add(canvasComputerGraveyard);
		tabComputerGraveyard.setContent(paneComputerGraveyard);
		tabComputerGraveyard.setGraphic(new AdaptableImageView(ResourceManager.getIcon("grave.png"),
				new SimpleDoubleProperty(16.0), new SimpleDoubleProperty(16.0)));

		canvasComputerHand = new CanvasZoneDefault(paneComputerHand, ZoneType.HAND, imageViewCardZoom);
		paneComputerHand.getChildren().add(canvasComputerHand);
		tabComputerHand.setContent(paneComputerHand);
		tabComputerHand.setGraphic(new AdaptableImageView(ResourceManager.getIcon("hand.png"),
				new SimpleDoubleProperty(16.0), new SimpleDoubleProperty(16.0)));

		canvasExile = new CanvasZoneDefault(paneExile, ZoneType.EXILE, imageViewCardZoom);
		paneExile.getChildren().add(canvasExile);
		tabExile.setContent(paneExile);

		canvasHumanGraveyard = new CanvasZoneDefault(paneHumanGraveyard, ZoneType.GRAVEYARD, imageViewCardZoom);
		paneHumanGraveyard.getChildren().add(canvasHumanGraveyard);
		tabHumanGraveyard.setContent(paneHumanGraveyard);
		tabHumanGraveyard.setGraphic(new AdaptableImageView(ResourceManager.getIcon("grave.png"),
				new SimpleDoubleProperty(16.0), new SimpleDoubleProperty(16.0)));

		canvasHumanHand = new CanvasZoneDefault(paneHumanHand, ZoneType.HAND, imageViewCardZoom);
		paneHumanHand.getChildren().add(canvasHumanHand);
		tabHumanHand.setContent(paneHumanHand);
		tabHumanHand.setGraphic(new AdaptableImageView(ResourceManager.getIcon("hand.png"),
				new SimpleDoubleProperty(16.0), new SimpleDoubleProperty(16.0)));

		if (Constants.DEBUG) {
			startMatch();
		}
	}

	@Override
	public void setScreenController(Main screenController) {
		this.screenController = screenController;
	}

	@FXML
	public void startMatch() {
		LOGGER.debug("{} startMatch()", this);
		if (getMatchActive() != null) {
			stopMatch();
		}

		RuleEnforcer ruleEnforcer = new RuleEnforcer(playerStatusHuman.getEventBus());
		InputHuman inputHuman = null;

		String deckComputer = "vanillaartifact.json";
		String deckHuman = "vanillaartifact.json";
		Image avatarComputer = FileManager.getAvatarImages().get(0);
		Image avatarHuman = FileManager.getAvatarImages().get(1);

		IsPlayer playerOne = null;
		IsPlayer playerTwo = null;

		if (Constants.AI_ONLY) {
			playerOne = new Player(ruleEnforcer, "AI_ONE",
					MagicParser.parseDeckFromPath(FileManager.getDeckPath(deckComputer)));
			playerTwo = new Player(ruleEnforcer, "AI_TWO",
					MagicParser.parseDeckFromPath(FileManager.getDeckPath(deckHuman)));

			matchActive = new Match(ruleEnforcer, playerOne, playerTwo);

			playerStatusComputerOne.injectPlayerData(playerOne, new InputComputer(this, matchActive, playerOne),
					avatarComputer);

			playerStatusComputerTwo.injectPlayerData(playerTwo, new InputComputer(this, matchActive, playerTwo),
					avatarHuman);
		} else {
			playerOne = new Player(ruleEnforcer, "Computer",
					MagicParser.parseDeckFromPath(FileManager.getDeckPath(deckComputer)));
			playerTwo = new Player(ruleEnforcer, "Human",
					MagicParser.parseDeckFromPath(FileManager.getDeckPath(deckHuman)));

			matchActive = new Match(ruleEnforcer, playerOne, playerTwo);

			playerStatusComputerOne.injectPlayerData(playerOne, new InputComputer(this, matchActive, playerOne),
					avatarComputer);

			inputHuman = new InputHuman(this, matchActive, playerTwo);
			playerStatusHuman.injectPlayerData(this, playerTwo, inputHuman, avatarHuman);
		}

		// Erstelle Game Loop
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
		 * ListSprites
		 */
		canvasBattlefield.setListSprites(spriteListBattlefield);
		canvasBattlefield.setInput(inputHuman);
		canvasBattlefield.setMatchActive(matchActive);

		canvasComputerGraveyard.setListSprites(spriteListComputerGraveyard);
		canvasComputerGraveyard.setInput(inputHuman);

		canvasComputerHand.setListSprites(spriteListComputerHand);
		canvasComputerHand.setInput(inputHuman);

		canvasExile.setListSprites(spriteListExile);
		canvasExile.setInput(inputHuman);

		canvasHumanGraveyard.setListSprites(spriteListHumanGraveyard);
		canvasHumanGraveyard.setInput(inputHuman);

		canvasHumanHand.setListSprites(spriteListHumanHand);
		canvasHumanHand.setInput(inputHuman);

		canvasStack.setListSprites(spriteListStack);

		/**
		 * Linkes Panel
		 */
		// Elemente erstellen

		// Elemente binden
		bindSizeTab(tabStack, null, ZoneType.STACK);
		bindZoneStack(matchActive.getZoneStack(), spriteListStack);

		/**
		 * Mittleres Panel
		 */
		// Elemente binden
		bindSizeTab(tabBattlefield, null, ZoneType.BATTLEFIELD);
		bindZoneBattlefield(matchActive.getZoneBattlefield(), spriteListBattlefield);

		bindSizeTab(tabComputerGraveyard, playerOne, ZoneType.GRAVEYARD);
		bindSizeTab(tabComputerHand, playerOne, ZoneType.HAND);
		bindZoneDefault(playerOne.getZoneGraveyard(), spriteListComputerGraveyard);
		bindZoneDefault(playerOne.getZoneHand(), spriteListComputerHand);

		bindSizeTab(tabExile, null, ZoneType.EXILE);
		bindZoneDefault(matchActive.getZoneExile(), spriteListExile);

		bindSizeTab(tabHumanGraveyard, playerTwo, ZoneType.GRAVEYARD);
		bindSizeTab(tabHumanHand, playerTwo, ZoneType.HAND);
		bindZoneDefault(playerTwo.getZoneGraveyard(), spriteListHumanGraveyard);
		bindZoneDefault(playerTwo.getZoneHand(), spriteListHumanHand);

		/**
		 * Unteres Panel
		 */
		// Elemente binden
		labelTurnNumber.textProperty().bind(matchActive.propertyTurnNumber().asString());
		labelCurrentPhase.textProperty().bind(matchActive.propertyCurrentPhase().asString());
		labelCurrentStep.textProperty().bind(matchActive.propertyCurrentStep().asString());
		labelPlayerActive.textProperty().bind(matchActive.propertyPlayerActive().asString());
		labelHint.textProperty().bind(playerTwo.propertyPlayerState().asString());

		// Starten
		this.start();
	}

	public void stopMatch() {
		LOGGER.debug("{} stopMatch()", this);
		if (getMatchActive() != null) {
			matchActive = null;
		}

		spriteListBattlefield.clear();
		spriteListComputerGraveyard.clear();
		spriteListComputerHand.clear();
		spriteListExile.clear();
		spriteListHumanGraveyard.clear();
		spriteListHumanHand.clear();
		spriteListStack.clear();

		canvasBattlefield.draw();
		canvasComputerGraveyard.draw();
		canvasComputerHand.draw();
		canvasExile.draw();
		canvasHumanGraveyard.draw();
		canvasHumanHand.draw();
		canvasStack.draw();

		matchUpdater = null;
		previousTime = 0;
		secondsElapsedSinceLastFpsUpdate = 0f;
		framesSinceLastFpsUpdate = 0;

		super.stop();
	}

	@Override
	public String toString() {
		return "MatchPresenter";
	}

	private void bindSizeTab(Tab sizeTab, IsPlayer isPlayer, ZoneType zoneType) {
		sizeTab.textProperty().bind(createTabBinding(isPlayer, zoneType));
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

	private StringExpression createTabBinding(IsPlayer player, ZoneType zoneType) {
		if (zoneType.equals(ZoneType.BATTLEFIELD)) {
			return Bindings.concat("(").concat(matchActive.propertyBattlefieldSize().asString())
					.concat(") Battlefield");
		} else if (zoneType.equals(ZoneType.EXILE)) {
			return Bindings.concat("(").concat(matchActive.propertyExileSize().asString()).concat(") Exile");
		} else if (zoneType.equals(ZoneType.STACK)) {
			return Bindings.concat("(").concat(matchActive.propertyStackSize().asString()).concat(") Stack");
		} else if (zoneType.equals(ZoneType.HAND)) {
			return Bindings.concat("(").concat(player.propertyHandSize().asString()).concat(") ")
					.concat(player.getDisplayName());
		} else if (zoneType.equals(ZoneType.GRAVEYARD)) {
			return Bindings.concat("(").concat(player.propertyGraveSize().asString()).concat(") ")
					.concat(player.getDisplayName());
		} else {
			throw new IllegalArgumentException();
		}
	}

}
