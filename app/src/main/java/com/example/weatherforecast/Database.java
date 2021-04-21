package com.example.weatherforecast;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteOpenHelper {


    public static final String CITY_TABLE = "CITY_TABLE";
    public static final String COLUMN_CITY_NAME = "CITY_NAME";
    public static final String COLUMN_ID = "ID";

    public Database(@Nullable Context context) {
        super(context, "city.db", null, 1);
    }

    @Override

    public void onCreate(SQLiteDatabase db) {

        String createTable = "CREATE TABLE " + CITY_TABLE + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_CITY_NAME + ")";

        db.execSQL(createTable);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE " + CITY_TABLE );
        onCreate(db);
    }

    public boolean addOne(City city)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_CITY_NAME, city.name);

        Log.d("sql app", "adding data " + city.toString() + " to " + CITY_TABLE);

        long insert = db.insert(CITY_TABLE, null , cv);
        if(insert == -1)
        {
            return false;
        }
        else{
            return true;
        }

    }

    public List<String> listAll(){

        List<String> returnList = new ArrayList<>();

        String queryString = "SELECT * FROM " + CITY_TABLE;
        SQLiteDatabase db = this.getReadableDatabase() ;
        Cursor cursor = db.rawQuery(queryString, null);

        if(cursor.moveToFirst())
        {
            do{
                int cityID = cursor.getInt(0);
                String cityName = cursor.getString(1);
                City newCity = new City(cityID, cityName);
                returnList.add(newCity.name);

            }while(cursor.moveToNext());
        }
        else
        {
            //failure, nothing is added to the list
        }

        cursor.close();
        db.close();

        return returnList;
    }

    public boolean delete(String city)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        String queryString = "DELETE FROM CITY_TABLE WHERE CITY_NAME= '" + city+"'" ;
        Cursor cursor = db.rawQuery(queryString, null);

        if(cursor.moveToFirst())
        {
            cursor.close();
            return true;

        }
        else
        {
            cursor.close();
            return false;
        }

    }

    public int count(String city)
    {
        int count;
        SQLiteDatabase db = this.getReadableDatabase();
        String queryString = "SELECT * FROM CITY_TABLE WHERE CITY_NAME= '" + city+"'" ;
        Cursor cursor = db.rawQuery(queryString, null);
        count = cursor.getCount();

        return count;
    }

}
