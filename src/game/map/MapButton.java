package game.map;

import game.player.Player;
import javafx.scene.shape.Circle;

public class MapButton extends Circle {
    Location loc;
    MapElement me;

    public MapButton(double x, double y, double r, MapElement me) {
        super( x, y, r );
        this.loc = loc;
        this.me = me;
    }

    public void update() {
        if( !me.isEmpty() ){
            /*this.setFill(Color.WHITESMOKE);*/
            this.setOpacity(0.7);
            Player p = ( (Buildable) me ).getPlayer();
            this.setFill(p.getColor());
        }
    }
}
