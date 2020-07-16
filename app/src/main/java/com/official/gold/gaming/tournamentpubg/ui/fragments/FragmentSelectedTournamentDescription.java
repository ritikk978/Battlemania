//For show description in selected tournament or match in tab layout
package com.official.gold.gaming.tournamentpubg.ui.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
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
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.official.gold.R;
import com.official.gold.gaming.tournamentpubg.models.CurrentUser;
import com.official.gold.gaming.tournamentpubg.utils.CustomTypefaceSpan;
import com.official.gold.gaming.tournamentpubg.utils.LoadingDialog;
import com.official.gold.gaming.tournamentpubg.utils.UserLocalStore;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class FragmentSelectedTournamentDescription extends Fragment {

    TextView matchTitleAndNumber;
    TextView team;
    TextView entryFee;
    TextView mode;
    TextView matchType;
    TextView map;
    TextView matchSchedule;
    TextView winningPrize;
    TextView perKill;
    TextView about;
    TextView sponser;
    CardView rData;
    LinearLayout roomDetail;
    TextView roomId;
    TextView roomPass;
    Button playNow;
    LinearLayout joinedll;
    LinearLayout sponsorll;
    String mIds,matchNames,matchTimes,winPrizes,perKills,entryFees,type,version,maps,matchTypes,matchDescs,noOfPlayers,numberOfPosition,memberId,matchUrl,roomIds,roomPassword,matchSponsers;
    String join_status=null;
    String packagename="";
    RequestQueue mQueue;
    LoadingDialog loadingDialog;
    SwipeRefreshLayout pullToRefresh;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_selectedtournament_description, container, false);

        pullToRefresh=(SwipeRefreshLayout)root.findViewById(R.id.pullToRefreshselecttournament);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefresh.setRefreshing(true);
                refresh();
                pullToRefresh.setRefreshing(false);
            }
        });
        matchTitleAndNumber=(TextView)root.findViewById(R.id.matchtitleandnumber);
        team=(TextView)root.findViewById(R.id.team);
        entryFee=(TextView)root.findViewById(R.id.entryfee);
        mode=(TextView)root.findViewById(R.id.mode);
        matchType=(TextView)root.findViewById(R.id.matchtype);
        map=(TextView)root.findViewById(R.id.map);
        matchSchedule=(TextView)root.findViewById(R.id.matchschedule);
        winningPrize=(TextView)root.findViewById(R.id.winningprize);
        perKill=(TextView)root.findViewById(R.id.perkill);
        about=(TextView)root.findViewById(R.id.about);
        sponser=(TextView)root.findViewById(R.id.sponsor);
        rData=(CardView) root.findViewById(R.id.registereddata);
        roomDetail=(LinearLayout) root.findViewById(R.id.roomdetailll);
        roomId=(TextView)root.findViewById(R.id.roomid);
        roomPass=(TextView)root.findViewById(R.id.roompass);
        playNow=(Button)root.findViewById(R.id.playnow);
        joinedll=(LinearLayout)root.findViewById(R.id.joinedll);
        sponsorll=(LinearLayout)root.findViewById(R.id.sponsorll);
        rData.setVisibility(View.GONE);

        Intent intent=getActivity().getIntent();
        final String mid=intent.getStringExtra("M_ID");

        SharedPreferences sp=getContext().getSharedPreferences("currencyinfo", Context.MODE_PRIVATE);
        final String selectedcurrency= sp.getString("currency","₹");

        //single match api call for description of that match
        mQueue = Volley.newRequestQueue(getActivity());
        mQueue.getCache().clear();
        final UserLocalStore userLocalStore = new UserLocalStore(getActivity());
        final CurrentUser user = userLocalStore.getLoggedInUser();

        String url = getResources().getString(R.string.api)+"single_match/"+mid+"/"+user.getMemberid();

        final JsonObjectRequest request = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONObject obj=response.getJSONObject("match");
                            Log.d("selected play ",obj.toString());

                            mIds=obj.getString("m_id");
                            matchNames=obj.getString("match_name");
                            matchTimes=obj.getString("match_time");
                            winPrizes=obj.getString("win_prize");
                            perKills=obj.getString("per_kill");
                            entryFees=obj.getString("entry_fee");
                            type=obj.getString("type");
                            version=obj.getString("version");
                            maps=obj.getString("MAP");
                            matchTypes=obj.getString("match_type");
                            matchDescs=obj.getString("match_desc");
                            noOfPlayers=obj.getString("no_of_player");
                            numberOfPosition=obj.getString("number_of_position");
                            memberId=obj.getString("member_id");
                            matchUrl=obj.getString("match_url");
                            roomIds=obj.getString("room_id");
                            roomPassword=obj.getString("room_password");
                            join_status=obj.getString("join_status");
                            matchSponsers=obj.getString("match_sponsor");

                            packagename=obj.getString("package_name");
                            matchTitleAndNumber.setText(matchNames+" - Match #"+mIds);
                            team.setText(type);
                            entryFee.setText(selectedcurrency+" "+entryFees);
                            if(TextUtils.equals(selectedcurrency,"₹")){
                                Typeface font = Typeface.DEFAULT;
                                SpannableStringBuilder SS = new SpannableStringBuilder(entryFee.getText().toString());
                                SS.setSpan (new CustomTypefaceSpan("", font), 0, 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                                entryFee.setText(SS);
                            }
                            mode.setText(version);
                            if (matchTypes.matches("1")){
                                matchType.setText("PAID");
                            }else {
                                matchType.setText("FREE");
                            }
                            map.setText(maps);
                            matchSchedule.setText(matchTimes);
                            winningPrize.setText(selectedcurrency+" "+winPrizes);
                            if(TextUtils.equals(selectedcurrency,"₹")){
                                Typeface font = Typeface.DEFAULT;
                                SpannableStringBuilder SS = new SpannableStringBuilder(winningPrize.getText().toString());
                                SS.setSpan (new CustomTypefaceSpan("", font), 0, 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                                winningPrize.setText(SS);
                            }
                            perKill.setText(selectedcurrency+" "+perKills);
                            if(TextUtils.equals(selectedcurrency,"₹")){
                                Typeface font = Typeface.DEFAULT;
                                SpannableStringBuilder SS = new SpannableStringBuilder(perKill.getText().toString());
                                SS.setSpan (new CustomTypefaceSpan("", font), 0, 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                                perKill.setText(SS);
                            }
                            about.setText(Html.fromHtml(matchDescs));
                            about.setMovementMethod(LinkMovementMethod.getInstance());

                            if(TextUtils.equals(matchSponsers,"")){
                                sponsorll.setVisibility(View.GONE);
                            }else {
                                sponsorll.setVisibility(View.VISIBLE);
                                sponser.setText(Html.fromHtml(matchSponsers));
                                sponser.setMovementMethod(LinkMovementMethod.getInstance());

                            }

                            //if room id and password updated from admin side and you joined that match then display id and pass
                            roomDetail.setVisibility(View.GONE);
                            if(TextUtils.equals(roomIds,"") || TextUtils.equals(roomPassword,"") || !TextUtils.equals(join_status,"true")){
                                roomDetail.setVisibility(View.GONE);
                            }else {
                                roomDetail.setVisibility(View.VISIBLE);
                                roomId.setText(roomIds);
                                roomPass.setText(roomPassword);
                                roomId.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                                        ClipData clip = ClipData.newPlainText("Room ID", roomIds);
                                        clipboard.setPrimaryClip(clip);
                                        Toast.makeText(getActivity(), "Room ID Copied Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                roomPass.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                                        ClipData clip = ClipData.newPlainText("Room Password", roomPassword);
                                        clipboard.setPrimaryClip(clip);
                                        Toast.makeText(getActivity(), "Room Password Copied Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                // for open game
                                playNow.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        openApplication(getActivity(),packagename);
                                    }
                                });
                            }
                            JSONArray joinarr = response.getJSONArray("join_position");

                            // after join you will see room detail
                            if(join_status.matches("true")==true){
                                rData.setVisibility(View.VISIBLE);
                                JSON_PARSE_DATA_AFTER_WEBCALL_JOIN(joinarr);
                            }else {

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
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> headers = new HashMap<>();
                CurrentUser user = userLocalStore.getLoggedInUser();
                String credentials = user.getUsername()+":"+user.getPassword();
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", auth);
                return headers;
            }
        };
        request.setShouldCache(false);
        mQueue.add(request);

        return root;
    }

    public void JSON_PARSE_DATA_AFTER_WEBCALL_JOIN(JSONArray array) {

        for (int i = 0; i < array.length(); i++) {
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);

                View view = getLayoutInflater().inflate(R.layout.joined_member_data, null);
                TextView r_team=(TextView)view.findViewById(R.id.registeredteam);
                TextView r_position=(TextView)view.findViewById(R.id.registeredposition);
                TextView r_pubgname=(TextView)view.findViewById(R.id.registeredpubgname);

                r_team.setText("Team "+json.getString("team"));
                r_position.setText(json.getString("position"));
                r_pubgname.setText(json.getString("pubg_id"));
                joinedll.addView(view);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void openApplication(Context context, String packageN) {
        Intent i = context.getPackageManager().getLaunchIntentForPackage(packageN);

        if (i != null) {
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            context.startActivity(i);
        } else {
            try {
                Intent intent =new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageN));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
            catch (android.content.ActivityNotFoundException anfe) {
                Intent intent =new Intent(Intent.ACTION_VIEW,Uri.parse("Uri.parse(\"http://play.google.com/store/apps/details?id=\"" + packageN));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }
    }

    public void refresh(){
        getActivity().getSupportFragmentManager().beginTransaction().detach(this).attach(this).commit();

    }
}
