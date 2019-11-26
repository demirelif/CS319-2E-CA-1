package display.networkDisplay;

import display.networkDisplay.requests.BuildRequest;
import display.networkDisplay.requests.PlayerInfo;
import display.networkDisplay.requests.Requests;
import game.map.Map;
import game.map.MapButton;
import game.player.Civilization;
import game.player.Player;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

public class ClientConnection extends Thread {

    ObjectOutputStream os;
    Stage gameView;

    ClientGameScene clientGameScene;

    CountDownLatch mapLatch;

    public ClientConnection(Stage gameView) {
        this.gameView = gameView;
    }

    public void send(Serializable data) throws Exception {
        os.writeObject(data);
    }

    @Override
    public void run() {

            System.out.println("Someting Happened");

        try (Socket s = new Socket("localhost", 19999);
             ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(s.getInputStream())) {

            os = out;

            Player pl = new Player(Color.GREEN, Civilization.CivilizationEnum.SPAIN, ((int)(Math.random() * 50)) + "");
            PlayerInfo playerInfo = new PlayerInfo(pl);
            send(playerInfo);

            while (true) {

                try {
                    Serializable data = (Serializable) in.readObject();
                    System.out.println(data.toString());
                    if(data instanceof Map) {
                        System.out.println("DIDIT");

                        Platform.runLater(() -> {
                            try {

                                ClientGameScene clientGameScene = new ClientGameScene(mapLatch, (Map)data, this, pl);
                                gameView.setScene(clientGameScene.getScene());
                                this.clientGameScene = clientGameScene;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });


                    }
                    else if(data instanceof BuildRequest) {
                        System.out.println("OKEY");

                        Platform.runLater(() ->{
                            MapButton mb = ((BuildRequest)data).mapButton;
                            MapButton mapB = clientGameScene.findMapButton(mb.x, mb.y);
                            mapB.clientUpdate(new Player(((BuildRequest)data).playerInfo));
                        });

                    }
                    else if(data.equals(Requests.ADDED)) {
                        System.out.println("SENDIN");
                        send(Requests.GAME_INIT);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Problem");
        }
    }


}

