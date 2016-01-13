package com.teammvp.peregrinate;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.teammvp.peregrinate.NavigationFragments.ViewEventsFragment;
import com.teammvp.peregrinate.NavigationFragments.ViewPlacesFragment;

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

public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    List<NameValuePair> nameValuePairs;
    public static String info;
    ProgressDialog dialog = null;

    TextView fullname, emailAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Typeface helvetica = Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeue.otf");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        fullname = (TextView) header.findViewById(R.id.navHeaderName);
        emailAdd = (TextView) header.findViewById(R.id.navHeaderEmail);

        if (RetrieveActivity.fname.equalsIgnoreCase("none")) {
            fullname.setText(RetrieveActivity.username);
        } else {
            fullname.setText(RetrieveActivity.fname + " " + RetrieveActivity.lname);
        }
        emailAdd.setText(RetrieveActivity.email);
        fullname.setTypeface(helvetica);
        emailAdd.setTypeface(helvetica);


        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.dashboardLayout, new ViewEventsFragment()).commit();

        MainActivity.user = PreferenceManager.getDefaultSharedPreferences(this);
        MainActivity.userEditor = MainActivity.user.edit();
        MainActivity.userEditor.apply();
        MainActivity.userLoginStr = MainActivity.user.getString("loggedInUser", "None");


        Toast.makeText(this, "Name: " + RetrieveActivity.fname + " " + RetrieveActivity.lname + " Email: " + RetrieveActivity.email, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent close = new Intent(Intent.ACTION_MAIN);
            close.addCategory(Intent.CATEGORY_HOME);
            startActivity(close);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = new ViewEventsFragment();

        if (id == R.id.nav_events) {
            fragment = new ViewEventsFragment();
        } else if (id == R.id.nav_places) {
            fragment = new ViewPlacesFragment();
        } else if (id == R.id.nav_explore) {

        } else if (id == R.id.nav_routing) {

        } else if (id == R.id.nav_business_account) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_logout) {
            MainActivity.loginEditor.putBoolean("loggedIn", false);
            MainActivity.loginEditor.commit();
            if (MainActivity.userEditor != null) {
                MainActivity.userEditor.putString("loggedInUser", "None");
                MainActivity.userEditor.commit();
            }
            startActivity(new Intent(this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.dashboardLayout, fragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void openProfileActivity(View v) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        startActivity(new Intent(this, ProfileActivity.class));
    }
}
