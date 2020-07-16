//For Earn tab at home page
package com.official.gold.gaming.tournamentpubg.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.official.gold.gaming.tournamentpubg.models.CurrentUser;
import com.official.gold.gaming.tournamentpubg.utils.CustomTypefaceSpan;
import com.official.gold.gaming.tournamentpubg.utils.LoadingDialog;
import com.official.gold.gaming.tournamentpubg.ui.activities.MyWalletActivity;
import com.official.gold.R;
import com.official.gold.gaming.tournamentpubg.ui.activities.ReferandEarnActivity;
import com.official.gold.gaming.tournamentpubg.utils.UserLocalStore;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class EarnFragment extends Fragment {

    TextView noReferEarn;
    CardView balance;
    RequestQueue dQueue;
    UserLocalStore userLocalStore;
    LinearLayout refll;
    TextView balInEarn;
    TextView reftv;
    LoadingDialog loadingDialog;
    CurrentUser user;
    SharedPreferences sp;
    CardView referAndEarn;
    CardView scratchAndWin;
    String shareBody = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.earn_home, container, false);

        loadingDialog = new LoadingDialog(getActivity());
        sp = getActivity().getSharedPreferences("tabinfo", Context.MODE_PRIVATE);
        String selectedtab = sp.getString("selectedtab", "");
        if (TextUtils.equals(selectedtab, "0")) {
            loadingDialog.show();
        }
        referAndEarn = (CardView) view.findViewById(R.id.referandearn);
        scratchAndWin = (CardView) view.findViewById(R.id.scratchandwin);
        noReferEarn = (TextView) view.findViewById(R.id.noreferearn);
        referAndEarn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ReferandEarnActivity.class));
            }
        });

        ImageView share = (ImageView) view.findViewById(R.id.share);
        balInEarn = (TextView) view.findViewById(R.id.balinearn);
        reftv = (TextView) view.findViewById(R.id.reftv);
        refll = (LinearLayout) view.findViewById(R.id.refll);

        userLocalStore = new UserLocalStore(getContext());
        user = userLocalStore.getLoggedInUser();

        //dashboard api call start
        dQueue = Volley.newRequestQueue(getContext());
        dQueue.getCache().clear();

        String durl = getResources().getString(R.string.api) + "dashboard/" + user.getMemberid();

        final JsonObjectRequest drequest = new JsonObjectRequest(durl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject obj = new JSONObject(response.getString("web_config"));
                            if (TextUtils.equals(obj.getString("active_referral"), "1")) {
                                noReferEarn.setVisibility(View.GONE);
                            } else {
                                noReferEarn.setVisibility(View.VISIBLE);
                            }
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

                            SharedPreferences sp = getActivity().getSharedPreferences("currencyinfo", Context.MODE_PRIVATE);
                            String selectedcurrency = sp.getString("currency", "₹");
                            balInEarn.setText(selectedcurrency + " " + totalmoney);

                            if (TextUtils.equals(selectedcurrency, "₹")) {
                                Typeface font = Typeface.DEFAULT;
                                SpannableStringBuilder SS = new SpannableStringBuilder(balInEarn.getText().toString());
                                SS.setSpan(new CustomTypefaceSpan("", font), 0, 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                                balInEarn.setText(SS);
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

        drequest.setShouldCache(false);
        dQueue.add(drequest);
        // dashboard api call end

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody + " Referral Code : " + user.getUsername());
                startActivity(Intent.createChooser(sharingIntent, "Share using"));
            }
        });

        balance = (CardView) view.findViewById(R.id.balanceinearn);
        balance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), MyWalletActivity.class));
            }
        });
        return view;
    }

    public void JSON_PARSE_DATA_AFTER_WEBCALL(JSONArray array) {

        for (int i = 0; i < array.length(); i++) {
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);
                View view = getLayoutInflater().inflate(R.layout.referral_data, null);

                TextView rdate = (TextView) view.findViewById(R.id.rdate);
                TextView rplayername = (TextView) view.findViewById(R.id.rplayername);
                TextView rstatus = (TextView) view.findViewById(R.id.rstatus);

                rdate.setText(json.getString("date"));
                rplayername.setText(json.getString("user_name"));
                rstatus.setText(json.getString("status"));
                if (TextUtils.equals(json.getString("status"), "Upgraded")) {
                    rstatus.setTextColor(Color.parseColor("#008000"));
                } else {
                    rstatus.setTextColor(Color.BLACK);
                }
                refll.addView(view);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

