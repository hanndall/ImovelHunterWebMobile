package br.com.imovelhunter.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.SimpleDateFormat;

import br.com.imovelhunter.dominio.Mensagem;

/**
 * Created by BOTTOMUP 05 on 06/05/2015.
 */
public class MensagemDAO {

    public final static String CREATE_TABLE = "create table Mensagem( _idMensagem integer primary key AUTOINCREMENT," +
            "mensagem text not null," +
            "dataDaMensagem text not null," +
            "idUsuarioRemetente integer not null," +
            "nomeUsuarioRemetente text not null," +
            "idUsuarioDestinatario integer not null," +
            "nomeUsuarioDestinatario text not null);";

    public final static String DROP_TABLE = "DROP TABLE IF EXISTS Mensagem";


    public final static String TABLE_NAME = "Mensagem";


    ///////////////////////////////////////////////////////////////////////////////
    private final String ID = "_idMensagem";
    private final String MENSAGEM = "mensagem";
    private final String DATADAMENSAGEM = "dataDaMensagem";
    private final String IDUSUARIOREMETENTE = "idUsuarioRemetente";
    private final String NOMEUSUARIOREMETENTE = "nomeUsuarioRemetente";
    private final String IDUSUARIODESTINATARIO = "idUsuarioDestinatario";
    private final String NOMEUSUARIODESTINATARIO = "nomeUsuarioDestinatario";
    ////////////////////////////////////////////////////////////////////////////////

    private MyDatabaseHelper dbHelper;

    private SQLiteDatabase database;

    private Context context;

    private ContentValues values;

    private final SimpleDateFormat formatData = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public MensagemDAO(Context context){
        this.context = context;

        this.dbHelper = new MyDatabaseHelper(this.context);

        this.database = this.dbHelper.getWritableDatabase();

        this.values = new ContentValues();
    }


    public boolean inserirMensagem(Mensagem mensagem){
        this.values.clear();

        try {
            this.values.put(MENSAGEM, mensagem.getMensagem());
            try {
                this.values.put(DATADAMENSAGEM, this.formatData.format(mensagem.getDataEnvio()));
            } catch (Exception ex) {
                Log.e("MENSAGEMDAOERRO", ex.getMessage());
            }
            this.values.put(IDUSUARIOREMETENTE, mensagem.getUsuarioRemetente().getIdUsuario());
            this.values.put(NOMEUSUARIOREMETENTE, mensagem.getUsuarioRemetente().getNomeUsuario());
            this.values.put(IDUSUARIODESTINATARIO, mensagem.getUsuariosDestino().get(0).getIdUsuario());
            this.values.put(NOMEUSUARIODESTINATARIO, mensagem.getUsuariosDestino().get(0).getNomeUsuario());

            this.database.insertOrThrow(TABLE_NAME,null,this.values);

        }
        catch(Exception ex){
            Log.e("MENSAGEMDAOERRO", ex.getMessage());
            return false;
        }

        return true;
    }




}
