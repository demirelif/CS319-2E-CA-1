package Game;

import Game.Map.Location;
import Game.Map.Map;
import Game.Player.Player;

import java.util.ArrayList;

public class Game {
    ArrayList<Player> players;
    private int currentPlayerNo = 0;
    Player currentPlayer;
    int gameTurns = 0;
    boolean builtRoad = false;
    boolean builtVillage = false;
    Map map;
    int die1 = 0;
    int die2 = 0;

    public Game(Map m, ArrayList<Player> p) {
        map = m;
        players = p;
        currentPlayer = players.get(currentPlayerNo);
    }

    public int getDie1() {
        return die1;
    }

    public int getDie2() {
        return die2;
    }


    public boolean build(Location loc) {
        Player.Actions cost = map.getCost(loc);
        boolean settle = inSettlingPhase() &&
                ((cost == Player.Actions.BUILD_ROAD && !builtRoad)
                        || (cost == Player.Actions.BUILD_VILLAGE && !builtVillage));
        if (currentPlayer.canAfford(cost) || settle) {
            if (map.build(loc, currentPlayer)) {
                if (!settle)
                    currentPlayer.makeAction(cost);
                return true;
            }
        }
        return false;
    }

    public int getDiceValue () {
        return die1 + die2;
    }

    public int rollDice() {
        die1 = (int) (Math.random() * 6 + 1);
        die2 = (int) (Math.random() * 6 + 1);
        return getDiceValue();
    }

    public void endTurn () {
        int gameDir = 1;
        map.setSettlingPhase(inSettlingPhase());
        if (inReverseSettilingPhase()) {
            gameDir = -1;
        }
        gameTurns++;
        currentPlayerNo = (currentPlayerNo + gameDir) % players.size();
        currentPlayer = players.get(currentPlayerNo);
        map.generateResource(rollDice());
        builtRoad = false;
        builtVillage = false;
    }

    public boolean inSettlingPhase () {
        return gameTurns <= players.size();
    }

    public boolean inReverseSettilingPhase() {
        return gameTurns > players.size() / 2 && gameTurns <= players.size();
    }

    boolean checkVictory () {
        return currentPlayer.getVictoryPoints() >= 10;
    }
}
