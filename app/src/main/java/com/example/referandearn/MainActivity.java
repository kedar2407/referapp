package com.example.referandearn;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    EditText mFullName, mEmail, mPassword, mPhone, cpassword;
    Button mRegisterBtn, login;
    private ProgressDialog mProgressDialog;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String UserId,fuser, refferal, refree;
    public String cutid;
FirebaseUser firebaseUser;
    TextView createtv,creatv2;
    String TAG = "splash";
    static int count;
    private DatabaseReference userDb;
    public DatabaseReference userDbref,refTABLE;
    ImageView profileImageView;
    Map<String, Object> refer = new HashMap<>();

    StorageReference storageReference;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fStore = FirebaseFirestore.getInstance();
        mFullName = findViewById(R.id.fullNameet);
        mPassword = findViewById(R.id.password);
        mEmail = findViewById(R.id.emailadress);
        login = findViewById(R.id.login);
        cpassword = findViewById(R.id.confimpassword);
        mPhone = findViewById(R.id.mobile);
        mRegisterBtn = findViewById(R.id.register);
        fAuth = FirebaseAuth.getInstance();
        profileImageView = findViewById(R.id.imgView);
        storageReference = FirebaseStorage.getInstance().getReference();
        mProgressDialog = new ProgressDialog(this);
        userDb = FirebaseDatabase.getInstance().getReference().child("users");
        refTABLE = FirebaseDatabase.getInstance().getReference().child("refer");
        userDbref = FirebaseDatabase.getInstance().getReference().child("refers");
//        fuser= FirebaseAuth.getInstance().getCurrentUser().getUid().toString().trim();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

//from here iam handeling the refer link
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null; 
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();

                            Log.e(TAG, " my referlink " + deepLink.toString());
                            //   "http://www.blueappsoftware.com/myrefer.php?custid=cust123-prod456"
                            String referlink = deepLink.toString();
                            try {

                                referlink = referlink.substring(referlink.lastIndexOf("=") + 1);
                                Log.e(TAG, " substring " + referlink); //cust123-prod456
                                cutid = referlink.substring(0, referlink.indexOf("cid"));
                                Toast.makeText(Register.this, cutid, Toast.LENGTH_SHORT).show();



//                                createtv.setText(cutid);

//here is the string valuews of 1st users

                                Log.e(TAG, " custid " + cutid.replace("http://https://examplere.page.link/?",""));

                                // shareprefernce (prodid, custid);
                                //sharepreference  (refercustid, custid)


                            } catch (Exception e) {
                                Log.e(TAG, " error " + e.toString());
                            }


                        }


                        // Handle the deep link. For example, open the linked
                        // content, or apply promotional credit to the user's
                        // account.
                        // ...

                        // ...
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "getDynamicLink:onFailure", e);
                    }
                });


        if (fAuth.getCurrentUser() != null) {
            if (getIntent().getExtras() == null) {
                startActivity(new Intent(Register.this, splashscreen.class));
            }
        }

//        StorageReference profileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
//        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//                Picasso.get().load(uri).into(profileImageView);
//            }
//        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this, Login.class));
            }
        });

////        this is the Button where i want to implement the action
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Map<String, Object> user = new HashMap<>();

                final String name = mFullName.getText().toString().trim();
//                these are new users data
                final String email = mEmail.getText().toString().trim();
                final String pass = mPassword.getText().toString().trim();
                final String mobile = mPhone.getText().toString().trim();
                final String cPass = cpassword.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Please Enter Email ");
                    return;
                }
                if (TextUtils.isEmpty(name)) {
                    mFullName.setError("Please Enter Full Name ");
                    return;
                }
                if (TextUtils.isEmpty(pass)) {
                    mPassword.setError("Please Enter Password ");
                    return;
                }
                if (TextUtils.isEmpty(mobile)) {
                    mPhone.setError("Please Enter Email ");
                    return;
                }
                if (cPass != pass) {
                    cpassword.setError("Password Must Match");
                }
                if (pass.length() < 6) {
                    mPassword.setError("Password Must be 6 character");
                    return;
                }


                fAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Register.this, "user Created", Toast.LENGTH_SHORT).show();
                            UserId = fAuth.getUid();

                            DocumentReference documentReference = fStore.collection("users").document(UserId);

                            user.put("fname", name);
                            user.put("email", email);
                            user.put("pass", pass);

//                            here im putting new users data to firestore
                         documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Register.this, "adedd", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Register.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });


//                            FirebaseDatabase.getInstance().getReference().child("whoReffered").
//                                    child("userid2").child(cutid).child(firebaseUser.getEmail());

                            FirebaseDatabase.getInstance().getReference().child("refusers").
                                    child(cutid.replace("http://https://examplere.page.link/?","")).child(email.replace(".","dot")).setValue(true);

//                                checkForPermission();
                            startActivity(new Intent(Register.this, MainActivity.class));

                        } else {
                            Toast.makeText(Register.this, task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        });

        Button adhar = findViewById(R.id.adhar);
        adhar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(openGalleryIntent, 1000);

                Toast.makeText(Register.this, "", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();

                //profileImage.setImageURI(imageUri);
                if (checkPermissionREAD_EXTERNAL_STORAGE(this)) {
                    // do your stuff..
                    uploadImageToFirebase(imageUri);
                }

            }
        }

    }

    private void uploadImageToFirebase(Uri imageUri) {
        // uplaod image to firebase storage
        final StorageReference fileRef = storageReference.child("users/" + fAuth.getCurrentUser().getUid() + "/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Picasso.get().load(uri).into(profileImageView);


                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed.", Toast.LENGTH_SHORT).show();
            }
        });

    }


    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Register) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,
                            Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Register) context,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{permission},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // do your stuff
                } else {
                    Toast.makeText(Register.this, "GET_ACCOUNTS Denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }

    private void checkForPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            getContacts();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                    Toast.makeText(this, "Read Contact Permissions are required", Toast.LENGTH_LONG).show();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            1);
                }

            }
        }
    }

    private void getContacts() {
        mProgressDialog.setTitle("Creating Account ...");
        mProgressDialog.show();
        Cursor contacts = getContentResolver().
                query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[]{
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME.toString(),
                        ContactsContract.CommonDataKinds.Phone.NUMBER.toString()}, null, null, null);

        HashMap map = new HashMap();
        if (contacts != null) {
            while (contacts.moveToNext()) {
                map.put(contacts.getString(contacts.
                                getColumnIndex((ContactsContract.CommonDataKinds.Phone.NUMBER))),
                        contacts.getString(contacts.
                                getColumnIndex((ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))));
            }
            contacts.close();
        }
        userDb.updateChildren(map).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                mProgressDialog.hide();
                Toast.makeText(Register.this, "Successfully uploaded ", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mProgressDialog.hide();
                Toast.makeText(Register.this, "Something went wrong " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void getreffels(String referer) {
        HashMap map = new HashMap();
        if (refferal != null) {
            map.put(referer, refree);

        }
        userDbref.updateChildren(map).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(Register.this, "succes put", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Register.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });


    }

}
