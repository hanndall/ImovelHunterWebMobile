package br.com.imovelhunter.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.imovelhunter.dominio.Notificacao;
import br.com.imovelhunter.dominio.Usuario;

/**
 * Created by Washington Luiz on 06/06/2015.
 */
public class NotificacaoDAO {

    public static final String CREATE_TABLE = "create table notificacao(_idNotificacao integer primary key AUTOINCREMENT," +
            "dataNotificacao DATETIME DEFAULT CURRENT_TIMESTAMP," +
            "idUsuario integer not null," +
            "idImovel integer not null," +
            "preco real not null," +
            "numero text not null," +
            "rua text not null," +
            "situacao text not null," +
            "tipo text not null," +
            "imagem text," +
            "latitude real not null," +
            "longitude real not null);";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS notificacao";

    public static final String TABLE_NAME = "notificacao";

    private final AtributosNotificacao atb =  new AtributosNotificacao();
    private class AtributosNotificacao{
        String TABLE = "notificacao";
        String ID = "_idNotificacao";
        String DATA = "dataNotificacao";
        String IDUSUARIO = "idUsuario";
        String IDIMOVEL = "idImovel";
        String PRECO = "preco";
        String NUMERO = "numero";
        String RUA = "rua";
        String SITUACAO = "situacao";
        String TIPO = "tipo";
        String IMAGEM = "imagem";
        String LATITUDE = "latitude";
        String LONGITUDE = "longitude";
    }
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////

    private MyDatabaseHelper dbHelper;

    private SQLiteDatabase database;

    private Context context;

    private ContentValues values;

    private final SimpleDateFormat formatData = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    private Calendar calendario;

    public NotificacaoDAO(Context context){
        this.context = context;

        this.calendario = Calendar.getInstance();

        this.dbHelper = new MyDatabaseHelper(this.context);

        this.database = this.dbHelper.getWritableDatabase();

        this.values = new ContentValues();
    }


    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////


    /**
     * Insere a notificação
     * @param notificacao
     * @return
     */
    public boolean inserir(Notificacao notificacao){
        this.values.clear();
        this.database = this.dbHelper.getWritableDatabase();

        try{

            this.values.put(atb.LONGITUDE,notificacao.getLongitude());
            this.values.put(atb.LATITUDE,notificacao.getLatitude());
            this.values.put(atb.IDIMOVEL,notificacao.getIdImovel());
            this.values.put(atb.IDUSUARIO,notificacao.getIdUsuario());
            this.values.put(atb.IMAGEM,notificacao.getImagem());
            this.values.put(atb.NUMERO,notificacao.getNumero());
            this.values.put(atb.PRECO,notificacao.getPreco());
            this.values.put(atb.RUA,notificacao.getRua());
            this.values.put(atb.SITUACAO,notificacao.getSituacao());
            this.values.put(atb.TIPO,notificacao.getTipo());
            try {
                this.values.put(atb.DATA, this.formatData.format(notificacao.getDataNotificacao()));
            }
            catch(Exception ex){
                Log.e("ERROBANCOCONVERTERDATA",ex.getMessage());
            }

            notificacao.setIdNotificacao(this.database.insertOrThrow(TABLE_NAME,null,this.values));


        }catch(Exception ex){
            Log.e("ERROBANCO",ex.getMessage());
            return false;
        }


        return true;
    }


    /**
     * Remove a notificação
     * @param not
     * @return
     */
    public boolean removerNotificacao(Notificacao not){
        try{
            this.database = this.dbHelper.getWritableDatabase();



            //String where = atb.IDUSUARIO+"= ?"+" AND "+atb.IDIMOVEL+"= ?";
            String where = atb.ID+"= ?";

            int result = this.database.delete(TABLE_NAME,where,new String[]{String.valueOf(not.getIdNotificacao())});

            if(result == -1){
                return false;
            }

        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }

        return true;
    }


    /**
     * Lista as notificações do usuário
     * @param usuario
     * @return
     */
    public List<Notificacao> listarNotificacoesPeloUsuario(Usuario usuario){
        List<Notificacao> notificacaos = new ArrayList<Notificacao>();

        long idUsuario = usuario.getIdUsuario();

        Cursor cursor = null;

        try{

            this.database = this.dbHelper.getReadableDatabase();

            String[] colunas = new String[]{atb.ID,atb.DATA,atb.IDIMOVEL,atb.IDUSUARIO,atb.IMAGEM,atb.LATITUDE,atb.LONGITUDE,atb.NUMERO,atb.PRECO,atb.RUA,atb.SITUACAO,atb.TIPO};

            String where = atb.IDUSUARIO+" = ?";

            String argumentos[] = new String[] { String.valueOf(usuario.getIdUsuario())};

            cursor = database.query(TABLE_NAME,colunas,where,argumentos,null,null,null);

            if (cursor != null && cursor.moveToFirst()) {

                Notificacao not = new Notificacao();



                this.parseNot(cursor,not);


                notificacaos.add(not);

            }

            while(cursor.moveToNext()){

                Notificacao not = new Notificacao();

                this.parseNot(cursor,not);

                notificacaos.add(not);

            }



        }catch(Exception ex){
            Log.e("ERROBANCO",ex.getMessage());
            return null;
        }



        return notificacaos;
    }


    private void parseNot(Cursor cursor,Notificacao not){



        try{
            Date data = formatData.parse(cursor.getString(cursor.getColumnIndex(atb.DATA)));
            not.setDataNotificacao(data);
        }catch (Exception ex){
            Log.e("ERROCONVERTERDATAMENSA",ex.getMessage());
        }

        not.setIdNotificacao(cursor.getInt(cursor.getColumnIndex(atb.ID)));
        not.setIdImovel(cursor.getInt(cursor.getColumnIndex(atb.IDIMOVEL)));
        not.setImagem(cursor.getString(cursor.getColumnIndex(atb.IMAGEM)));
        not.setIdUsuario(cursor.getInt(cursor.getColumnIndex(atb.IDUSUARIO)));
        not.setNumero(cursor.getString(cursor.getColumnIndex(atb.NUMERO)));
        not.setRua(cursor.getString(cursor.getColumnIndex(atb.RUA)));
        not.setTipo(cursor.getString(cursor.getColumnIndex(atb.TIPO)));
        not.setLatitude(cursor.getDouble(cursor.getColumnIndex(atb.LATITUDE)));
        not.setPreco(cursor.getDouble(cursor.getColumnIndex(atb.PRECO)));
        not.setSituacao(cursor.getString(cursor.getColumnIndex(atb.SITUACAO)));
        not.setLongitude(cursor.getDouble(cursor.getColumnIndex(atb.LONGITUDE)));


    }




}
