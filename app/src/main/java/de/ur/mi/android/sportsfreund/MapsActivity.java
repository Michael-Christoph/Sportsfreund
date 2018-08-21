package de.ur.mi.android.sportsfreund;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.google.android.gms.maps.GoogleMap.*;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private final LatLng REGENSBURG = new LatLng(49, 12);
    private LatLng newLocation;

    final int REQUEST_CODE = 1;
    private String titelAnleitung = "Anleitung";
    private String textAnleitung = "Ziehen Sie den Marker zu der Stelle auf der Karte, die Sie als Treffpunkt festlegen wollen";
    private String positiveButton = "OK";
    private String textBestätigung = "Wollen sie diesen Ort als Treffpunkt übernehmen?";
    private String markertitle = "Ort des Spiels";
    public static final String KEY_LOCATION_LAT= "lKeyLat";
    public static final String KEY_LOCATION_LONG = "lKeyLong";

    public static final String KEY_LOCATION_LAT_D= "lKeyLatDetails";
    public static final String KEY_LOCATION_LONG_D = "lKeyLongDetails";
    public static final String KEY_GAME_NAME = "GameName";

    public static final String KEY_LOCATION = "location";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can addGameToDatabase markers or lines, addGameToDatabase listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Intent intent = getIntent();

        if (intent.getExtras() == null) {
            Log.d("wo samma", "selectLocation ");
            selectLocation();
        } else {
            showLocDetail();
        }
    }

        private void selectLocation () {

            showInstructionDialog();


            mMap.addMarker( new MarkerOptions().position( REGENSBURG ).title( markertitle ).draggable( true ) );
            CameraUpdate center=
                    CameraUpdateFactory.newLatLng(REGENSBURG);

            CameraUpdate zoom=CameraUpdateFactory.zoomTo(10);

            mMap.moveCamera(center);
            mMap.animateCamera(zoom);

            mMap.setOnMarkerDragListener( new OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {

                    newLocation = marker.getPosition();
                    confirmLocation();


                }
            } );
        }


        private void goBackToNewGame () {

            Intent result = new Intent( this, NewGame.class );


            double locLong = newLocation.longitude;
            double locLat = newLocation.latitude;

            result.putExtra( KEY_LOCATION_LAT, locLat );
            result.putExtra( KEY_LOCATION_LONG, locLong );


            /*
            // dummy:
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location dummyLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            result.putExtra(KEY_LOCATION,new Location)
            */
            setResult( Activity.RESULT_OK, result );
            finish();

        }

        private void showInstructionDialog () {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder( this );
            dialogBuilder.setTitle( R.string.title_maps_instruction );
            dialogBuilder.setMessage( R.string.text_maps_instruction );
            dialogBuilder.setPositiveButton( R.string.ok, new Dialog.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            } );
            AlertDialog dialog = dialogBuilder.create();
            dialog.show();
        }

        private void confirmLocation () {

            AlertDialog.Builder dialogBuilder2 = new AlertDialog.Builder( this );
            dialogBuilder2.setTitle( R.string.title_maps_instruction );
            dialogBuilder2.setMessage( R.string.text_maps_verify );
            dialogBuilder2.setPositiveButton( R.string.ok, new Dialog.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    goBackToNewGame();
                }
            } );
            AlertDialog dialog = dialogBuilder2.create();
            dialog.show();
        }

    private void showLocDetail() {
        double locLong = getIntent().getExtras().getDouble(KEY_LOCATION_LONG_D);
        double locLat = getIntent().getExtras().getDouble( KEY_LOCATION_LAT_D );
        String gameTitle = "Hier wird " + getIntent().getExtras().getString( KEY_GAME_NAME ) +" gespielt";

        LatLng gameLoc = new LatLng(locLat, locLong);

        mMap.addMarker( new MarkerOptions().position( gameLoc ).title( gameTitle ));

        CameraUpdate center=
                CameraUpdateFactory.newLatLng(gameLoc);
        CameraUpdate zoom =
                CameraUpdateFactory.zoomTo(10);

        mMap.moveCamera(center);
        mMap.animateCamera(zoom);
    }

}

