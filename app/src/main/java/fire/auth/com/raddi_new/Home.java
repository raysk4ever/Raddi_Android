package fire.auth.com.raddi_new;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {

    List<PostAdapter> mList;

    FirebaseAuth mFirebaseAuth;
    FirebaseFirestore mFire;

    PostAdapterOri postAdater;

    FloatingActionButton mFloatingActionButton;

    RecyclerView mRecyclerView;
    String docid = null;

        private android.support.v7.widget.Toolbar mToolbar;

        DrawerLayout mDrawerLayout;
        ActionBarDrawerToggle mActionBarDrawerToggle;
        android.support.v7.widget.Toolbar tb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFire = FirebaseFirestore.getInstance();

//        tb = findViewById(R.id.mToolbar);

//        tb.setBackgroundColor(new Color(Color.WHITE));
//        tb.setElevation(0);
//        tb.setTitle("Home");
//        ActionBar ab = getSupportActionBar();
//        ab.setElevation(0);
//        ab.setTitle("Homes");
       /* mDrawerLayout = (DrawerLayout) findViewById(R.id.mdrawer);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();*/

        mList = new ArrayList<>();
        postAdater = new PostAdapterOri(mList, getApplicationContext());

        mRecyclerView = findViewById(R.id.home_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(postAdater);



        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, PostBook.class));
            }
        });


//        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.mToolbar);
//        setSupportActionBar(mToolbar);
//        getSupportActionBar().setTitle("RADDI");




        mFire.collection("Post")
                .orderBy("TimeStamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                for (DocumentChange doc : documentSnapshots.getDocumentChanges()){
                    if (doc.getType() == DocumentChange.Type.ADDED){

                        mList.add(doc.getDocument().toObject(PostAdapter.class));
                        postAdater.notifyDataSetChanged();

                    }
                    if (doc.getType() == DocumentChange.Type.REMOVED){
                        mList.remove(doc.getDocument().toObject(PostAdapter.class));
                        postAdater.notifyDataSetChanged();
                    }
                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //if (mActionBarDrawerToggle.onOptionsItemSelected(item)){
          //  return true;
        //     }
        switch (item.getItemId()){
            case  R.id.menu_profile_btn:
                startActivity(new Intent(Home.this,ProfileActivityNew.class));
                break;

            case  R.id.menu_about_btn:
                startActivity(new Intent(Home.this,AboutUs.class));
                break;


            case R.id.menu_feedback_btn:
                startActivity(new Intent(Home.this, Feedback.class));
                break;

            case R.id.menu_logout_btn:
                mFirebaseAuth.signOut();
                startActivity(new Intent(Home.this, MainActivity.class));
                finish();
                break;

            case R.id.menu_allpost:
                Intent userAllPostIntent = new Intent(getApplicationContext(), UserAllPost.class);
                startActivity(userAllPostIntent);
                break;

            case R.id.menu_refresh:
                postAdater.notifyDataSetChanged();
                Toast.makeText(this, "refreshing", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
        //return super.onOptionsItemSelected(item);
    }



    private void getCurrentLocation() {

        Toast.makeText(this, "abi kaam krna baki hai isme....", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mFire.collection("user").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot dc: task.getResult().getDocuments())
                {
                    if (dc.getString("email").equals(mFirebaseAuth.getCurrentUser().getEmail())){
                        mFire.collection("user").document(dc.getId()).update("uid",
                                mFirebaseAuth.getCurrentUser().getUid());
                    }
                }
            }
        });


    }
}
