package fire.auth.com.raddi_new;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageActivity;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import id.zelory.compressor.Compressor;

public class PostBook extends AppCompatActivity implements AdapterView.OnItemSelectedListener, LocationListener {

    FirebaseAuth mAuth;
    FirebaseFirestore mFirebaseFirestore;
    StorageReference mStorageRef;
    Uri DownloadUri = Uri.parse(" ");
    Bitmap thumb;
    byte data[];
    ImageView mImagePost1;
    EditText mBookName, mPrice, mDescription, mWriter;
    Button mPostButton;
    Spinner mStdSpinner, mSubSpinner;
    ProgressDialog mProgressDialog;
    String category;
    String locationsend;

    LocationManager locationManager;
    double longitude, latitude;
    String city = null, country, state, locality = null;

    EditText postTextLocation;
    Button postLocationBtn;

    Boolean hasImg = false;
    private Toolbar mTopToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_book);


        postTextLocation = findViewById(R.id.postTextLocation);
        postLocationBtn = findViewById(R.id.post_getBtn);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);





        mProgressDialog = new ProgressDialog(this);

        mImagePost1 = (ImageView) findViewById(R.id.post_image_1);

        mStorageRef = FirebaseStorage.getInstance().getReference("PostImage");

        mBookName = (EditText) findViewById(R.id.name_et);
        mPrice = (EditText) findViewById(R.id.price_et);
        mDescription = (EditText) findViewById(R.id.description_et);
        mWriter = (EditText) findViewById(R.id.writer_et);

        //mSubSpinner = (Spinner) findViewById(R.id.sub_spinner);
        mStdSpinner = (Spinner) findViewById(R.id.std_spinner);

        mPostButton = (Button) findViewById(R.id.post_btn);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();

        mStdSpinner.setOnItemSelectedListener(this);

        List<String> std = new ArrayList<>();
        std.add("Engineering");
        std.add("Class 1-5");
        std.add("class 6-8");
        std.add("class 9-10");
        std.add("class 11-12");
        std.add("Medical");
        std.add("Commerce");
        std.add("Arts");
        std.add("Novel");
        std.add("UPSC");
        std.add("SSC");
        std.add("Bank");
        std.add("Other");

        ArrayAdapter<String> std_Adaptar = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, std);
        std_Adaptar.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mStdSpinner.setAdapter(std_Adaptar);
        mStdSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = mStdSpinner.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        mTopToolbar = (Toolbar) findViewById(R.id.mToolbarBookPost);
//        setSupportActionBar(mTopToolbar);
//        getSupportActionBar().setTitle("Book Post");

        mImagePost1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(PostBook.this);
            }
        });

        mPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasImg) {
                    mProgressDialog.setTitle("Please Wait...");
                    mProgressDialog.setMessage("Your Books are post");
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.show();

                    if (!TextUtils.isEmpty(mBookName.getText()) &&
                            !TextUtils.isEmpty(mPrice.getText()) &&
                            !TextUtils.isEmpty(mDescription.getText())) {

                        StorageReference filePath = mStorageRef
                                .child(mAuth.getUid() + ".jpg");

                        filePath.putFile(DownloadUri)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        if (postTextLocation.getText() == null) {
                                            locationsend = city;
                                        } else {
                                            locationsend = postTextLocation.getText().toString();
                                        }
                                        final Map<String, Object> imp = new HashMap<>();
                                        imp.put("name", mBookName.getText().toString());
                                        imp.put("price", mPrice.getText().toString());
                                        imp.put("description", mDescription.getText().toString());
                                        imp.put("uid", mAuth.getUid());
                                        imp.put("TimeStamp", FieldValue.serverTimestamp());
                                        imp.put("postImage", taskSnapshot.getDownloadUrl().toString());
                                        imp.put("userEmail", mAuth.getCurrentUser().getEmail());
                                        imp.put("userName", mAuth.getCurrentUser().getDisplayName());
                                        imp.put("category", category);
                                        imp.put("subLocality", locality);
                                        imp.put("locality", locationsend);
                                        imp.put("likes", 0);
                                        imp.put("writer", mWriter.getText().toString());

                                        mFirebaseFirestore.collection("Post")
                                                .add(imp)
                                                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                                        if (task.isSuccessful()) {
                                                            mProgressDialog.dismiss();
                                                            Toast.makeText
                                                                    (PostBook.this, "Book Posted" , Toast.LENGTH_SHORT)
                                                                    .show();
                                                            task.getResult().update("postId", task.getResult().getId().toString());


                                                            task.getResult().update("docId", task.getResult().getId());

                                                            Intent postInted = new Intent(getApplicationContext(), Home.class);
                                                            startActivity(postInted);
                                                            finish();

                                                        }else{

                                                        }
                                                    }
                                                });

                                    }
                                });
                    } else {
                        mProgressDialog.dismiss();
                        Toast.makeText(PostBook.this, "Invaild Input", Toast.LENGTH_SHORT)
                                .show();

                    }
                } else {
                    Toast.makeText(PostBook.this, "Please select a Post Image...", Toast.LENGTH_SHORT).show();
                }
            }
        });
        postLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Location location = getCurrentLocationNet();

                if (location != null) {
                    Toast.makeText(PostBook.this, "server 1", Toast.LENGTH_SHORT).show();
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    //postTextLocation.setText(String.valueOf(latitude) + String.valueOf(longitude));
                } else {

                    location = getCurrentLocation();
                    if (location !=null){
                        Toast.makeText(PostBook.this, "server 2", Toast.LENGTH_SHORT).show();
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                        }else {
                        Toast.makeText(PostBook.this, "Cant retrieve location right now or turn on location, enter manually", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private Location getCurrentLocationNet() {
        Location l = null;
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(PostBook.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, this);

            l = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        return l;

    }

    private Location getCurrentLocation() {

        Location l = null;

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(PostBook.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);
            l = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

       return l;

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data!= null){
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK){
                Uri uri = result.getUri();
                mImagePost1.setImageURI(uri);
                DownloadUri = uri;
                hasImg = true;
            }
        }
        }else {
            hasImg = false;
            Toast.makeText(this, "Invalid Image", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
       switch (requestCode){
           case 1:
               if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                   Toast.makeText(this, "Permission Granted, Good to go now!", Toast.LENGTH_SHORT).show();
               }else {
                   Toast.makeText(this, "Permission Denied, Disabling Location Functionality...", Toast.LENGTH_SHORT).show();
               }

           case 2:  if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
               Toast.makeText(this, "Permission Granted, Good to go now!", Toast.LENGTH_SHORT).show();
           }else {
               Toast.makeText(this, "Permission Denied, Disabling Location Functionality...", Toast.LENGTH_SHORT).show();
           }

       }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onLocationChanged(Location location)
    {
        Geocoder geocoder= new Geocoder(this);
        try {

            List<Address> l = geocoder.getFromLocation(latitude, longitude, 1);
            locality = l.get(0).getSubLocality();
            city = l.get(0).getLocality();
            postTextLocation.setText(locality + ", " + city);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider)
    {

    }

    @Override
    public void onProviderDisabled(String provider) {
    }
}
