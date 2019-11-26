package display.networkDisplay;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public class ClientScene {

    private Group root;
    private Scene scene;
    ObjectOutputStream outx;
    ObjectInputStream inx;
    ClientConnection clientConnection;

    public ClientScene(Stage gameView) throws IOException {
        root = new Group();

        clientConnection = new ClientConnection(gameView);

        clientConnection.start();

    }

    public Scene getScene() {
        scene = new Scene(root);
        return scene;
    }

}

