package com.lootlo.lootlo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class myRef extends AppCompatActivity {

    DatabaseReference databaseReference;

    FirebaseAuth.AuthStateListener mAuthListner;
    FirebaseAuth auth;
    FirebaseUser user;
    EditText editText;
    Button button;
    TextView textView;
    ProgressDialog progressDialog;
    RelativeLayout relativeLayout;
    TextView shareText;
    Button shareButton;

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(mAuthListner);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ref);

        shareText=(TextView) findViewById(R.id.shareText);
        shareButton=(Button) findViewById(R.id.shareButton);

        relativeLayout = (RelativeLayout) findViewById(R.id.myRef_layout);
        editText= (EditText) findViewById(R.id.myRef_code);
        button= (Button) findViewById(R.id.create);
        textView= (TextView) findViewById(R.id.already);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Loding...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();



        user=FirebaseAuth.getInstance().getCurrentUser();
        auth = FirebaseAuth.getInstance();
        mAuthListner=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser()==null){
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                }
            }
        };

        databaseReference= FirebaseDatabase.getInstance().getReferenceFromUrl("https://lootlo-2a2d2.firebaseio.com");

        databaseReference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("ref")){
                    editText.setVisibility(EditText.GONE);
                    button.setVisibility(Button.GONE);
                    shareButton.setVisibility(Button.VISIBLE);
                    String ref=dataSnapshot.child("ref").getValue(String.class);
                    relativeLayout.setVisibility(RelativeLayout.VISIBLE);
                    textView.setText(ref);
                    progressDialog.dismiss();

                }
                else {
                    relativeLayout.setVisibility(RelativeLayout.GONE);
                    shareButton.setVisibility(Button.GONE);
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    // Share Button tapped

    public void share(View view){
        Intent shareing =new Intent(Intent.ACTION_SEND);
        shareing.setType("text/plain");
        shareing.putExtra(Intent.EXTRA_SUBJECT,"Refer msg");
        shareing.putExtra(Intent.EXTRA_TEXT,"For Get Referral Boanus You Have To Download This App");
        startActivity(Intent.createChooser(shareing,"Share usingy"));
    }


    public void create(View view){
        final String s=editText.getText().toString();
        databaseReference.child("refer").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(s)){
                    databaseReference.child("refer").child(s).setValue(user.getUid());
                    databaseReference.child(user.getUid()).child("ref").setValue(s);
                }
                else {
                    Toast.makeText(getApplicationContext(),"Please select Other refer code",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
