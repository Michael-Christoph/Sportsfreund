package de.ur.mi.android.sportsfreund;

import android.os.AsyncTask;

import com.google.firebase.database.*;

public class RealtimeSync extends AsyncTask<Game,Integer,Boolean> {

    private DatabaseReference mDatabaseRoot;

    @Override
    protected Boolean doInBackground(Game... games) {

        mDatabaseRoot = FirebaseDatabase.getInstance().getReference();
        String gameId = mDatabaseRoot.push().getKey();
        mDatabaseRoot.child(gameId).setValue(games[0]);
        //mDatabaseRoot.setValue(games[0]);
        return false;
    }
}
