package br.com.imovelhunter.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import br.com.imovelhunter.dominio.Notificacao;

/**
 * Created by BOTTOMUP 05 on 06/05/2015.
 */
public class MyDatabaseHelper  extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "imovelhunterdb";

    private static final int DATABASE_VERSION = 5;



    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.beginTransaction();
        database.execSQL(MensagemDAO.CREATE_TABLE);
        database.execSQL(NotificacaoDAO.CREATE_TABLE);
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    // Method is called during an upgrade of the database,
    @Override
    public void onUpgrade(SQLiteDatabase database,int oldVersion,int newVersion){
        Log.w(MyDatabaseHelper.class.getName(), "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        database.beginTransaction();
        database.execSQL(MensagemDAO.DROP_TABLE);
        database.execSQL(NotificacaoDAO.DROP_TABLE);
        database.setTransactionSuccessful();
        database.endTransaction();
        onCreate(database);
    }
}