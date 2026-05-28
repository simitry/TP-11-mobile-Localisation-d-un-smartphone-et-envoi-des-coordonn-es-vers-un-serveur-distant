package com.example.localisationsmartphone;

/*
 * Petit objet métier côté Android.
 * Il reprend les colonnes demandées dans le TP : latitude, longitude, date_position et imei.
 */
public class Position {

    public long id;
    public double latitude;
    public double longitude;
    public double altitude;
    public float accuracy;
    public String datePosition;
    public String imei;

    public Position(double latitude,
                    double longitude,
                    double altitude,
                    float accuracy,
                    String datePosition,
                    String imei) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.accuracy = accuracy;
        this.datePosition = datePosition;
        this.imei = imei;
    }
}
