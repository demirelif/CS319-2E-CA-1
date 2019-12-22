package game.map;
import game.player.Player;

import java.io.Serializable;

public class MapCorner implements MapElement, Buildable, Serializable {
    enum Types { EMPTY, VILLAGE, CITY }
    Types type = Types.EMPTY;
    Location loc;
    Player player = null;

    MapCorner(Location l) {
        loc = l;
    }

    public Location getLocation() {
        return loc;
    }

    public boolean isEmpty() {
        return type == Types.EMPTY;
    }

    public void build( Player player) {
        type = type == Types.EMPTY ? Types.VILLAGE : Types.CITY;
        this.player = player;
    }

    public Player.Actions getCost() {
        return type == Types.EMPTY ? Player.Actions.BUILD_VILLAGE : Player.Actions.BUILD_CITY;
    }

    public Player getPlayer() {
        return player;
    }

    public Types getType(){
        return type;
    }

    public boolean hasCity() { return type.equals(Types.CITY);}
}
