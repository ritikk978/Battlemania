//For all upcoming tournament data
package com.official.gold.gaming.tournamentpubg.models;

public class TournamentData {

    String mId;
    String matchBanner;
    String matchName;
    String mTime;
    String matchTime;
    String winPrize;
    String perKill;
    String entryFee;
    String type;
    String version;
    String map;
    String noOfPlayer;
    String memberId;
    String matchType;
    String numberOfPosition;
    String roomId;
    String roomPassword;
    String joinStatus;
    String gameName;
    String prizeDescreption;

    public TournamentData(String mId, String matchBanner, String matchName, String mTime, String matchTime, String winPrize, String perKill, String entryFee, String type, String version, String map, String noOfPlayer, String memberId, String matchType, String numberOfPosition, String roomId, String roomPassword, String joinStatus, String gameName, String prizeDescreption) {

        this.mId = mId;
        this.matchBanner = matchBanner;
        this.matchName = matchName;
        this.mTime = mTime;
        this.matchTime = matchTime;
        this.winPrize = winPrize;
        this.perKill = perKill;
        this.entryFee = entryFee;
        this.type = type;
        this.version = version;
        this.map = map;
        this.noOfPlayer = noOfPlayer;
        this.memberId = memberId;
        this.matchType = matchType;
        this.numberOfPosition = numberOfPosition;
        this.roomId = roomId;
        this.roomPassword = roomPassword;
        this.joinStatus = joinStatus;
        this.gameName = gameName;
        this.prizeDescreption = prizeDescreption;
    }

    public String getMid() {
        return mId;
    }

    public void setMid(String mId) {
        this.mId = mId;
    }

    public String getMatchbanner() {
        return matchBanner;
    }

    public void setMatchbanner(String matchBanner) {
        this.matchBanner = matchBanner;
    }

    public String getMatchname() {
        return matchName;
    }

    public void setMatchname(String matchName) {
        this.matchName = matchName;
    }

    public String getMtime() {
        return mTime;
    }

    public void setMtime(String mTime) {
        this.mTime = mTime;
    }

    public String getMatchtime() {
        return matchTime;
    }

    public void setMatchtime(String matchTime) {
        this.matchTime = matchTime;
    }

    public String getWinprize() {
        return winPrize;
    }

    public void setWinprize(String winPrize) {
        this.winPrize = winPrize;
    }

    public String getPerkill() {
        return perKill;
    }

    public void setPerkill(String perKill) {
        this.perKill = perKill;
    }

    public String getEntryfee() {
        return entryFee;
    }

    public void setEntryfee(String entryFee) {
        this.entryFee = entryFee;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public String getNoofplayer() {
        return noOfPlayer;
    }

    public void setNoofplayer(String noOfPlayer) {
        this.noOfPlayer = noOfPlayer;
    }

    public String getMemberid() {
        return memberId;
    }

    public void setMemberid(String memberId) {
        this.memberId = memberId;
    }

    public String getMatchtype() {
        return matchType;
    }

    public void setMatchtype(String matchType) {
        this.matchType = matchType;
    }

    public String getNumberofposition() {
        return numberOfPosition;
    }

    public void setNumberofposition(String numberOfPosition) {
        this.numberOfPosition = numberOfPosition;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoompassword() {
        return roomPassword;
    }

    public void setRoompassword(String roomPassword) {
        this.roomPassword = roomPassword;
    }

    public String getJoinstatus() {
        return joinStatus;
    }

    public void setJoinstatus(String joinStatus) {
        this.joinStatus = joinStatus;
    }

    public String getGamename() {
        return gameName;
    }

    public void setGamename(String gameName) {
        this.gameName = gameName;
    }

    public String getPrizedescreption() {
        return prizeDescreption;
    }

    public void setPrizedescreption(String prizeDescreption) {
        this.prizeDescreption = prizeDescreption;
    }
}
