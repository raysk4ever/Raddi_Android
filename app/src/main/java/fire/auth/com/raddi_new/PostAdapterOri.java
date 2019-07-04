package fire.auth.com.raddi_new;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostAdapterOri extends RecyclerView.Adapter<PostAdapterOri.ViewHolder>{

    List<PostAdapter> postlist;
    FirebaseFirestore mFire;
    FirebaseAuth mAuth;
    Context context;
    String bookName, category, locality, price, writer, email, desc;
    Button postLikeBtn;
    int likeCount = 0;
    String postId;

    public PostAdapterOri(){}
    public PostAdapterOri(List<PostAdapter> postlist, Context context) {
        this.postlist = postlist;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View mview = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.laytest,viewGroup, false);

        mFire = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        return new ViewHolder(mview);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
       viewHolder.setIsRecyclable(false);

        bookName = postlist.get(i).getName();
        price = postlist.get(i).getPrice();
        locality = postlist.get(i).getLocality();
        category = postlist.get(i).getCategory();
        desc = postlist.get(i).getDescription();
        email = postlist.get(i).getUserEmail();
        likeCount = postlist.get(i).getLikes();
        postId = postlist.get(i).getDocId();
        writer = postlist.get(i).getWriter();

        viewHolder.mBookname.setText(bookName);
        viewHolder.mPrice.setText("₹ "+ price);
        viewHolder.mDescription.setText(locality);
        viewHolder.mCategory.setText(category);


        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.loading);

        Glide.with(context).setDefaultRequestOptions(requestOptions)
                .load(postlist.get(i).getPostImage()).into(viewHolder.mBookImage);

        viewHolder.postCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent postIntent = new Intent(context, PostPage.class);
                postIntent.putExtra("UID", postlist.get(i).getUid());
                postIntent.putExtra("bookname",postlist.get(i).getName());
                postIntent.putExtra("desc",postlist.get(i).getDescription());
                postIntent.putExtra("price",postlist.get(i).getPrice());
                postIntent.putExtra("email",postlist.get(i).getUserEmail());
                postIntent.putExtra("category",postlist.get(i).getCategory());
                postIntent.putExtra("writer",postlist.get(i).getWriter());
                postIntent.putExtra("postId", postlist.get(i).getPostId());

                postIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(postIntent);

            }
        });

        mFire.collection("Post/"+postlist.get(i).getDocId()+"/likes").document(mAuth.getCurrentUser().getEmail()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (documentSnapshot.exists()){
                    //Toast.makeText(context, "Liked", Toast.LENGTH_SHORT).show();
                    viewHolder.postLikeBtn.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_fill_24dp));
                }else{
                    //Toast.makeText(context, "Disliked", Toast.LENGTH_SHORT).show();
                    viewHolder.postLikeBtn.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_border_black_24dp));
                }
            }
        });


        mFire.collection("Post/"+postlist.get(i).getDocId()+"/likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (!documentSnapshots.isEmpty()){
                    updateLikes(documentSnapshots.size(), viewHolder);
                }else {
                    updateLikes(0, viewHolder);
                }
            }
        });

        viewHolder.likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mFire.collection("Post")
                        .document(postlist.get(i).getDocId())
                        .collection("likes")
                        .document(mAuth.getCurrentUser().getEmail())
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().exists()) {
                                Map<String, Object> likemap = new HashMap<>();
                                likemap.put("time", FieldValue.serverTimestamp());

                                mFire.collection("Post/" + postlist.get(i).getDocId() + "/likes")
                                        .document(mAuth.getCurrentUser().getEmail())
                                        .set(likemap);
                            } else {
                                mFire.collection("Post/" + postlist.get(i).getDocId() + "/likes")
                                        .document(mAuth.getCurrentUser().getEmail()).delete();
                            }

                        }else{
                            Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        viewHolder.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = "Hii, i found "
                        +postlist.get(i).getName() +
                        " book in just "
                        + postlist.get(i).getPrice() +
                        ", there are many more books.." +
                        "check out this newly awesome RADDI Android App..." +
                        "Available on playstore";

                Intent i = new Intent(Intent.ACTION_SEND, Uri.parse("https://play.google.com/store/appsAe/details?id=com.online_books.anshika.books_1"));
                i.putExtra(Intent.EXTRA_TEXT, content);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setType("text/html");
                context.startActivity(Intent.createChooser(i,
                        "Select app to share"));
            }
        });
    }

    private void updateLikes(int count, ViewHolder vh) {
        vh.likeTv.setText(count + " likes");
    }

    @Override
    public int getItemCount() {
        return postlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View mv;
        TextView mBookname, mName, mPrice, mDescription, mCategory, mMobile;
        ImageView mBookImage;
        LinearLayout likeLayout;
        ImageButton postLikeBtn;
        ImageButton wishBtn;
        LinearLayout postCard;
        ImageButton shareBtn;
        TextView likeTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mv = itemView;
            likeTv = mv.findViewById(R.id.likesTv);
            mBookname = (TextView) mv.findViewById(R.id.home_bookName);
            mPrice = (TextView) mv.findViewById(R.id.home_price);
            mDescription = (TextView) mv.findViewById(R.id.home_description);
            mBookImage = (ImageView)mv.findViewById(R.id.home_bookImage);
            postCard = (LinearLayout) mv.findViewById(R.id.post_card);
            mCategory = mv.findViewById(R.id.home_category);
            postLikeBtn= mv.findViewById(R.id.postLikeBtn);
            shareBtn = mv.findViewById(R.id.shareBtn);
            likeLayout = mv.findViewById(R.id.likeLayout);
        }
    }
}