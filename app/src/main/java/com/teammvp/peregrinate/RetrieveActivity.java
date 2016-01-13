package com.teammvp.peregrinate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.HeaderViewListAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.StringTokenizer;

public class RetrieveActivity extends AppCompatActivity {

    List<NameValuePair> nameValuePairs;
    public static String info;
    ProgressDialog dialog = null;

    public static String fname, lname, gender, birthday, email, username, password;

    TextView fullname, emailAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve);

        MainActivity.user = PreferenceManager.getDefaultSharedPreferences(this);
        MainActivity.userEditor = MainActivity.user.edit();
        MainActivity.userEditor.apply();
        MainActivity.userLoginStr = MainActivity.user.getString("loggedInUser", "None");


        dialog = ProgressDialog.show(RetrieveActivity.this, "",
                "Please wait...", true);
        new Thread(new Runnable() {
            public void run() {
                Check();
            }
        }).start();
    }

    public void Check() {
        try {
            StringBuilder content = new StringBuilder();
            String responseStr;
            URL url = new URL("http://peregrinate.esy.es/android/user/retrieve_user_info.php");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            nameValuePairs = new ArrayList<>(2);
            nameValuePairs.add(new BasicNameValuePair("user", MainActivity.userLoginStr));
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

            try {
                int i = 0;
                String[] divide = new String[7];
                String delimsDiv = "^";
                StringTokenizer div = new StringTokenizer(responseStr, delimsDiv);
                while (div.hasMoreElements()) {
                    divide[i] = (String) div.nextElement();
                    i++;
                }

                fname = divide[0];
                lname = divide[1];
                username = divide[2];
                password = divide[3];
                birthday = divide[4];

                if (Integer.parseInt(divide[5]) == 1) {
                    gender = "Male";
                }
                if (Integer.parseInt(divide[5]) == 2) {
                    gender = "Female";
                }
                email = divide[6];
                dialog.dismiss();

                emailAdd.setText(email);
                System.out.println("Tangina!" + fname + lname + username + password + birthday + gender + email);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

            startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
        } catch (Exception e) {
            dialog.dismiss();
            System.out.println("Exception RetrieveActivity Check() : " + e.getMessage());
            startActivity(new Intent(getApplicationContext(), DashboardActivity.class));

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

}
