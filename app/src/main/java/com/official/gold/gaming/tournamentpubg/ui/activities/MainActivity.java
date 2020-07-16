// For Login
package com.official.gold.gaming.tournamentpubg.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
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

public class MainActivity extends AppCompatActivity {

    Button createNewAccount, signinMain;
    EditText userNameMain, passwordMain;
    TextView resetPassword;
    Boolean doubleBackToExitPressedOnce = false;
    RequestQueue mQueue, dQueue;
    UserLocalStore userLocalStore;
    LoadingDialog loadingDialog;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadingDialog = new LoadingDialog(this);

        mQueue = Volley.newRequestQueue(getApplicationContext());
        mQueue.getCache().clear();
        userLocalStore = new UserLocalStore(this);

        createNewAccount = (Button) findViewById(R.id.createnewaccount);
        signinMain = (Button) findViewById(R.id.signin_main);
        userNameMain = (EditText) findViewById(R.id.username_main);
        passwordMain = (EditText) findViewById(R.id.password_main);
        resetPassword = (TextView) findViewById(R.id.resetpassword);

        userNameMain.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {

                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        SpannableString ss = new SpannableString("Forgot Password? Reset Now");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {

            }
            @Override
            public void updateDrawState(TextPaint ds) {

            }
        };
        ss.setSpan(new StyleSpan(Typeface.BOLD), 17, 26, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(clickableSpan, 17, 26, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        resetPassword.setText(ss);
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //forgot password
                resetPassword();
            }
        });

        createNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CreateNewAccount.class));
            }
        });
        signinMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String password = passwordMain.getText().toString();
                String username = userNameMain.getText().toString();

                if (TextUtils.isEmpty(userNameMain.getText().toString())) {
                    userNameMain.setError("Username required...");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    passwordMain.setError("Password required...");
                    return;
                }

                //after validate call login api
                loginuser(username, password, "login");
            }
        });
    }
    public void resetPassword() {

        final Dialog builder = new Dialog(MainActivity.this);
        builder.setContentView(R.layout.resetpassword);
        final EditText fpemail = builder.findViewById(R.id.fpemail);
        final Button fpsendemail = (Button) builder.findViewById(R.id.fpsendemail);
        Button fpcancel = (Button) builder.findViewById(R.id.fpcancel);

        fpsendemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(fpemail.getText().toString()) || !android.util.Patterns.EMAIL_ADDRESS.matcher(fpemail.getText().toString()).matches()) {
                    fpemail.setError("Enter valid email");
                } else {
                    loadingDialog.show();
                    builder.dismiss();

                    //call forgotpassword api
                    mQueue = Volley.newRequestQueue(getApplicationContext());

                    String url = getResources().getString(R.string.api) + "forgotpassword";

                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("email", fpemail.getText().toString().trim());
                    params.put("submit", "forgotpass");

                    final JsonObjectRequest request = new JsonObjectRequest(url, new JSONObject(params),
                            new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    loadingDialog.dismiss();

                                    try {
                                        Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
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
                    mQueue.add(request);
                }
            }
        });

        fpcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
            }
        });
        builder.show();
    }

    public void loginuser(final String user_name, final String password, final String submit) {

        loadingDialog.show();
        //call login api
        final String URL = getResources().getString(R.string.api) + "login";

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_name", user_name);
        params.put("password", password);
        params.put("submit", submit);

        JsonObjectRequest request_json = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            String message = response.getString("message");
                            if (TextUtils.equals(status, "true")) {

                                Toast.makeText(MainActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();

                                JSONObject obj = new JSONObject(message);
                                String member_id = obj.getString("member_id");
                                CurrentUser cUser = new CurrentUser(member_id, user_name, password);
                                final UserLocalStore userLocalStore = new UserLocalStore(getApplicationContext());
                                userLocalStore.storeUserData(cUser);

                                dQueue = Volley.newRequestQueue(getApplicationContext());
                                dQueue.getCache().clear();

                                String durl = getResources().getString(R.string.api) + "dashboard/" + member_id;

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
                                                    loadingDialog.dismiss();
                                                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));

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

                            } else {
                                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                                loadingDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            loadingDialog.dismiss();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                Log.d("login error response", error.getMessage());
            }
        });
        request_json.setShouldCache(false);
        mQueue.add(request_json);
    }
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finishAffinity();
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 1500);
    }
}
