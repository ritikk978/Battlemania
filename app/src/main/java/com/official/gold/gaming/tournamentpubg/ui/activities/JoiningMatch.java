//For join into any tournament or match
package com.official.gold.gaming.tournamentpubg.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.official.gold.gaming.tournamentpubg.models.CurrentUser;
import com.official.gold.gaming.tournamentpubg.utils.CustomTypefaceSpan;
import com.official.gold.gaming.tournamentpubg.utils.LoadingDialog;
import com.official.gold.R;
import com.official.gold.gaming.tournamentpubg.utils.UserLocalStore;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class JoiningMatch extends AppCompatActivity {

    TextView joinCurrentBalance;
    TextView matchEntryFeePerPersion;
    TextView totalPayableBalAmount;
    TextView joinTeam;
    TextView joinPosition;
    TextView playerName;
    RequestQueue mQueue, dQueue, jQueue;
    UserLocalStore userLocalStore;
    String memberId = null;
    private String pubgId = null;
    String currentBal = null;
    Button joinCancel;
    Button join;
    ImageView back;
    String entryFee = null;
    LinearLayout joinLl;
    String matchId = null;
    String matchName = null;
    String pTeam = null;
    String pPosition = null;
    String pPubgId = null;
    String joinStatus = null;
    String gameName = "";
    String pName = "";
    boolean canJoin = true;
    boolean nameCheck = true;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joining_match);

        loadingDialog = new LoadingDialog(this);
        loadingDialog.show();

        joinCurrentBalance = (TextView) findViewById(R.id.joincurrentbal);
        matchEntryFeePerPersion = (TextView) findViewById(R.id.matchentryfeeperperson);
        totalPayableBalAmount = (TextView) findViewById(R.id.totalpayableamount);
        joinTeam = (TextView) findViewById(R.id.jointeam);
        joinPosition = (TextView) findViewById(R.id.joinposition);
        playerName = (TextView) findViewById(R.id.playername);
        joinCancel = (Button) findViewById(R.id.joincancel);
        join = (Button) findViewById(R.id.joinjoin);
        joinLl = (LinearLayout) findViewById(R.id.joinll);

        Intent intent = getIntent();
        final String teamposition = intent.getStringExtra("TEAMPOSITION");

        matchId = intent.getStringExtra("MATCH_ID");
        matchName = intent.getStringExtra("MATCH_NAME");
        entryFee = intent.getStringExtra("ENTRY_FEE");
        joinStatus = intent.getStringExtra("JOINSTATUS");
        gameName = intent.getStringExtra("GAME_NAME");
        pName = intent.getStringExtra("PLAYER_NAME");
        playerName.setText(gameName + " Name");

        back = (ImageView) findViewById(R.id.backfromjoin);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SelectedGameActivity.class);
                startActivity(intent);
            }
        });

        userLocalStore = new UserLocalStore(getApplicationContext());
        final CurrentUser user = userLocalStore.getLoggedInUser();

        memberId = user.getMemberid();
        pubgId = pName;
        JSONArray arr = null;
        try {
            arr = new JSONArray(teamposition);
            JSON_PARSE_DATA_AFTER_WEBCALLextra(arr, pubgId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SharedPreferences sp = getSharedPreferences("currencyinfo", Context.MODE_PRIVATE);
        final String selectedcurrency = sp.getString("currency", "₹");

        //dashboard api call
        dQueue = Volley.newRequestQueue(getApplicationContext());
        dQueue.getCache().clear();

        String durl = getResources().getString(R.string.api) + "dashboard/" + user.getMemberid();

        final JsonObjectRequest drequest = new JsonObjectRequest(durl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loadingDialog.dismiss();

                        try {

                            JSONObject memobj = new JSONObject(response.getString("member"));

                            currentBal = memobj.getString("wallet_balance");
                            joinCurrentBalance.setText("Your Current Balance : " + selectedcurrency + currentBal);
                            if (TextUtils.equals(selectedcurrency, "₹")) {
                                Typeface font = Typeface.DEFAULT_BOLD;
                                SpannableStringBuilder SS = new SpannableStringBuilder(getResources().getString(R.string.Rs));
                                SS.setSpan(new CustomTypefaceSpan("", font), 0, 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                                joinCurrentBalance.setText(TextUtils.concat("Your Current Balance : ", SS, Html.fromHtml("<b>" + currentBal + "</b>")));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

        drequest.setShouldCache(false);
        dQueue.add(drequest);

        joinCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SelectedGameActivity.class));
            }
        });

    }

    public void JSON_PARSE_DATA_AFTER_WEBCALLextra(final JSONArray array, final String pubgId) {

        SharedPreferences sp = getSharedPreferences("currencyinfo", Context.MODE_PRIVATE);
        String selectedcurrency = sp.getString("currency", "₹");
        matchEntryFeePerPersion.setText("Match Entry Fee Per Persion: " + selectedcurrency + entryFee);
        if (TextUtils.equals(selectedcurrency, "₹")) {
            Typeface font = Typeface.DEFAULT_BOLD;
            SpannableStringBuilder SS = new SpannableStringBuilder(getResources().getString(R.string.Rs));
            SS.setSpan(new CustomTypefaceSpan("", font), 0, 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            matchEntryFeePerPersion.setText(TextUtils.concat("Match Entry Fee Per Persion: ", SS, entryFee));
        }
        totalPayableBalAmount.setText("Total Payable Amount : " + selectedcurrency + Integer.parseInt(entryFee) * array.length());
        if (TextUtils.equals(selectedcurrency, "₹")) {
            Typeface font = Typeface.DEFAULT_BOLD;
            SpannableStringBuilder SS = new SpannableStringBuilder(getResources().getString(R.string.Rs));
            SS.setSpan(new CustomTypefaceSpan("", font), 0, 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            totalPayableBalAmount.setText(TextUtils.concat("Total Payable Amount : ", SS, String.valueOf(Integer.parseInt(entryFee) * array.length())));
        }

        joinLl.removeAllViews();
        final String[] pidlist = new String[array.length()];
        View view;

        for (int i = 0; i < array.length(); i++) {
            JSONObject json;
            try {
                json = array.getJSONObject(i);

                view = getLayoutInflater().inflate(R.layout.selected_team_position, null);
                final TextView joinTeam = (TextView) view.findViewById(R.id.jointeam);
                final TextView joinPosition = (TextView) view.findViewById(R.id.joinposition);
                final EditText joinPlayerName = (EditText) view.findViewById(R.id.joinpubgname);
                final int finalI = i;

                joinPlayerName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }
                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }
                    @Override
                    public void afterTextChanged(Editable editable) {
                        pidlist[finalI] = editable.toString();
                    }
                });

                if (i == 0) {
                    if (!TextUtils.equals(joinStatus, "true")) {
                        joinPlayerName.setText(pubgId);
                        if (!TextUtils.equals(pubgId, "")) {
                            joinPlayerName.setEnabled(false);
                        }
                    }
                }
                pTeam = json.getString("team");
                pPosition = json.getString("position");
                joinTeam.setText("Team " + pTeam);
                joinPosition.setText(pPosition);
                joinLl.addView(view);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // For join process see comment with start and end at end of text

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int count = 0;
                nameCheck = false;
                canJoin = false;

                /* player name blank or not check start*/
                for (int check = 0; check < array.length(); check++) {
                    if (TextUtils.equals(pidlist[check], "")) {
                        Toast.makeText(JoiningMatch.this, "Please enter " + gameName + " name", Toast.LENGTH_SHORT).show();
                        nameCheck = false;
                        return;
                    } else {
                        nameCheck = true;
                    }
                }
                /*player name blank or not check end*/

                String[] member = new String[array.length()];
                JSONArray jsonArray = new JSONArray();

                for (int i = 0; i < array.length(); i++) {
                    JSONObject json = null;
                    try {
                        json = array.getJSONObject(i);
                        view = getLayoutInflater().inflate(R.layout.selected_team_position, null);
                        EditText joinPlayerName = (EditText) view.findViewById(R.id.joinpubgname);
                        if (i == count) {
                            joinPlayerName.setText(pidlist[i]);
                        }
                        pPubgId = joinPlayerName.getText().toString().trim();
                        pTeam = json.getString("team");
                        pPosition = json.getString("position");

                        JSONObject jsOb = new JSONObject();
                        jsOb.put("team", pTeam);
                        jsOb.put("position", pPosition);
                        jsOb.put("pubg_id", pPubgId);

                        jsonArray.put(jsOb);
                        count++;

                        /*check duplicate start*/
                        if (nameCheck = true) {

                            member[i] = pidlist[i];
                            if (i > 0) {
                                for (int i1 = 0; i1 < member.length; i1++) {
                                    for (int j = i1 + 1; j < member.length; j++) {
                                        if (!String.valueOf(member[i1]).equals("null") && !String.valueOf(member[j]).equals("null")) {
                                            if (TextUtils.equals(member[i1], member[j])) {
                                                Toast.makeText(JoiningMatch.this, "Please enter unique Pubg name", Toast.LENGTH_SHORT).show();
                                                canJoin = false;
                                                member = new String[0];
                                                jsonArray = new JSONArray();
                                                return;
                                            } else {
                                                canJoin = true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        /*check duplicate end*/
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                // after all validation call joinmatch
                joinmatch(matchId, memberId, matchName, jsonArray);
            }
        });
    }

    void joinmatch(final String matchId, String memberId, final String matchName, JSONArray finalJsonArray) {

        if (canJoin = true) {
            loadingDialog.show();
            jQueue = Volley.newRequestQueue(getApplicationContext());

            String jurl = getResources().getString(R.string.api) + "join_match_process";

            final UserLocalStore userLocalStore = new UserLocalStore(getApplicationContext());
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("submit", "joinnow");
                jsonObject.put("match_id", matchId);
                jsonObject.put("member_id", memberId);
                jsonObject.put("teamposition", finalJsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            final JsonObjectRequest jrequest = new JsonObjectRequest(jurl, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            loadingDialog.dismiss();
                            try {
                                if (response.getString("status").matches("true")) {
                                    Intent intent = new Intent(getApplicationContext(), SuccessJoinActivity.class);
                                    intent.putExtra("MATCH_NAME", matchName);
                                    intent.putExtra("MATCH_ID", matchId);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(JoiningMatch.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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
            jQueue.add(jrequest);
        }
    }
}
