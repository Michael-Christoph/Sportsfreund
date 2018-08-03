package de.ur.mi.android.sportsfreund;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.firebase.database.*;

public class AddGameAsyncTask extends AsyncTask<Game,Integer,Boolean> {

    private DatabaseReference mDatabaseGames;
    private String positiveButton = "Ja";
    private String negativeButton = "Nein";
    private String titelFehlermeldung = "WICHTIGER HINWEIS";
    private String textFehlermeldung = "Das Spiel, das du gerade hinzufügen wolltest, konnte leider" +
            " nicht veröffentlicht werden. Willst du zur Eingabemaske zurückkehren?";
    private String toastGameAdded = "Spiel wurde erstellt!";

    @Override
    protected Boolean doInBackground(Game... games) {

        //mDatabaseRoot = FirebaseDatabase.getInstance().getReference();
        mDatabaseGames = FirebaseDatabase.getInstance().getReference("games");
        String gameId = mDatabaseGames.push().getKey();
        mDatabaseGames.child(gameId).setValue(games[0],new DatabaseReference.CompletionListener(){
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference){
                if (databaseError != null){
                    //showCommitFailedDialog();
                } else {
                    //Toast.makeText(this,toastGameAdded,Toast.LENGTH_SHORT).show();
                }
            }
        });
        //return false;
        return true;
    }
    @Override
    protected void onPostExecute(Boolean result){
        super.onPostExecute(result);

    }
    /*
    private void showCommitFailedDialog() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder();
        dialogBuilder.setTitle(titelFehlermeldung);
        dialogBuilder.setMessage(textFehlermeldung);
        dialogBuilder.setPositiveButton(positiveButton, new Dialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Intent intent = new Intent(AddGameAsyncTask.this,NewGame.class);
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
