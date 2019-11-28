package game;

import java.io.Serializable;
import java.util.Random;

/*
 Resource class consists of all 5 resource types in the game. It has the counts of the resources as integers.
 */

public class Resource implements Serializable {
    int resourcesTypes = 5;
    int brick;
    int wood;
    int sheep;
    int wheat;
    int ore;
    public Resource() {
        this( 0, 0, 0, 0, 0);
    }

    public Resource( int brick, int wood, int sheep, int wheat, int ore ) {
        this.brick = brick;

        this.wood = wood;

        this.sheep = sheep;

        this.wheat = wheat;

        this.ore = ore;
    }

    public Resource add( Resource rsc ) { // increase the resources by adding the given resource
        brick += rsc.brick;
        wood += rsc.wood;
        sheep += rsc.sheep;
        wheat += rsc.wheat;
        ore += rsc.ore;
        return this;
    }

    public Resource multiply( Resource rsc ){ // increase the resources bu multiplying them with the given resource
        brick *= rsc.brick;
        wood *= rsc.wood;
        sheep *= rsc.sheep;
        wheat *= rsc.wheat;
        ore *= rsc.ore;
        return this;
    }

    public int getBrick() {
        return brick;
    }

    public int getWood() {
        return wood;
    }

    public int getSheep() {
        return sheep;
    }

    public int getWheat() {
        return wheat;
    }

    public int getOre() {
        return ore;
    }

    public Resource substract( Resource rsc ) { // if the given resource is smaller than this.resource, this is decreased
        if ( getBrick() > rsc.getBrick() && getOre() > rsc.getOre() && getWheat() > rsc.getWheat()
                && getWood() > rsc.getWood() && getSheep() > rsc.getSheep() ) {
            brick -= rsc.brick;
            wood -= rsc.wood;
            sheep -= rsc.sheep;
            wheat -= rsc.wheat;
            ore -= rsc.ore;
        }
        return this;
    }

    public boolean biggerEquals( Resource rsc ) {
        return brick >= rsc.brick &&
        wood >= rsc.wood &&
        sheep >= rsc.sheep &&
        wheat >= rsc.wheat &&
        ore >= rsc.ore;
    }

    public int totalCount() {
        return brick + wood + sheep + wheat + ore;
    }

    public void removeRandom( int removeNo ){ // removes given # of resources randomly
        for ( int i = 0; i < removeNo; i++ ){
            int which = new Random().nextInt(5);

            if ( which == 0 && getBrick() > 0 ){
                brick--;
            }
            else if ( which == 1 && getWood() > 0 ){
                wood--;
            }
            else if ( which == 2 && getSheep() > 0 ){
                sheep--;
            }
            else if ( which == 3 && getWheat() > 0 ){
                wheat--;
            }
            else if ( which == 4 && getOre() > 0 ){
                ore--;
            }
        }
    }

    boolean isZero() { return this.totalCount() == 0; }
}
