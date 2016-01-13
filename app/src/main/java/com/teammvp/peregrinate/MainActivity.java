package com.teammvp.peregrinate;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.qualcomm.vuforia.Vuforia;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    public static SharedPreferences login;
    public static SharedPreferences.Editor loginEditor;
    public boolean userLogin;

    public static SharedPreferences user;
    public static SharedPreferences.Editor userEditor;
    public static String userLoginStr;

    final int MY_PERMISSIONS_REQUEST_GET_CAMERA = 1;
    final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int permissionCheckCamera = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA);

        int permissionCheckWriteExternalStorage = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_GET_CAMERA);

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }

        login = PreferenceManager.getDefaultSharedPreferences(this);
        loginEditor = login.edit();
        loginEditor.apply();
        userLogin = login.getBoolean("loggedIn", false);
        user = PreferenceManager.getDefaultSharedPreferences(this);
        userEditor = user.edit();
        userEditor.apply();
        userLoginStr = user.getString("loggedInUser", "None");

        //peregrinateTitle.setTransitionName("peregrinateTitle");
        //final ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, peregrinateTitle, peregrinateTitle.getTransitionName());

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    if (userLogin) {
                        startActivity(new Intent(getApplicationContext(), RetrieveActivity.class));
                    }
                    if (!userLogin) {
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    }
                }
            }, 2000);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_GET_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (userLogin) {
                                startActivity(new Intent(getApplicationContext(), RetrieveActivity.class));
                            }
                            if (!userLogin) {
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            }
                        }
                    }, 2000);
                } else {

                }
                return;
            }

            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (userLogin) {
                                startActivity(new Intent(getApplicationContext(), RetrieveActivity.class));
                            }
                            if (!userLogin) {
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            }
                        }
                    }, 2000);
                } else {
                }
                return;

                // other 'case' lines to check for other
                // permissions this app might request
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //do nothing
    }
}
