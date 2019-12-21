package game.player;

import network.requests.PlayerInfo;
import game.Resource;
import javafx.scene.paint.Color;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Player implements Serializable {

    public enum Actions { BUILD_ROAD, BUILD_VILLAGE, BUILD_CITY, BUY_DEV_CARD };
    public String name;

    Color color;
    Civilization civ;
    private Resource res;
    int armySize = 0;
    int roadLength = 0;
    // cards numbers
    int roadBuildingCards = 0;
    int yearOfPlentyCards = 0;
    int knightCards = 0;
    int monopolyCards = 0;
    int victoryPointCards = 0;
    int pirateCards = 0;
    List<DevelopmentCards> devCards = new ArrayList<>();

    int victoryPoints = 0;

    int diceCounter = 0; // the event occurs if the needed # of dice comes 3 times
    private int pirateCounter = -1;
    
    public Resource getRes() {
        return res;
    }

    public void setRes(Resource res) {
        this.res = res;
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name +
                ", res=" + res.toString() +
                '}';
    }

    public Player(PlayerInfo playerInfo) {
        color = Color.rgb(playerInfo.r,playerInfo.g,playerInfo.b);
        res = new Resource(playerInfo.resourceInfo);
        civ = playerInfo.civilization;
        this.name = playerInfo.name;
    }

    public Player(Color c, Civilization.CivilizationEnum civilizationEnum, String name) {
        color = c;
        res = new Resource( 4, 4, 4, 4, 4);
        civ = new Civilization(civilizationEnum);
        this.name = name;
    }

    public Civilization getCivilization() {
        return civ;
    }

    @Override
    public boolean equals(Object object) {
        return name.equals(((Player)object).name);
    }

    public Color getColor() {
        return color;
    }

    public boolean addDevelopmentCard( DevelopmentCards card ){
        devCards.add(card);
        return true;
    }

    public List<DevelopmentCards> getDevelopmentCards() {
        return devCards;
    };

    public boolean playDevelopmentCard( DevelopmentCards devCard ) {
        if(devCards.contains(devCard)) {
            devCards.remove(devCard);
            switch (devCard) {
                case KNIGHT:
                    armySize++;
                    break;
                case VICTORY_POINT:
                    incrementVictoryPoints(1);
                    break;
                case PIRATE:
                    pirateCounter = (int) (Math.random() * 15 + 1);
                    break;
            }
            return true;
        }
        return false;
    }

    public boolean canAfford(Actions a) {
        switch(a) {
            case BUILD_ROAD:
                return res.biggerEquals(civ.roadCost);
            case BUILD_VILLAGE:
                return res.biggerEquals(civ.villageCost);
            case BUILD_CITY:
                return res.biggerEquals(civ.cityCost);
            case BUY_DEV_CARD:
                return res.biggerEquals(civ.devCardCost);
        }
        return false;
    }

    public Resource payForAction(Actions a) {
        switch(a) {
            case BUILD_ROAD:
                return res.substract(civ.roadCost);
            case BUILD_VILLAGE:
                return res.substract(civ.villageCost);
            case BUILD_CITY:
                return res.substract(civ.cityCost);
            case BUY_DEV_CARD:
                return res.substract(civ.devCardCost);
        }
        return res;
    }

    public void looseResource( int removed ){
        res.removeRandom(removed);
    }

    public Resource addResource( Resource res ) {
        this.res.add( res );
        return this.res;
    }

    public Resource multiplyResource( Resource res ){
        this.res.multiply( res );
        return this.res;
    }

    public Resource decreaseResource(Resource res ){
        this.res.substract(res);
        return this.res;
    }

    public Civilization.CivilizationEnum getCivilizationType(){
        return civ.cEnum;
    }

    public int getWood(){
        return res.getWood();
    }

    public int getSheep(){
        return res.getSheep();
    }

    public int getWheat(){
        return res.getWheat();
    }

    public int getOre(){ return res.getOre(); }

    public int getBrick(){
        return res.getBrick();
    }

    public int incrementVictoryPoints( int i) {
        victoryPoints += i;
        return victoryPoints;
    }

    public int decreaseVictoryPoints( int i ){
        victoryPoints -= i;
        return victoryPoints;
    }

    public void decreasePirateCounter(){
        pirateCounter--;
    }

    public int getPirateCounter(){
        return pirateCounter;
    }

    public void resetPirateCounter(){
        pirateCounter = -1;
    }

    public boolean isBereket(){
        return civ.isBereket();
    }

    public void resetResources(){
        res.resetResources();
    }

    public void resetSheep(){
        res.resetSheep();
    }

    public boolean decreaseArmySize(int no){
        if ( armySize >= no ) {
            armySize -= no;
            return true;
        }
        return false;
    }
    public void increaseArmySize(int no){
        armySize += no;
    }
    public int getDiceCounter(){
        return diceCounter;
    }
    public void increaseDiceCounter(){
        diceCounter++;
    }
    public  void resetDiceCounter(){ diceCounter = -1;}
    public boolean checkVictory () {
        return victoryPoints >= 10;
    }
    public int totalResource(){ return res.totalCount();}
    public int getKnightCards(){return knightCards;}
    public int getArmySize(){return armySize;}
    public int getMonopolyCards(){return monopolyCards;}
    public int getRoadLength(){return roadLength;}
    public int getYearOfPlentyCards(){return yearOfPlentyCards;}
}
