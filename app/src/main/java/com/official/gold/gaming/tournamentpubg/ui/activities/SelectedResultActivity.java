//For description about completed match
package com.official.gold.gaming.tournamentpubg.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class SelectedResultActivity extends AppCompatActivity {

    ImageView back;
    RequestQueue mQueue;
    TextView resultTitleAndNumber;
    TextView resultTime;
    TextView resultPrizePool;
    TextView resultPerKill;
    TextView resultEntryFee;
    LinearLayout winResultLl;
    LinearLayout fullResultLl;
    LinearLayout resultNotificationCardview;
    TextView resultNotification;
    LoadingDialog loadingDialog;
    CardView imageViewSelectedCardview;
    ImageView imgeviewSelected;
    SwipeRefreshLayout pullToRefresh;
    UserLocalStore userLocalStore;
    CurrentUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_result);

        loadingDialog = new LoadingDialog(this);
        loadingDialog.show();

        pullToRefresh = (SwipeRefreshLayout) findViewById(R.id.pullToRefreshselectresult);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                pullToRefresh.setRefreshing(true);
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
                pullToRefresh.setRefreshing(false);
            }
        });
        resultTitleAndNumber = (TextView) findViewById(R.id.resulttitleandnumber);
        resultTime = (TextView) findViewById(R.id.resulttime);
        resultPrizePool = (TextView) findViewById(R.id.resultprizepool);
        resultPerKill = (TextView) findViewById(R.id.resultperkill);
        resultEntryFee = (TextView) findViewById(R.id.resultentryfee);
        resultNotificationCardview = (LinearLayout) findViewById(R.id.resultnotificationll);
        resultNotification = (TextView) findViewById(R.id.resultnotification);
        winResultLl = (LinearLayout) findViewById(R.id.winresultll);
        fullResultLl = (LinearLayout) findViewById(R.id.fullresultll);
        back = (ImageView) findViewById(R.id.backfromselectedresult);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SelectedGameActivity.class);
                intent.putExtra("N", "2");
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        String mid = intent.getStringExtra("M_ID");
        String baner = intent.getStringExtra("BANER");

        imageViewSelectedCardview = (CardView) findViewById(R.id.imageviewselectedresultcardview);
        imgeviewSelected = (ImageView) findViewById(R.id.imageviewselectedresult);
        if (!TextUtils.equals(baner, "")) {
            imageViewSelectedCardview.setVisibility(View.VISIBLE);
            Picasso.get().load(Uri.parse(baner)).placeholder(R.drawable.default_battlemania).fit().into(imgeviewSelected);
        } else {
            imgeviewSelected.setImageDrawable(getDrawable(R.drawable.default_battlemania));
        }
        SharedPreferences sp = getSharedPreferences("currencyinfo", Context.MODE_PRIVATE);
        final String selectedcurrency = sp.getString("currency", "₹");
        mQueue = Volley.newRequestQueue(getApplicationContext());
        mQueue.getCache().clear();
        userLocalStore = new UserLocalStore(getApplicationContext());
        user = userLocalStore.getLoggedInUser();

        //call single_game_result for detail about result tab tournament
        String url = getResources().getString(R.string.api) + "single_game_result/" + mid;

        final JsonObjectRequest request = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject obj = response.getJSONObject("match_deatils");
                            resultTitleAndNumber.setText(obj.getString("match_name") + " - Match #" + obj.getString("m_id"));
                            resultTime.setText(Html.fromHtml("Organised on <b>" + obj.getString("match_time") + "</b>"));
                            resultPrizePool.setText(Html.fromHtml("Winning Prize : <b>" + selectedcurrency + " " + obj.getString("win_prize") + "</b>"));
                            if (TextUtils.equals(selectedcurrency, "₹")) {
                                Typeface font = Typeface.DEFAULT_BOLD;
                                SpannableStringBuilder SS = new SpannableStringBuilder(getResources().getString(R.string.Rs));
                                SS.setSpan(new CustomTypefaceSpan("", font), 0, 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                                resultPrizePool.setText(TextUtils.concat(Html.fromHtml("Winning Prize  : "), SS, Html.fromHtml("<b>" + obj.getString("win_prize") + "</b>")));
                            }
                            resultPerKill.setText(Html.fromHtml("Per Kill : <b>" + selectedcurrency + " " + obj.getString("per_kill") + "</b>"));
                            if (TextUtils.equals(selectedcurrency, "₹")) {
                                Typeface font = Typeface.DEFAULT_BOLD;
                                SpannableStringBuilder SS = new SpannableStringBuilder(getResources().getString(R.string.Rs));
                                SS.setSpan(new CustomTypefaceSpan("", font), 0, 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                                resultPerKill.setText(TextUtils.concat(Html.fromHtml("Per Kill : "), SS, Html.fromHtml("<b>" + obj.getString("per_kill") + "</b>")));
                            }
                            resultEntryFee.setText(Html.fromHtml("Entry Fee : <b>" + selectedcurrency + " " + obj.getString("entry_fee") + "</b>"));
                            if (TextUtils.equals(selectedcurrency, "₹")) {
                                Typeface font = Typeface.DEFAULT_BOLD;
                                SpannableStringBuilder SS = new SpannableStringBuilder(getResources().getString(R.string.Rs));
                                SS.setSpan(new CustomTypefaceSpan("", font), 0, 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                                resultEntryFee.setText(TextUtils.concat(Html.fromHtml("Entry fee : <b>"), SS, Html.fromHtml("<b>" + obj.getString("entry_fee") + "</b>")));
                            }
                            if (TextUtils.equals(obj.getString("result_notification"), "null") || TextUtils.equals(obj.getString("result_notification"), "")) {
                                resultNotificationCardview.setVisibility(View.GONE);
                            } else {
                                resultNotificationCardview.setVisibility(View.VISIBLE);
                                resultNotification.setText(obj.getString("result_notification"));
                            }
                            JSONArray winarr = response.getJSONArray("match_winner");
                            JSON_PARSE_DATA_AFTER_WEBCALL(winarr);
                            JSONArray fullarr = response.getJSONArray("full_result");
                            JSON_PARSE_DATA_AFTER_WEBCALL_FULL(fullarr);
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
    }

    public void JSON_PARSE_DATA_AFTER_WEBCALL(JSONArray array) {

        for (int i = 0; i < array.length(); i++) {
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);
                View view1 = getLayoutInflater().inflate(R.layout.selectedresultdata, null);
                TextView winno = (TextView) view1.findViewById(R.id.no);
                TextView winpname = (TextView) view1.findViewById(R.id.pname);
                TextView winkills = (TextView) view1.findViewById(R.id.kills);
                TextView winwining = (TextView) view1.findViewById(R.id.wining);

                winno.setText(String.valueOf(i + 1));
                winpname.setText(json.getString("pubg_id"));
                winkills.setText(json.getString("killed"));
                winwining.setText(json.getString("total_win"));
                winResultLl.addView(view1);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void JSON_PARSE_DATA_AFTER_WEBCALL_FULL(JSONArray array) {

        for (int i = 0; i < array.length(); i++) {
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);

                View view2 = getLayoutInflater().inflate(R.layout.selectedresultdata, null);
                TextView winno = (TextView) view2.findViewById(R.id.no);
                TextView winpname = (TextView) view2.findViewById(R.id.pname);
                TextView winkills = (TextView) view2.findViewById(R.id.kills);
                TextView winwining = (TextView) view2.findViewById(R.id.wining);

                winno.setText(String.valueOf(i + 1));
                winpname.setText(json.getString("pubg_id"));
                winkills.setText(json.getString("killed"));
                winwining.setText(json.getString("total_win"));
                if (TextUtils.equals(json.getString("user_name"), user.getUsername())) {
                    fullResultLl.addView(view2, 0);
                } else {
                    fullResultLl.addView(view2);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
