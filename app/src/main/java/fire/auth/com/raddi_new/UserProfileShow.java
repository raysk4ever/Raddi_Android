package fire.auth.com.raddi_new;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileShow extends AppCompatActivity {
    ImageView mUserImag, mUserCall, bigImg;
    CircleImageView mProfileImg;

    TextView mUserName, mUserMobile;
    FirebaseFirestore mFire;
    FirebaseAuth mAuth;
    AppCompatDialog dialog;
    String mi, uid, mobile, email;
    String profileurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_show);

        Bundle bundle = getIntent().getExtras();
        String name = bundle.getString("name");
        mobile = bundle.getString("mobile");
        email = bundle.getString("email");
        uid = bundle.getString("uid");

        mFire = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

//        mProfileImg =  (CircleImageView)findViewById(R.id.user_pic);
        mUserCall = (ImageView) findViewById(R.id.user_call);
        mUserName = (TextView) findViewById(R.id.user_name);
        mUserMobile = (TextView) findViewById(R.id.user_mobile);

        dialog = new AppCompatDialog(this);
        dialog.setContentView(R.layout.profile_view);

        bigImg = dialog.findViewById(R.id.profileViewImage);

        mFire.collection("user").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){

                    for (DocumentSnapshot doc : task.getResult().getDocuments()){
                        if (doc.getString("email").equals(email)){
                            if(!doc.getString("profileImage").equals("default")){

                                Glide.with(getApplicationContext())
                                        .load(doc.getString("profileImage"))
                                        .into(mProfileImg);
                                Glide.with(getApplicationContext())
                                        .load(doc.getString("profileImage"))
                                        .into(bigImg);


                            }
                        }
                    }

                }
            }
        });


        mUserName.setText(name);
        mUserMobile.setText(mobile);

//        mProfileImg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.show();
//
//            }
//        });

        mUserCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+mobile));
                if (ActivityCompat.checkSelfPermission(UserProfileShow.this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                        return;
                }
                startActivity(callIntent);
            }
        });
    }
}