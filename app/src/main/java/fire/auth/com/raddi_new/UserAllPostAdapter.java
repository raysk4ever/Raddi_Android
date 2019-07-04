package fire.auth.com.raddi_new;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class UserAllPostAdapter extends RecyclerView.Adapter<UserAllPostAdapter.ViewHolder> {

    List<PostAdapter> mList;
    AppCompatDialog compatDialog;
    Context context;
    TextView deletetv;
    String currentDocid, bookName, bookPrice, bookdesc, postId;

    FirebaseFirestore mFire;

    public UserAllPostAdapter() {

    }

    public UserAllPostAdapter(List<PostAdapter> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.currentpostuser, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {

        compatDialog = new AppCompatDialog(context);
        compatDialog.setContentView(R.layout.delete_post_layout);
        mFire = FirebaseFirestore.getInstance();

         bookName = mList.get(i).getName();
         bookPrice = mList.get(i).getPrice();
         String uid = mList.get(i).getUid();
         bookdesc = mList.get(i).getDescription();
         postId = mList.get(i).getDocId();

        deletetv = compatDialog.findViewById(R.id.deletePosttv);

        viewHolder.bookName.setText(mList.get(i).getName());
        viewHolder.mPrice_del.setText(mList.get(i).getPrice());
        viewHolder.mDelete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


               mFire.collection("Post").document(mList.get(i).getDocId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {
                       mList.remove(i);
                        context.startActivity(new Intent(context, Home.class));
                       Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                   }
               });
            }
        });

        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent post = new Intent(context, RequestPageSection.class);
                post.putExtra("postId",postId);
                context.startActivity(post);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView bookName,mPrice_del;
        Button mDelete_btn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            bookName = mView.findViewById(R.id.del_book_name);
            mPrice_del = mView.findViewById(R.id.del_price);
            mDelete_btn = mView.findViewById(R.id.delete_btn);

        }
    }
}
