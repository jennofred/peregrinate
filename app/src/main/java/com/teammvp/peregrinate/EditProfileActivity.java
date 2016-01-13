package com.teammvp.peregrinate;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditProfileActivity extends Activity implements View.OnClickListener{

    Button UpdateProfile;
    EditText name, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);

        UpdateProfile = (Button) findViewById(R.id.UpdateProfile);

        UpdateProfile.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.UpdateProfile:

                break;
        }
    }
}
