//For current login user data
package com.official.gold.gaming.tournamentpubg.models;

public class CurrentUser {

    public String memberId;
    public String userName;
    public String password;

    public CurrentUser(String memberId, String userName, String password) {
        this.memberId = memberId;
        this.userName = userName;
        this.password = password;
    }

    public String getMemberid() {
        return memberId;
    }

    public void setMemberid(String memberId) {
        this.memberId = memberId;
    }

    public String getUsername() {
        return userName;
    }

    public void setUsername(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
