package fire.auth.com.raddi_new;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivityNew extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore mFire;
    String currentUid;
    TextView name, mob, mail,edit;
    ProgressDialog mProgress;
    int count = 0;
    String docKey = "", dpU;
    private Toolbar mTopToolbar;
    CircleImageView profile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_main_show);

        mAuth = FirebaseAuth.getInstance();
        mFire = FirebaseFirestore.getInstance();

        currentUid = mAuth.getCurrentUser().getUid();

        name = findViewById(R.id.mainProfileName);
        mob = findViewById(R.id.mainProfileMob);
        mail = findViewById(R.id.mainProfileEmail);
        edit= findViewById(R.id.edit_tv);
        profile = findViewById(R.id.profile2_img);

//        mTopToolbar = (Toolbar) findViewById(R.id.mToolbarProfile);
//        setSupportActionBar(mTopToolbar);
//        getSupportActionBar().setTitle("Profile");

        mFire.collection("user").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                for(DocumentSnapshot doc: documentSnapshots.getDocuments()){
                    if(doc.getString("uid").equals(mAuth.getCurrentUser().getUid())){
                        String cname = doc.getString("name");
                        String cmob = doc.getString("mobile");
                        String cemail = doc.getString("email");
                        String dp = doc.getString("profileImage");

                        docKey = doc.getId();
                        name.setText(cname);
                        mail.setText(cemail);
                        mob.setText(cmob);

                        if(!dp.equals("default")){
                            Glide.with(getApplicationContext())
                                    .load(dp)
                                    .into(profile);
                            dpU = dp;
                        }

                        Toast.makeText(ProfileActivityNew.this, "Found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivityNew.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        mFire.collection("Post").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                for(DocumentSnapshot doc: documentSnapshots.getDocuments()){
                    if(doc.getString("uid").toString().equals(currentUid)) {
                        count++;

                    }
                }
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent myIntent = new Intent(ProfileActivityNew.this, ProfileEdits.class);
                myIntent.putExtra("name", name.getText());
                myIntent.putExtra("mobile", mob.getText());
                myIntent.putExtra("mail", mail.getText());
                myIntent.putExtra("userDocKey", docKey);
                myIntent.putExtra("dp", dpU);

                ProfileActivityNew.this.startActivity(myIntent);
            }
        });

    }
}
