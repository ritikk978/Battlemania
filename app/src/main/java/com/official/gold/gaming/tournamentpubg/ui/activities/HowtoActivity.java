//For app tutorial
package com.official.gold.gaming.tournamentpubg.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HowtoActivity extends AppCompatActivity {

    ImageView back;
    RequestQueue mQueue;
    LoadingDialog loadingDialog;
    LinearLayout howtoLl;
    String videoId = "";
    String description = "";
    String videoUrl = "";
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_howto);

        loadingDialog = new LoadingDialog(this);
        loadingDialog.show();

        howtoLl = (LinearLayout) findViewById(R.id.howtoll);
        back = (ImageView) findViewById(R.id.backfromhowto);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                intent.putExtra("N", "2");
                startActivity(intent);
            }
        });

        //for youtube link call youtube_link api
        mQueue = Volley.newRequestQueue(this);
        mQueue.getCache().clear();

        String url = getResources().getString(R.string.api) + "youtube_link";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingDialog.dismiss();

                JSONArray arr = null;
                try {
                    arr = response.getJSONArray("youtube_links");
                    JSON_PARSE_DATA_AFTER_WEBCALL(arr);
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

        if (savedInstanceState != null) {
            webView.loadData(videoUrl, "text/html", "utf-8");
        }
    }
    public void JSON_PARSE_DATA_AFTER_WEBCALL(JSONArray array) {

        for (int i = 0; i < array.length(); i++) {
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);

                description = json.getString("youtube_link_title");
                String url = json.getString("youtube_link");
                String regExp = "/.*(?:youtu.be\\/|v\\/|u/\\w/|embed\\/|watch\\?.*&?v=)";
                Pattern compiledPattern = Pattern.compile(regExp);
                Matcher matcher = compiledPattern.matcher(url);
                if (matcher.find()) {
                    int start = matcher.end();
                    //Log.d("check", url.substring(start, start+11));
                    videoId = url.substring(start, start + 11);
                }
                View view = getLayoutInflater().inflate(R.layout.howto_view, null);
                webView = (WebView) view.findViewById(R.id.webview);
                webView.setWebChromeClient(new MyCrome() {
                });
                webView.getSettings().setJavaScriptEnabled(true);
                videoUrl = "<html><body style=\"line-height:25px;\">  <b> " + description + "</b><br><iframe width=\"100%\" height=\"90%\" src=\"http://www.youtube.com/embed/" + videoId + "\" frameborder=\"0\" allowfullscreen></iframe></body></html>";
                webView.loadData(videoUrl, "text/html", "utf-8");

                Button play = (Button) view.findViewById(R.id.playinyoutube);
                play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://" + videoId));
                        startActivity(intent);
                    }
                });

                howtoLl.addView(view);

            } catch (JSONException e) {

                e.printStackTrace();
            }
        }
    }
    public class MyCrome extends WebChromeClient {

        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        protected FrameLayout mFullscreenContainer;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        MyCrome() {
            // Required to play YouTube video in some android versions
        }

        public Bitmap getDefaultVideoPoster() {
            if (HowtoActivity.this == null) {
                return Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
            }
            return Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
        }
        public void onHideCustomView() {

            ((FrameLayout) HowtoActivity.this.getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            HowtoActivity.this.getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            HowtoActivity.this.setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;

        }

        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback) {
            if (this.mCustomView != null) {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            mCustomView.setBackgroundColor(Color.BLACK);
            this.mOriginalSystemUiVisibility = HowtoActivity.this.getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = HowtoActivity.this.getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout) HowtoActivity.this.getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            HowtoActivity.this.getWindow().getDecorView().setSystemUiVisibility(3846);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        webView.restoreState(savedInstanceState);
    }
}
