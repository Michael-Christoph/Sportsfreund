package de.ur.mi.android.sportsfreund;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RealtimeDbAdapter {

    private static String positiveButton = "Ja";
    private static String negativeButton = "Nein";
    private static String titelFehlermeldung = "WICHTIGER HINWEIS";
    private static String textFehlermeldung = "Das Spiel, das du gerade hinzufügen wolltest, konnte leider" +
            " nicht veröffentlicht werden. Willst du zur Eingabemaske zurückkehren?";
    private static String toastGameAdded = "Spiel wurde erstellt!";

    DatabaseReference mDatabaseGamesRef = FirebaseDatabase.getInstance().getReference("games");

    public static boolean addGame(Game game){
        boolean addedSuccessfully = false;
        DatabaseReference mDatabaseGames = FirebaseDatabase.getInstance().getReference("games");
        String gameId = mDatabaseGames.push().getKey();
        mDatabaseGames.child(gameId).setValue(game,new DatabaseReference.CompletionListener(){
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference){
                if (databaseError != null){
                    //showCommitFailedDialog();
                } else {
                    //Toast.makeText(,toastGameAdded,Toast.LENGTH_SHORT).show();
                }
            }
        });

        return addedSuccessfully;
    }
    /*
    private static void showCommitFailedDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder();
        dialogBuilder.setTitle(titelFehlermeldung);
        dialogBuilder.setMessage(textFehlermeldung);
        dialogBuilder.setPositiveButton(positiveButton, new Dialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Intent intent = new Intent(AddGameAsyncTask.this,NewGameActivity.class)
            }
        });
        dialogBuilder.setNegativeButton(negativeButton,new Dialog.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){

            }
        });
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }
    */
}
