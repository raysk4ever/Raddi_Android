package fire.auth.com.raddi_new;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Feedback extends AppCompatActivity {

    EditText mFeedName, mFeedMobile, mFeedMsg;
    Button mFeedBtn;

    FirebaseAuth mAuth;
    FirebaseFirestore mFeedStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        mFeedName = (EditText) findViewById(R.id.feed_name);
        mFeedMobile = (EditText) findViewById(R.id.feed_mobile);
        mFeedMsg = (EditText) findViewById(R.id.feed_msg);
        mFeedBtn = (Button) findViewById(R.id.feed_btn);

        mFeedStore = FirebaseFirestore.getInstance();

        mFeedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mFeedName.getText()) &&
                        !TextUtils.isEmpty(mFeedMobile.getText()) &&
                        !TextUtils.isEmpty(mFeedMsg.getText())) {
                    Map<String, Object> feed = new HashMap<>();
                    feed.put("name", mFeedName.getText().toString());
                    feed.put("mobile", mFeedMobile.getText().toString());
                    feed.put("feedback", mFeedMsg.getText().toString());

                    mFeedStore.collection("feed")
                            .add(feed)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    mFeedName.setText("");
                                    mFeedMobile.setText("");
                                    mFeedMsg.setText("");
                                    Toast.makeText(Feedback.this, "Feedback Submitted", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Feedback.this, "please retry", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }
}
