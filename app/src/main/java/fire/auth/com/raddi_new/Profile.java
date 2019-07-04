package fire.auth.com.raddi_new;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {

    FirebaseFirestore mFire;
    FirebaseAuth mFirebaseAuth;
    StorageReference mStorageReference;

    CircleImageView mProfileImg,mImageAdd;
    RadioGroup profilesex;
    String sexy = "";

    ImageView p,mEditNameBtn;
    EditText tvName, tvMobile;
    TextView tvEmail;
    RadioButton maler, femaler, sex;
    String imgUrl;

    AppCompatDialog compatDialog;

    EditText e1;
    Button b1, updateBtn;
    String userDocKey;
    private boolean hasImg = false;
    private Uri DownloadUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_new);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFire = FirebaseFirestore.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference("/ProfileImage/mProfileImg");

        tvName =(EditText) findViewById(R.id.profile2Name2);
        tvEmail =(TextView) findViewById(R.id.profile2Email2);
        tvMobile =(EditText) findViewById(R.id.profile2Mob2);
        mProfileImg = (CircleImageView) findViewById(R.id.profile2_img);
        //mEditNameBtn = findViewById(R.id.profileNameEditBtn);
        updateBtn = (Button) findViewById(R.id.update_btn);
        maler = findViewById(R.id.profile2Male);
        femaler = findViewById(R.id.profile2Female);
       mImageAdd = (CircleImageView) findViewById(R.id.imageView);
        mImageAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(Profile.this);
            }
        });
        final String cemail = mFirebaseAuth.getCurrentUser().getEmail().toString();

        profilesex = findViewById(R.id.profileSex);
        profilesex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                sex = findViewById(checkedId);

            }
        });

     mFire.collection("user").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
         @Override
         public void onComplete(@NonNull Task<QuerySnapshot> task) {
            if (task.isSuccessful()){
                for (DocumentSnapshot doc:task.getResult().getDocuments()){
                    if (doc.getString("uid").equals(mFirebaseAuth.getCurrentUser().getUid())){
                          userDocKey = doc.getId();

                            tvName.setText(doc.getString("name").toString());
                            tvEmail.setText(doc.getString("email").toString());
                            tvMobile.setText(doc.getString("mobile").toString());
                            sexy = doc.getString("sex");
                            //Toast.makeText(Profile.this, sexy, Toast.LENGTH_SHORT).show();
                            switch (sexy){
                                case "Male":
                                    maler.setChecked(true);
                                    femaler.setChecked(false);
                                    break;
                                case "Female":
                                    maler.setChecked(false);
                                    femaler.setChecked(true);
                                    break;
                                    default:
                                        Toast.makeText(Profile.this, "Update your profile...", Toast.LENGTH_SHORT).show();
                                        break;
                            }

                            imgUrl = doc.getString("profileImage");
                            RequestOptions requestOptions = new RequestOptions();
                            requestOptions.placeholder(R.drawable.loading);
                            Glide.with(getApplicationContext()).setDefaultRequestOptions(requestOptions)
                                    .load(imgUrl).into(mProfileImg);
                    }
                }
            }
         }
     });


        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(Profile.this, "Update", Toast.LENGTH_SHORT).show();


                //String email = tvEmail.getText().toString();
                String mobile = tvMobile.getText().toString();
                String name = tvName.getText().toString();
                Map<String, Object> usermao = new HashMap<>();
                usermao.put("mobile", mobile);
                usermao.put("name", name);
                usermao.put("sex", sex.getText());
             mFire.collection("user").document(userDocKey).update(usermao).addOnSuccessListener(new OnSuccessListener<Void>() {
                 @Override
                 public void onSuccess(Void aVoid) {
                     Toast.makeText(Profile.this, "Updated!", Toast.LENGTH_SHORT).show();
                 }
             });
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (data!= null){
            if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK){
                    Uri uri = result.getUri();
                    //mProfileImg.setImageURI(uri);
                    hasImg = true;
                mStorageReference.child(mFirebaseAuth.getCurrentUser().getEmail())
                        .putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(Profile.this, "profile updated!", Toast.LENGTH_SHORT).show();
                        mProfileImg.setImageURI(taskSnapshot.getUploadSessionUri());
                        mFire.collection("user").document(userDocKey).update("profileImage", taskSnapshot.getDownloadUrl().toString());

                    }
                });
                }
            }
        }else {
            hasImg = false;
            Toast.makeText(this, "Invalid Image", Toast.LENGTH_SHORT).show();
        }
    }
}