package fire.auth.com.raddi_new;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    TextInputLayout mEmail, mPassword;
    Button mLogin, mCreate;
    TextView sb_blink, mForgot;

    private FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), Home.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ProgressDialog mProgressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        mForgot = (TextView)findViewById(R.id.forget_tv);
        mEmail = (TextInputLayout) findViewById(R.id.email_login_tv);
        mPassword = (TextInputLayout) findViewById(R.id.password_login_tv);
        mLogin = (Button) findViewById(R.id.login_page_btn);
        mCreate = (Button) findViewById(R.id.create_btn);

        sb_blink = (TextView)findViewById(R.id.sab_blink_tv);
        Animation textAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.blink);
        sb_blink.startAnimation(textAnimation);

        mCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent create_intent = new Intent(getApplicationContext(),CreatePage.class);
                startActivity(create_intent);
                finish();
                //Toast.makeText(MainActivity.this, "Please Registered Your Account ", Toast.LENGTH_LONG).show();
            }
        });

        mForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "this section is under development", Toast.LENGTH_SHORT).show();
            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog.setMessage("Please Wait");
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
                mAuth.signInWithEmailAndPassword(
                        mEmail.getEditText().getText().toString(),
                        mPassword.getEditText().getText().toString()
                ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mProgressDialog.dismiss();
                             if (task.isSuccessful()){
                                 startActivity(new Intent(getApplicationContext(), Home.class));
                                 finish();
                                 //Toast.makeText(MainActivity.this, "Login your Account", Toast.LENGTH_SHORT).show();
                             }else{
                                 Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                             }
                    }
                });
            }
        });



    }

}