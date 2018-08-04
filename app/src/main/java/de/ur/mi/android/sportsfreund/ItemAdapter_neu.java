package de.ur.mi.android.sportsfreund;

import android.content.Context;
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

public class ItemAdapter_neu extends ArrayAdapter<Game> {

    private ArrayList<Game> gamesInDatabase;
    private ArrayList<Game> gamesForCurrentView;
    private DatabaseReference firebaseGameRef;

    private String toastGameAdded = "Spiel wurde erstellt!";
    private String toastAddGameFailed = "Sorry, dein Spiel konnte nicht erstellt werden. Bitte" +
            " versuche es später noch einmal!";

    public ItemAdapter_neu(Context context,ArrayList<Game> gamesForCurrentView) {
        //super(context, R.layout.list_item,items);
        //super(context,0,items);

        super(context,0,gamesForCurrentView);
        this.gamesForCurrentView = gamesForCurrentView;
        gamesInDatabase = new ArrayList<>();
        firebaseGameRef = FirebaseDatabase.getInstance().getReference().child("games");
        //MP: adds offline persistence even if app is destroyed.
        firebaseGameRef.keepSynced(true);
        firebaseGameRef.addChildEventListener(new GamesChildEventListener());

    }
    class GamesChildEventListener implements ChildEventListener{

        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Game game = dataSnapshot.getValue(Game.class);
            game.setKey(dataSnapshot.getKey());
            gamesForCurrentView.add(0,game);
            sortGamesAccordingToActionBar();
            notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            String key = dataSnapshot.getKey();
            Game updatedGame = dataSnapshot.getValue(Game.class);
            for (Game game: gamesForCurrentView){
                if (game.getKey().equals(key)){
                    game.setValues(updatedGame);
                    notifyDataSetChanged();
                }
            }
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            String key = dataSnapshot.getKey();
            for (Game game: gamesForCurrentView){
                if (game.getKey().equals(key)){
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
            Log.e("bla","Database error: " + databaseError);
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.list_item,parent,false);

        String title = getItem(position).getGameName();
        String body = "Ort: " + getItem(position).getGameLocation() + "; Zeit: " + getItem(position).getGameTime();

        TextView titleText = (TextView) view.findViewById(R.id.TitleText);
        TextView bodyText = (TextView)  view.findViewById(R.id.BodyText);

        titleText.setText(title);
        bodyText.setText(body);




        return view;
    }
    public void add(Game game, final Context context){
        firebaseGameRef.push().setValue(game,new DatabaseReference.CompletionListener(){
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference){
                if (databaseError != null){
                    Toast.makeText(context,toastAddGameFailed,Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context,toastGameAdded,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void remove(Game game){
        firebaseGameRef.child(game.getKey()).removeValue();
    }
    public void addParticipantToGame(Game game, String participantId){
        game.addParticipant(participantId);
        firebaseGameRef.child(game.getKey()).setValue(game);
    }
    private void sortGamesAccordingToActionBar() {
        if (MainActivity.allGamesIsCurrentView()){
            sortGamesFromDatabaseByProximity();
            //if current view is "signed in games" only
        } else {
            filterSignedInGamesAndSortByTime();
        }
    }
    private void sortGamesFromDatabaseByProximity()  {
        // Sortieren der Spiele aus Firebase
        gamesInDatabase = (ArrayList<Game>) gamesForCurrentView.clone();
        Collections.sort(gamesForCurrentView, new Comparator<Game>() {
            @Override
            public int compare(Game game, Game t1) {
                double proximityGame = game.getProximity(NavigationHelperDummy.getLastKnownLocation());
                double proximityt1 = t1.getProximity(NavigationHelperDummy.getLastKnownLocation());
                int comparisonResult = Double.compare(proximityGame,proximityt1);
                return comparisonResult;
            }
        });
        Log.d("bla","gamesForCurrentView nach sort: " + gamesForCurrentView.toString());
        //notifyDataSetChanged();
    }
    private void filterSignedInGamesAndSortByTime()  {

    }
}
