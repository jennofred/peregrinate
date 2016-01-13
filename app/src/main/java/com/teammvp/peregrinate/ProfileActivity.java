package com.teammvp.peregrinate;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.rajawali3d.loader.LoaderOBJ;
import org.rajawali3d.loader.fbx.LoaderFBX;
import org.rajawali3d.materials.textures.TextureManager;
import org.rajawali3d.renderer.RajawaliRenderer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.rajawali3d.Object3D;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{
    public static final String UPLOAD_KEY = "image";

    private int PICK_IMAGE_REQUEST = 1;
    private int IMAGE_CAPTURE = 2;

    private ImageView imageView;

    private Bitmap bitmap, squareBitmap;

    private Uri filePath;

    HttpPost httppost;
    StringBuffer buffer;
    HttpResponse response;
    HttpClient httpclient;
    List<NameValuePair> nameValuePairs;

    TextView fullname, username;
    Button editProfile;
    Boolean icon = false;

    ListView chooseAction = null;
    AlertDialog chooseActionDialog = null;

    private final String imageURL = "http://peregrinate.esy.es/android/user/getImage.php?id=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //initialize every view
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(RetrieveActivity.username);
        imageView = (ImageView) findViewById(R.id.displayPicture);
        getImage();
        fullname = (TextView) findViewById(R.id.fullName);
        username = (TextView) findViewById(R.id.photoUsername);
        editProfile = (Button) findViewById(R.id.editProfile);
        fullname.setText(RetrieveActivity.fname + " " + RetrieveActivity.lname);
        username.setText(RetrieveActivity.username);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeue.otf");
        fullname.setTypeface(custom_font);
        username.setTypeface(custom_font);
        editProfile.setTypeface(custom_font);
        editProfile.setOnClickListener(this);


        chooseAction = new ListView(this);
        String[] chooseActionString = {"Take a photo", "Choose a photo"};
        ArrayAdapter<String> chooseActionAdapter = new ArrayAdapter<String>(this, R.layout.gender_list, R.id.genderList, chooseActionString);
        chooseAction.setAdapter(chooseActionAdapter);
        chooseAction.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewGroup viewGroup = (ViewGroup) view;
                TextView txt = (TextView) viewGroup.findViewById(R.id.genderList);
                String selectedAction = txt.getText().toString();
                if (selectedAction.equalsIgnoreCase("Take a photo")) {
                    openCamera();
                }
                if (selectedAction.equalsIgnoreCase("Choose a photo")) {
                    showFileChooser();
                }
                chooseActionDialog.dismiss();
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setCancelable(true);
                builder.setView(chooseAction);
                if (chooseActionDialog == null) {
                    chooseActionDialog = builder.create();
                }
                chooseActionDialog.show();
            }
        });

    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        startActivityForResult(intent, IMAGE_CAPTURE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.editProfile:

                startActivity(new Intent(this, EditProfileActivity.class));

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_CAPTURE && resultCode == RESULT_OK) {
            File f = new File(Environment.getExternalStorageDirectory().toString());
            for (File temp : f.listFiles()) {
                if (temp.getName().equals("temp.jpg")) {
                    f = temp;
                    break;
                }
            }
            try {
                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

                bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                        bitmapOptions);


                //make bitmap to square
                if (bitmap.getWidth() >= bitmap.getHeight()) {

                    squareBitmap = Bitmap.createBitmap(
                            bitmap,
                            bitmap.getWidth() / 2 - bitmap.getHeight() / 2,
                            0,
                            bitmap.getHeight(),
                            bitmap.getHeight()
                    );

                } else {

                    squareBitmap = Bitmap.createBitmap(
                            bitmap,
                            0,
                            bitmap.getHeight() / 2 - bitmap.getWidth() / 2,
                            bitmap.getWidth(),
                            bitmap.getWidth()
                    );
                }


                imageView.setImageBitmap(squareBitmap);

                String path = android.os.Environment
                        .getExternalStorageDirectory()
                        + File.separator
                        + "Phoenix" + File.separator + "default";
                f.delete();
                OutputStream outFile = null;
                File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                try {
                    outFile = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                    outFile.flush();
                    outFile.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage("Confirm upload?")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        uploadImage();
                                    }
                                }
                        )
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                if (bitmap.getWidth() >= bitmap.getHeight()) {

                    squareBitmap = Bitmap.createBitmap(
                            bitmap,
                            bitmap.getWidth() / 2 - bitmap.getHeight() / 2,
                            0,
                            bitmap.getHeight(),
                            bitmap.getHeight()
                    );

                } else {

                    squareBitmap = Bitmap.createBitmap(
                            bitmap,
                            0,
                            bitmap.getHeight() / 2 - bitmap.getWidth() / 2,
                            bitmap.getWidth(),
                            bitmap.getWidth()
                    );
                }

                imageView.setImageBitmap(squareBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
            builder.setTitle("Confirm");
            builder.setMessage("Confirm upload?")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    uploadImage();
                                }
                            }
                    )
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void uploadImage() {
        class UploadImage extends AsyncTask<Bitmap, Void, String> {

            ProgressDialog loading;
            RequestHandler rh = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ProfileActivity.this, "Uploading Image", "Please wait...", true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
            }

            @Override
            protected String doInBackground(Bitmap... params) {
                Bitmap bitmap = params[0];
                String uploadImage = getStringImage(bitmap);
                String result = null;

                try {
                    httpclient = new DefaultHttpClient();
                    httppost = new HttpPost("http://peregrinate.esy.es/android/user/user_image_upload.php");
                    nameValuePairs = new ArrayList<NameValuePair>(2);
                    nameValuePairs.add(new BasicNameValuePair(UPLOAD_KEY, uploadImage));
                    nameValuePairs.add(new BasicNameValuePair("username", MainActivity.userLoginStr));
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
                    response = httpclient.execute(httppost);
                    result = httpclient.execute(httppost, responseHandler);
                    System.out.println(result);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(ProfileActivity.this, "Upload image successful", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    System.out.println("Exception : " + e.getMessage());
                }

                return result;
            }
        }

        UploadImage ui = new UploadImage();
        ui.execute(bitmap);
    }

    //get display picture from server
    private void getImage() {
        String id = RetrieveActivity.username;
        class GetImage extends AsyncTask<String, Void, Bitmap> {

            ImageView bmImage;

            public GetImage(ImageView bmImage) {
                this.bmImage = bmImage;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                bmImage.setImageBitmap(bitmap);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Bitmap doInBackground(String... strings) {
                String url = imageURL + strings[0];
                Bitmap mIconFinal = null;
                Bitmap mIcon = null;
                try {
                    InputStream in = new java.net.URL(url).openStream();
                    mIcon = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                }

                if (mIcon != null) {
                    icon = true;
                    if (mIcon.getWidth() >= mIcon.getHeight()) {

                        mIconFinal = Bitmap.createBitmap(
                                mIcon,
                                mIcon.getWidth() / 2 - mIcon.getHeight() / 2,
                                0,
                                mIcon.getHeight(),
                                mIcon.getHeight()
                        );

                    } else {

                        mIconFinal = Bitmap.createBitmap(
                                mIcon,
                                0,
                                mIcon.getHeight() / 2 - mIcon.getWidth() / 2,
                                mIcon.getWidth(),
                                mIcon.getWidth()
                        );
                    }
                }


                return mIconFinal;
            }
        }

//        if (icon){
        GetImage gi = new GetImage(imageView);
        gi.execute(id);
//        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            super.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}