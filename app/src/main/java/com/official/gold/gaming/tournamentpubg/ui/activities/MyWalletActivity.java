//For add money, withdraw and transaction
package com.official.gold.gaming.tournamentpubg.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.official.gold.gaming.tournamentpubg.ui.adapters.TransactionAdapter;
import com.official.gold.gaming.tournamentpubg.models.TransactionDetails;
import com.official.gold.gaming.tournamentpubg.utils.UserLocalStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.android.volley.Request.Method.GET;


public class MyWalletActivity extends AppCompatActivity {

    ImageView back;
    TextView balance;
    TextView winMoneyTv;
    TextView joinMoneyTv;
    RequestQueue dQueue,mQueue;
    UserLocalStore userLocalStore;
    LoadingDialog loadingDialog;
    String winMoney="";
    String joinMoney="";
    String totalMoney="";
    RecyclerView transactionRv;
    TransactionAdapter myAdapter;
    List<TransactionDetails> mData;
    SwipeRefreshLayout pullToRefresh;
    Button addBtn;
    Button withdrawBtn;
    TextView earnings;
    TextView payouts;
    SharedPreferences sp;
    String selectedCurrency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wallet);

        loadingDialog=new LoadingDialog(this);
        loadingDialog.show();

        sp=getSharedPreferences("currencyinfo",Context.MODE_PRIVATE);
        selectedCurrency= sp.getString("currency","₹");

        balance=(TextView)findViewById(R.id.balanceinwallet);
        winMoneyTv=(TextView)findViewById(R.id.winmoneyinwallet);
        joinMoneyTv=(TextView)findViewById(R.id.joinmoneyinwallet);
        earnings=(TextView)findViewById(R.id.earnings);
        payouts=(TextView)findViewById(R.id.payouts);

        back=(ImageView)findViewById(R.id.backfromwallet);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),HomeActivity.class);
                intent.putExtra("N","2");
                startActivity(intent);
            }
        });
        addBtn=(Button) findViewById(R.id.addbtn);
        withdrawBtn=(Button) findViewById(R.id.withdrawbtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),AddMoneyActivity.class));
            }
        });
        withdrawBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),WithdrawMoneyActivity.class));
            }
        });

        userLocalStore = new UserLocalStore(getApplicationContext());
        final CurrentUser user = userLocalStore.getLoggedInUser();

        //dashboard api call
        dQueue = Volley.newRequestQueue(getApplicationContext());
        dQueue.getCache().clear();

        String durl = getResources().getString(R.string.api)+"dashboard/"+user.getMemberid();

        final JsonObjectRequest drequest = new JsonObjectRequest(durl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loadingDialog.dismiss();
                        try {
                            JSONObject memobj=new JSONObject(response.getString("member"));
                            winMoney=memobj.getString("wallet_balance");
                            joinMoney=memobj.getString("join_money");
                            if(TextUtils.equals(winMoney,"null")){
                                winMoney="0";
                            }
                            if(TextUtils.equals(joinMoney,"null")){
                                joinMoney="0";
                            }
                            totalMoney=String.valueOf(Integer.parseInt(winMoney)+Integer.parseInt(joinMoney));
                            balance.setText(selectedCurrency+" "+totalMoney);
                            if(TextUtils.equals(selectedCurrency,"₹")){
                                Typeface font = Typeface.DEFAULT;
                                SpannableStringBuilder SS = new SpannableStringBuilder(balance.getText().toString());
                                SS.setSpan (new CustomTypefaceSpan("", font), 0, 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                                balance.setText(SS);
                            }
                            winMoneyTv.setText("Win money : "+selectedCurrency+" "+winMoney);
                            if(TextUtils.equals(selectedCurrency,"₹")){
                                Typeface font = Typeface.DEFAULT_BOLD  ;
                                SpannableStringBuilder SS = new SpannableStringBuilder(getResources().getString(R.string.Rs));
                                SS.setSpan (new CustomTypefaceSpan("", font), 0, 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                                winMoneyTv.setText(TextUtils.concat(Html.fromHtml("Win money : "),SS,  Html.fromHtml(" "+winMoney)));
                            }
                            joinMoneyTv.setText("Join money : "+selectedCurrency+" "+joinMoney);
                            if(TextUtils.equals(selectedCurrency,"₹")){
                                Typeface font = Typeface.DEFAULT_BOLD  ;
                                SpannableStringBuilder SS = new SpannableStringBuilder(getResources().getString(R.string.Rs));
                                SS.setSpan (new CustomTypefaceSpan("", font), 0, 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                                joinMoneyTv.setText(TextUtils.concat("Join money : ",SS,  " "+joinMoney));
                            }

                            JSONObject  totwinobj=new JSONObject(response.getString("tot_win"));
                            if (TextUtils.equals(totwinobj.getString("total_win"),"null")){
                                earnings.setText(selectedCurrency+" 0");
                            }else {
                                earnings.setText(selectedCurrency+" "+totwinobj.getString("total_win"));
                            }
                            if(TextUtils.equals(selectedCurrency,"₹")){
                                Typeface font = Typeface.DEFAULT;
                                SpannableStringBuilder SS = new SpannableStringBuilder(earnings.getText().toString());
                                SS.setSpan (new CustomTypefaceSpan("", font), 0, 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                                earnings.setText(SS);
                            }

                            JSONObject  totwithobj=new JSONObject(response.getString("tot_withdraw"));

                            if (TextUtils.equals(totwithobj.getString("tot_withdraw"),"null")){
                                payouts.setText(selectedCurrency+" 0");
                            }else {
                                payouts.setText(selectedCurrency+" "+totwithobj.getString("tot_withdraw"));
                            }
                            if(TextUtils.equals(selectedCurrency,"₹")){
                                Typeface font = Typeface.DEFAULT;
                                SpannableStringBuilder SS = new SpannableStringBuilder(payouts.getText().toString());
                                SS.setSpan (new CustomTypefaceSpan("", font), 0, 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                                payouts.setText(SS);
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
        drequest.setShouldCache(false);
        dQueue.add(drequest);

        pullToRefresh=(SwipeRefreshLayout)findViewById(R.id.pullToRefreshtransaction);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefresh.setRefreshing(true);
                refresh();
                pullToRefresh.setRefreshing(false);
            }
        });

        transactionRv=(RecyclerView)findViewById(R.id.transactionrv);
        final LinearLayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(false);
        layoutManager.setReverseLayout(false);
        transactionRv.setHasFixedSize(true);
        transactionRv.setLayoutManager(layoutManager);
        mData =new ArrayList<>();

        //call transaction api
        mQueue = Volley.newRequestQueue(getApplicationContext());
        mQueue.getCache().clear();

        String url = getResources().getString(R.string.api)+"transaction";

        final JsonObjectRequest request = new JsonObjectRequest(GET,url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray arr = response.getJSONArray("transaction");
                            JSON_PARSE_DATA_AFTER_WEBCALL(arr);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        loadingDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VolleyError", "error" + error.getMessage());
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
    }
    public void JSON_PARSE_DATA_AFTER_WEBCALL(JSONArray array) {

        for (int i = 0; i < array.length(); i++) {
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);
                TransactionDetails data=new TransactionDetails(json.getString("transaction_id"),json.getString("note"),json.getString("match_id"),json.getString("note_id"),json.getString("date"),json.getString("join_money"),json.getString("win_money"),json.getString("deposit"),json.getString("withdraw"));
                mData.add(data);
                myAdapter = new TransactionAdapter(MyWalletActivity.this, mData);
                myAdapter.notifyDataSetChanged();
                transactionRv.setAdapter(myAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public void refresh(){
        finish();
        overridePendingTransition( 0, 0);
        startActivity(getIntent());
        overridePendingTransition( 0, 0);
        pullToRefresh.setRefreshing(false);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(getApplicationContext(),HomeActivity.class);
        intent.putExtra("N","2");
        startActivity(intent);
    }
}
