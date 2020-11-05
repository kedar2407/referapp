package com.example.referandearn;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    RelativeLayout rell1, rell2,relmain;
    EditText mEmail,mPassword;
    Button mLoginBtn,fpassword;
    FirebaseAuth fAuth;


    Handler handler = new Handler ();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //My two Relative Layouts
            rell1.setVisibility(View.VISIBLE);
            rell2.setVisibility(View.VISIBLE);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLoginBtn=findViewById(R.id.loginb);
        fpassword=findViewById(R.id.forgot);
        mEmail=findViewById(R.id.emailadress);
        mPassword=findViewById(R.id.password);
        rell1 = (RelativeLayout)findViewById(R.id.rel1);
        relmain=findViewById(R.id.relmain);
        rell2 = (RelativeLayout)findViewById(R.id.rel2);
        relmain = (RelativeLayout)findViewById(R.id.relmain);
        fAuth= FirebaseAuth.getInstance();
        handler.postDelayed(runnable,00);



//        if (fAuth.getCurrentUser() != null) {
//            if (getIntent().getExtras() == null) {
//                startActivity(new Intent(Login.this, splashscreen.class));
//            }
//        }

        Button signup=findViewById(R.id.sighnupbutton);
        signup.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        startActivity(new Intent(Login.this,Register.class).putExtra("false","false"));
    }

        });
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email is Required.");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password is Required.");
                    return;
                }

                if(password.length() < 6){
                    mPassword.setError("Password Must be >= 6 Characters");
                    return;
                }


                // authenticate the user


                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Login.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }else {
                            Toast.makeText(Login.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        });
            fpassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final EditText resetMail = new EditText(v.getContext());
                    resetMail.setBackgroundResource(R.drawable.outlineeditext);
                    final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                    passwordResetDialog.setTitle("Reset Password ?");
                    passwordResetDialog.setMessage("Enter Your Email To Received Reset Link.");
                    passwordResetDialog.setView(resetMail);

                    passwordResetDialog.setPositiveButton("Reset Password", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // extract the email and send reset link
                            String mail = resetMail.getText().toString();
                            fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Login.this, "Reset Link Sent To Your Email.", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Login.this, "Error ! Reset Link is Not Sent" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });

                    passwordResetDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // close the dialog
                        }
                    });

                    passwordResetDialog.create().show();

                }
            });


    }
}



