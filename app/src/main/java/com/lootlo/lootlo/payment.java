package com.lootlo.lootlo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class payment extends AppCompatActivity {
    FirebaseAuth.AuthStateListener mAuthListner;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference databaseReference;
    TextView bal;


    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(mAuthListner);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        bal= (TextView) findViewById(R.id.balnce);


        user = FirebaseAuth.getInstance().getCurrentUser();
        auth = FirebaseAuth.getInstance();
        mAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            }
        };


        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://lootlo-2a2d2.firebaseio.com");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bal.setText(dataSnapshot.child(user.getUid()).child("bal").getValue()+" Rs");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
