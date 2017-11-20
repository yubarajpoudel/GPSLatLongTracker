package servicetutorial.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.app.NavUtils;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yubaraj on 11/18/17.
 */

public class DbHandler extends SQLiteOpenHelper {
    static int DATABASE_VERSION = 1;
    static String DATABASE_NAME = "offlinelog";

    public DbHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL = "CREATE TABLE " + DATABASE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT, lat REAL, lng REAL)";
        db.execSQL(SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.delete(DATABASE_NAME, null, null);
        onCreate(db);
    }

    public boolean saveData(Double lat, Double lng) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("lat", lat);
        cv.put("lng", lng);
        long result = db.insert(DATABASE_NAME, null, cv);
        db.close();
        return result > 0;
    }

    public List<LatLng> getData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DATABASE_NAME, null, null, null, null, null, null);
        List<LatLng> latLngList = new ArrayList<>();
        while (cursor.moveToNext()) {
            latLngList.add(new LatLng(cursor.getFloat(1), cursor.getFloat(2)));
        }
        cursor.close();
        db.close();
        return latLngList;
    }

    public boolean deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        float result = db.delete(DATABASE_NAME, null, null);
        db.close();
        return result > 0;
    }

    public boolean isDataExists() {
        SQLiteDatabase db = this.getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db, DATABASE_NAME) == 0;
    }
}
