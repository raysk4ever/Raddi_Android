package fire.auth.com.raddi_new;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Wishlist extends AppCompatActivity {

    RecyclerView mRecyclerView;
    List<wishmodel> mList;
    wishAdapter adapter;

    FirebaseFirestore mFire;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        mFire = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mList = new ArrayList<>();
        adapter = new wishAdapter(mList);

        mRecyclerView = findViewById(R.id.recy_wishlist);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);

        mFire.collection("Post").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                for (DocumentSnapshot documentSnapshot: documentSnapshots){
                    mFire.collection("Post/"
                            +documentSnapshot.getString("docId")
                            +"/wishList").document(mAuth.getCurrentUser().getEmail()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                            mList.add(documentSnapshot.toObject(wishmodel.class));
                            adapter.notifyDataSetChanged();

                        }
                    });
                }

            }
        });

    }

}
