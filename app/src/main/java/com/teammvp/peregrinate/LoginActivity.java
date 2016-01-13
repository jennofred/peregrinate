package com.teammvp.peregrinate;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    String usernameStr, passwordStr;

    EditText usernameTextField, passwordTextField;
    ImageView signUp, loginWithFacebook, emptyUsername, emptyPassword;
    ScrollView loginLayout;
    Button loginButton;
    ConnectivityManager connManager;
    LinearLayout invalid;

    List<NameValuePair> nameValuePairs;
    ProgressDialog dialog = null;
    private Boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setSharedElementExitTransition(TransitionInflater.from(this).inflateTransition(R.transition.shared_element_transition_login));
            getWindow().setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.shared_element_transition_login));
        }

        //views initialization
        usernameTextField = (EditText) findViewById(R.id.usernameTextField);
        passwordTextField = (EditText) findViewById(R.id.passwordTextField);
        loginButton = (Button) findViewById(R.id.loginButton);
        loginWithFacebook = (ImageView) findViewById(R.id.loginWithFacebook);
        signUp = (ImageView) findViewById(R.id.signUp);
        invalid = (LinearLayout) findViewById(R.id.invalid);
        emptyUsername = (ImageView) findViewById(R.id.emptyUsername);
        emptyPassword = (ImageView) findViewById(R.id.emptyPassword);
        loginLayout = (ScrollView) findViewById(R.id.scrollLayout);

        //font styling
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/BebasNeue.otf");
        Typeface helvetica_font = Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeue.otf");
        usernameTextField.setTypeface(helvetica_font);
        passwordTextField.setTypeface(helvetica_font);
        loginButton.setTypeface(custom_font);

        connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        loginWithFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Not available yet. Tap sign up instead", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        loginLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(loginLayout.getWindowToken(), 0);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                NetworkInfo mMobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                if (mWifi.isConnected() || mMobile.isConnected()) {
                    usernameStr = usernameTextField.getText().toString().trim();
                    passwordStr = passwordTextField.getText().toString().trim();
                    if (usernameStr.length() == 0) {
                        emptyUsername.setVisibility(View.VISIBLE);
                    }
                    if (passwordStr.length() == 0) {
                        emptyPassword.setVisibility(View.VISIBLE);
                    }
                    if (passwordStr.length() == 0 && usernameStr.length() == 0) {
                        emptyUsername.setVisibility(View.VISIBLE);
                        emptyPassword.setVisibility(View.VISIBLE);
                    }
                    if (passwordStr.length() > 0 && usernameStr.length() > 0) {
                        emptyUsername.setVisibility(View.INVISIBLE);
                        emptyPassword.setVisibility(View.INVISIBLE);
                        invalid.setVisibility(View.INVISIBLE);
                        dialog = ProgressDialog.show(LoginActivity.this, "",
                                "Logging in...", true);
                        new Thread(new Runnable() {
                            public void run() {
                                Check();
                            }
                        }).start();
                    }
                } else {
                    NoConnAlert();
                    invalid.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public void signUp(View v) {
        if (Build.VERSION.SDK_INT >= 21) {
            v.setTransitionName("signUpTransition");
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, v, v.getTransitionName());

            startActivity(new Intent(this, RegisterActivity.class), optionsCompat.toBundle());
        }
        else {
            startActivity(new Intent(this, RegisterActivity.class));
        }
    }

    public void Check() {
        try {
            StringBuilder content = new StringBuilder();
            String responseStr;
            URL url = new URL("http://peregrinate.esy.es/android/user/login.php");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            nameValuePairs = new ArrayList<>(2);
            nameValuePairs.add(new BasicNameValuePair("username", usernameTextField.getText().toString().trim()));
            nameValuePairs.add(new BasicNameValuePair("password", passwordTextField.getText().toString().trim()));
            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(nameValuePairs));
            writer.flush();
            writer.close();
            os.close();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line);
            }
            System.out.println("Response : " + content);
            bufferedReader.close();
            responseStr = content.toString();
            urlConnection.disconnect();

            if (responseStr.equals("No Such User Found")) {
                showAlert();
                invalid.setVisibility(View.VISIBLE);
                dialog.dismiss();
            } else if (responseStr.equalsIgnoreCase("User Found")) {
                dialog.dismiss();
                MainActivity.loginEditor.putBoolean("loggedIn", true);
                MainActivity.loginEditor.commit();
                MainActivity.userEditor.putString("loggedInUser", usernameStr);
                MainActivity.userEditor.commit();
                startActivity(new Intent(this, RetrieveActivity.class));

            } else {
                ConnError();
            }

        } catch (Exception e) {
            dialog.dismiss();
            System.out.println("Exception : " + e.getMessage());

        }
    }

    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public void showAlert() {
        LoginActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("Login Error.");
                builder.setMessage("Invalid username or password. Please try again.")
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

    public void ConnError() {
        dialog.dismiss();
        LoginActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("Uh oh..");
                builder.setMessage("Something went wrong. Please try again later.")
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

    public void NoConnAlert() {
        LoginActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("Uhmm..");
                builder.setMessage("I think you forgot to turn on your wifi or data.")
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

    @Override
    public void onBackPressed() {
        Intent close = new Intent(Intent.ACTION_MAIN);
        close.addCategory(Intent.CATEGORY_HOME);
        startActivity(close);
    }
}
