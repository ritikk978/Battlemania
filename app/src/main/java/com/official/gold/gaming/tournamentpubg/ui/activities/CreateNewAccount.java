//For register new user
package com.official.gold.gaming.tournamentpubg.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.official.gold.gaming.tournamentpubg.utils.LoadingDialog;
import com.official.gold.R;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;

public class CreateNewAccount extends AppCompatActivity {

    EditText userNameEt, emailEt, mobileEt, passwordEt, promoCodeEt, confirmPasswordEt;
    Button registerNewAccount, signIn;
    RequestQueue mQueue;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_account);

        loadingDialog = new LoadingDialog(this);

        userNameEt = (EditText) findViewById(R.id.register_username);
        emailEt = (EditText) findViewById(R.id.register_email);
        mobileEt = (EditText) findViewById(R.id.register_mobilenumber);
        passwordEt = (EditText) findViewById(R.id.register_password);
        promoCodeEt = (EditText) findViewById(R.id.register_promocode);
        confirmPasswordEt = (EditText) findViewById(R.id.register_confirmpassword);
        registerNewAccount = (Button) findViewById(R.id.registernewaccount);

        mQueue = Volley.newRequestQueue(getApplicationContext());
        mQueue.getCache().clear();

        userNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(final CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        promoCodeEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() > 0) {

                } else {
                    registerNewAccount.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        signIn = (Button) findViewById(R.id.signin);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        registerNewAccount.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                final String userName = userNameEt.getText().toString().trim();
                final String email = emailEt.getText().toString().trim();
                final String mobileNumber = mobileEt.getText().toString().trim();
                String password = passwordEt.getText().toString().trim();
                String confirmPasssword = confirmPasswordEt.getText().toString().trim();
                final String promoCode = promoCodeEt.getText().toString().trim();

                if (TextUtils.isEmpty(userName)) {
                    userNameEt.setError("Username required...");
                    return;
                }
                if (userName.contains(" ")) {
                    userNameEt.setError("No Space Allowed");
                    return;
                }

                if (TextUtils.isEmpty(mobileNumber)) {
                    mobileEt.setError("Mobile number required...");
                    return;
                }
                if (mobileNumber.length() < 10 || mobileNumber.length() > 10) {
                    mobileEt.setError("Wrong mobile number...");
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    emailEt.setError("Email required...");
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailEt.setError("Wrong email address...");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    passwordEt.setError("Password required...");
                    return;
                }
                if (TextUtils.isEmpty(confirmPasssword)) {
                    confirmPasswordEt.setError("Retype your password...");
                    return;
                }

                if (!TextUtils.equals(password, confirmPasssword)) {
                    confirmPasswordEt.setError("Password not matched...");
                    return;
                }
                registeruser(promoCode, userName, mobileNumber, email, password, confirmPasssword, "register");
            }
        });
    }

    public void registeruser(final String promoCode, final String userNameEt, final String mobile_no, final String emailId, final String password, final String cPassword, final String submit) {

        loadingDialog.show();

        //register api call for new user
        final String URL = getResources().getString(R.string.api) + "register";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("promo_code", promoCode);
        params.put("user_name", userNameEt);
        params.put("mobile_no", mobile_no);
        params.put("email_id", emailId);
        params.put("password", password);
        params.put("cpassword", cPassword);
        params.put("submit", submit);

        JsonObjectRequest request_json = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        loadingDialog.dismiss();
                        try {
                            String status = response.getString("status");
                            String message = response.getString("message");
                            if (TextUtils.equals(status, "true")) {
                                Toast.makeText(CreateNewAccount.this, "Registration Successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            } else {
                                Toast.makeText(CreateNewAccount.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        request_json.setShouldCache(false);
        mQueue.add(request_json);
    }
}
