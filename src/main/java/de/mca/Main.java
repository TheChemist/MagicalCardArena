package de.mca;

import java.util.HashMap;

import com.airhacks.afterburner.injection.Injector;
import com.airhacks.afterburner.views.FXMLView;
import com.google.inject.Guice;

import de.mca.io.FileManager;
import de.mca.io.ResourceManager;
import de.mca.presenter.IsStackableScreen;
import de.mca.presenter.MatchView;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author Maximilian Werling
 *
 */
public class Main extends Application {

	public class ScreenStacker extends StackPane {

		public ScreenStacker() {
			super();
		}

		// This method tries to displayed the screen with a predefined name.
		// First it makes sure the screen has been already loaded. Then if there
		// is more than one screen the new screen is been added second, and then
		// the current screen is removed. If there isn't any screen being
		// displayed, the new screen is just added to the root.
		public void setScreen(final String name) {
			final DoubleProperty opacity = opacityProperty();

			Node screen = getScreen(name);
			AnchorPane.setTopAnchor(screen, 0.0);
			AnchorPane.setRightAnchor(screen, 0.0);
			AnchorPane.setBottomAnchor(screen, 0.0);
			AnchorPane.setLeftAnchor(screen, 0.0);
			if (!getChildren().isEmpty()) { // if there is more than one screen
				Timeline fade = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(opacity, 1.0)),
						new KeyFrame(new Duration(1000), new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent t) {
								// remove the displayed screen
								getChildren().remove(0);
								// add the screen
								getChildren().add(0, screen);
								Timeline fadeIn = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)),
										new KeyFrame(new Duration(1000), new KeyValue(opacity, 1.0)));
								fadeIn.play();
							}
						}, new KeyValue(opacity, 0.0)));
				fade.play();

			} else {
				setOpacity(0.0);
				// no one else been displayed, then just show
				getChildren().add(screen);
				Timeline fadeIn = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)),
						new KeyFrame(new Duration(1000), new KeyValue(opacity, 1.0)));
				fadeIn.play();
			}
		}
	}

	public static void main(String[] args) {
		System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "DEBUG");

		ResourceManager.loadResourceFile();
		FileManager.loadDeckData();
		FileManager.loadCardData();
		FileManager.loadAvatarImages();

		launch(args);
	}

	private ScreenStacker sceneStacker = null;

	private HashMap<String, FXMLView> screens = new HashMap<>();

	public void addScreen(String name, FXMLView screen) {
		((IsStackableScreen) screen.getPresenter()).setScreenController(this);
		screens.put(name, screen);
	}

	public Node getScreen(String name) {
		return screens.get(name).getView();
	}

	public void setScreen(String name) {
		sceneStacker.setScreen(name);
	}

	@Override
	public void start(Stage primaryStage) {
		final com.google.inject.Injector injector = Guice.createInjector(new MainModule());
		Injector.setInstanceSupplier(new Injector.InstanceProvider() {

			@Override
			public Object instantiate(Class<?> c) {
				return injector.getInstance(c);
			}

			@Override
			public boolean isInjectionAware() {
				return true;
			}

			@Override
			public boolean isScopeAware() {
				return true;
			}
		});

		addScreen("main", new MainView());
		addScreen("match", new MatchView());

		sceneStacker = new ScreenStacker();
		sceneStacker.setScreen("main");

		final Scene scene = new Scene(sceneStacker);

		primaryStage.setTitle("MCA - Magical Card Arena");
		primaryStage.setScene(scene);
		primaryStage.centerOnScreen();
		primaryStage.setMinWidth(1280.0);
		primaryStage.setMinHeight(720.0);
		primaryStage.show();
	}

	@Override
	public void stop() throws Exception {
		Injector.forgetAll();
		// TODO: Auskommentiert lassen, verhindert Fehleranzeige
		// System.exit(0);
	}
}
