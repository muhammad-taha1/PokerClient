package client.address.view;

import java.io.DataOutputStream;
import java.io.IOException;

import client.address.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class GameOverviewController {

	@FXML
	private ImageView card1;

	@FXML
	private ImageView card2;

	@FXML
	private ImageView potCard1;

	@FXML
	private ImageView potCard2;

	@FXML
	private ImageView potCard3;

	@FXML
	private ImageView potCard4;

	@FXML
	private ImageView potCard5;

	@FXML
	private Button bet;

	@FXML
	private Button check;

	@FXML
	private Button fold;

	@FXML
	private Label funds;

	@FXML
	private TextField betValue;

	@FXML
	private Pane loadPane;

	@FXML
	private Label pot;

	private String betMsg = "";

	private DataOutputStream out;


	private MainApp mainApp;

	private int lastBet;

	private Integer fundsValue;

	public GameOverviewController() {

	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;

	}

	public Void updateHandImage(String handCards) {

		String[] cards = handCards.split(":");

		this.card1.setImage(new Image("/imgs/cards/" + cards[0].toString() + ".gif"));
		this.card2.setImage(new Image("/imgs/cards/" + cards[1].toString() + ".gif"));
		return null;
	}

	public Void updatePotImage(String potCards) {

		String[] cards = potCards.split(":");

		potCard1.setImage(new Image("/imgs/cards/" + cards[0].toString() + ".gif"));
		potCard2.setImage(new Image("/imgs/cards/" + cards[1].toString() + ".gif"));
		potCard3.setImage(new Image("/imgs/cards/" + cards[2].toString() + ".gif"));

		if (cards.length > 3) {
			potCard4.setImage(new Image("/imgs/cards/" + cards[3].toString() + ".gif"));

			if (cards.length == 5) potCard5.setImage(new Image("/imgs/cards/" + cards[4].toString() + ".gif"));
		}
		return null;

	}

	public Void updateUserValues(Integer funds, int lastBet) {

		System.out.println("updating user params: funds = " + funds + " lastBet = " + lastBet);
		this.funds.setText(funds.toString());

		this.lastBet = lastBet;
		this.fundsValue = funds;

		EnableButtons();

		return null;
	}

	public Void addWinnings(Integer toUpdate) {
		this.fundsValue += toUpdate;
		this.funds.setText(this.fundsValue.toString());
		return null;
	}

	@FXML
	private void checkAction() {
		try {
			System.out.println("in chekActions " + lastBet);
			if (lastBet > 0) {
				Alert alert = new Alert(AlertType.WARNING);
				alert.initOwner(mainApp.getPrimaryStage());
				alert.setTitle("Invalid bet amount");
				alert.setHeaderText(null);
				alert.setContentText("Cannot check now, have to meet the last bet amount");

				alert.showAndWait();
				return;
			}

			out.writeUTF("bet " + 0);
			disableButtons();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	private void foldAction() {
		try {
			out.writeUTF("fold");
			disableButtons();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Void updatePot(String value) {
		this.pot.setText(value);
		return null;
	}


	@FXML
	private void betAction() {

		try {
			System.out.println("lastbet: " + lastBet);
			System.out.println("betamnt: " + Integer.parseInt(this.betValue.getText()));

			if (Integer.parseInt(this.betValue.getText()) < lastBet || Integer.parseInt(this.betValue.getText()) > this.fundsValue) {
				Alert alert = new Alert(AlertType.WARNING);
				alert.initOwner(mainApp.getPrimaryStage());
				alert.setTitle("Invalid bet amount");
				alert.setHeaderText(null);
				alert.setContentText("Bet amount should be bigger than " + this.lastBet + " and smaller than " + this.fundsValue);

				alert.showAndWait();
				return;
			}
			out.writeUTF("bet " + this.betValue.getText());
			this.fundsValue -= Integer.parseInt(this.betValue.getText());
			this.funds.setText(this.fundsValue.toString());
			disableButtons();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void setServerOutStream(DataOutputStream out) {
		this.out = out;
	}

	public Void clearPotImages() {
		potCard1.setImage(null);
		potCard2.setImage(null);
		potCard3.setImage(null);
		potCard4.setImage(null);
		potCard5.setImage(null);

		return null;
	}

	public Void EnableButtons() {
		this.fold.setDisable(false);
		this.bet.setDisable(false);
		this.check.setDisable(false);
		return null;
	}

	public void disableButtons() {
		this.fold.setDisable(true);
		this.bet.setDisable(true);
		this.check.setDisable(true);
	}

	public void loadingOff() {

		loadPane.setDisable(true);
		loadPane.setVisible(false);
	}

	public Void showWinMsg(String serverMsg) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.initOwner(mainApp.getPrimaryStage());
		alert.setTitle("Player Won!");
		alert.setHeaderText(null);
		alert.setContentText(serverMsg);

		alert.showAndWait();
		return null;

	}
}
