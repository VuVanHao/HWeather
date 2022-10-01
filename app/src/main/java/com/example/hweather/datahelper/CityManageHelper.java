package com.example.hweather.datahelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CityManageHelper extends SQLiteOpenHelper {
    private final static String DB_NAME = "CITY_MANAGE";
    private final static String TABLE_MANAGE_NAME = "CITY_SEARCH_NAME";
    private final static int VERSION = 1;
    private final static String ID_COL = "id";
    private final static String NAME_COL = "NAME";
    private SQLiteDatabase sqLiteDatabase;

    public CityManageHelper(@Nullable Context context) {
        super(context,DB_NAME,null,VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "CREATE TABLE " + TABLE_MANAGE_NAME +" (\n" +
                " \'"+ ID_COL + "\' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                " \'"+ NAME_COL + "\' TEXT NOT NULL )";
        sqLiteDatabase.execSQL(sql);
        Log.d("Query : ",sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        if (i1 > i)
        {
            sqLiteDatabase.delete(TABLE_MANAGE_NAME,null,null);
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
        sqLiteDatabase.insert(TABLE_MANAGE_NAME,null,contentValues);

        sqLiteDatabase.close();

        //C2
        // String sql = insert into table_name ....
        //execSQL.()
        //close()
    }

    public void deleteSearchCityName(String cityName)
    {
        sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(TABLE_MANAGE_NAME,"NAME = ?", new String[]{cityName});

        sqLiteDatabase.close();
    }

    public void updateCityName(String cityName,String newName)
    {
        sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME_COL,newName);
        sqLiteDatabase.update(TABLE_MANAGE_NAME,contentValues,"NAME = ?", new String[]{cityName});

        sqLiteDatabase.close();
    }

    public ArrayList<String> getListCityName()
    {
        ArrayList<String> listCity = new ArrayList<>();
        sqLiteDatabase = getReadableDatabase();

        Cursor cursor = sqLiteDatabase
                .rawQuery("SELECT*FROM " + TABLE_MANAGE_NAME ,null);
        //cursor.moveToFirst();
        if(cursor != null && cursor.moveToFirst())
        {
            do
            {
                String rawCity = cursor.getString(1);
                listCity.add(rawCity);
            }while (cursor.moveToNext());
        }
        /*do
        {
            String rawCity = cursor.getString(1);
            listCity.add(rawCity);
        }while (cursor.moveToNext());*/

//        Log.d("Size list : ",listCity.toString());
        return listCity;
    }

    public int CountRecord()
    {
        int count = 0;
        sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT *FROM " + TABLE_MANAGE_NAME,null);
        return cursor.getCount();
    }

    public String getNameRowOne()
    {
        String name = "";
        sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT "  + NAME_COL+ " FROM " + TABLE_MANAGE_NAME + " WHERE id == 1",null);
        if(cursor != null && cursor.moveToFirst())
        {
            do
            {
                name = cursor.getString(0);
            }while (cursor.moveToNext());
        }
        return name;
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
