package br.com.imovelhunter.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.com.imovelhunter.dominio.Notificacao;

/**
 * Created by Rodrigo on 18/05/2015.
 */
public class NotificacaoDAO {

    public final static String CREATE_TABLE = "create table notificacao(_idNotificacao INTEGER PRIMARY KEY AUTOINCREMENT," +
            "rua TEXT," +
            "date DEFAULT CURRENT_TIMESTAMP," +
            "numero TEXT," +
            "caminhoImagem TEXT," +
            "preco REAL," +
            "tipo TEXT," +
            "situacao TEXT," +
            "latitude REAL," +
            "longitude REAL" +
            ");";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS notificacao";

    public final static String TABLE_NAME = "notificacao";

    private MyDatabaseHelper dbHelper;

    // private Context context;
//private ContentValues values;


    public NotificacaoDAO(Context context) {
        dbHelper = new MyDatabaseHelper(context);
    }

    public void inserir(Notificacao notificacao) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("rua", notificacao.getRua());
       values.put("numero", notificacao.getNumero());
        values.put("caminhoImagem", notificacao.getCaminhoImagem());
        values.put("preco", notificacao.getPreco());
        values.put("tipo", notificacao.getTipo());
        values.put("situacao", notificacao.getSituacao());
        values.put("latitude", notificacao.getLatitude());
       values.put("longitude", notificacao.getLongitude());



   Long x =  database.insert("notificacao", null, values);


            database.close();
    }

    public void excluir(int id) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.delete("notificacao", "_idNotificacao=?", new String[]{(Integer.toString(id))});
        //testar metodo
    }

    public List<Notificacao> listar() {
        List<Notificacao> notificacaos = new ArrayList<Notificacao>();
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM notificacao ORDER BY datetime (date)DESC", null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("_idNotificacao"));
            String rua = cursor.getString(cursor.getColumnIndex("rua"));
            String numero = cursor.getString(cursor.getColumnIndex("numero"));
            double preco = cursor.getDouble(cursor.getColumnIndex("preco"));
            String caminhoImagem = cursor.getString(cursor.getColumnIndex("caminhoImagem"));
            String tipo = cursor.getString(cursor.getColumnIndex("tipo"));
            String situacao = cursor.getString(cursor.getColumnIndex("situacao"));
            double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
            double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
            int idNotificacao = cursor.getInt(cursor.getColumnIndex("_idNotificacao"));

            Notificacao notificacao = new Notificacao();
            notificacao.setIdNotificacao(id);
            notificacao.setRua(rua);
            notificacao.setNumero(numero);
            notificacao.setPreco(preco);
            notificacao.setCaminhoImagem(caminhoImagem);
            notificacao.setTipo(tipo);
            notificacao.setSituacao(situacao);
            notificacao.setLatitude(latitude);
            notificacao.setLongitude(longitude);
            notificacao.setIdNotificacao(idNotificacao);


            notificacaos.add(notificacao);

        }
        cursor.close();
        database.close();
        return notificacaos;
    }
}
