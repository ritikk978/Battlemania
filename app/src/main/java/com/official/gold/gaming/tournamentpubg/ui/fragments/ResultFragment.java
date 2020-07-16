//For Result tab at selected game page
package com.official.gold.gaming.tournamentpubg.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.official.gold.gaming.tournamentpubg.models.AllGameResultData;
import com.official.gold.gaming.tournamentpubg.ui.adapters.AllgameResultAdapter;
import com.official.gold.gaming.tournamentpubg.models.CurrentUser;
import com.official.gold.gaming.tournamentpubg.utils.LoadingDialog;
import com.official.gold.R;
import com.official.gold.gaming.tournamentpubg.utils.UserLocalStore;
import com.facebook.shimmer.ShimmerFrameLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.android.volley.Request.Method.GET;

public class ResultFragment extends Fragment {

    RecyclerView recyclerView;
    AllgameResultAdapter myAdapter;
    List<AllGameResultData> mData;
    RequestQueue mQueue;
    TextView noCompleted;
    LoadingDialog loadingDialog;
    ShimmerFrameLayout shimer;
    SwipeRefreshLayout pullToRefresh;
    CurrentUser user;
    UserLocalStore userLocalStore;

    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.result_home, container, false);

        loadingDialog = new LoadingDialog(getContext());

        shimer = (ShimmerFrameLayout) root.findViewById(R.id.shimmerresult);
        shimer.showShimmer(true);
        pullToRefresh = (SwipeRefreshLayout) root.findViewById(R.id.pullToRefreshresult);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefresh.setRefreshing(true);
                refresh();
                pullToRefresh.setRefreshing(false);
            }
        });
        userLocalStore = new UserLocalStore(getContext());

        recyclerView = (RecyclerView) root.findViewById(R.id.allgameresultrecyclerview);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(false);
        layoutManager.setReverseLayout(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        noCompleted = (TextView) root.findViewById(R.id.nocompleted);

        mData = new ArrayList<>();

        /*all_game_result api call start*/
        mQueue = Volley.newRequestQueue(getContext());
        mQueue.getCache().clear();

        final UserLocalStore userLocalStore = new UserLocalStore(getContext());
        user = userLocalStore.getLoggedInUser();

        SharedPreferences sp = getActivity().getSharedPreferences("gameinfo", Context.MODE_PRIVATE);
        String matchid = sp.getString("gameid", "");

        String url = getResources().getString(R.string.api) + "all_game_result/" + matchid + "/" + user.getMemberid();

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("member_id", user.getMemberid());

        final JsonObjectRequest request = new JsonObjectRequest(GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray arr = response.getJSONArray("all_game_result");
                            noCompleted.setVisibility(View.GONE);
                            if (!TextUtils.equals(response.getString("all_game_result"), "[]")) {
                                noCompleted.setVisibility(View.GONE);
                            } else {
                                noCompleted.setVisibility(View.VISIBLE);
                            }
                            JSON_PARSE_DATA_AFTER_WEBCALL(arr);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        shimer.setVisibility(View.GONE);
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
        /*all_game_result api call end*/

        return root;
    }


    public void JSON_PARSE_DATA_AFTER_WEBCALL(JSONArray array) {

        for (int i = 0; i < array.length(); i++) {
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);
                AllGameResultData data = new AllGameResultData(json.getString("m_id"), json.getString("match_banner"), json.getString("match_name"), json.getString("m_time"), json.getString("match_time"), json.getString("win_prize"), json.getString("per_kill"), json.getString("entry_fee"), json.getString("type"), json.getString("version"), json.getString("MAP"), json.getString("no_of_player"), json.getString("match_url"), json.getString("member_id"), json.getString("match_type"), json.getString("join_status"));
                mData.add(data);
                myAdapter = new AllgameResultAdapter(getContext(), mData);
                myAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(myAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void refresh() {
        getActivity().getSupportFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }
}
