package network;

import display.networkDisplay.ClientGameScene;
import javafx.beans.property.SimpleBooleanProperty;
import network.requests.BuildRequest;
import network.requests.EndTurnInfo;
import network.requests.PlayerInfo;
import network.requests.Requests;
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

public class ClientConnection extends Connection {
    String name;
    String IPAddress;
    SimpleBooleanProperty connectionFailed;

    public ClientConnection(Stage gameView, String name, String IPAddress, SimpleBooleanProperty connectionFailed) {
        super(gameView);
        this.name = name;
        this.IPAddress = IPAddress;
        this.connectionFailed = connectionFailed;
    }

    @Override
    public void send(Serializable data) throws Exception {
        os.writeObject(data);
    }

    @Override
    public void run() {

        System.out.println("Something Happened");

        try (Socket s = new Socket(IPAddress, 31923);
             ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(s.getInputStream())
        ) {

            os = out;

            Player px = null;
            boolean playerAccepted = false;
            while(!playerAccepted) {
                px = new Player(Color.GREEN, Civilization.CivType.SPAIN, name);
                PlayerInfo playerInfo = new PlayerInfo(px);
                send(playerInfo);

                System.out.println("YAAAY");
                Requests playerAcceptedResult = (Requests) in.readObject();
                if(playerAcceptedResult.equals(Requests.PLAYER_OK)) {
                    send(Requests.GAME_INIT);
                    playerAccepted = true;
                }
            }
            Player pl = px;


            while (true) {

                try {
                    Serializable data = (Serializable) in.readObject();
                    System.out.println(data.toString());
                    if(data instanceof Map) {
                        Platform.runLater(() -> {
                            try {

                                ClientGameScene clientGameScene = new ClientGameScene(mapLatch, (Map)data, this, pl);
                                gameView.setScene(clientGameScene.getScene());
                                this.networkGameScene = clientGameScene;
                                closePopUp();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });


                    }
                    else if(data instanceof BuildRequest) {

                        Platform.runLater(() ->{
                            MapButton mb = ((BuildRequest)data).mapButton;
                            MapButton mapB = networkGameScene.findMapButton(mb);
                            Player builderPlayer = new Player(((BuildRequest)data).playerInfo);
                            mapB.clientUpdate(builderPlayer);
                            if(builderPlayer.equals(pl)) {
                                System.out.println("UPDATING MY RES");
                                networkGameScene.updateResourcesInTurn(builderPlayer);
                            }
                        });

                    }

                    else if(data instanceof EndTurnInfo) {
                        EndTurnInfo endTurnInfo = (EndTurnInfo) data;

                        Platform.runLater(() ->{
                            ((ClientGameScene) networkGameScene).displayDice(endTurnInfo.getDie1(), endTurnInfo.getDie2());

                            networkGameScene.updateResources(new Player(endTurnInfo.getPlayerInfo()),endTurnInfo.getCurrentPlayerInfo().name);
                            System.out.println(endTurnInfo.getCurrentPlayerInfo().name + "\n" +
                                    endTurnInfo.getPlayerInfo().name + "\n" +
                                    networkGameScene.getPlayer().name);
                            if(new Player(endTurnInfo.getCurrentPlayerInfo()).equals(networkGameScene.getPlayer()))
                                ((ClientGameScene) networkGameScene).enableEndTurn();
                        });


                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Problem");
            connectionFailed.set(true);
        }
    }

}

