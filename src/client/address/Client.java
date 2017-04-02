package client.address;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.FutureTask;

import client.address.view.GameOverviewController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

public class Client {


	private GameOverviewController controller;

	public Client(String ipAddress, String port, String name, MainApp mainApp) throws Exception {


		Socket client = new Socket(ipAddress, Integer.valueOf(port));

		// just to verify that connection has been successful
		OutputStream outToServer = client.getOutputStream();
		DataOutputStream out = new DataOutputStream(outToServer);

		out.writeUTF(name);
		InputStream inFromServer = client.getInputStream();
		DataInputStream in = new DataInputStream(inFromServer);

		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource("view/GameOverview.fxml"));
		AnchorPane gameView = (AnchorPane) loader.load();
		mainApp.getRootLayout().setCenter(gameView);

		// give controller to main app
		controller = loader.getController();
		controller.setMainApp(mainApp);
		controller.setName(name);

		Platform.setImplicitExit(false);
		startServerComm(client);
	}

	public void startServerComm(Socket client) throws Exception {
		// actual work of the client. Do nothing unless server sends something
		Thread c = new Thread() {

			@Override
			public void run() {
				try {
					while (true) {
						InputStream inFromServer = client.getInputStream();
						DataInputStream in = new DataInputStream(inFromServer);

						OutputStream outFromServer = client.getOutputStream();
						DataOutputStream out = new DataOutputStream(outFromServer);

						// tell controller the output for server
						controller.setServerOutStream(out);

						if (in.available() <= 0) {
							//wait
							continue;
						}
						else if (in.available() > 0) {
							// get server msgs
							String serverMsg = in.readUTF();
							System.out.println("Server says: " + serverMsg);

							if (serverMsg.equalsIgnoreCase("Start game")) {
								controller.loadingOff();
							}

							if (serverMsg.contains("numberOfPlayers")) {
								String[] numPlayers = serverMsg.split(" ");
								controller.showPlayers(numPlayers[1]);

							}

							if (serverMsg.equalsIgnoreCase("TOKEN")) {
								// immediately after token server will also send us our budget and last betted amount
								int funds = in.readInt();
								int lastBet = in.readInt();

								FutureTask<Void> updateUserValues = new FutureTask<Void>(() -> controller.updateUserValues(funds, lastBet));
								Platform.runLater(updateUserValues);

								// block until work is complete
								updateUserValues.get();
							}

							if (serverMsg.contains("POT")) {
								String[] pot = serverMsg.split(" ");

								FutureTask<Void> updatePot = new FutureTask<Void>(() -> controller.updatePot(pot[1]));
								Platform.runLater(updatePot);

								// block until work is complete
								updatePot.get();
							}

							if (serverMsg.contains("hand")) {
								String[] handCards = serverMsg.split(" ");
								controller.updateHandImage(handCards[1]);
							}

							if (serverMsg.contains("potCards")) {
								String[] potCards = serverMsg.split(" ");
								System.out.println("potcards == " + potCards[1]);
								controller.updatePotImage(potCards[1]);
							}

							if (serverMsg.contains("updateWinnings")) {
								int funds = in.readInt();
								FutureTask<Void> updatePot = new FutureTask<Void>(() -> controller.addWinnings(funds));
								Platform.runLater(updatePot);
								// block until work is complete
								updatePot.get();

								controller.disableButtons();
							}

							if (serverMsg.contains("round over")) {
								controller.clearPotImages();

								FutureTask<Void> updatePot = new FutureTask<Void>(() -> controller.updatePot("0"));
								Platform.runLater(updatePot);
								// block until work is complete
								updatePot.get();

							}

							if (serverMsg.contains("won with")) {
								FutureTask<Void> winMsg = new FutureTask<Void>(() -> controller.showWinMsg(serverMsg));
								Platform.runLater(winMsg);
								winMsg.get();
								System.out.println("alert winning complete!!");
							}
						}
					}
				}
				catch (Exception e) {
					throw new RuntimeException(e);
				}

			}
		};
		c.start();
	}



}
