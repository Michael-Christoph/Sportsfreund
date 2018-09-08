package de.ur.mi.android.sportsfreund;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

//cf. https://developers.google.com/maps/documentation/android-sdk/start
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String LOG_TAG = "MapsActivity";
    private static final int ZOOM_LEVEL = 10;

    private GoogleMap mMap;
    private final LatLng REGENSBURG = new LatLng(49, 12);
    private LatLng newLocation;

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
            Log.d(LOG_TAG, "selectLocation ");
            selectLocation();
        } else {
            showLocDetail();
        }
    }

    private void selectLocation () {

        showInstructionDialog();

        String markerTitle = "Ort des Spiels";
        mMap.addMarker( new MarkerOptions().position( REGENSBURG ).title( markerTitle ).draggable( true ) );
        CameraUpdate center=
                CameraUpdateFactory.newLatLng(REGENSBURG);

        CameraUpdate zoom=CameraUpdateFactory.zoomTo(ZOOM_LEVEL);

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
        });
    }


    private void goBackToNewGame () {
        Intent result = new Intent( this, NewGameActivity.class );

        double locLong = newLocation.longitude;
        double locLat = newLocation.latitude;

        result.putExtra(GlobalVariables.KEY_LOCATION_LAT, locLat );
        result.putExtra(GlobalVariables.KEY_LOCATION_LONG, locLong );

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
        double locLong = getIntent().getExtras().getDouble(GlobalVariables.KEY_LOCATION_LONG_D);
        double locLat = getIntent().getExtras().getDouble(GlobalVariables.KEY_LOCATION_LAT_D);
        String gameTitle = "Hier wird " + getIntent().getExtras().getString(GlobalVariables.KEY_GAME_NAME) +" gespielt";

        LatLng gameLoc = new LatLng(locLat,locLong);

        mMap.addMarker( new MarkerOptions().position( gameLoc ).title( gameTitle ));

        CameraUpdate center = CameraUpdateFactory.newLatLng(gameLoc);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(10);

        mMap.moveCamera(center);
        mMap.animateCamera(zoom);
    }
}

