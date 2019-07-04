package fire.auth.com.raddi_new;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserAllPost extends AppCompatActivity {

    List<PostAdapter> mList;
    RecyclerView mRecyclerView;
    UserAllPostAdapter mAdapter;

    FirebaseAuth mAuth;
    FirebaseFirestore mFire;
    private android.support.v7.widget.Toolbar mTopbars;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_all_post);

        mFire = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mList = new ArrayList<>();
        mAdapter = new UserAllPostAdapter(mList);

        mRecyclerView = findViewById(R.id.userAllPost_List);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

//        mTopbars = findViewById(R.id.mToolbarMyPost);
//        setSupportActionBar(mTopbars);
//        getSupportActionBar().setTitle("My Post");


        mFire.collection("Post")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                        for (DocumentChange documentChange : documentSnapshots.getDocumentChanges()){
                            if (documentChange.getType() == DocumentChange.Type.ADDED){
                                if (documentChange.getDocument().getString("userEmail").equals(mAuth.getCurrentUser().getEmail()))
                                mList.add(documentChange.getDocument().toObject(PostAdapter.class));
                                mAdapter.notifyDataSetChanged();
                              //  Toast.makeText(UserAllPost.this, "hii", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });




    }
}
