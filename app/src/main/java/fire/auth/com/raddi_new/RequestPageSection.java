package fire.auth.com.raddi_new;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class RequestPageSection extends AppCompatActivity {

    RecyclerView mRecyclerView;
    RequestListAdapter requestListAdapter;
    List<RequestListModal> listModals;

    FirebaseAuth mAuth;
    FirebaseFirestore mFire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_page_section);


        listModals = new ArrayList<>();
        requestListAdapter = new RequestListAdapter(listModals);

        Bundle bundle = getIntent().getExtras();


        mRecyclerView = findViewById(R.id.requestPostList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(requestListAdapter);

        mFire = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

//        Toast.makeText(this, "PostId " +
//                bundle.getString("postId")
//                , Toast.LENGTH_SHORT).show();

        mFire.collection("Post")
                .document(bundle.getString("postId"))
                .collection("request")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
//                Toast.makeText(RequestPageSection.this, "Count: " + documentSnapshots.size(), Toast.LENGTH_SHORT).show();
                for(DocumentSnapshot doc : documentSnapshots.getDocuments()){
//                    Toast.makeText(RequestPageSection.this, "Doc Id" + doc.getId(), Toast.LENGTH_SHORT).show();
                    listModals.add(doc.toObject(RequestListModal.class));
                }
            requestListAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RequestPageSection.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });







    }
}
