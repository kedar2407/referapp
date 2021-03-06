package com.example.referandearn;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth fAuth;
    String userId,count;
    FirebaseUser user;
    DatabaseReference databaseReference;
    Query query;
    String email;ListView listview;
    ArrayList<String> list=new ArrayList<>();
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        fAuth = FirebaseAuth.getInstance();
        count=String.valueOf(Register.count);
        user = fAuth.getCurrentUser();
        recyclerView=findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userId=user.getEmail().replace(".","dot");


        listview=(ListView)findViewById(R.id.listview);

        final ArrayAdapter<String> adapter= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list);
        email=fAuth.getCurrentUser().getEmail().replace(".","dot");




        FirebaseRecyclerOptions<model> options =
                    new FirebaseRecyclerOptions.Builder<model>()
                            .setQuery(FirebaseDatabase.getInstance().getReference().child("refusers").child(email)
                                    , model.class)
                            .build();

myadapter myadapter=new myadapter(options);
recyclerView.setAdapter(myadapter);

            databaseReference= FirebaseDatabase.getInstance().getReference("refusers").child(email);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    Toast.makeText(MainActivity.this, email, Toast.LENGTH_LONG).show();

//                    list.add(dataSnapshot.getValue().toString());
                    list.add(String.valueOf(dataSnapshot.getValue()));

                    listview.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                                   }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            query = databaseReference.orderByChild("email");
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }

            });


//            resendCode.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(final View v) {
//
//                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            Toast.makeText(v.getContext(), "Verification Email Has been Sent.", Toast.LENGTH_SHORT).show();
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.d("tag", "onFailure: Email not sent " + e.getMessage());
//                        }
//                    });
//                }
//            });


        ImageButton generate = findViewById(R.id.sharebtn);
        ImageButton logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth.signOut();
                startActivity(new Intent(MainActivity.this,Login.class));
            }
        });




// Attach a listener to read the data at our posts reference

        TextView user = findViewById(R.id.username);
        user.setText(fAuth.getCurrentUser().getEmail());

        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    createlink();

            }
        });


    }



    public void createlink(){
        Log.e("main", "create link ");

        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.blueappsoftware.com/"))
                .setDynamicLinkDomain("examplere.page.link")
                // Open links with this app on Android
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                // Open links with com.example.ios on iOS
                //.setIosParameters(new DynamicLink.IosParameters.Builder("com.example.ios").build())
                .buildDynamicLink();
//click -- link -- google play store -- inistalled/ or not  ----
        Uri dynamicLinkUri = dynamicLink.getUri();
        Log.e("main", "  Long refer "+ dynamicLink.getUri());

        createReferlink(userId);

    }

    public void createReferlink(String custid) {
        // manuall link
        String sharelinktext = "https://examplere.page.link/?" +
                "link=http://https://examplere.page.link/?" + custid + "cid" +
                "&apn=" + getPackageName() +
                "&st=" + "My Refer Link" +
                "&sd=" + "Reward Coins 20" +
                "&si=" + "https://www.blueappsoftware.com/logo-1.png";

        Log.e("mainactivity", "sharelink - " + sharelinktext);
        // shorten the link
        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                //.setLongLink(dynamicLink.getUri())    // enable it if using firebase method dynamicLink
                .setLongLink(Uri.parse(sharelinktext))  // manually
                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            // Short link created
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();
                            Log.e("main ", "short link " + shortLink.toString());
                            // share app dialog
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_TEXT, shortLink.toString());
                            intent.setType("text/plain");
                            startActivity(intent);


                        } else {
                            // Error
                            // ...
                            Log.e("main", " error " + task.getException());

                        }
                    }
                });


    }

}


