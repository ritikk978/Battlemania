//For Me tab at home page
package com.official.gold.gaming.tournamentpubg.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.official.gold.gaming.tournamentpubg.ui.activities.AboutusActivity;
import com.official.gold.gaming.tournamentpubg.models.CurrentUser;
import com.official.gold.gaming.tournamentpubg.utils.CustomTypefaceSpan;
import com.official.gold.gaming.tournamentpubg.ui.activities.CustomerSupportActivity;
import com.official.gold.gaming.tournamentpubg.ui.activities.HowtoActivity;
import com.official.gold.gaming.tournamentpubg.ui.activities.LeaderboardActivity;
import com.official.gold.gaming.tournamentpubg.utils.LoadingDialog;
import com.official.gold.gaming.tournamentpubg.ui.activities.MainActivity;
import com.official.gold.gaming.tournamentpubg.ui.activities.MyProfileActivity;
import com.official.gold.gaming.tournamentpubg.ui.activities.MyReferralsActivity;
import com.official.gold.gaming.tournamentpubg.ui.activities.MyStatisticsActivity;
import com.official.gold.gaming.tournamentpubg.ui.activities.MyWalletActivity;
import com.official.gold.R;
import com.official.gold.gaming.tournamentpubg.ui.activities.TermsandConditionActivity;
import com.official.gold.gaming.tournamentpubg.ui.activities.TopPlayerActivity;
import com.official.gold.gaming.tournamentpubg.utils.UserLocalStore;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class MeFragment extends Fragment {

    TextView userName;
    TextView playCoin;
    TextView myWallet;
    TextView myProfile;
    TextView aboutUs;
    TextView customerSupport;
    TextView logOut;
    TextView shareApp;
    TextView winning;
    TextView myStatistics;
    TextView topPlayer;
    TextView matchesPlayed;
    TextView totalKilled;
    TextView amountWon;
    TextView appVersion;
    TextView appTutorial;
    TextView myReff;
    TextView leaderboard;
    TextView termAndCondition;
    LinearLayout staticResult;
    RequestQueue mQueue, vQueue;
    UserLocalStore userLocalStore;
    LoadingDialog loadingDialog;
    String userNameforlogout = "";
    SharedPreferences sp;
    String shareBody = "";

    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        loadingDialog = new LoadingDialog(getContext());
        sp = getActivity().getSharedPreferences("tabinfo", Context.MODE_PRIVATE);
        String selectedtab = sp.getString("selectedtab", "");
        if (TextUtils.equals(selectedtab, "2")) {
            loadingDialog.show();
        }
        View root = inflater.inflate(R.layout.me_home, container, false);

        userName = (TextView) root.findViewById(R.id.username);
        playCoin = (TextView) root.findViewById(R.id.playcoin);
        myWallet = (TextView) root.findViewById(R.id.mywallet);
        myProfile = (TextView) root.findViewById(R.id.myprofile);
        aboutUs = (TextView) root.findViewById(R.id.aboutus);
        customerSupport = (TextView) root.findViewById(R.id.customersupport);
        logOut = (TextView) root.findViewById(R.id.logout);
        appTutorial = (TextView) root.findViewById(R.id.howto);
        shareApp = (TextView) root.findViewById(R.id.shareapp);
        winning = (TextView) root.findViewById(R.id.winning);
        myStatistics = (TextView) root.findViewById(R.id.mystatisics);
        topPlayer = (TextView) root.findViewById(R.id.topplayer);
        myReff = (TextView) root.findViewById(R.id.myreff);
        leaderboard = (TextView) root.findViewById(R.id.leaderboard);
        termAndCondition = (TextView) root.findViewById(R.id.tandc);
        staticResult = (LinearLayout) root.findViewById(R.id.staticsresult);
        matchesPlayed = (TextView) root.findViewById(R.id.matchesplayed);
        totalKilled = (TextView) root.findViewById(R.id.totalkilled);
        amountWon = (TextView) root.findViewById(R.id.amountwon);
        appVersion = (TextView) root.findViewById(R.id.appversion);
        userLocalStore = new UserLocalStore(getContext());

        final CurrentUser user = userLocalStore.getLoggedInUser();
        userNameforlogout = user.getUsername();

        //dashboard api call start
        mQueue = Volley.newRequestQueue(getContext());
        mQueue.getCache().clear();

        String url = getResources().getString(R.string.api) + "dashboard/" + user.getMemberid();

        final JsonObjectRequest request = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject obj = new JSONObject(response.getString("web_config"));
                            shareBody = obj.getString("share_description");
                            JSONObject memobj = new JSONObject(response.getString("member"));

                            String winmoney = memobj.getString("wallet_balance");
                            String joinmoney = memobj.getString("join_money");
                            if (TextUtils.equals(winmoney, "null")) {
                                winmoney = "0";
                            }
                            if (TextUtils.equals(joinmoney, "null")) {
                                joinmoney = "0";
                            }
                            String totalmoney = String.valueOf(Integer.parseInt(winmoney) + Integer.parseInt(joinmoney));

                            userName.setText(memobj.getString("user_name"));
                            SharedPreferences sp = getActivity().getSharedPreferences("currencyinfo", Context.MODE_PRIVATE);
                            String selectedcurrency = sp.getString("currency", "₹");
                            playCoin.setText(selectedcurrency + " " + totalmoney);
                            if (TextUtils.equals(selectedcurrency, "₹")) {
                                Typeface font = Typeface.DEFAULT;
                                SpannableStringBuilder SS = new SpannableStringBuilder(playCoin.getText().toString());
                                SS.setSpan(new CustomTypefaceSpan("", font), 0, 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                                playCoin.setText(SS);
                            }

                            JSONObject totplayobj = new JSONObject(response.getString("tot_match_play"));
                            if (TextUtils.equals(totplayobj.getString("total_match"), "null")) {
                                matchesPlayed.setText("0");
                            } else {
                                matchesPlayed.setText(totplayobj.getString("total_match"));
                            }
                            JSONObject totkillobj = new JSONObject(response.getString("tot_kill"));

                            if (TextUtils.equals(totkillobj.getString("total_kill"), "null")) {
                                totalKilled.setText("0");
                            } else {
                                totalKilled.setText(totkillobj.getString("total_kill"));
                            }

                            JSONObject totwinobj = new JSONObject(response.getString("tot_win"));

                            if (TextUtils.equals(totwinobj.getString("total_win"), "null")) {
                                amountWon.setText("0");
                            } else {
                                amountWon.setText(selectedcurrency + " " + totwinobj.getString("total_win"));
                                if (TextUtils.equals(selectedcurrency, "₹")) {
                                    Typeface font = Typeface.DEFAULT;
                                    SpannableStringBuilder SS = new SpannableStringBuilder(amountWon.getText().toString());
                                    SS.setSpan(new CustomTypefaceSpan("", font), 0, 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                                    amountWon.setText(SS);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        loadingDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("**VolleyError", "error" + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> headers = new HashMap<>();
                CurrentUser user = userLocalStore.getLoggedInUser();
                String credentials = user.getUsername() + ":" + user.getPassword();
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", auth);
                return headers;
            }
        };
        request.setShouldCache(false);
        mQueue.add(request);
        //dashboard api call end

        staticResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), MyStatisticsActivity.class));
            }
        });

        myWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), MyWalletActivity.class));
            }
        });

        myProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), MyProfileActivity.class));
            }
        });

        shareApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody + "Referral Code : " + user.getUsername());
                startActivity(Intent.createChooser(sharingIntent, "Share using"));
            }
        });

        myStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), MyStatisticsActivity.class));
            }
        });

        topPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), TopPlayerActivity.class));
            }
        });

        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), AboutusActivity.class));
            }
        });

        customerSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), CustomerSupportActivity.class));
            }
        });

        myReff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MyReferralsActivity.class));
            }
        });

        leaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), LeaderboardActivity.class));
            }
        });

        termAndCondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), TermsandConditionActivity.class));
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutall();
            }
        });

        appTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), HowtoActivity.class));
            }
        });

       /* version api call start*/
        vQueue = Volley.newRequestQueue(getContext());
        vQueue.getCache().clear();
        String vurl = getResources().getString(R.string.api) + "version/android";
        JsonObjectRequest vrequest = new JsonObjectRequest(Request.Method.GET, vurl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    appVersion.setText("Version : " + response.getString("version"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", error.toString());
            }
        });

        vrequest.setShouldCache(false);
        vQueue.add(vrequest);
        /*version api call end*/

        return root;
    }

    private void logoutall() {
        userLocalStore.clearUserData();
        Toast.makeText(getActivity(), "Log out Successfully", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getActivity(), MainActivity.class));
    }
}
