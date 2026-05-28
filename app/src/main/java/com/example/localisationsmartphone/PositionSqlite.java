package com.example.localisationsmartphone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*
 * Ici on remplace phpMyAdmin/MySQL par SQLite local.
 * L'application peut donc fonctionner même si Apache, MySQL ou XAMPP ne sont pas lancés.
 */
public class PositionSqlite extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "localisation.db";
    private static final int DATABASE_VERSION = 1;

    public PositionSqlite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE position (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "latitude REAL NOT NULL, " +
                        "longitude REAL NOT NULL, " +
                        "altitude REAL NOT NULL, " +
                        "accuracy REAL NOT NULL, " +
                        "date_position TEXT NOT NULL, " +
                        "imei TEXT NOT NULL" +
                        ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS position");
        onCreate(db);
    }

    public long create(Position position) {
        ContentValues row = new ContentValues();
        row.put("latitude", position.latitude);
        row.put("longitude", position.longitude);
        row.put("altitude", position.altitude);
        row.put("accuracy", position.accuracy);
        row.put("date_position", position.datePosition);
        row.put("imei", position.imei);

        return getWritableDatabase().insert("position", null, row);
    }

    public int count() {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT COUNT(*) FROM position", null);
        try {
            cursor.moveToFirst();
            return cursor.getInt(0);
        } finally {
            cursor.close();
        }
    }

    public Position last() {
        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT id, latitude, longitude, altitude, accuracy, date_position, imei " +
                        "FROM position ORDER BY id DESC LIMIT 1",
                null
        );

        try {
            if (!cursor.moveToFirst()) {
                return null;
            }

            Position position = new Position(
                    cursor.getDouble(1),
                    cursor.getDouble(2),
                    cursor.getDouble(3),
                    cursor.getFloat(4),
                    cursor.getString(5),
                    cursor.getString(6)
            );
            position.id = cursor.getLong(0);
            return position;
        } finally {
            cursor.close();
        }
    }
}
