package de.ur.mi.android.sportsfreund;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

//cf. http://www.rosebotics.org/android-firebase-v3/unit?unit=1&lesson=13 lecture series
// introducing firebase realtime database
public class ItemAdapter extends ArrayAdapter<Game> {

    private static final String LOG_TAG = "ItemAdapter";

    private static boolean showNoGps = false;

    private ArrayList<Game> gamesInDatabase;
    private ArrayList<Game> gamesForCurrentView;
    private ArrayList<Game> gamesWithCurrentUser;
    private DatabaseReference firebaseGameRef;

    public ItemAdapter(Context context, ArrayList<Game> gamesForCurrentView) {
        super(context, 0, gamesForCurrentView);

        this.gamesForCurrentView = gamesForCurrentView;
        gamesInDatabase = new ArrayList<>();
        gamesWithCurrentUser = new ArrayList<>();
        firebaseGameRef = FirebaseDatabase.getInstance().getReference().child(GlobalVariables.FIREBASE_GAMES_NODE);
        //adds offline persistence even if app is destroyed
        firebaseGameRef.keepSynced(true);
        firebaseGameRef.addChildEventListener(new GamesChildEventListener());
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.list_item, parent, false);

        Game gameToView = getItem(position);
        String title = gameToView.getGameName();
        String body = "Zeit: " + gameToView.getGameDate() + ", " + gameToView.getGameTime() + " Uhr";
        String body2;
        if (gameToView.distanceToGame(getContext()) == null || showNoGps){
            Log.d(LOG_TAG,"showNoGps: " + showNoGps);
            body2 = getContext().getString(R.string.distanceNotAvailable);
        } else {
            if (gameToView.distanceToGame(getContext()) < 1000) {
                body2 = Math.round(gameToView.distanceToGame(getContext())) + " m";
            } else {
                body2 = Math.round(gameToView.distanceToGame(getContext())/1000) + " km";
            }

        }
        TextView titleText = view.findViewById(R.id.TitleText);
        TextView bodyText = view.findViewById(R.id.BodyText);
        TextView body2Text = view.findViewById(R.id.body_2_text);
        titleText.setText(title);
        bodyText.setText(body);
        body2Text.setText(body2);

        return view;
    }

    //listens for changes in firebase database
    class GamesChildEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Game game = dataSnapshot.getValue(Game.class);
            game.setKey(dataSnapshot.getKey());
            gamesInDatabase.add(0,game);
            //remove game immediately afterwards if it is historical
            if (gameIsAlreadyOver(game)){
                remove(game,getContext());
            } else {
                Log.d(LOG_TAG,"game is not yet historical: " + game.getGameName());
            }

            renewViewAccordingToSelectedView();
        }

        //in our case, this only concerns added/removed participants
        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            String key = dataSnapshot.getKey();
            Game updatedGame = dataSnapshot.getValue(Game.class);
            updatedGame.setKey(key);
            if (updatedGame.getParticipants().size() == 0){
                remove(updatedGame,getContext());
            } else {
                for (Game game: gamesInDatabase){
                    if (game.getKey().equals(key)) {
                        game.setValues(updatedGame);
                        renewViewAccordingToSelectedView();
                    }
                }
            }
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            String key = dataSnapshot.getKey();
            for (Game game : gamesInDatabase) {
                if (game.getKey().equals(key)) {
                    gamesInDatabase.remove(game);
                    renewViewAccordingToSelectedView();
                    return;
                }
            }
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.e(LOG_TAG, "Database error: " + databaseError);
        }
    }

    public void addGameToDatabase(Game game, final Context context) {
        Log.d(LOG_TAG,"entered addGameToDatabase-method");
        firebaseGameRef.push().setValue(game, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Toast.makeText(context, R.string.toast_add_game_failed, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, R.string.toast_game_added, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void remove(Game game, Context context) {
        if (game != null){
            firebaseGameRef.child(game.getKey()).removeValue();
            Toast.makeText(context, context.getString(R.string.toast_game_deleted) + game.getGameName(),Toast.LENGTH_SHORT).show();
        } else {
            Log.d(LOG_TAG,"game is null, no game removed");
        }
    }
    public void removeGamesViaName(String gameName,Context context){
        for (Game gameInDatabase: gamesInDatabase){
            if (gameInDatabase.getGameName().equals(gameName)){
                remove(gameInDatabase,context);
            }
        }
    }

    public void addParticipantToGame(Game game, String participantId, Context context) {
        game.addParticipant(participantId, context);
        firebaseGameRef.child(game.getKey()).setValue(game);
    }

    public void removeParticipantFromGame(Game game, String participantId, Context context,String successMessage) {
        game.removeParticipant(participantId, context);
        if (game.getParticipants().size() > 0){
            Log.d(LOG_TAG,"still participants left");
            firebaseGameRef.child(game.getKey()).setValue(game);
            showAppropriateToast(context,successMessage);
        } else {
            Log.d(LOG_TAG,"last participant has unregistered");
            remove(game,context);
        }
    }

    public void removeParticipantFromGameViaKey(String gameKey, String participantId, final Context contextForToast, final String successMessage){
        Log.d(LOG_TAG,"entered removeParticipantFromGameViaKey: " + gameKey + participantId);
        Query participantQuery = firebaseGameRef.child(gameKey).child("participants").orderByValue().equalTo(participantId);
        Log.d(LOG_TAG,"Query: " + participantQuery);

        participantQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot myParticipantSnapshot: dataSnapshot.getChildren()){
                    Log.d(LOG_TAG,"myParticipantSnaphshot: " + myParticipantSnapshot.toString());
                    myParticipantSnapshot.getRef().removeValue();
                    showAppropriateToast(contextForToast,successMessage);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(LOG_TAG,"An error occurred: " + databaseError.toString());
            }
        });
    }

    public void renewViewAccordingToSelectedView() {
        if (MainActivity.allGamesIsCurrentView()) {
            sortGamesFromDatabaseByProximity();
        //if current view is "signed in games" only
        } else {
            filterSignedInGamesAndSortByTime();
        }
        notifyDataSetChanged();
    }

    public void sortGamesFromDatabaseByProximity() {
        gamesForCurrentView.clear();
        gamesForCurrentView.addAll((ArrayList<Game>) gamesInDatabase.clone());

        if (gamesForCurrentView.size() >= 2 && gamesForCurrentView.get(0).distanceToGame(getContext()) != null){
            Log.d(LOG_TAG,"gamesForCurrentView oberstes Spiel: " + gamesForCurrentView.get(0).distanceToGame(getContext()));
            Collections.sort(gamesForCurrentView, new Comparator<Game>() {
                @Override
                public int compare(Game game, Game t1) {
                    Float proximityGame = game.distanceToGame(getContext());
                    Float proximityT1 = t1.distanceToGame(getContext());
                    if (proximityGame == null || proximityT1 == null){
                        return 0;
                    }
                    int comparisonResult = Float.compare(proximityGame,proximityT1);
                    return comparisonResult;
                }
            });
        } else if (gamesForCurrentView.size() < 2) {
            Log.d(LOG_TAG,"gamesForCurrentView ist kleiner 2");
        }
    }

    public void filterSignedInGamesAndSortByTime() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        gamesWithCurrentUser.clear();

        for (Game gameInDatabase: gamesInDatabase){
            if (gameInDatabase.getParticipants().contains(currentUser.getUid())){
                gamesWithCurrentUser.add(gameInDatabase);
            }
        }
        //sort games by time if there is more than one game
        if (gamesWithCurrentUser.size() >= 2){
            Collections.sort(gamesWithCurrentUser, new Comparator<Game>() {
                @Override
                public int compare(Game game, Game t1) {
                    String gameDate = game.getGameDate();
                    String t1Date = t1.getGameDate();
                    int comparisonResult = gameDate.compareTo(t1Date);
                    if (comparisonResult == 0){
                        String gameTime = game.getGameTime();
                        String t1Time = t1.getGameTime();
                        comparisonResult = gameTime.compareTo(t1Time);
                    }
                    return comparisonResult;
                }
            });
        } else {
            Log.d(LOG_TAG,"gamesForCurrentView.size() is < 2.");
        }

        gamesForCurrentView.clear();
        gamesForCurrentView.addAll((ArrayList<Game>) gamesWithCurrentUser.clone());

    }
    private void showAppropriateToast(Context context,String successMessage) {
        Toast.makeText(context,successMessage,Toast.LENGTH_SHORT).show();
    }

    //cf. method dateTimeIsHistory in NewGameActivity class
    public boolean gameIsAlreadyOver(Game game){
        String currentDate = SFHelper.getCurrentDateAsString();
        if (game.getGameDate().compareTo(currentDate) < 0){
            return true;
        } else {
            return false;
        }
    }
    public ArrayList<Game> getGamesInDatabase() {
        return gamesInDatabase;
    }

    //guarantees that distance is no longer shown as soon as user has turned of gps
    public static void setShowNoGps(boolean showNoGps) {
        ItemAdapter.showNoGps = showNoGps;
    }
}
