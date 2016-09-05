package de.mca.presenter;

import java.net.URL;
import java.util.ResourceBundle;

import de.mca.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * 
 * @author Maximilian Werling
 *
 */
public class MainPresenter implements Initializable, IsStackableScreen {

	@FXML
	private Button buttonExit;
	@FXML
	private Button buttonNewMatch;
	private Main screenController;

	@FXML
	public void buttonExitPressed(ActionEvent actionEvent) {
		Stage stage = (Stage) buttonExit.getScene().getWindow();
		stage.close();
	}

	@FXML
	public void buttonNewMatchPressed(ActionEvent actionEvent) {
		screenController.setScreen("match");
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	@Override
	public void setScreenController(Main screenController) {
		this.screenController = screenController;
	}

}
