package com.uy.esquivel.mobdeve_mp.dao;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.uy.esquivel.mobdeve_mp.model.Score;

import java.util.ArrayList;

public class ScoreDAOFirebaseImpl implements ScoreDAO{

    private final String PATH = "scores";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference(PATH);

    public ScoreDAOFirebaseImpl(Context context){
        final String TAG = "Listener";
        ChildEventListener childEventListener = new ChildEventListener() {

            //loaded on start
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
                Score score = dataSnapshot.getValue(Score.class);
                Log.d(TAG, "Added : " + score.getName());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
                Score score = dataSnapshot.getValue(Score.class);
                Log.d(TAG, "Changed : " + score.getName());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());
                Score score = dataSnapshot.getValue(Score.class);
                Log.d(TAG, "Moved : " + score.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                Toast.makeText(context, "Failed to load comments.",
                        Toast.LENGTH_SHORT).show();
            }
        };

        myRef.addChildEventListener(childEventListener);
    }

    @Override
    public long addScore(Score score) {
        final long[] result = {-1};
        myRef.push().setValue(score,
                new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError error,DatabaseReference ref) {
                        if(error != null){
                            Log.e("ERROR", "ERROR: "+ error.getMessage());
                        }else{
                            Log.d("SUCCESS", "DATA INSERTED");
                            result[0]= 1L;
                        }
                    }
                });

        return result[0];
    }

    @Override
    public ArrayList<Score> getTop10Scores() {
        ArrayList<Score> result = new ArrayList<>();

        myRef.addValueEventListener(new com.google.firebase.database.ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for(DataSnapshot data: snapshot.getChildren()){
                    Score score = new Score();
                    score.setScore(data.child("score").getValue(Integer.class));
                    score.setName(data.child("name").getValue(String.class));
                    result.add(score);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        return result;
    }

}
