//For withdraw money from app wallet
package com.official.gold.gaming.tournamentpubg.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.android.volley.AuthFailureError;
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

public class WithdrawMoneyActivity extends AppCompatActivity {

    ImageView back;
    String amount;
    int amountInt = 0;
    RequestQueue mQueue;
    LoadingDialog loadingDialog;
    TextView withdrawTitle;
    RadioGroup withdrawOption;
    RadioButton paytm;
    RadioButton phonepe;
    RadioButton googlepay;
    String withdrawMethod = "Paytm";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_money);

        back = (ImageView) findViewById(R.id.backfromwithdrawmoney);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MyWalletActivity.class);
                //intent.putExtra("N","2");
                startActivity(intent);
            }
        });

        loadingDialog = new LoadingDialog(WithdrawMoneyActivity.this);
        final EditText paytm_number = (EditText) findViewById(R.id.paytm_number);
        final EditText withdraw_amount = (EditText) findViewById(R.id.withdraw_amount);
        final Button withdraw_btn = (Button) findViewById(R.id.withdraw_btn);
        withdrawTitle = (TextView) findViewById(R.id.withdrawtitle);
        withdrawOption = (RadioGroup) findViewById(R.id.withdraw_option);
        paytm = (RadioButton) findViewById(R.id.paytm);
        paytm.setChecked(true);
        phonepe = (RadioButton) findViewById(R.id.phonepe);
        googlepay = (RadioButton) findViewById(R.id.googlepay);

        withdrawOption.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.paytm:
                        withdrawMethod = "Paytm";
                        break;
                    case R.id.phonepe:
                        withdrawMethod = "PhonePe";
                        break;
                    case R.id.googlepay:
                        withdrawMethod = "GooglePay";
                        break;
                    default:
                        withdrawMethod = "Paytm";
                }
                withdrawTitle.setText("Withdraw to " + withdrawMethod);
            }
        });

        withdraw_btn.setEnabled(false);
        withdraw_btn.setBackgroundColor(getResources().getColor(R.color.newdisablegreen));
        paytm_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() < 10 || charSequence.length() > 10) {
                    withdraw_btn.setEnabled(false);
                    withdraw_btn.setBackgroundColor(getResources().getColor(R.color.newdisablegreen));
                } else {
                    if (!TextUtils.isEmpty(withdraw_amount.getText().toString())) {
                        withdraw_btn.setEnabled(true);
                        withdraw_btn.setBackgroundColor(getResources().getColor(R.color.newgreen));
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        withdraw_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    if (!TextUtils.isEmpty(paytm_number.getText().toString()))
                        withdraw_btn.setEnabled(true);
                    withdraw_btn.setBackgroundColor(getResources().getColor(R.color.newgreen));
                } else {
                    withdraw_btn.setEnabled(false);
                    withdraw_btn.setBackgroundColor(getResources().getColor(R.color.newdisablegreen));
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        withdraw_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String paytmnumber = paytm_number.getText().toString().trim();

                amount = withdraw_amount.getText().toString().trim();
                try {
                    amountInt = Integer.parseInt(amount);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                /*withdraw api call start*/
                loadingDialog.show();
                mQueue = Volley.newRequestQueue(WithdrawMoneyActivity.this);
                mQueue.getCache().clear();

                final UserLocalStore userLocalStore = new UserLocalStore(getApplicationContext());
                final CurrentUser user = userLocalStore.getLoggedInUser();
                String url = getResources().getString(R.string.api) + "withdraw";

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("submit", "withdraw");
                hashMap.put("amount", withdraw_amount.getText().toString().trim());
                hashMap.put("pyatmnumber", paytm_number.getText().toString().trim());
                hashMap.put("withdraw_method", withdrawMethod);
                hashMap.put("member_id", user.getMemberid());

                final JsonObjectRequest request = new JsonObjectRequest(url, new JSONObject(hashMap),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                loadingDialog.dismiss();
                                try {
                                    final AlertDialog.Builder builder = new AlertDialog.Builder(WithdrawMoneyActivity.this);
                                    if (TextUtils.equals(response.getString("status"), "true")) {
                                        builder.setTitle("Succes!");
                                    } else {
                                        builder.setTitle("Error!");
                                    }
                                    builder.setMessage(response.getString("message"));
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                        }
                                    });
                                    builder.show();
                                    builder.create();
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
                /*withdraw api call end*/

                paytm_number.setText("");
                withdraw_amount.setText("");
                withdraw_btn.setEnabled(false);
            }
        });
    }
}
