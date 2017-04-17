package client.address.view;
import java.net.ConnectException;
import java.net.UnknownHostException;

import client.address.Client;
import client.address.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;

public class ConnectionOverviewController {

	@FXML
	private TextField ipAddress;

	@FXML
	private TextField serverPort;

	@FXML
	private TextField name;

	private MainApp mainApp;

	public ConnectionOverviewController() {

	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;

	}

	@FXML
	private void submitButton() {

		// check for empty texts
		if (ipAddress.getText().isEmpty() || serverPort.getText().isEmpty() || name.getText().isEmpty()) {

			Alert alert = new Alert(AlertType.WARNING);
			alert.initOwner(mainApp.getPrimaryStage());
			alert.setTitle("No text entered");
			alert.setHeaderText(null);
			alert.setContentText("Please enter the required information");

			alert.showAndWait();
			return;
		}

		// try connection with server
		try {
			Client client = new Client(ipAddress.getText(), serverPort.getText(), name.getText(), mainApp);


		// other error checks
		} catch (NumberFormatException e) {

			// invalid port number entry
			Alert alert = new Alert(AlertType.WARNING);
			alert.initOwner(mainApp.getPrimaryStage());
			alert.setTitle("Invalid Port number");
			alert.setHeaderText(null);
			alert.setContentText("Please enter the correct port number");

			alert.showAndWait();
			return;
		}
		catch (UnknownHostException e) {
			// invalid ip
			Alert alert = new Alert(AlertType.WARNING);
			alert.initOwner(mainApp.getPrimaryStage());
			alert.setTitle("Invalid IP number");
			alert.setHeaderText(null);
			alert.setContentText("Please enter the correct ip and port number");

			alert.showAndWait();
			return;

		}
		catch (ConnectException e) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.initOwner(mainApp.getPrimaryStage());
			alert.setTitle("Server refused connection");
			alert.setHeaderText(null);
			alert.setContentText("Server refused connection. Check the ip/port number, firewall settings and try again");

			alert.showAndWait();
			return;
		}
		catch (Exception e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(mainApp.getPrimaryStage());
			alert.setTitle("Unknown Error Occured");
			alert.setHeaderText(null);
			alert.setContentText(e.getMessage());

			e.printStackTrace();

			alert.showAndWait();
			return;
		}
	}
}
