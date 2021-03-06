package network;

import display.EventPopUp;
import display.networkDisplay.ServerGameScene;
import network.requests.BuildRequest;
import network.requests.EndTurnInfo;
import network.requests.PlayerInfo;
import network.requests.Requests;
import display.MapButton;
import game.map.MapElement;
import game.player.Civilization;
import game.player.Player;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class ServerConnection extends Connection {
    private int playerCount;
    Client[] clients;

    ObjectInputStream is;
    boolean gameInit = false;
    ArrayList<Player> players;
    ServerSocket ss;
    Socket s;

    static int initRequestCount = 0;
    CountDownLatch endTurnCount;
    private int currentConnectedClient = 1;

    public ServerConnection(Stage gameView, String roomName, String selectedVersion, int playerCount) {
        super(gameView);
        this.playerCount = playerCount;
        clients = new Client[playerCount - 1];
        players = new ArrayList<>();
        players.add(new Player(Color.RED, Civilization.CivType.OTTOMANS, "server"));
}

    @Override
    public void send(Serializable data) throws Exception {
        for (Client clss: clients) {
            clss.os.writeObject(data);
        }
    }

    @Override
    public void run() {
        mapLatch = new CountDownLatch(1);
        endTurnCount = new CountDownLatch(1);
        try {
            ss = new ServerSocket(31923);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int playerCount = 0;
        while (playerCount < this.playerCount - 1) {

            try {


                Socket s = ss.accept();

                ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(s.getInputStream());
                currentConnectedClient++;

                clients[playerCount] = new Client(s, out, in);

            } catch (Exception e) {
                e.printStackTrace();
            }
            playerCount++;
        }


        while (true) {
            for (Client client : clients) {
                try {
                    Serializable data;
                    if(!gameInit || client.player.equals(((ServerGameScene) networkGameScene).getGame().getCurrentPlayer())
                            // To prevent infinite loop on servers turn. Increases game performance
                            || (client.player.equals(((ServerGameScene) networkGameScene).getGame().getNextPlayer())
                                            && ((ServerGameScene) networkGameScene).getGame().getCurrentPlayer().name.equals("server"))) {

                        data = (Serializable) client.in.readObject();
                    }
                    else {
                        continue;
                    }

                    if(data.equals(Requests.GAME_INIT)) {
                        initRequestCount++;
                    }
                    if(initRequestCount == this.playerCount - 1) {

                        Platform.runLater(() -> {
                            try {
                                for (Player plrr: players) {

                                }
                                ServerGameScene serverGameScene = new ServerGameScene(players, gameView,this);
                                closePopUp();

                                gameView.setScene(serverGameScene.getScene());
                                this.networkGameScene = serverGameScene;
                                mapLatch.countDown();
                                gameInit = true;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });

                        mapLatch.await();
                        for(Client cls : clients) {
                            cls.os.writeObject(((ServerGameScene) networkGameScene).getMap());
                        }
                        initRequestCount--;
                    }

                    else if(data instanceof PlayerInfo) {
                        Player plrrx = new Player((PlayerInfo)data);
                        players.add(plrrx);
                        client.os.writeObject(Requests.PLAYER_OK);
                        client.setPlayer(plrrx);
                    }

                    else if(data.equals(Requests.END_TURN)) {
                        Platform.runLater(() -> {
                            try {
                                ((ServerGameScene) networkGameScene).endTurnProcess(endTurnCount);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                        endTurnCount.await();
                        endTurnCount = new CountDownLatch(1);
                    }

                    else if(data instanceof BuildRequest) {
                        MapElement a = ((BuildRequest)data).element;
                        MapButton mb = ((BuildRequest)data).mapButton;
                        Player ply = new Player(((BuildRequest)data).playerInfo);


                        if(((ServerGameScene) networkGameScene).getGame().getCurrentPlayer().equals(ply)) {
                            boolean built = ((ServerGameScene) networkGameScene).getGame().build(a.getLocation());

                            MapButton mapB = networkGameScene.findMapButton(mb);
                            mapB.update();
                            ((ServerGameScene) networkGameScene).updateResources(networkGameScene.getPlayer());

                            if (built) {
                                ((BuildRequest)data).setPlayerInfo(new PlayerInfo(((ServerGameScene) networkGameScene)
                                        .getGame().getCurrentPlayer()));
                                ((BuildRequest)data).setHasCity(mapB.hasCity());
                                send(data);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public void sendEndTurnInfo(Player currentPlayer, int die1, int die2, EventPopUp popUp) {

        try {
            for (Client client : clients) {
                EndTurnInfo endTurnInfo = new EndTurnInfo(new PlayerInfo(currentPlayer),
                        new PlayerInfo(client.player), die1, die2, popUp);

                client.os.writeObject(endTurnInfo);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public int getCurrentPlayerCount(){
        return currentConnectedClient;
    }

    public int getMaxPlayerCount() {
        return playerCount;
    }

}
