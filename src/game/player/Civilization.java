package game.player;
import game.Resource;

public class Civilization {
    Resource roadCost;
    Resource villageCost;
    Resource cityCost;
    Resource devCardCost;

    Civilization() {
        roadCost = new Resource( 1, 1, 0, 0, 0);
        villageCost = new Resource( 1, 1, 1, 1, 0);
        cityCost = new Resource( 0, 0, 0, 2, 3);
        devCardCost = new Resource( 0, 0, 1, 1, 1);
    }


}
