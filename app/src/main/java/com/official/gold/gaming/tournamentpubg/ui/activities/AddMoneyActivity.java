//For add money to app wallet
package com.official.gold.gaming.tournamentpubg.ui.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
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
import android.widget.Toast;
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
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import org.json.JSONException;
import org.json.JSONObject;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class AddMoneyActivity extends AppCompatActivity {

    String amount;
    int amountInt = 0;
    int minAmoubtInt = 0;
    ImageView back;
    RequestQueue mQueue, rQueue, pQueue;
    UserLocalStore userLocalStore;
    String custId = "";
    String finalTid = "";
    String finalTamount = "";
    LoadingDialog loadingDialog;
    int PAYPAL_REQUEST_CODE = 1234;
    PayPalConfiguration config;
    String payment = "";
    String minAmount = "";
    String modeStatus = "";

    @Override
    protected void onDestroy() {
        //only for paypal
        if (TextUtils.equals(payment, "PayPal")) {
            stopService(new Intent(AddMoneyActivity.this, PayPalService.class));
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money);

        // payment start
        // get payment gateway info in payment api
        pQueue = Volley.newRequestQueue(getApplicationContext());
        pQueue.getCache().clear();

        String url = getResources().getString(R.string.api) + "payment";
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        loadingDialog.dismiss();
                        try {
                            modeStatus = response.getString("status");
                            payment = response.getString("payment");
                            minAmount = response.getString("min_addmoney");
                            if (TextUtils.equals(payment, "PayPal")) {
                                if (TextUtils.equals(modeStatus, "Sandbox")) {
                                    Log.d("paypall", "sandbox");
                                    config = new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
                                            .clientId(response.getString("client_id"));
                                    Intent intent = new Intent(AddMoneyActivity.this, PayPalService.class);
                                    intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                                    startService(intent);
                                } else {
                                    Log.d("paypall", "production");
                                    config = new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_PRODUCTION)
                                            .clientId(response.getString("client_id"));
                                    Intent intent = new Intent(AddMoneyActivity.this, PayPalService.class);
                                    intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                                    startService(intent);
                                }
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
                userLocalStore = new UserLocalStore(getApplicationContext());
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
        pQueue.add(request);
        //payment end

        back = (ImageView) findViewById(R.id.backfromaddmoney);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MyWalletActivity.class);
                startActivity(intent);
            }
        });

        loadingDialog = new LoadingDialog(AddMoneyActivity.this);
        userLocalStore = new UserLocalStore(getApplicationContext());
        final CurrentUser user = userLocalStore.getLoggedInUser();
        custId = user.getMemberid();
        final EditText addamountedit = (EditText) findViewById(R.id.add_amount_edit);
        final Button addamountbtn = (Button) findViewById(R.id.add_amount_btn);
        addamountbtn.setEnabled(false);
        addamountbtn.setBackgroundColor(getResources().getColor(R.color.newdisablegreen));
        addamountedit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() > 0) {
                    addamountbtn.setEnabled(true);
                    addamountbtn.setBackgroundColor(getResources().getColor(R.color.newgreen));

                } else {
                    addamountbtn.setEnabled(false);
                    addamountbtn.setBackgroundColor(getResources().getColor(R.color.newdisablegreen));
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        addamountbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amount = addamountedit.getText().toString().trim();
                try {
                    amountInt = Integer.parseInt(amount);
                    minAmoubtInt = Integer.parseInt(minAmount);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                if (amountInt < minAmoubtInt) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(AddMoneyActivity.this);
                    builder.setTitle("Error!");
                    builder.setMessage("Enter minimum " + minAmount);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.show();
                    builder.create();
                } else {

                    if (TextUtils.equals(payment, "PayPal")) {
                        // if payment gateway select paypal
                        PaypalPayment(amount);
                    } else {
                        // if payment gateway select paytm
                        loadingDialog.show();
                        //add_money for paytm start
                        mQueue = Volley.newRequestQueue(getApplicationContext());
                        mQueue.getCache().clear();

                        String url = getResources().getString(R.string.api) + "add_money";
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("MID", "");
                        params.put("ORDER_ID", "");
                        params.put("CUST_ID", custId);
                        params.put("INDUSTRY_TYPE_ID", "");
                        params.put("CHANNEL_ID", "WAP");
                        params.put("TXN_AMOUNT", amount);
                        params.put("WEBSITE", "");
                        params.put("CALLBACK_URL", getResources().getString(R.string.api) + "verifyChecksum");

                        final JsonObjectRequest request = new JsonObjectRequest(url, new JSONObject(params),
                                new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(JSONObject response) {

                                        loadingDialog.dismiss();

                                        try {
                                            if (TextUtils.equals(response.getString("status"), "false")) {
                                                Toast.makeText(AddMoneyActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                            } else {
                                                JSONObject obj = new JSONObject(response.getString("message"));
                                                String varifyurl = getResources().getString(R.string.api) + "verifyChecksum";

                                                HashMap<String, String> paramMap = new HashMap<String, String>();
                                                paramMap.put("MID", obj.getString("MID"));
                                                paramMap.put("ORDER_ID", obj.getString("ORDER_ID"));
                                                paramMap.put("TXN_AMOUNT", amount);
                                                paramMap.put("WEBSITE", obj.getString("WEBSITE"));
                                                paramMap.put("INDUSTRY_TYPE_ID", obj.getString("INDUSTRY_TYPE_ID"));
                                                paramMap.put("CUST_ID", custId);
                                                paramMap.put("CALLBACK_URL", varifyurl);
                                                paramMap.put("CHANNEL_ID", "WAP");
                                                paramMap.put("CHECKSUMHASH", obj.getString("CHECKSUMHASH"));
                                                PaytmPay(paramMap);
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
                        request.setShouldCache(false);
                        mQueue.add(request);
                        //add_money for paytm end
                    }
                }
            }
        });
    }

    public void PaytmPay(Map<String, String> paramMap) {
        PaytmPGService Service = null;

        // check test or production?
        if (TextUtils.equals(modeStatus, "Test")) {
            Service = PaytmPGService.getStagingService("");
        } else {
            Service = PaytmPGService.getProductionService();
        }

        PaytmOrder Order = new PaytmOrder((HashMap<String, String>) paramMap);
        Service.initialize(Order, null);
        Service.startPaymentTransaction(AddMoneyActivity.this, true, true, new PaytmPaymentTransactionCallback() {

            @Override
            public void someUIErrorOccurred(String inErrorMessage) {
                Toast.makeText(getApplicationContext(), " UI Error Occur. ", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onTransactionResponse(Bundle inResponse) {

                rQueue = Volley.newRequestQueue(getApplicationContext());
                rQueue.getCache().clear();
                userLocalStore = new UserLocalStore(getApplicationContext());
                //  call paytm_response  api after paytm gateway response
                String rurl = getResources().getString(R.string.api) + "paytm_response";

                HashMap<String, String> rparams = new HashMap<String, String>();
                rparams.put("order_id", inResponse.getString("ORDERID"));
                rparams.put("reason", inResponse.getString("RESPMSG"));
                rparams.put("amount", inResponse.getString("TXNAMOUNT"));
                rparams.put("banktransectionno", inResponse.getString("TXNID"));

                if (TextUtils.equals(inResponse.getString("STATUS"), "TXN_SUCCESS")) {
                    rparams.put("status", "1");
                } else {
                    rparams.put("status", "2");
                }
                finalTid = inResponse.getString("TXNID");
                finalTamount = inResponse.getString("TXNAMOUNT");

                final JsonObjectRequest rrequest = new JsonObjectRequest(rurl, new JSONObject(rparams),
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {

                                try {
                                    if (TextUtils.equals(response.getString("status"), "true")) {
                                        Intent intent = new Intent(getApplicationContext(), TansactionSuccessActivity.class);
                                        intent.putExtra("TID", finalTid);
                                        intent.putExtra("TAMOUNT", finalTamount);
                                        startActivity(intent);

                                    } else {
                                        Intent intent = new Intent(getApplicationContext(), TransactionFailActivity.class);
                                        intent.putExtra("TID", finalTid);
                                        intent.putExtra("TAMOUNT", finalTamount);
                                        startActivity(intent);
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
                rrequest.setShouldCache(false);
                rQueue.add(rrequest);
            }

            @Override
            public void networkNotAvailable() {
                Log.d("LOG", "UI Error Occur.");
                Toast.makeText(getApplicationContext(), " UI Error Occur. ", Toast.LENGTH_LONG).show();
            }

            @Override
            public void clientAuthenticationFailed(String inErrorMessage) {
                Log.d("LOG", "UI Error Occur.");
                Toast.makeText(getApplicationContext(), " Severside Error " + inErrorMessage, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onErrorLoadingWebPage(int iniErrorCode,
                                              String inErrorMessage, String inFailingUrl) {
                Log.d("LOG", inErrorMessage);
            }

            @Override
            public void onBackPressedCancelTransaction() {
                Log.d("LOG", "Back");
            }

            @Override
            public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                Log.d("LOG", "Payment Transaction Failed " + inErrorMessage);
                Toast.makeText(getApplicationContext(), "Payment Transaction Failed ", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void PaypalPayment(String amount) {

        PayPalPayment payment = new PayPalPayment(new BigDecimal(String.valueOf(amount)), "USD", "Pay for Battle Mania wallet", PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(AddMoneyActivity.this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("paypal", "in activity result");

        if (requestCode == PAYPAL_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if ((confirmation != null)) {
                    JSONObject paymentdetails = confirmation.toJSONObject();
                    try {
                        final JSONObject responsedetails = (JSONObject) paymentdetails.get("response");

                        //  call paypal_response  api after paypal gateway response
                        mQueue = Volley.newRequestQueue(getApplicationContext());
                        mQueue.getCache().clear();

                        String url = getResources().getString(R.string.api) + "paypal_response";

                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("member_id", custId);
                        params.put("id", responsedetails.getString("id"));
                        params.put("amount", String.valueOf(amountInt));
                        params.put("state", responsedetails.getString("state"));
                        final JsonObjectRequest request = new JsonObjectRequest(url, new JSONObject(params),
                                new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(JSONObject response) {

                                        loadingDialog.dismiss();
                                        try {
                                            if (TextUtils.equals(response.getString("status"), "true")) {
                                                Intent intent = new Intent(getApplicationContext(), TansactionSuccessActivity.class);
                                                intent.putExtra("TID", responsedetails.getString("id"));
                                                intent.putExtra("TAMOUNT", String.valueOf(amountInt));
                                                startActivity(intent);
                                            } else {
                                                Intent intent = new Intent(getApplicationContext(), TransactionFailActivity.class);
                                                intent.putExtra("TID", responsedetails.getString("id"));
                                                intent.putExtra("TAMOUNT", String.valueOf(amountInt));
                                                startActivity(intent);
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
                        request.setShouldCache(false);
                        mQueue.add(request);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Cancel", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Toast.makeText(getApplicationContext(), "Invalid", Toast.LENGTH_SHORT).show();
        }
    }
}
