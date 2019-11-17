package Game;

import Game.Map.Location;
import Game.Map.Map;
import Game.Player.DevelopmentCards;
import Game.Player.Player;
import java.util.Collections;
import java.util.ArrayList;


public class Game {
    ArrayList<Player> players;
    private int currentPlayerNo = 0;
    Player currentPlayer;
    Map map;
    ArrayList<DevelopmentCards> developmentCards = new ArrayList<DevelopmentCards>();;
    int die1 = 0;
    int die2 = 0;

    public void createDevelopmentCards(){
        for ( int i = 0; i < 20; i++ )
            developmentCards.add(DevelopmentCards.KNIGHT);
        for ( int i = 20; i < 25; i++)
            developmentCards.add(DevelopmentCards.VICTORY_POINT);
        for ( int i = 25; i < 28; i++ )
            developmentCards.add(DevelopmentCards.ROAD_BUILDING);
        for ( int i = 28; i < 31; i++ )
            developmentCards.add(DevelopmentCards.YEAR_OF_PLENTY);
    }

    public boolean shuffleDevelopmentCards(){
        Collections.shuffle(developmentCards);
        return true;
    }

    public Game( Map m, ArrayList<Player> p ) {
        map = m;
        players = p;
        currentPlayer = players.get( currentPlayerNo );
    }

    public boolean build( Location loc ) {
        Player.Actions cost = map.getCost(loc);
        if( currentPlayer.canAfford(cost) ) {
            if( map.build(loc, currentPlayer) ) {
                currentPlayer.makeAction( cost );
                return true;
            }
        }
        return false;
    }

    public int getDiceValue() {
        return die1 + die2;
    }

    public int rollDice() {
        die1 = (int) ( Math.random() * 6 + 1 );
        die2 = (int) ( Math.random() * 6 + 1 );
        return getDiceValue();
    }

    public void endTurn() {
        currentPlayerNo = ( currentPlayerNo + 1 ) % players.size();
        currentPlayer = players.get( currentPlayerNo);
        map.generateResource( rollDice() );
    }
}
