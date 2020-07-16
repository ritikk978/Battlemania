//For Splash screen
package com.official.gold.gaming.tournamentpubg.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
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
import com.official.gold.gaming.tournamentpubg.utils.UserLocalStore;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class FirstActivity extends AppCompatActivity {

    String versionName = null;
    String latestVersionName = null;
    RequestQueue vQueue, dQueue;
    UserLocalStore userLocalStore;
    LoadingDialog loadingDialog;
    ProgressDialog mProgressDialog;
    String downloadUrl = "";
    int waitTime = 0;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        mProgressDialog = new ProgressDialog(FirstActivity.this);
        mProgressDialog.setMessage("Downloading...");
        mProgressDialog.setTitle("Battle Game");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);

        loadingDialog = new LoadingDialog(this);
        loadingDialog.show();

        userLocalStore = new UserLocalStore(this);

        final CurrentUser user = userLocalStore.getLoggedInUser();
        Log.d("check memberid", user.getMemberid());
        if (!user.getMemberid().isEmpty()) {

            //dashboard api call for currency info
            dQueue = Volley.newRequestQueue(getApplicationContext());
            dQueue.getCache().clear();

            String durl = getResources().getString(R.string.api) + "dashboard/" + user.getMemberid();

            final JsonObjectRequest drequest = new JsonObjectRequest(durl, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {

                                JSONObject obj = new JSONObject(response.getString("web_config"));

                                sp = getSharedPreferences("currencyinfo", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                if (TextUtils.equals(obj.getString("currency"), "INR")) {
                                    editor.putString("currency", "₹");
                                } else if (TextUtils.equals(obj.getString("currency"), "USD")) {
                                    editor.putString("currency", "$");
                                }
                                editor.apply();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("**VolleyErrorfirst", "error" + error.getMessage());
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
        }

        // call version api for check latest version
        vQueue = Volley.newRequestQueue(getApplicationContext());
        vQueue.getCache().clear();

        String vurl = getResources().getString(R.string.api) + "version/android";

        JsonObjectRequest vrequest = new JsonObjectRequest(Request.Method.GET, vurl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    latestVersionName = response.getString("version");
                    downloadUrl = response.getString("url");

                    try {
                        versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                    } catch (PackageManager.NameNotFoundException e) {

                    }
                    if (TextUtils.equals(versionName, latestVersionName)) {
                        loadingDialog.dismiss();
                        final Handler tipsHanlder = new Handler();
                        Runnable tipsRunnable = new Runnable() {
                            @Override
                            public void run() {
                                tipsHanlder.postDelayed(this, 1000);

                                if (TextUtils.equals(String.valueOf(waitTime), "1")) {

                                    if (!user.getUsername().equals("") && !user.getPassword().equals("")) {

                                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));

                                    } else {
                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));

                                    }
                                }
                                waitTime++;
                            }
                        };
                        tipsHanlder.post(tipsRunnable);

                    } else {
                        loadingDialog.dismiss();
                        startActivity(new Intent(getApplicationContext(), AppUpdateActivity.class));
                        final AlertDialog.Builder builder = new AlertDialog.Builder(FirstActivity.this);
                        builder.setCancelable(false);
                        builder.setTitle("New Version available");
                        builder.setMessage("Download Now....");
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                                System.exit(0);
                            }
                        });
                        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("errorversion", error.toString());
            }
        });

        vrequest.setShouldCache(false);
        vQueue.add(vrequest);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        recreate();
    }
}
