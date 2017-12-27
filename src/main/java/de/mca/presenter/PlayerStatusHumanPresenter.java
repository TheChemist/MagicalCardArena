package de.mca.presenter;

import java.net.URL;
import java.util.ResourceBundle;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import de.mca.io.ResourceManager;
import de.mca.model.InputHuman;
import de.mca.model.enums.ColorType;
import de.mca.model.interfaces.IsPlayer;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * 
 * @author Maximilian Werling
 *
 */
public class PlayerStatusHumanPresenter implements Initializable {

	private final EventBus eventBus = new EventBus();
	private InputHuman input;
	@FXML
	private Label labelAvatar;
	@FXML
	private Label labelBlackIcon;
	@FXML
	private Label labelBlackValue;
	@FXML
	private Label labelBlueIcon;
	@FXML
	private Label labelBlueValue;
	@FXML
	private Label labelColorlessIcon;
	@FXML
	private Label LabelColorlessValue;
	@FXML
	private Label labelGreenIcon;
	@FXML
	private Label labelGreenValue;
	@FXML
	private Label labelLifeIcon;
	@FXML
	private Label labelLifeValue;
	@FXML
	private Label labelRedIcon;
	@FXML
	private Label labelRedValue;
	@FXML
	private Label labelWhiteIcon;
	@FXML
	private Label labelWhiteValue;
	private ImageView loadingIcon;
	private MatchPresenter parent;
	private IsPlayer player;
	@FXML
	private Button progressButton;

	@Subscribe
	public void examineButtonChange(GameStatusChange progressNameChange) {
		if (progressNameChange.getProgressButtonText().equals("concede")) {
			parent.stopMatch();
			return;
		}

		if (progressNameChange.getDisableProgressButton()) {
			progressButton.setText("");
			progressButton.setGraphic(loadingIcon);
			progressButton.setDisable(true);
		} else {
			progressButton.setText(progressNameChange.getProgressButtonText());
			progressButton.setGraphic(null);
			progressButton.setDisable(false);
		}
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		getEventBus().register(this);

		loadingIcon = new AdaptableImageView(ResourceManager.getIcon("busy.gif"), new SimpleDoubleProperty(32.0),
				new SimpleDoubleProperty(32.0));
		progressButton.setGraphic(loadingIcon);
		progressButton.setDisable(true);
		progressButton.setOnAction(actionEvent -> {
			input.progress();
		});

		initializeIconLabel(ResourceManager.getIcon("heart.png"), labelLifeIcon);

		initializeIconLabel(ResourceManager.getIcon("b.png"), labelBlackIcon);
		initializeIconLabel(ResourceManager.getIcon("u.png"), labelBlueIcon);
		initializeIconLabel(ResourceManager.getIcon("1.png"), labelColorlessIcon);
		initializeIconLabel(ResourceManager.getIcon("g.png"), labelGreenIcon);
		initializeIconLabel(ResourceManager.getIcon("r.png"), labelRedIcon);
		initializeIconLabel(ResourceManager.getIcon("w.png"), labelWhiteIcon);
	}

	public void injectPlayerData(MatchPresenter parent, IsPlayer player, InputHuman input, Image avatar) {
		this.parent = parent;
		this.input = input;
		this.player = player;

		labelLifeValue.textProperty().bind(player.propertyLife().asString());

		bindManaPool();
		initializeIconLabel(avatar, labelAvatar);
	}

	private void bindManaPool() {
		player.getManaPool().propertyMapMana().addListener(new MapChangeListener<ColorType, Integer>() {

			@Override
			public void onChanged(
					javafx.collections.MapChangeListener.Change<? extends ColorType, ? extends Integer> change) {
				ColorType colorType = change.getKey();
				String stringValue = change.getValueAdded() == null ? "0" : change.getValueAdded().toString();

				if (colorType.equals(ColorType.BLACK)) {
					labelBlackValue.setText(stringValue);
				} else if (change.getKey().equals(ColorType.BLUE)) {
					labelBlueValue.setText(stringValue);
				} else if (change.getKey().equals(ColorType.GREEN)) {
					labelGreenValue.setText(stringValue);
				} else if (change.getKey().equals(ColorType.NONE)) {
					LabelColorlessValue.setText(stringValue);
				} else if (change.getKey().equals(ColorType.RED)) {
					labelRedValue.setText(stringValue);
				} else if (change.getKey().equals(ColorType.WHITE)) {
					labelWhiteValue.setText(stringValue);
				}
			}
		});
	}

	private void initializeIconLabel(Image icon, Label label) {
		label.setGraphic(new AdaptableImageView(icon, label.heightProperty(), label.widthProperty()));
	}

	EventBus getEventBus() {
		return eventBus;
	}

}
