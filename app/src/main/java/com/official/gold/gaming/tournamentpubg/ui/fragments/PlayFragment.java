//For Play tab at selected home page
package com.official.gold.gaming.tournamentpubg.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.official.gold.gaming.tournamentpubg.models.CurrentUser;
import com.official.gold.gaming.tournamentpubg.utils.CustomTypefaceSpan;
import com.official.gold.gaming.tournamentpubg.models.GameData;
import com.official.gold.gaming.tournamentpubg.utils.LoadingDialog;
import com.official.gold.gaming.tournamentpubg.ui.activities.MyWalletActivity;
import com.official.gold.R;
import com.official.gold.gaming.tournamentpubg.ui.activities.SelectedGameActivity;
import com.official.gold.gaming.tournamentpubg.utils.UserLocalStore;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import static com.android.volley.Request.Method.GET;

public class PlayFragment extends Fragment {

    CardView balance;
    RequestQueue mQueue, dQueue;
    TextView balInPlay;
    UserLocalStore userLocalStore;
    TextView noUpcoming;
    LoadingDialog loadingDialog;
    ShimmerFrameLayout shimer;
    CurrentUser user;
    LinearLayout allGameLl;
    SwipeRefreshLayout pullToRefresh;

    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.play_home, container, false);

        loadingDialog = new LoadingDialog(getContext());
        shimer = (ShimmerFrameLayout) root.findViewById(R.id.shimmerplay);

        pullToRefresh = (SwipeRefreshLayout) root.findViewById(R.id.pullToRefreshplay);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefresh.setRefreshing(true);
                refresh();
                pullToRefresh.setRefreshing(false);
            }
        });

        userLocalStore = new UserLocalStore(getContext());
        user = userLocalStore.getLoggedInUser();
        allGameLl = (LinearLayout) root.findViewById(R.id.allgamell);
        balInPlay = (TextView) root.findViewById(R.id.balinplay);
        balance = (CardView) root.findViewById(R.id.balanceinplay);
        balance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), MyWalletActivity.class));
            }
        });

        mQueue = Volley.newRequestQueue(getContext());
        mQueue.getCache().clear();
        noUpcoming = (TextView) root.findViewById(R.id.noupcominginplay);

        /*dashboard start*/
        dQueue = Volley.newRequestQueue(getContext());
        String durl = getResources().getString(R.string.api) + "dashboard/" + user.getMemberid();
        final JsonObjectRequest drequest = new JsonObjectRequest(durl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
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

                            SharedPreferences sp = getActivity().getSharedPreferences("currencyinfo", Context.MODE_PRIVATE);
                            String selectedcurrency = sp.getString("currency", "₹");

                            balInPlay.setText(selectedcurrency + " " + totalmoney);

                            if (TextUtils.equals(selectedcurrency, "₹")) {
                                Typeface font = Typeface.DEFAULT;
                                SpannableStringBuilder SS = new SpannableStringBuilder(balInPlay.getText().toString());
                                SS.setSpan(new CustomTypefaceSpan("", font), 0, 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                                balInPlay.setText(SS);
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

        /*dashboard end*/
        viewallgame();
        return root;

    }

    public void viewallgame() {

        /*all_game api call start*/
        mQueue = Volley.newRequestQueue(getContext());
        mQueue.getCache().clear();
        String url = getResources().getString(R.string.api) + "all_game";
        final UserLocalStore userLocalStore = new UserLocalStore(getContext());

        final JsonObjectRequest request = new JsonObjectRequest(GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray arr = response.getJSONArray("all_game");

                            if (!TextUtils.equals(response.getString("all_game"), "[]")) {
                                noUpcoming.setVisibility(View.GONE);
                            } else {
                                noUpcoming.setVisibility(View.VISIBLE);
                            }
                            JSON_PARSE_DATA_AFTER_WEBCALL(arr);
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
        /*all_game api call end*/

    }

    public void JSON_PARSE_DATA_AFTER_WEBCALL(JSONArray array) {

        for (int i = 0; i < array.length(); i++) {
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);

                final GameData data = new GameData(json.getString("game_id"), json.getString("game_name"), json.getString("game_image"), json.getString("status"));

                View view = getLayoutInflater().inflate(R.layout.allgamedata, null);
                CardView gamecardview = (CardView) view.findViewById(R.id.gamecardview);
                ImageView gamebaner = (ImageView) view.findViewById(R.id.gamebanner);
                TextView gamename = (TextView) view.findViewById(R.id.gamename);

                Picasso.get().load(Uri.parse(data.getGameimage())).placeholder(R.drawable.default_battlemania).fit().into(gamebaner);
                gamename.setText(data.getGamename());
                gamecardview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), SelectedGameActivity.class);
                        SharedPreferences sp = getActivity().getSharedPreferences("gameinfo", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("gametitle", data.getGamename());
                        editor.putString("gameid", data.getGameid());
                        editor.apply();
                        startActivity(intent);
                    }
                });
                allGameLl.addView(view);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void refresh() {
        getActivity().getSupportFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }
}
