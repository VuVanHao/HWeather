package com.example.hweather.datahelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CitySearchHelper extends SQLiteOpenHelper {
    private final static String DB_NAME = "CITY";
    private final static String TABLE_SEARCH_NAME = "CITY_MANAGE_NAME";
    private final static int VERSION = 1;
    private final static String ID_COL = "id";
    private final static String NAME_COL = "NAME";
    private SQLiteDatabase sqLiteDatabase;

    public CitySearchHelper(@Nullable Context context) {
        super(context,DB_NAME,null,VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "CREATE TABLE " + TABLE_SEARCH_NAME +" (\n" +
                " \'"+ ID_COL + "\' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                " \'"+ NAME_COL + "\' TEXT NOT NULL )";
        sqLiteDatabase.execSQL(sql);
        Log.d("Query : ",sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        if (i1 > i)
        {
            sqLiteDatabase.delete(TABLE_SEARCH_NAME,null,null);
        }

    }

    public void closeSql()
    {
        sqLiteDatabase = getReadableDatabase();
        sqLiteDatabase.close();
    }


    public void addSearchCity(String cityName)
    {
        //C1
        sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME_COL,cityName);
        sqLiteDatabase.insert(TABLE_SEARCH_NAME,null,contentValues);

        sqLiteDatabase.close();

        //C2
        // String sql = insert into table_name ....
        //execSQL.()
        //close()
    }

    public void deleteSearchCityName(String cityName)
    {
        sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(TABLE_SEARCH_NAME,"NAME = ?", new String[]{cityName});

        sqLiteDatabase.close();
    }

    public void updateCityName(String cityName,String newName)
    {
        sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME_COL,newName);
        sqLiteDatabase.update(TABLE_SEARCH_NAME,contentValues,"NAME = ?", new String[]{cityName});

        sqLiteDatabase.close();
    }

    public ArrayList<String> getListCityName()
    {
        ArrayList<String> listCity = new ArrayList<>();
        sqLiteDatabase = getReadableDatabase();

        Cursor cursor = sqLiteDatabase
                .rawQuery("SELECT*FROM " + TABLE_SEARCH_NAME + " ORDER BY " +  ID_COL + " DESC",null);
        cursor.moveToFirst();
        if(cursor != null && cursor.moveToFirst())
        {
            do
            {
                String rawCity = cursor.getString(1);
                listCity.add(rawCity);
            }while (cursor.moveToNext());
        }

//        Log.d("Size list : ",listCity.toString());
        return listCity;
    }

    //Kết nối Stetho
    //B1 : implement thư viện
    //B2 : Tạo class MyApplication extends Application
    /*@Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(getApplicationContext());
    }*/
    //B3 : vào trong Manifest trong thẻ <Application > khai báo
    //android:name=".MyApplication"
}
