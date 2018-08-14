package de.ur.mi.android.sportsfreund;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

//cf. http://www.rosebotics.org/android-firebase-v3/unit?unit=1&lesson=13 lecture series
// introducing firebase realtime database
public class ItemAdapter_neu extends ArrayAdapter<Game> {

    private ArrayList<Game> gamesInDatabase;
    private ArrayList<Game> gamesForCurrentView;
    private DatabaseReference firebaseGameRef;

    /*
    private String toastGameAdded = "Spiel wurde erstellt!";
    private String toastAddGameFailed = "Sorry, dein Spiel konnte nicht erstellt werden. Bitte" +
            " versuche es später noch einmal!";
    */
    private String toastGameDeleted = "Spiel wurde gelöscht: ";

    public ItemAdapter_neu(Context context, ArrayList<Game> gamesForCurrentView) {
        //super(context, R.layout.list_item,items);
        //super(context,0,items);

        super(context, 0, gamesForCurrentView);
        this.gamesForCurrentView = gamesForCurrentView;
        gamesInDatabase = new ArrayList<>();
        firebaseGameRef = FirebaseDatabase.getInstance().getReference().child("games");
        //MP: adds offline persistence even if app is destroyed.
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
        String body = "Zeit: " + gameToView.getDate() + ", " + gameToView.getGameTime() + " Uhr";
        String body2;
        if (gameToView.distanceToGame(getContext()) == null){
            body2 = "Entfernung kann ohne GPS nicht angezeigt werden!";
        } else {
            if (gameToView.distanceToGame(getContext()) <= 1000) {
                body2 = Math.round(gameToView.distanceToGame(getContext())) + " m";
            }
            else {
                body2 = Math.round(gameToView.distanceToGame(getContext())/1000) + " km";
            }

        }
        TextView titleText = (TextView) view.findViewById(R.id.TitleText);
        TextView bodyText = (TextView) view.findViewById(R.id.BodyText);
        TextView body2Text = (TextView) view.findViewById(R.id.body_2_text);

        titleText.setText(title);
        bodyText.setText(body);
        body2Text.setText(body2);


        return view;
    }

    class GamesChildEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Game game = dataSnapshot.getValue(Game.class);
            game.setKey(dataSnapshot.getKey());
            gamesForCurrentView.add(0, game);
            sortGamesAccordingToActionBar();
            notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            String key = dataSnapshot.getKey();
            Game updatedGame = dataSnapshot.getValue(Game.class);
            for (Game game : gamesForCurrentView) {
                if (game.getKey().equals(key)) {
                    game.setValues(updatedGame);
                    notifyDataSetChanged();
                }
            }
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            String key = dataSnapshot.getKey();
            for (Game game : gamesForCurrentView) {
                if (game.getKey().equals(key)) {
                    gamesForCurrentView.remove(game);
                    notifyDataSetChanged();
                    return;
                }
            }
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.e("bla", "Database error: " + databaseError);
        }
    }

    public void add(Game game, final Context context) {
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
        firebaseGameRef.child(game.getKey()).removeValue();
        Toast.makeText(context, toastGameDeleted + game.getGameName(),Toast.LENGTH_SHORT).show();
    }

    public void addParticipantToGame(Game game, String participantId, Context context) {
        game.addParticipant(participantId, context);
        Log.d("ItemAdapter","game.getKey() ist wohl null? " + game.getKey().toString() );
        firebaseGameRef.child(game.getKey()).setValue(game);
    }

    public void removeParticipantFromGame(Game game, String participantId, Context context) {
        game.removeParticipant(participantId, context);
        if (game.getParticipants().size() > 0){
            Log.d("ItemAdapter","still participants left");
            firebaseGameRef.child(game.getKey()).setValue(game);
        } else {
            Log.d("ItemAdapter","last participant has unregistered");
            remove(game,context);
        }

    }

    private void sortGamesAccordingToActionBar() {
        if (MainActivity.allGamesIsCurrentView()) {
            sortGamesFromDatabaseByProximity();
            //if current view is "signed in games" only
        } else {
            filterSignedInGamesAndSortByTime();
        }
    }

    private void sortGamesFromDatabaseByProximity() {

        if (gamesForCurrentView.size() >= 2 &&
                gamesForCurrentView.get(0).distanceToGame(getContext()) != null) {
            // Sortieren der Spiele aus Firebase
            gamesInDatabase = (ArrayList<Game>) gamesForCurrentView.clone();
            Collections.sort(gamesForCurrentView, new Comparator<Game>() {
                @Override
                public int compare(Game game, Game t1) {
                    float proximityGame = game.distanceToGame(getContext());
                    float proximityt1 = t1.distanceToGame(getContext());
                    int comparisonResult = Float.compare(proximityGame, proximityt1);
                    return comparisonResult;
                }
            });
            Log.d("bla", "gamesForCurrentView nach sort: " + gamesForCurrentView.toString());
            //notifyDataSetChanged();
        } else {
            Log.d("ItemAdapter","gamesForCurrentView ist kleiner 2 oder distanceToGame liefert null.");
        }

    }

    private void filterSignedInGamesAndSortByTime() {

    }
}
