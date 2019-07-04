package fire.auth.com.raddi_new;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class PostPage extends AppCompatActivity {

    private static final String TAG = "mlogfirepostpage";
    TextView mBookNameShow, mDescriptionShow,mWriter_show, mUserName, mMobile, mPrice, mAddress, mDistt,mCategory;
    ImageView mBookShow;
    Button mUserProfile_btn;

    String uid, bname, price, desc, email, category1, mobile, sname,smobile, sWriter;

    FirebaseFirestore mFire;
    FirebaseAuth mFirebaseAuth;
    String postemail;
    Bundle postData;
    String status = "null";

    //android.support.v7.widget.Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_full);

        mBookNameShow = (TextView) findViewById(R.id.book_name_show);
        mDescriptionShow = (TextView) findViewById(R.id.description_show);
        mUserName = (TextView) findViewById(R.id.user_name_show);
        mMobile = (TextView) findViewById(R.id.mobile_show);
        mPrice = (TextView) findViewById(R.id.price_show);
        mAddress = (TextView) findViewById(R.id.address_show);
        mDistt = (TextView) findViewById(R.id.distt_show);
        mCategory = (TextView) findViewById(R.id.category_show);
        mWriter_show = (TextView) findViewById(R.id.writer_tv);

        mUserProfile_btn = (Button) findViewById(R.id.view_user_btn);
        //mToolbar =findViewById(R.id.detail_tool);

        mBookShow = findViewById(R.id.book_show);
        mBookShow.refreshDrawableState();

        postData = getIntent().getExtras();

        bname = postData.getString("bookname");
        price = postData.getString("price");
        desc = postData.getString("desc");
        email = postData.getString("email");
        uid = postData.getString("UID");
        category1 = postData.getString("category");
        mobile = postData.getString("mobile");
        sWriter = postData.getString("writer");


        mBookNameShow.setText(bname);
        mPrice.setText("₹ " + price);
        mDescriptionShow.setText(desc);
        mCategory.setText(category1);
        mMobile.setText(mobile);
        mWriter_show.setText(sWriter);

        //setSupportActionBar(mToolbar);
        //mToolbar.setDuplicateParentStateEnabled(true);



        mFire = FirebaseFirestore.getInstance();
        mFirebaseAuth=FirebaseAuth.getInstance();
        checkReq();

//       showinfo();
        mFire.collection("Post").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                for (DocumentSnapshot doc : documentSnapshots.getDocuments()) {
                    if (doc.getString("name").equals(bname)
                            && doc.getString("price").equals(price)
                            && doc.getString("writer").equals(sWriter)
                            && doc.getString("uid").equals(uid)) {
                        String imgUrl = doc.getString("postImage");
                        RequestOptions r = new RequestOptions();
                        r.placeholder(R.drawable.loading);

                        Glide.with(getApplicationContext())
                                .setDefaultRequestOptions(r)
                                .load(imgUrl)
                                .into(mBookShow);
                    }
                }
            }
        });

      mUserProfile_btn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              mUserProfile_btn.setEnabled(false);

//              if(mUserProfile_btn.getText().equals("Request for Contact")){
//                  mUserProfile_btn.setText("Waiting for Response");
//              }else{
//                  mUserProfile_btn.setText("Request for Contact");
//              }

              Map<String, String> requestMap = new HashMap();
              requestMap.put("postId", postData.getString("postId"));
              requestMap.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
              requestMap.put("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());

              mFire.collection("Post")
                      .document(postData.getString("postId"))
                      .collection("request")
                      .document(mFirebaseAuth.getCurrentUser().getEmail())
                      .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                  @Override
                  public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                      if(task.isSuccessful()){
                          if (!task.getResult().exists()){

                              Map<String, Object> reqMap = new HashMap<>();
//                              reqMap.put("TimeStamp",FieldValue.serverTimestamp());
                              reqMap.put("status", "waiting");
                              reqMap.put("email", mFirebaseAuth.getCurrentUser().getEmail());
                              reqMap.put("postId", postData.getString("postId"));

                              mFire.collection("Post/" + postData.getString("postId") + "/request")
                                      .document(mFirebaseAuth.getCurrentUser().getEmail())
                                      .set(reqMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                  @Override
                                  public void onSuccess(Void aVoid) {
                                      mUserProfile_btn.setEnabled(true);
                                      checkReq();
                                  }
                              });

                          } else {
                              mFire.collection("Post/" + postData.getString("postId") + "/request")
                                      .document(mFirebaseAuth.getCurrentUser().getEmail()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                  @Override
                                  public void onSuccess(Void aVoid) {
                                      mUserProfile_btn.setEnabled(true);
//                                      Toast.makeText(PostPage.this, "Waiting for user", Toast.LENGTH_SHORT).show();
//                                      Toast.makeText(PostPage.this, "", Toast.LENGTH_SHORT).show();
//                                      mUserProfile_btn.setText("Wating for Response");
                                        checkReq();
                                  }
                              });
                          }
                      }
                  }
              });
          }
      });




        mMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+smobile));

                if (ActivityCompat.checkSelfPermission(PostPage.this,
                        Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(PostPage.this,
                                new String[]{Manifest.permission.CALL_PHONE},1);

                }
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        mFire.collection("Post")
                .document(postData.getString("postId"))
                .collection("request")
                .document(mFirebaseAuth.getCurrentUser().getEmail())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
//                    Toast.makeText(PostPage.this, "Status: " + documentSnapshot.getString("status"), Toast.LENGTH_SHORT).show();
                    mFire.collection("user")
                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot documentSnapshots) {
                            for (DocumentSnapshot doc : documentSnapshots.getDocuments()) {
                                if (doc.getString("email").equals(email)) {
                                    sname = doc.getString("name");
                                    mUserName.setText(sname);
                                    postemail = doc.getString("email");
                                    smobile = doc.getString("mobile");
                                    mMobile.setText(smobile);

                                }
                            }
                        }
                    });
                }
            }
        });

    }

    private void showinfo() {

        checkReq();
        if(status.equals("accepted")){
            mFire.collection("user").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot documentSnapshots) {

                    for (DocumentSnapshot doc : documentSnapshots.getDocuments()) {
                        if (doc.getString("email").equals(email)) {
                            sname = doc.getString("name");
                            mUserName.setText(sname);
                            postemail = doc.getString("email");
                            smobile = doc.getString("mobile");
                            mMobile.setText(smobile);

                        }
                    }
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PostPage.this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }else{
            mUserName.setText("Unknown");
            mMobile.setText("xxxxxxxxxx");
        }



    }

    private void checkReq() {
        mFire.collection("Post")
                .document(postData.getString("postId"))
                .collection("request")
                .document(mFirebaseAuth.getCurrentUser().getEmail())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if (!task.getResult().exists()){

                            mUserProfile_btn.setText("Request for Contact");
                    } else {
                        mUserProfile_btn.setText("Wating for Response");
                    }
                }
            }
        });
    }
}