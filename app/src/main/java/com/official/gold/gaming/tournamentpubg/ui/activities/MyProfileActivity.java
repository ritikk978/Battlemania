//For update profile and change password
package com.official.gold.gaming.tournamentpubg.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
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

public class MyProfileActivity extends AppCompatActivity {

    EditText profileFirstName;
    EditText profileLastName;
    EditText profileUserName;
    EditText profileEmail;
    EditText profileMobileNumber;
    EditText profileDateOfBirth;
    RadioGroup profileGender;
    RadioButton male;
    RadioButton female;
    Button profileSave;
    EditText oldPassword;
    EditText newPassword;
    EditText retypeNewPassword;
    Button profileReset;
    Dialog dialog;
    ImageView back;
    RequestQueue mQueue, uQueue, rQueue;
    UserLocalStore userLocalStore;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        loadingDialog = new LoadingDialog(this);
        loadingDialog.show();

        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                intent.putExtra("N", "2");
                startActivity(intent);
            }
        });

        profileFirstName = (EditText) findViewById(R.id.profile_firstname);
        profileLastName = (EditText) findViewById(R.id.profile_lastname);
        profileUserName = (EditText) findViewById(R.id.profile_username);
        profileEmail = (EditText) findViewById(R.id.profile_email);
        profileMobileNumber = (EditText) findViewById(R.id.profile_mobilenumber);
        profileDateOfBirth = (EditText) findViewById(R.id.profile_dateofbirth);
        profileGender = (RadioGroup) findViewById(R.id.profile_gender);
        male = (RadioButton) findViewById(R.id.male);
        female = (RadioButton) findViewById(R.id.female);
        profileSave = (Button) findViewById(R.id.profile_save);
        oldPassword = (EditText) findViewById(R.id.old_password);
        newPassword = (EditText) findViewById(R.id.new_password);
        retypeNewPassword = (EditText) findViewById(R.id.retype_new_password);
        profileReset = (Button) findViewById(R.id.profile_reset);

        dialog = new Dialog(this);
        userLocalStore = new UserLocalStore(getApplicationContext());
        final CurrentUser user = userLocalStore.getLoggedInUser();

        mQueue = Volley.newRequestQueue(getApplicationContext());
        mQueue.getCache().clear();

        //my_profile api for get user data
        String url = getResources().getString(R.string.api) + "my_profile/" + user.getMemberid();

        final JsonObjectRequest request = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loadingDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response.getString("my_profile"));
                            profileFirstName.setText(obj.getString("first_name"));
                            profileLastName.setText(obj.getString("last_name"));
                            if (TextUtils.equals(obj.getString("first_name"), "null")) {
                                profileFirstName.setText("");
                            }
                            if (TextUtils.equals(obj.getString("last_name"), "null")) {
                                profileLastName.setText("");
                            }
                            profileUserName.setText(obj.getString("user_name"));
                            profileEmail.setText(obj.getString("email_id"));
                            profileMobileNumber.setText(obj.getString("mobile_no"));
                            profileDateOfBirth.setText(obj.getString("dob"));
                            if (TextUtils.equals(obj.getString("dob"), "null")) {
                                profileDateOfBirth.setText("");
                            }
                            if (obj.getString("gender").matches("0")) {
                                male.setChecked(true);
                                female.setChecked(false);
                            } else if (obj.getString("gender").matches("1")) {
                                female.setChecked(true);
                                male.setChecked(false);
                            } else {
                                male.setChecked(false);
                                female.setChecked(false);
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

        profileDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setContentView(R.layout.dobpicker);

                final DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.datepicker);
                Button dob_set = (Button) dialog.findViewById(R.id.dob_set);
                Button dob_cancel = (Button) dialog.findViewById(R.id.dob_cancel);

                dob_set.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        String selecteddate = datePicker.getDayOfMonth() + "/" + (datePicker.getMonth() + 1) + "/" + datePicker.getYear();
                        profileDateOfBirth.setText(selecteddate);
                    }
                });
                dob_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        profileSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String firstname = profileFirstName.getText().toString();
                String lastname = profileLastName.getText().toString();
                final String username = profileUserName.getText().toString();
                if (username.contains(" ")) {
                    profileUserName.setError("No Space Allowed");
                    return;
                }
                String email = profileEmail.getText().toString();
                String mobilenumber = profileMobileNumber.getText().toString();
                String promocode = "";
                String dateofbirth = profileDateOfBirth.getText().toString();
                String gender = "";
                switch (profileGender.getCheckedRadioButtonId()) {
                    case R.id.male:
                        gender = "0";
                        break;
                    case R.id.female:
                        gender = "1";
                        break;
                    default:
                        gender = "-1";
                }
                loadingDialog.show();
                uQueue = Volley.newRequestQueue(getApplicationContext());
                uQueue.getCache().clear();

                //update_myprofile api call after validate all field
                String uurl = getResources().getString(R.string.api) + "update_myprofile";

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("member_id", user.getMemberid());
                params.put("first_name", firstname);
                params.put("last_name", lastname);
                params.put("user_name", username);
                params.put("mobile_no", mobilenumber);
                params.put("email_id", email);
                params.put("dob", dateofbirth);
                params.put("gender", gender);
                params.put("submit", "save");
                params.put("member_pass", user.getPassword());

                final JsonObjectRequest urequest = new JsonObjectRequest(uurl, new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                loadingDialog.dismiss();
                                try {
                                    if (response.getString("status").matches("true")) {
                                        Toast.makeText(MyProfileActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();

                                        userLocalStore = new UserLocalStore(getApplicationContext());
                                        final CurrentUser user = userLocalStore.getLoggedInUser();
                                        CurrentUser cUser = new CurrentUser(user.getMemberid(), username, user.getPassword());
                                        UserLocalStore userLocalStore = new UserLocalStore(getApplicationContext());
                                        userLocalStore.storeUserData(cUser);
                                    } else {
                                        Toast.makeText(MyProfileActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
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
                urequest.setShouldCache(false);
                uQueue.add(urequest);
            }
        });
        profileReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String old_pass = oldPassword.getText().toString();
                final String new_pass = newPassword.getText().toString().trim();
                final String retype_new_pass = retypeNewPassword.getText().toString().trim();
                if (TextUtils.isEmpty(old_pass)) {
                    oldPassword.setError("Enter Password");
                    return;
                }
                if (TextUtils.isEmpty(new_pass)) {
                    newPassword.setError("Enter new password");
                    return;
                }
                if (TextUtils.isEmpty(retype_new_pass)) {
                    retypeNewPassword.setError("Enter new password again");
                    return;
                }
                if (!TextUtils.equals(new_pass, retype_new_pass)) {
                    retypeNewPassword.setError("Password not matched...");
                    return;
                }
                loadingDialog.show();
                rQueue = Volley.newRequestQueue(getApplicationContext());
                rQueue.getCache().clear();

                //update_myprofile api call after validate all password field for password reset
                String rurl = getResources().getString(R.string.api) + "update_myprofile";

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("oldpass", old_pass);
                params.put("newpass", new_pass);
                params.put("confpass", retype_new_pass);
                params.put("submit", "reset");
                params.put("member_id", user.getMemberid());

                final JsonObjectRequest rrequest = new JsonObjectRequest(rurl, new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                loadingDialog.dismiss();
                                try {
                                    if (response.getString("status").matches("true")) {
                                        Toast.makeText(MyProfileActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                        userLocalStore = new UserLocalStore(getApplicationContext());
                                        final CurrentUser user = userLocalStore.getLoggedInUser();
                                        CurrentUser cUser = new CurrentUser(user.getMemberid(), user.getUsername(), retype_new_pass);
                                        UserLocalStore userLocalStore = new UserLocalStore(getApplicationContext());
                                        userLocalStore.storeUserData(cUser);

                                    } else {
                                        Toast.makeText(MyProfileActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                    }
                                    oldPassword.setText("");
                                    newPassword.setText("");
                                    retypeNewPassword.setText("");
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

                rrequest.setShouldCache(false);
                rQueue.add(rrequest);
            }
        });
    }
}
