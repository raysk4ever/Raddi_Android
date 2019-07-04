package fire.auth.com.raddi_new;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.List;

class RequestListAdapter extends RecyclerView.Adapter<RequestListAdapter.ViewHolder> {

    List<RequestListModal> mList;
    String postId, email;
    FirebaseFirestore mFire;
    FirebaseAuth mAuth;

    public RequestListAdapter(List<RequestListModal> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view  = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.request_list, viewGroup, false);

        postId = mList.get(i).getPostId();
        email = mList.get(i).getEmail();

        mFire = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.homeNookName.setText(mList.get(i).getStatus() + mList.get(i).getEmail());

        viewHolder.reqestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore.getInstance().collection("Post")
                        .document(postId)
                        .collection("request")
                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                            mFire.collection("Post")
                                    .document(postId)
                                    .collection("request")
                                    .document(email)
                                    .update("status", "accepted");
                        }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView homeNookName;
        Button reqestBtn;

        View mView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            homeNookName = mView.findViewById(R.id.requestPostName);
            reqestBtn = mView.findViewById(R.id.requestPostBtn);
        }
    }
}
