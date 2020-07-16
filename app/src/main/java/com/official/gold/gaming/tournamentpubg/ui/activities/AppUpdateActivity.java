//For update
package com.official.gold.gaming.tournamentpubg.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.official.gold.BuildConfig;
import com.official.gold.gaming.tournamentpubg.kotlin.MainMotor;
import com.official.gold.R;
import com.official.gold.gaming.tournamentpubg.utils.UserLocalStore;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static android.os.Environment.getExternalStoragePublicDirectory;

public class AppUpdateActivity extends AppCompatActivity {

    CardView cardUpdate;
    String downloadUrl = "";
    String latestVersionName = null;
    RequestQueue vQueue;
    UserLocalStore userLocalStore;
    LinearLayout llDownload;
    TextView progressDownload;
    ProgressBar progressBar;
    SharedPreferences sp;
    Boolean downloaded = false;
    TextView updateBtn;
    TextView updateInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_update);

        llDownload = (LinearLayout) findViewById(R.id.lldownload);
        progressDownload = (TextView) findViewById(R.id.progressdownload);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        updateBtn = (TextView) findViewById(R.id.updatebtn);
        updateInfo = (TextView) findViewById(R.id.updateinfo);
        cardUpdate = (CardView) findViewById(R.id.cardupdate);

        //get download url,version and update description for update app
        vQueue = Volley.newRequestQueue(getApplicationContext());

        String vurl = getResources().getString(R.string.api) + "version/android";

        JsonObjectRequest vrequest = new JsonObjectRequest(Request.Method.GET, vurl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    latestVersionName = response.getString("version");
                    downloadUrl = response.getString("url");
                    updateInfo.setText(Html.fromHtml(response.getString("description")));
                } catch (
                        JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", error.toString());
            }
        });
        vQueue.add(vrequest);

        sp = getSharedPreferences("downloadinfo", Context.MODE_PRIVATE);
        downloaded = Boolean.valueOf(sp.getString("downloaded", "false"));

        if (downloaded == true) {
            sp = getSharedPreferences("downloadinfo", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("downloaded", "false");
            editor.apply();
            installapp("battlemania-" + latestVersionName + ".apk");
        }

        cardUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (downloaded == false) {
                    // check storage permission for download new app version
                    if (ContextCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        updateBtn.setText("Downloading...");
                        cardUpdate.setEnabled(false);
                        update(downloadUrl);
                    } else {
                        //storage permission not grant request for it
                        requestStoragePermission();
                    }
                }
            }
        });
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of download the latest version of Battle Mania.")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(AppUpdateActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    private void update(String downloadUrl) {

        final String fileName = "battlemania-" + latestVersionName + ".apk";
        String url = downloadUrl;
        Log.d("download url", downloadUrl);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("Updating....");
        request.setTitle(fileName);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        final DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);

        new Thread(new Runnable() {

            @Override
            public void run() {

                boolean downloading = true;
                while (downloading) {

                    DownloadManager.Query q = new DownloadManager.Query();
                    Cursor cursor = manager.query(q);
                    cursor.moveToFirst();
                    final int bytes_downloaded = cursor.getInt(cursor
                            .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    final int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                        downloading = false;
                    }
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            llDownload.setVisibility(View.VISIBLE);
                            progressBar.setMax(100);
                            progressBar.setProgress((int) (((bytes_downloaded * 100l) / bytes_total)));
                            progressDownload.setText(String.valueOf((int) (((bytes_downloaded * 100l) / bytes_total))) + "/100");
                        }
                    });
                    cursor.close();
                }

            }
        }).start();

        BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {

                downloaded = true;
                sp = getSharedPreferences("downloadinfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("downloaded", "true");
                editor.apply();

                updateBtn.setText("Download Completed");
                cardUpdate.setEnabled(false);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    //check install unknown app permission
                    if (getPackageManager().canRequestPackageInstalls()) {
                        installapp(fileName);
                    } else {
                        Intent intentper = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intentper, 23);
                    }
                } else {
                    installapp(fileName);
                }
                unregisterReceiver(this);

            }
        };
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateBtn.setText("Downloading...");
                cardUpdate.setEnabled(false);
                update(downloadUrl);
            } else {
                Toast.makeText(this, "Storage Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 23) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Install Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void installapp(String fileName) {

        //after download install app
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            MainMotor mainMotor = new MainMotor(getApplication());
            mainMotor.install(FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".fileprovider", new File(getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS) + "/" + fileName)));
        } else {
            Intent install = new Intent(Intent.ACTION_VIEW);
            install.setDataAndType(Uri.fromFile(new File(getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS) + "/" + fileName)), "application/vnd.android.package-archive");
            install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(install);
            finish();
        }
    }
    @Override
    protected void onRestart() {
        super.onRestart();

        //after install unknown app permission app restart and install new version after download
        if (downloaded == true) {
            sp = getSharedPreferences("downloadinfo", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("downloaded", "false");
            editor.apply();
            updateBtn.setText("Download Completed");
            cardUpdate.setEnabled(false);
            installapp("battlemania-" + latestVersionName + ".apk");
        }
    }
}
