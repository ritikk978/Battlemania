//For detail about customer support options
package com.official.gold.gaming.tournamentpubg.ui.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
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
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class CustomerSupportActivity extends AppCompatActivity {

    RequestQueue mQueue;
    LoadingDialog loadingDialog;
    String custAdd = "";
    String custPhone = "";
    String custEmail = "";
    String custStreet = "";
    String custTime = "";
    String custInstaId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_support);

        loadingDialog = new LoadingDialog(this);
        loadingDialog.show();

        ImageView back = (ImageView) findViewById(R.id.backfromcustomersupport);
        final TextView addressView = (TextView) findViewById(R.id.address);
        final TextView phoneView = (TextView) findViewById(R.id.phone);
        final TextView emailView = (TextView) findViewById(R.id.email);
        final TextView streetView = (TextView) findViewById(R.id.street);
        final TextView timeView = (TextView) findViewById(R.id.time);
        final TextView instagramView = (TextView) findViewById(R.id.instagram);
        final ImageView call = (ImageView) findViewById(R.id.call);
        final ImageView sms = (ImageView) findViewById(R.id.sms);
        final ImageView mail = (ImageView) findViewById(R.id.mail);
        final ImageView insta = (ImageView) findViewById(R.id.insta);

        // customer_support api call
        mQueue = Volley.newRequestQueue(this);
        mQueue.getCache().clear();

        String url = getResources().getString(R.string.api) + "customer_support";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(JSONObject response) {

                loadingDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response.getString("customer_support"));

                    custAdd = jsonObject.getString("company_address");
                    custPhone = jsonObject.getString("comapny_phone");
                    custEmail = jsonObject.getString("company_email");
                    custStreet = jsonObject.getString("company_street");
                    custTime = jsonObject.getString("company_time");
                    custInstaId = jsonObject.getString("insta_link");

                    addressView.setText("Address : " + custAdd);
                    phoneView.setText("Phone : " + custPhone);
                    emailView.setText("Email : " + custEmail);
                    instagramView.setText("Instagram : " + custInstaId);
                    streetView.setText("Street : " + custStreet);
                    timeView.setText("Time : " + custTime);

                    call.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent call_intent = new Intent(Intent.ACTION_DIAL);
                            call_intent.setData(Uri.parse("tel:" + custPhone));
                            startActivity(call_intent);
                        }
                    });

                    sms.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent sms_intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto: " + custPhone));
                            startActivity(sms_intent);
                        }
                    });
                    mail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(Intent.ACTION_SEND);
                            i.setType("message/rfc822");
                            i.putExtra(Intent.EXTRA_EMAIL, new String[]{custEmail});
                            try {
                                startActivity(Intent.createChooser(i, "Send mail..."));
                            } catch (android.content.ActivityNotFoundException ex) {
                                Toast.makeText(getApplicationContext(), "There are no email apps installed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    insta.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Uri uri = Uri.parse("https://www.instagram.com/" + custInstaId + "/");
                            Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                            likeIng.setPackage("com.instagram.android");

                            try {
                                startActivity(likeIng);
                            } catch (ActivityNotFoundException e) {
                                startActivity(new Intent(Intent.ACTION_VIEW,
                                        uri));
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", error.toString());
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

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                intent.putExtra("N", "4");
                startActivity(intent);
            }
        });
    }
}
