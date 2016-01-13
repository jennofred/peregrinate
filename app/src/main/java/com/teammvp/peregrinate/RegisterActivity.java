package com.teammvp.peregrinate;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Text;


public class RegisterActivity extends AppCompatActivity {

    String emailStr, passwordStr, usernameStr, birthdayStr, genderStr;
    EditText emailTextFieldSignUp, usernameTextFieldSignUp, passwordTextFieldSignUp, birthdayTextFieldSignUp, genderTextFieldSignUp;
    ImageView emptyEmail, emptyUsername, emptyPassword, emptyBirthday, emptyGender;
    Button signUpButton;
    ListView gender = null;
    AlertDialog genderDialog = null;

    NetworkInfo mWifi;
    NetworkInfo mMobile;
    ConnectivityManager connManager;

    ProgressDialog dialog = null;

    HttpPost httppost;
    StringBuffer buffer;
    HttpResponse response;
    HttpClient httpclient;
    List<NameValuePair> nameValuePairs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.shared_element_transition_login));
        }

        Typeface bebasNeue = Typeface.createFromAsset(getAssets(), "fonts/BebasNeue.otf");
        Typeface helveticaNeue = Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeue.otf");

        emailTextFieldSignUp = (EditText) findViewById(R.id.emailTextFieldSignUp);
        usernameTextFieldSignUp = (EditText) findViewById(R.id.usernameTextFieldSignUp);
        passwordTextFieldSignUp = (EditText) findViewById(R.id.passwordTextFieldSignUp);
        birthdayTextFieldSignUp = (EditText) findViewById(R.id.birthdayTextFieldSignUp);
        genderTextFieldSignUp = (EditText) findViewById(R.id.genderTextFieldSignUp);
        signUpButton = (Button) findViewById(R.id.signUpButton);

        emailTextFieldSignUp.setTypeface(helveticaNeue);
        usernameTextFieldSignUp.setTypeface(helveticaNeue);
        passwordTextFieldSignUp.setTypeface(helveticaNeue);
        birthdayTextFieldSignUp.setTypeface(helveticaNeue);
        genderTextFieldSignUp.setTypeface(helveticaNeue);
        signUpButton.setTypeface(bebasNeue);

        emptyEmail = (ImageView) findViewById(R.id.emptyEmail);
        emptyPassword = (ImageView) findViewById(R.id.emptyPassword);
        emptyUsername = (ImageView) findViewById(R.id.emptyUsername);
        emptyBirthday = (ImageView) findViewById(R.id.emptyBirthday);
        emptyGender = (ImageView) findViewById(R.id.emptyGender);

        birthdayTextFieldSignUp.setInputType(0);
        genderTextFieldSignUp.setInputType(0);

        gender = new ListView(this);
        String[] genderString = {"Male", "Female"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(this, R.layout.gender_list, R.id.genderList, genderString);
        gender.setAdapter(genderAdapter);
        gender.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewGroup viewGroup = (ViewGroup) view;
                TextView txt = (TextView) viewGroup.findViewById(R.id.genderList);
                String selectedGender = txt.getText().toString();
                genderTextFieldSignUp.setText(selectedGender);
                genderDialog.dismiss();
            }
        });

        connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

//        mWifi = connManager.getActiveNetworkInfo();
        mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        mMobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        birthdayTextFieldSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(birthdayTextFieldSignUp.getWindowToken(), 0);

                DatePickerDialog dialog = new DatePickerDialog(v);
                dialog.setCancelable(true);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                dialog.show(ft, "DatePicker");
            }
        });

        genderTextFieldSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(genderTextFieldSignUp.getWindowToken(), 0);
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                builder.setCancelable(true);
                builder.setView(gender);
                if (genderDialog == null) {
                    genderDialog = builder.create();
                }
                genderDialog.show();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emptyEmail.setVisibility(View.INVISIBLE);
                emptyPassword.setVisibility(View.INVISIBLE);
                emptyUsername.setVisibility(View.INVISIBLE);
                emptyBirthday.setVisibility(View.INVISIBLE);
                emptyGender.setVisibility(View.INVISIBLE);

                emailStr = emailTextFieldSignUp.getText().toString().trim();
                passwordStr = passwordTextFieldSignUp.getText().toString().trim();
                usernameStr = usernameTextFieldSignUp.getText().toString().trim();
                birthdayStr = birthdayTextFieldSignUp.getText().toString().trim();
                genderStr = genderTextFieldSignUp.getText().toString().trim();

                if (emailStr.length() == 0) {
                    emptyEmail.setVisibility(View.VISIBLE);
                }
                if (passwordStr.length() == 0) {
                    emptyPassword.setVisibility(View.VISIBLE);
                }
                if (usernameStr.length() == 0) {
                    emptyUsername.setVisibility(View.VISIBLE);
                }
                if (birthdayStr.length() == 0) {
                    emptyBirthday.setVisibility(View.VISIBLE);
                }
                if (genderStr.length() == 0) {
                    emptyGender.setVisibility(View.VISIBLE);
                }
                if (passwordStr.length() != 0 && emailStr.length() != 0 && usernameStr.length() != 0 && birthdayStr.length() != 0 && genderStr.length() != 0) {
                    if (mWifi.isConnected() || mMobile.isConnected()) {
                        dialog = ProgressDialog.show(RegisterActivity.this, "",
                                "Please wait...", true);
                        new Thread(new Runnable() {
                            public void run() {
                                Check();
                            }
                        }).start();
                    } else {
                        Snackbar.make(v, "You don't have internet connection", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
            }
        });
    }

    public void onStart() {
        super.onStart();
        final EditText textDate = (EditText) findViewById(R.id.birthdayTextFieldSignUp);
        textDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(textDate.getWindowToken(), 0);

                    DatePickerDialog dialog = new DatePickerDialog(v);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    dialog.show(ft, "DatePicker");
                }
            }
        });
        final EditText genderTextField = (EditText) findViewById(R.id.genderTextFieldSignUp);
        genderTextField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(genderTextField.getWindowToken(), 0);

                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setTitle("Choose your gender");
                    builder.setCancelable(true);
                    builder.setView(gender);
                    if (genderDialog == null) {
                        genderDialog = builder.create();
                    }

                    genderDialog.show();
                }
            }
        });
    }

    public void Check() {
        try {
            httpclient = new DefaultHttpClient();
            httppost = new HttpPost("http://peregrinate.esy.es/android/user/regcheck.php");
            nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("email", emailTextFieldSignUp.getText().toString().trim()));
            nameValuePairs.add(new BasicNameValuePair("username", usernameTextFieldSignUp.getText().toString().trim()));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            response = httpclient.execute(httppost);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            System.out.println("Response : " + response);
            runOnUiThread(new Runnable() {
                public void run() {
                    if (response.equalsIgnoreCase("Username already exist!")) {
                        Toast.makeText(getApplicationContext(), "Username already exist! Please try another one.", Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                }
            });

            if (response.equalsIgnoreCase("Username is valid")) {
                Reg();
            } else {
                showAlert();
            }
        } catch (Exception e) {
            dialog.dismiss();
            System.out.println("Exception : " + e.getMessage());
        }
    }

    public void Reg() {
        try {
            httpclient = new DefaultHttpClient();
            httppost = new HttpPost("http://peregrinate.esy.es/android/user/register.php");
            nameValuePairs = new ArrayList<NameValuePair>(5);
            nameValuePairs.add(new BasicNameValuePair("email", emailTextFieldSignUp.getText().toString().trim()));
            nameValuePairs.add(new BasicNameValuePair("username", usernameTextFieldSignUp.getText().toString().trim()));
            nameValuePairs.add(new BasicNameValuePair("password", passwordTextFieldSignUp.getText().toString().trim()));
            nameValuePairs.add(new BasicNameValuePair("birthday", birthdayTextFieldSignUp.getText().toString().trim()));
            nameValuePairs.add(new BasicNameValuePair("gender", genderTextFieldSignUp.getText().toString().trim().toLowerCase()));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            response = httpclient.execute(httppost);
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(RegisterActivity.this, "Registration Successful. You can now login.", Toast.LENGTH_SHORT).show();
                }
            });
            dialog.dismiss();
            startActivity(new Intent(this, LoginActivity.class));
        } catch (Exception e) {
            dialog.dismiss();
            System.out.println("Exception : " + e.getMessage());
        }
    }

    public void showAlert() {
        RegisterActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                builder.setTitle("Error");
                builder.setMessage("Username or email already exist!")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }
}
