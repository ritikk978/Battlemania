// For store login data in local storage
package com.official.gold.gaming.tournamentpubg.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.official.gold.gaming.tournamentpubg.models.CurrentUser;

public class UserLocalStore {

    public static final String SP_NAME = "userDetails";
    SharedPreferences userLocalDatabase;

    public UserLocalStore(Context context) {
        userLocalDatabase = context.getSharedPreferences(SP_NAME, 0);
    }

    public void storeUserData(CurrentUser cUser) {
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        userLocalDatabaseEditor.putString("memberid", cUser.memberId);
        userLocalDatabaseEditor.putString("username", cUser.userName);
        userLocalDatabaseEditor.putString("password", cUser.password);
        userLocalDatabaseEditor.commit();
    }

    public void clearUserData() {
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        userLocalDatabaseEditor.clear();
        userLocalDatabaseEditor.commit();
    }

    public CurrentUser getLoggedInUser() {
        String memberid = userLocalDatabase.getString("memberid", "");
        String username = userLocalDatabase.getString("username", "");
        String password = userLocalDatabase.getString("password", "");
        CurrentUser cUser = new CurrentUser(memberid, username, password);
        return cUser;
    }
}
