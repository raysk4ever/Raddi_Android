package fire.auth.com.raddi_new;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class CreatePage extends AppCompatActivity {

    TextInputLayout mName, mMobile, mEmail_create, mPassword_create;
    Button mLogin_create, mCreate_create;
    //CircleImageView mProfile_image;
    ImageView mPasswordShow;

    StorageReference mStorageRef;
    FirebaseFirestore db;
    Uri DownloadUri = Uri.parse(" ");

    Boolean hasImg = false;

    CheckBox tnc;


    FirebaseAuth mAuth;
    FirebaseUser mUser;



//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && data != null){
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            if (resultCode == RESULT_OK){
//                final Uri uri = result.getUri();
//                mProfile_image.setImageURI(uri);
//                DownloadUri = uri;
//                hasImg = true;
//            }
//
//        }else {
//            hasImg = false;
//            Toast.makeText(this, "Please select valid image, try again..", Toast.LENGTH_SHORT).show();
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_page);

        db = FirebaseFirestore.getInstance();

        mStorageRef = FirebaseStorage.getInstance().getReference("ProfileImage");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mLogin_create = (Button) findViewById(R.id.login_btn);
        mCreate_create = (Button) findViewById(R.id.create_page_btn);
        mCreate_create.setEnabled(false);

        tnc = findViewById(R.id.createCheckBox);
        tnc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                AlertDialog.Builder alBuilder = new AlertDialog.Builder(getApplicationContext());
//                alBuilder.setTitle(getString(R.string.alertTitle));
//                alBuilder.setMessage(getString(R.string.alertMessage));
//                alBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                });
//                AlertDialog al = alBuilder.create();
//                al.show();

//                mCreate_create.setEnabled(false);


                final Dialog dialog = new Dialog(CreatePage.this);
                dialog.setContentView(R.layout.dialog_view);
                dialog.show();

                Button ok = dialog.findViewById(R.id.alertOk);
                Button cancel = dialog.findViewById(R.id.alertCancle);

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        tnc.setEnabled(true);
                            tnc.setChecked(true);
                            dialog.dismiss();
                            mCreate_create.setEnabled(true);
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        tnc.setEnabled(false);
                        tnc.setChecked(false);
                        dialog.dismiss();
                        mCreate_create.setEnabled(false);
                    }
                });

            }
        });


        final ProgressDialog mProgressDialog = new ProgressDialog(this);

        mName = (TextInputLayout) findViewById(R.id.name_tv);
        //mMobile = (TextInputLayout) findViewById(R.id.mobile_tv);
        mEmail_create = (TextInputLayout) findViewById(R.id.email_tv);
        mPassword_create = (TextInputLayout) findViewById(R.id.password_tv);

        //mProfile_image = (CircleImageView) findViewById(R.id.profile_image);


        mLogin_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(CreatePage.this,MainActivity.class);
                startActivity(loginIntent);
            }
        });

//        mProfile_image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                CropImage.activity()
//                        .setAspectRatio(1,1)
//                        .setGuidelines(CropImageView.Guidelines.ON)
//                        .start(CreatePage.this);
//
//            }
//        });


        mCreate_create.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {



               mProgressDialog.setTitle("Please Wait");
               mProgressDialog.setMessage("loading......");
               mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
               mProgressDialog.setCancelable(false);
               mProgressDialog.show();


               if (!TextUtils.isEmpty(mName.getEditText().getText())&&
                       //!TextUtils.isEmpty(mMobile.getEditText().getText())&&
                       !TextUtils.isEmpty(mEmail_create.getEditText().getText())&&
                       !TextUtils.isEmpty(mPassword_create.getEditText().getText())) {


                  // if (hasImg) {


                       mAuth.createUserWithEmailAndPassword(
                               mEmail_create.getEditText().getText().toString(),
                               mPassword_create.getEditText().getText().toString()
                       ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                           @Override
                           public void onComplete(@NonNull Task<AuthResult> task) {

                               if (task.isSuccessful()) {

//                                   mStorageRef
//                                           .child(mEmail_create.getEditText().getText().toString())
//                                           .putFile(DownloadUri)
//                                           .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                                               @Override
//                                               public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                   Map<String, Object> user = new HashMap<>();
                                                   user.put("name", mName.getEditText().getText().toString());
                                                   user.put("mobile", "0123456789");
                                                   user.put("email", mEmail_create.getEditText().getText().toString());
                                                   user.put("password", mPassword_create.getEditText().getText().toString());
//                                                   user.put("profileImage", taskSnapshot.getDownloadUrl().toString());
                                                   user.put("profileImage", "default");
                                                   user.put("sex", "Male");

                                                   db.collection("user")
                                                           .add(user)
                                                           .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                               @Override
                                                               public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                   if (task.isSuccessful()) {
                                                                       db.collection("user").document(task.getResult().getId()).update("uid", task.getResult().getId());
                                                                       Toast.makeText(CreatePage.this, "welcome", Toast.LENGTH_SHORT).show();
                                                                       Intent home_intent = new Intent(CreatePage.this, Home.class);
                                                                       startActivity(home_intent);
                                                                       finish();
                                                                       mProgressDialog.dismiss();
                                                                   } else {
                                                                       mProgressDialog.dismiss();
                                                                       Toast.makeText(CreatePage.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                   }
                                                               }
                                                           });
                                               }
//                                           })
//                                           .addOnFailureListener(new OnFailureListener() {
//                                               @Override
//                                               public void onFailure(@NonNull Exception e) {
//                                                   mProgressDialog.dismiss();
//                                                   Toast.makeText(CreatePage.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                               }
//                                           });
                               //}
                               else {
                                   mProgressDialog.dismiss();
                                   Toast.makeText(CreatePage.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                               }


                           }
                       });

               }
           }
       });
    }
}