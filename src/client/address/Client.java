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

							if (serverMsg.equalsIgnoreCase("TOKEN")) {
								// immediately after token server will also send us our budget and last betted amount
								int funds = in.readInt();
								int lastBet = in.readInt();

								FutureTask<Void> updateUserValues = new FutureTask<Void>(() -> controller.updateUserValues(funds, lastBet));
								Platform.runLater(updateUserValues);

								// block until work is complete
								updateUserValues.get();
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
