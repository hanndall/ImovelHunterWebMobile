package br.com.imovelhunter.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by BOTTOMUP 05 on 06/05/2015.
 */
public class MyDatabaseHelper  extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "imovelhunterdb";

    private static final int DATABASE_VERSION = 1;



    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(MensagemDAO.CREATE_TABLE);
    }

    // Method is called during an upgrade of the database,
    @Override
    public void onUpgrade(SQLiteDatabase database,int oldVersion,int newVersion){
        Log.w(MyDatabaseHelper.class.getName(), "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        database.execSQL(MensagemDAO.DROP_TABLE);
        onCreate(database);
    }
}