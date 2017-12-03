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

import com.ebanx.swipebtn.OnStateChangeListener;
import com.ebanx.swipebtn.SwipeButton;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class earn_money extends AppCompatActivity implements RewardedVideoAdListener  {
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    FirebaseUser user;
    TextView bal;
    int balnce;
    int adcount;
    int indigator=0;
    int indigator2=0;
    private RewardedVideoAd mAd;
    FirebaseAuth.AuthStateListener mAuthListner;
    RelativeLayout referLay;
    Button referGet;
    EditText referCode;
    ProgressDialog progressDialog;
    SwipeButton swipeButton;
    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(mAuthListner);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earn_money);

        swipeButton=(SwipeButton) findViewById(R.id.swipe_btn);
        swipeButton.setOnStateChangeListener(new OnStateChangeListener() {
            @Override
            public void onStateChange(boolean active) {
                show();
            }
        });
        progressDialog=new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        referLay= (RelativeLayout) findViewById(R.id.refer);
        referGet= (Button) findViewById(R.id.getRefer);
        referCode= (EditText) findViewById(R.id.refercode);



        MobileAds.initialize(getApplicationContext(),"ca-app-pub-3940256099942544~3347511713");

        mAd = MobileAds.getRewardedVideoAdInstance(this);
        mAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();

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
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(user.getUid())){
                }
                else {
                    Toast.makeText(getApplicationContext(),"Welcome To Loot Lo App",Toast.LENGTH_LONG).show();
                    databaseReference.child(user.getUid()).child("bal").setValue(5);
                    databaseReference.child(user.getUid()).child("count").setValue(0);
                    progressDialog.dismiss();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(user.getUid())) {
                    balnce = dataSnapshot.child(user.getUid()).child("bal").getValue(Integer.class);
                }
                if (dataSnapshot.child(user.getUid()).hasChild("count")){
                    adcount=dataSnapshot.child(user.getUid()).child("count").getValue(Integer.class);
                }
                else {
                    adcount=10;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        bal();


    }

    public void bal(){
        bal=(TextView) findViewById(R.id.bal);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bal.setText(dataSnapshot.child(user.getUid()).child("bal").getValue()+" Rs");
                progressDialog.dismiss();
                if (dataSnapshot.child(user.getUid()).hasChild("earn")){
                    referLay.setVisibility(RelativeLayout.GONE);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void loadRewardedVideoAd() {
        mAd.loadAd("ca-app-pub-3940256099942544/5224354917", new AdRequest.Builder().build());
    }

    @Override
    public void onRewardedVideoAdLoaded() {

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        loadRewardedVideoAd();

    }

    @Override
    public void onRewarded(RewardItem rewardItem) {

        databaseReference.child(user.getUid()).child("bal").setValue(balnce+1);
        if (adcount<5){
            databaseReference.child(user.getUid()).child("count").setValue(adcount+1);
        }
        if (adcount==5){
            databaseReference.child(user.getUid()).child("count").removeValue();
            databaseReference.child("ref").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final String s=dataSnapshot.child(user.getUid()).getValue(String.class);

                    databaseReference.child(s).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (indigator==0) {
                                int bal2 = dataSnapshot.child("bal").getValue(Integer.class);
                                credit(bal2, s);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            databaseReference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (indigator2==0) {
                        int bal2 = dataSnapshot.child("bal").getValue(Integer.class);
                        credit2(bal2, user.getUid());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }





    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }

    @Override
    protected void onPause() {
        mAd.pause(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        mAd.resume(this);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mAd.destroy(this);
        super.onDestroy();
    }

    public void show(){
        if (mAd.isLoaded()) {
            mAd.show();
        }
        else {
            Toast.makeText(getApplicationContext(),"Please Wait Until Ad is Loaded",Toast.LENGTH_LONG).show();
            loadRewardedVideoAd();
        }
    }


    public void getRef(View view){
        final String ref=referCode.getText().toString();
      databaseReference.child("refer").addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
              if (dataSnapshot.hasChild(ref)){
                  String s=dataSnapshot.child(ref).getValue(String.class);
                  databaseReference.child("ref").child(user.getUid()).setValue(s);
                  databaseReference.child(user.getUid()).child("earn").setValue(ref);
              }
          }

          @Override
          public void onCancelled(DatabaseError databaseError) {

          }
      });
    }

    public void gotoRef(View view){
        startActivity(new Intent(getApplicationContext(),myRef.class));
    }

    public void credit(Integer integer,String s){
        databaseReference.child(s).child("bal").setValue(integer+5);
        indigator++;
    }
    public void credit2(Integer integer,String s){
        databaseReference.child(s).child("bal").setValue(integer+5);
        indigator2++;
    }
}
