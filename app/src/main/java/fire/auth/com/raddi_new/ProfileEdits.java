package fire.auth.com.raddi_new;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileEdits extends AppCompatActivity {
    EditText name_edit, mobile_edit, email_edit, password_edit;
    Button update_edit_btn;
    CircleImageView dp_img, dp_edit;
    private Toolbar mTopToolbar;
    Uri DownloadUri = Uri.parse(" ");
    Boolean hasImg = false;
    String userDocKey, dp = "null";

    StorageReference storageReference;
    FirebaseAuth mAuth;
    FirebaseFirestore mFire;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edits);

        final Bundle bundle = getIntent().getExtras();


        name_edit = findViewById(R.id.name_edit);
        mobile_edit = findViewById(R.id.mobile_edit);
        email_edit = findViewById(R.id.email_edit);
        dp_img = findViewById(R.id.dp);
        dp_edit = findViewById(R.id.dp_edit);
        update_edit_btn = findViewById(R.id.update_edit_btn);

//        mTopToolbar = (Toolbar) findViewById(R.id.mToolbarProfileUpdate);
//        setSupportActionBar(mTopToolbar);
//        getSupportActionBar().setTitle("Edit Profile");

        name_edit.setText(bundle.getString("name"));
        mobile_edit.setText(bundle.getString("mobile"));
        email_edit.setText(bundle.getString("mail"));


        userDocKey = bundle.getString("userDocKey");

        dp_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(ProfileEdits.this);

            }
        });
        dp = bundle.getString("dp");
//        Toast.makeText(this, dp, Toast.LENGTH_SHORT).show();
        if(!dp.equals("default")) {
            Glide.with(getApplicationContext())
                    .load(dp)
                    .into(dp_img);
        }
        mAuth = FirebaseAuth.getInstance();
        mFire = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        update_edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                     if(hasImg){
                        storageReference.child(mAuth.getCurrentUser().getEmail())
                                .putFile(DownloadUri)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                                        Log.d("dockey", "onSuccess: " + userDocKey);
                                        mFire.collection("user").document(userDocKey).update("profileImage", taskSnapshot.getDownloadUrl().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                startActivity(new Intent(getApplicationContext(), Home.class));
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ProfileEdits.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                     }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && data != null){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK){
                final Uri uri = result.getUri();
                dp_img.setImageURI(uri);
                DownloadUri = uri;
                hasImg = true;
            }

        }else {
            hasImg = false;
            Toast.makeText(this, "Please select valid image, try again..", Toast.LENGTH_SHORT).show();
        }
    }
}
