package client.address.view;

import java.io.DataOutputStream;
import java.io.IOException;

import client.address.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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

	public void updateHandImage(String handCards) {

		String[] cards = handCards.split(":");

		this.card1.setImage(new Image("/imgs/cards/" + cards[0].toString() + ".gif"));
		this.card2.setImage(new Image("/imgs/cards/" + cards[1].toString() + ".gif"));
	}

	public void updatePotImage(String potCards) {

		String[] cards = potCards.split(":");

		potCard1.setImage(new Image("/imgs/cards/" + cards[0].toString() + ".gif"));
		potCard2.setImage(new Image("/imgs/cards/" + cards[1].toString() + ".gif"));
		potCard3.setImage(new Image("/imgs/cards/" + cards[2].toString() + ".gif"));

		if (cards.length > 3) {
			potCard4.setImage(new Image("/imgs/cards/" + cards[3].toString() + ".gif"));

			if (cards.length == 5) potCard5.setImage(new Image("/imgs/cards/" + cards[4].toString() + ".gif"));
		}

	}

	public Void updateUserValues(Integer funds, int lastBet) {
		this.funds.setText(funds.toString());

		this.lastBet = lastBet;
		this.fundsValue = funds;

		return null;
	}


	@FXML
	private void betAction() {
//		if (Integer.parseInt(this.betValue.getText()) < lastBet || Integer.parseInt(this.betValue.getText()) > this.fundsValue) {
//			throw new Exception("bet value is lower than the last bet or larger than your current funds");
//		}

		try {
			out.writeUTF("bet " + this.betValue.getText());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void setServerOutStream(DataOutputStream out) {
		this.out = out;
	}
}
