package de.mca.presenter;

import java.net.URL;
import java.util.ResourceBundle;

import de.mca.io.ResourceManager;
import de.mca.model.InputComputer;
import de.mca.model.enums.ColorType;
import de.mca.model.interfaces.IsPlayer;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;

/**
 * 
 * @author Maximilian Werling
 *
 */
public class PlayerStatusComputerPresenter implements Initializable {

	private InputComputer input;
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
	private IsPlayer player;
	@FXML
	private ProgressIndicator progressIndicator;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initializeIconLabel(ResourceManager.getIcon("heart.png"), labelLifeIcon);

		initializeIconLabel(ResourceManager.getIcon("b.png"), labelBlackIcon);
		initializeIconLabel(ResourceManager.getIcon("u.png"), labelBlueIcon);
		initializeIconLabel(ResourceManager.getIcon("1.png"), labelColorlessIcon);
		initializeIconLabel(ResourceManager.getIcon("g.png"), labelGreenIcon);
		initializeIconLabel(ResourceManager.getIcon("r.png"), labelRedIcon);
		initializeIconLabel(ResourceManager.getIcon("w.png"), labelWhiteIcon);
	}

	public void injectPlayerData(IsPlayer player, InputComputer input, Image avatar) {
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

}
