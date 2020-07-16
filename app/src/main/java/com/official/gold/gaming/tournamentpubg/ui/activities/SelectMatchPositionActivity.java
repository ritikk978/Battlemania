//For selecting posion in room for any tournament joining
package com.official.gold.gaming.tournamentpubg.ui.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
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
import com.official.gold.gaming.tournamentpubg.models.JoinSingleMatchData;
import com.official.gold.gaming.tournamentpubg.utils.LoadingDialog;
import com.official.gold.R;
import com.official.gold.gaming.tournamentpubg.ui.adapters.SelectMatchPositionAdapter;
import com.official.gold.gaming.tournamentpubg.utils.UserLocalStore;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectMatchPositionActivity extends AppCompatActivity {

    ImageView back;
    RequestQueue mQueue;
    TextView matchTitleBar;
    Button join;
    RecyclerView rv;
    private StaggeredGridLayoutManager manager;
    SelectMatchPositionAdapter myAdapter;
    List<JoinSingleMatchData> mData;
    LinearLayout teamAbcd;
    LinearLayout team1234;
    TextView c;
    TextView d;
    String joinStatus = "";
    String gameName = "";
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_match_position);

        loadingDialog = new LoadingDialog(this);
        loadingDialog.show();

        teamAbcd = (LinearLayout) findViewById(R.id.teamabcd);
        team1234 = (LinearLayout) findViewById(R.id.team1234);
        c = (TextView) findViewById(R.id.c);
        d = (TextView) findViewById(R.id.d);

        mData = new ArrayList<>();

        rv = (RecyclerView) findViewById(R.id.positionrv);
        join = (Button) findViewById(R.id.joinfinal);
        back = (ImageView) findViewById(R.id.backfromselectposition);
        matchTitleBar = (TextView) findViewById(R.id.matchtitlebarinposition);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SelectedGameActivity.class);
                startActivity(intent);
            }
        });
        Intent intent = getIntent();
        String mid = intent.getStringExtra("MATCH_ID");
        final String matchname = intent.getStringExtra("MATCH_NAME");
        final String type = intent.getStringExtra("TYPE");
        String no_of_position = intent.getStringExtra("TOTAL");
        joinStatus = intent.getStringExtra("JOINSTATUS");
        gameName = intent.getStringExtra("GAME_NAME");

        if (TextUtils.equals(type, "Solo")) {
            //for solo check box layout
            teamAbcd.setVisibility(View.GONE);
            team1234.setVisibility(View.GONE);
            manager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
        } else if (TextUtils.equals(type, "Duo")) {
            //for duo check box layout
            int totalteam = Integer.parseInt(no_of_position) / 2;
            c.setVisibility(View.GONE);
            d.setVisibility(View.GONE);

            for (int k = 1; k <= totalteam; k++) {
                LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lparams.gravity = Gravity.CENTER;
                LinearLayout layout = new LinearLayout(this);
                layout.setLayoutParams(lparams);
                layout.setOrientation(LinearLayout.HORIZONTAL);

                TextView textView = new TextView(this);
                textView.setLayoutParams(lparams);
                textView.setText("Team " + k);
                textView.setTextColor(Color.BLACK);
                textView.setGravity(Gravity.CENTER);
                textView.setHeight(80);
                layout.addView(textView);
                team1234.addView(layout);
            }
            manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        } else {
            //for squad check box layout
            int totalteam = Integer.parseInt(no_of_position) / 4;

            for (int k = 1; k <= totalteam; k++) {
                LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lparams.gravity = Gravity.CENTER;
                LinearLayout layout = new LinearLayout(this);
                layout.setLayoutParams(lparams);
                layout.setOrientation(LinearLayout.HORIZONTAL);

                TextView textView = new TextView(this);
                textView.setLayoutParams(lparams);
                textView.setText("Team " + k);
                textView.setTextColor(Color.BLACK);
                textView.setGravity(Gravity.CENTER);
                textView.setHeight(80);
                layout.addView(textView);
                team1234.addView(layout);
            }
            manager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
        }

        rv.setHasFixedSize(true);
        rv.setLayoutManager(manager);

        mQueue = Volley.newRequestQueue(getApplicationContext());
        mQueue.getCache().clear();
        final UserLocalStore userLocalStore = new UserLocalStore(getApplicationContext());
        final CurrentUser user = userLocalStore.getLoggedInUser();

        // for get info about blank space in room for showing check box checked or unchecked
        String url = getResources().getString(R.string.api) + "join_match_single/" + mid;

        final JsonObjectRequest request = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject msgobj = new JSONObject(response.getString("message"));
                            JSONObject matchobj = new JSONObject(msgobj.getString("match"));
                            matchTitleBar.setText(matchobj.getString("match_name"));
                            JSONArray resarr = msgobj.getJSONArray("result");
                            JSON_PARSE_DATA_AFTER_WEBCALL(resarr, matchobj.getString("m_id"), matchobj.getString("entry_fee"), matchname, type, msgobj.getString("pubg_id"));

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
        request.setShouldCache(false);
        mQueue.add(request);
    }
    public void JSON_PARSE_DATA_AFTER_WEBCALL(final JSONArray array, final String matchid, final String entryfee, final String mname, String type, final String playername) {

        String[] teamnumber = new String[array.length()];
        for (int i = 0; i < array.length(); i++) {
            try {
                final JSONObject json = array.getJSONObject(i);
                teamnumber[i] = "Team " + json.getString("team");
                if (i > 0) {
                    for (int i1 = 0; i1 < teamnumber.length; i1++) {
                        for (int j = i1 + 1; j < teamnumber.length; j++) {
                            if (TextUtils.equals(teamnumber[i1], teamnumber[j])) {
                                teamnumber[j] = "";
                            }
                        }
                    }
                }
                if (TextUtils.equals(type, "Solo")) {
                    teamnumber[i] = "";
                }
                JoinSingleMatchData data = new JoinSingleMatchData(json.getString("user_name").trim(), json.getString("pubg_id").trim(), json.getString("team"), json.getString("position"), teamnumber[i]);
                mData.add(data);
                myAdapter = new SelectMatchPositionAdapter(SelectMatchPositionActivity.this, mData);
                myAdapter.notifyDataSetChanged();
                rv.setAdapter(myAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        loadingDialog.dismiss();
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myAdapter.checkBoxList.size() == 0) {
                    Toast.makeText(SelectMatchPositionActivity.this, "Please select any position", Toast.LENGTH_SHORT).show();
                } else {
                    if (myAdapter.checkBoxList.size() != 1) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(SelectMatchPositionActivity.this);
                        dialog.setMessage("You can select only 1 Spot.");
                        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
                        //Toast.makeText(SelectMatchPositionActivity.this, "You can select only 1 Spot in Free Match.", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(getApplicationContext(), JoiningMatch.class);
                        intent.putExtra("MATCH_NAME", mname);
                        intent.putExtra("MATCH_ID", matchid);
                        intent.putExtra("ENTRY_FEE", entryfee);
                        intent.putExtra("TEAMPOSITION", myAdapter.checkBoxList.toString());
                        intent.putExtra("JOINSTATUS", joinStatus);
                        intent.putExtra("GAME_NAME", gameName);
                        intent.putExtra("PLAYER_NAME", playername);
                        startActivity(intent);
                    }
                }
            }
        });
    }
}
