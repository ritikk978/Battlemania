//For game wise top player list
package com.official.gold.gaming.tournamentpubg.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.official.gold.gaming.tournamentpubg.models.CurrentUser;
import com.official.gold.gaming.tournamentpubg.utils.LoadingDialog;
import com.official.gold.R;
import com.official.gold.gaming.tournamentpubg.ui.adapters.TopplayerAdapter;
import com.official.gold.gaming.tournamentpubg.models.TopplayerData;
import com.official.gold.gaming.tournamentpubg.utils.UserLocalStore;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TopPlayerActivity extends AppCompatActivity {

    ImageView back;
    RecyclerView recyclerView;
    TopplayerAdapter myAdapter;
    List<TopplayerData> mData;
    RequestQueue mQueue;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_player);

        loadingDialog = new LoadingDialog(this);
        loadingDialog.show();

        back = (ImageView) findViewById(R.id.backfromtopplayer);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                intent.putExtra("N", "2");
                startActivity(intent);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.topplayerrecyclerview);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        mData = new ArrayList<>();

        mQueue = Volley.newRequestQueue(TopPlayerActivity.this);
        mQueue.getCache().clear();

        // for top player
        String url = getResources().getString(R.string.api) + "top_players";

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingDialog.dismiss();

                JSONObject player = null;
                try {
                    JSONObject jsnobject = new JSONObject(response.getString("top_players"));
                    JSONArray arrgame = response.getJSONArray("game");
                    JSON_PARSE_DATA_AFTER_WEBCALLgame(arrgame, jsnobject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VolleyError", "error" + error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> headers = new HashMap<>();
                UserLocalStore userLocalStore = new UserLocalStore(getApplicationContext());
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

    public void JSON_PARSE_DATA_AFTER_WEBCALLgame(JSONArray array, JSONObject object) {

        for (int i = 0; i < array.length(); i++) {
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);
                Log.e("game", json.getString("game_name"));
                Log.d("top player", object.getString(json.getString("game_name")));
                JSONArray arr = object.getJSONArray(json.getString("game_name"));
                JSON_PARSE_DATA_AFTER_WEBCALL(arr, json.getString("game_name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public void JSON_PARSE_DATA_AFTER_WEBCALL(JSONArray array, String gamename) {

        for (int i = 0; i < array.length(); i++) {
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);
                if (i != 0) {
                    gamename = "";
                }
                TopplayerData data = new TopplayerData(gamename, json.getString("winning"), json.getString("user_name"), json.getString("member_id"), json.getString("pubg_id"));
                Log.e("winning", json.getString("winning"));
                mData.add(data);
                myAdapter = new TopplayerAdapter(TopPlayerActivity.this, mData);
                myAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(myAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
