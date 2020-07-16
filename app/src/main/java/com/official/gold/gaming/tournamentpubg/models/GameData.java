//For all game data
package com.official.gold.gaming.tournamentpubg.models;

public class GameData {

    String gameId;
    String gameName;
    String gameImage;
    String gameStatus;

    public GameData(String gameId, String gameName, String gameImage, String gameStatus) {
        this.gameId = gameId;
        this.gameName = gameName;
        this.gameImage = gameImage;
        this.gameStatus = gameStatus;
    }

    public String getGameid() {
        return gameId;
    }

    public void setGameid(String gameId) {
        this.gameId = gameId;
    }

    public String getGamename() {
        return gameName;
    }

    public void setGamename(String gameName) {
        this.gameName = gameName;
    }

    public String getGameimage() {
        return gameImage;
    }

    public void setGameimage(String gameImage) {
        this.gameImage = gameImage;
    }

    public String getGamestatus() {
        return gameStatus;
    }

    public void setGamestatus(String gameStatus) {
        this.gameStatus = gameStatus;
    }
}
