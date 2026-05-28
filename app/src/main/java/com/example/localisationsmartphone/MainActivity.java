package com.example.localisationsmartphone;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends Activity {

    private static final int LOCATION_PERMISSION_CODE = 44;

    /*
     * Pour un émulateur Android, 10.0.2.2 pointe vers l'ordinateur hôte.
     * Si tu lances le backend PHP avec:
     * php -S 0.0.0.0:8000 -t server/localisation
     * alors cette URL fonctionne depuis l'émulateur.
     */
    private static final String INSERT_URL =
            "http://10.0.2.2:8000/createPosition.php";

    private TextView tvInfo;
    private TextView tvStatus;
    private PositionSqlite sqlite;
    private RequestQueue requestQueue;
    private LocationManager locationManager;

    private final LocationListener gpsListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Position position = makePositionFromLocation(location);
            saveShowAndSend(position, true);
        }

        @Override
        public void onProviderEnabled(String provider) {
            toast(getString(R.string.provider_enabled, provider));
        }

        @Override
        public void onProviderDisabled(String provider) {
            toast(getString(R.string.provider_disabled, provider));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvInfo = findViewById(R.id.tvInfo);
        tvStatus = findViewById(R.id.tvStatus);

        Button btnStartGps = findViewById(R.id.btnStartGps);
        Button btnSaveFake = findViewById(R.id.btnSaveFake);
        Button btnSendLast = findViewById(R.id.btnSendLast);

        sqlite = new PositionSqlite(this);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        refreshStatus("SQLite local prêt.");

        btnStartGps.setOnClickListener(view -> startGps());
        btnSaveFake.setOnClickListener(view -> saveShowAndSend(fakeCasablancaPosition(), false));
        btnSendLast.setOnClickListener(view -> sendLastPosition());
    }

    private void startGps() {
        if (!hasLocationPermission()) {
            requestPermissions(
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.READ_PHONE_STATE
                    },
                    LOCATION_PERMISSION_CODE
            );
            return;
        }

        try {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    60000,
                    150,
                    gpsListener
            );

            Location lastKnown = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnown != null) {
                saveShowAndSend(makePositionFromLocation(lastKnown), true);
            } else {
                refreshStatus("GPS démarré. En attente d'une position...");
            }
        } catch (SecurityException exception) {
            refreshStatus("Permission localisation refusée.");
        }
    }

    private boolean hasLocationPermission() {
        return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private Position makePositionFromLocation(Location location) {
        return new Position(
                location.getLatitude(),
                location.getLongitude(),
                location.hasAltitude() ? location.getAltitude() : 0.0,
                location.hasAccuracy() ? location.getAccuracy() : 0.0f,
                now(),
                phoneIdentifier()
        );
    }

    private Position fakeCasablancaPosition() {
        return new Position(
                33.5731,
                -7.5898,
                0.0,
                8.0f,
                now(),
                phoneIdentifier()
        );
    }

    private void saveShowAndSend(Position position, boolean sendToServer) {
        long id = sqlite.create(position);
        position.id = id;

        showPosition(position);
        refreshStatus("Position enregistrée localement. Total SQLite : " + sqlite.count());

        if (sendToServer) {
            sendPosition(position);
        }
    }

    private void showPosition(Position position) {
        String message =
                "ID local : " + position.id +
                        "\nLatitude : " + position.latitude +
                        "\nLongitude : " + position.longitude +
                        "\nAltitude : " + position.altitude +
                        "\nPrécision : " + position.accuracy + " m" +
                        "\nDate : " + position.datePosition +
                        "\nIdentifiant : " + position.imei;

        tvInfo.setText(message);
    }

    private void sendLastPosition() {
        Position last = sqlite.last();
        if (last == null) {
            refreshStatus("Aucune position locale à envoyer.");
            return;
        }

        sendPosition(last);
    }

    private void sendPosition(Position position) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                INSERT_URL,
                response -> refreshStatus("Serveur : " + response.trim()),
                error -> refreshStatus("Envoi impossible. Position gardée en SQLite local.")
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("latitude", String.valueOf(position.latitude));
                params.put("longitude", String.valueOf(position.longitude));
                params.put("date_position", position.datePosition);
                params.put("imei", position.imei);
                return params;
            }
        };

        requestQueue.add(request);
    }

    private String now() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date());
    }

    private String phoneIdentifier() {
        /*
         * Android moderne bloque l'accès réel à l'IMEI pour les apps normales.
         * Pour le TP, on garde le nom de champ "imei", mais on stocke ANDROID_ID.
         */
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private void refreshStatus(String message) {
        tvStatus.setText(message);
        toast(message);
    }

    private void toast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
