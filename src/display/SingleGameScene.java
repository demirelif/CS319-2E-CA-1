package display;

import game.Game;
import game.map.*;
import game.player.Civilization;
import game.player.DevelopmentCards;
import game.player.Player;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class SingleGameScene extends GameScene {
    int numberOfPlayers;
    Game game;
    Player[] players;
    Font fsmall;

    public SingleGameScene(Stage primaryStage, Player[] players, int numberOfPlayers) throws IOException, InterruptedException {
        super(primaryStage);
        map = new Map();
        this.numberOfPlayers = numberOfPlayers;
        fsmall = Font.loadFont(new FileInputStream(new File("res/MinionPro-BoldCn.otf")), 25);

        this.players = players;
        addBackground();
        createGameAndTiles();
        addPlayerResourcesMenu();

        Text playerListText = new Text("Player List");
        playerListText.setFont(fsmall);
        playerListText.setFill(Color.WHITE);

        Text devCardListText = new Text("Development Cards");
        devCardListText.setFont(fsmall);
        devCardListText.setFill(Color.WHITE);
        Rectangle liner = new Rectangle(200, 35);
        liner.setFill(Color.TRANSPARENT);
        Rectangle liner2 = new Rectangle(200, 35);
        liner2.setFill(Color.TRANSPARENT);
        lists = new VBox(5);
        lists.getChildren().addAll(liner,playerListText,createPlayerList(),liner2,devCardListText,createDevCardList());
        //lists.getChildren().add(createDevCardList());
        lists.setAlignment(Pos.CENTER);
        lists.setPrefSize(200,DefaultUISpecifications.SCREEN_HEIGHT * 6 / 7);
        lists.setSpacing(1);
        root.getChildren().add(lists);

        updatePlayerList();
        displayDice(game.getDie1(), game.getDie2());
    }

    protected void createLists(VBox lists, VBox devCardList, VBox playerList){

    }

    @Override
    protected void createPlayerResourceBoxes() throws IOException {
        resourceBoxes[0] = new ResourceBox(players[0], "BRICK");
        resourceBoxes[1] = new ResourceBox(players[0], "WOOD");
        resourceBoxes[2] = new ResourceBox(players[0], "SHEEP");
        resourceBoxes[3] = new ResourceBox(players[0], "WHEAT");
        resourceBoxes[4] = new ResourceBox(players[0], "ORE");
    }


    @Override
    protected void setupButtons() {
        endTurnButton.setOnMouseClicked(e -> {
            game.endTurn();
            updateResources(game.getCurrentPlayer());
            updateDevCards(game.getCurrentPlayer());
            displayDice(game.getDie1(), game.getDie2());
            updatePlayerList();
            EventPopUp popUp = checkGameEvent(game);
            if( popUp != null ) {
                try {
                    popUp.initPopUp(root,gameView);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            /*for( Pair t : tokens ) {
                setTokenDisplay((MapToken) t.getKey(), (Location) t.getValue());
            }*/
        });
        buyDevCardButton.setOnMouseClicked(e -> {
            game.buyDevelopmentCard();
            updateResources(game.getCurrentPlayer());
            updateDevCards(game.getCurrentPlayer());
        });
    }

    protected void nonTileMouseClicked(MapButton mb, MapElement a) {
        mb.setOnMouseClicked(e -> {
            game.build(a.getLocation());
            if(game.checkVictory()) {
                try {
                    new EventPopUp(root,game.getCurrentPlayer()+ " WON","Congratulations!",null);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            mb.update();
            updateResources(game.getCurrentPlayer());
            updateDevCards(game.getCurrentPlayer());
        });
        mb.setOnMouseEntered(e->{
            if(game.buildCheck(a.getLocation())) {
                mb.lightlyPaint(game.getCurrentPlayer());
            }
        });
        mb.setOnMouseExited(e->{
                mb.removeLightlyPaint();
        });
    }

    @Override
    protected void createGameAndTiles() throws FileNotFoundException {
        game = new Game(map, new ArrayList<Player>(Arrays.asList(players)));
        super.createGameAndTiles();
    }

    @Override
    protected void tileMouseClicked(MapButton mb, MapTile a) {
        mb.setOnMouseClicked(e->{
            game.moveRobber(a.getLocation());
        });
    }

    private void updateResources(Player player) {
        for ( int i = 0; i < resourceBoxes.length; i++ ) {
            resourceBoxes[i].update(player);
        }
        turnOfPlayer.setText("Turn of " + game.getCurrentPlayer().name);
        turnOfPlayer.setTextFill(game.getCurrentPlayer().getColor());
    }


    protected void updatePlayerList() {
        playerList.getItems().clear();
        for(int i = 0; i < numberOfPlayers; i++){
            HBox all = new HBox(4);
            VBox namesAndCiv = new VBox(3);
            Text nameOfThePlayer = new Text(players[i].name);
            nameOfThePlayer.setTextAlignment(TextAlignment.LEFT);
            Text civOfThePlayer = new Text(players[i].getCivilizationType().name());
            civOfThePlayer.setTextAlignment(TextAlignment.LEFT);
            namesAndCiv.getChildren().addAll(nameOfThePlayer,civOfThePlayer);
            namesAndCiv.setAlignment(Pos.CENTER_LEFT);
            Text vp = new Text((players[i].getVictoryPoints()) + " VP");
            Text army = new Text((players[i].getArmySize() + "AS"));
            vp.setTextAlignment(TextAlignment.LEFT);
            army.setTextAlignment(TextAlignment.LEFT);
            all.getChildren().addAll(namesAndCiv,vp, army);
            all.setAlignment(Pos.CENTER_LEFT);
            all.setSpacing(15);
            playerList.getItems().add(all);
        }
    }

    private void updateDevCards(Player player) {
        devCardList.getItems().clear();
        ObservableList<Button> devCardButtons = FXCollections.observableArrayList();
        List<DevelopmentCards> devCards = player.getDevelopmentCards();
        for( DevelopmentCards d : devCards ) {
            Button button = new Button(""+d.name());
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    game.playDevelopmentCard(d);
                    updateDevCards(player);
                    updatePlayerList();
                }
            });
            devCardButtons.add(button);
        }
        devCardList.setItems(devCardButtons);
    }
}
