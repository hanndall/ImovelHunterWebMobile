package br.com.imovelhunter.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.imovelhunter.dominio.Mensagem;
import br.com.imovelhunter.dominio.Usuario;

/**
 * Created by BOTTOMUP 05 on 06/05/2015.
 */
public class MensagemDAO {

    public final static String CREATE_TABLE = "create table Mensagem( _idMensagem integer primary key AUTOINCREMENT," +
            "mensagem text not null," +
            "dataDaMensagem DATETIME DEFAULT CURRENT_TIMESTAMP," +
            "idUsuarioRemetente integer not null," +
            "nomeUsuarioRemetente text not null," +
            "idUsuarioDestinatario integer not null," +
            "lida integer not null," +
            "nomeUsuarioDestinatario text not null);";

    public final static String DROP_TABLE = "DROP TABLE IF EXISTS Mensagem";


    public final static String TABLE_NAME = "Mensagem";


    ///////////////////////////////////////////////////////////////////////////////
    private final AtributosMensagem atributos = new AtributosMensagem();
    private class AtributosMensagem {
        protected final String ID = "_idMensagem";
        protected final String MENSAGEM = "mensagem";
        protected final String DATADAMENSAGEM = "dataDaMensagem";
        protected final String IDUSUARIOREMETENTE = "idUsuarioRemetente";
        protected final String NOMEUSUARIOREMETENTE = "nomeUsuarioRemetente";
        protected final String IDUSUARIODESTINATARIO = "idUsuarioDestinatario";
        protected final String NOMEUSUARIODESTINATARIO = "nomeUsuarioDestinatario";
        protected final String LIDA = "lida";
    }
    ////////////////////////////////////////////////////////////////////////////////

    private MyDatabaseHelper dbHelper;

    private SQLiteDatabase database;

    private Context context;

    private ContentValues values;

    private final SimpleDateFormat formatData = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    private Calendar calendario;

    public MensagemDAO(Context context){
        this.context = context;

        this.calendario = Calendar.getInstance();

        this.dbHelper = new MyDatabaseHelper(this.context);

        this.database = this.dbHelper.getWritableDatabase();

        this.values = new ContentValues();
    }


    /**
     * Insere uma mensagem no banco de dados =)
     * @param mensagem
     * @return
     */
    public boolean inserirMensagem(Mensagem mensagem){
        this.values.clear();

        try {
            this.values.put(atributos.MENSAGEM, mensagem.getMensagem());
            try {
                this.values.put(atributos.DATADAMENSAGEM, this.formatData.format(mensagem.getDataEnvio()));
            } catch (Exception ex) {
                Log.e("MENSAGEMDAOERRO", ex.getMessage());
            }
            this.values.put(atributos.IDUSUARIOREMETENTE, mensagem.getUsuarioRemetente().getIdUsuario());
            this.values.put(atributos.NOMEUSUARIOREMETENTE, mensagem.getUsuarioRemetente().getNomeUsuario());
            this.values.put(atributos.IDUSUARIODESTINATARIO, mensagem.getUsuariosDestino().get(0).getIdUsuario());
            this.values.put(atributos.NOMEUSUARIODESTINATARIO, mensagem.getUsuariosDestino().get(0).getNomeUsuario());
            this.values.put(atributos.LIDA,mensagem.getLida() ? 1 : 0);
            mensagem.setIdMensagem(this.database.insertOrThrow(TABLE_NAME,null,this.values));



        }
        catch(Exception ex){
            Log.e("MENSAGEMDAOERRO", ex.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Método criado para listar as mensagens recebidas aos pouco, visando melhorar a performance da aplicação
     * @param index
     * @param quantidade
     * @return
     */
    public List<Mensagem> listarMensagensPorRange(int index, int quantidade){
        List<Mensagem> listaMensagem = new ArrayList<Mensagem>();
        Cursor cursor = null;

        try {


            this.database = this.dbHelper.getReadableDatabase();

            String[] colunas = new String[] { atributos.ID,atributos.DATADAMENSAGEM,atributos.IDUSUARIODESTINATARIO,atributos
                    .IDUSUARIOREMETENTE,atributos.LIDA,atributos.MENSAGEM,atributos.NOMEUSUARIODESTINATARIO,atributos.NOMEUSUARIOREMETENTE};



            cursor = database.query(TABLE_NAME,colunas,null,null,null,null,atributos.ID+" DESC"," limit "+index+" offset "+quantidade);



            if (cursor != null && cursor.moveToFirst()) {
                Mensagem mensagem = new Mensagem();
                Usuario usuarioRemetente = new Usuario();
                Usuario usuarioDestinatario = new Usuario();
                List<Usuario> listaUsuarioDestinatario = new ArrayList<Usuario>();

                mensagem.setIdMensagem(cursor.getInt(cursor.getColumnIndex(atributos.ID)));

                int idUsuarioRemetente = cursor.getInt(cursor.getColumnIndex(atributos.IDUSUARIOREMETENTE));
                int idUsuarioDestinatario = cursor.getInt(cursor.getColumnIndex(atributos.IDUSUARIODESTINATARIO));
                Boolean lida = cursor.getInt(cursor.getColumnIndex(atributos.LIDA)) == 1 ? true : false;
                String nomeUsuarioRemetente = cursor.getString(cursor.getColumnIndex(atributos.NOMEUSUARIOREMETENTE));
                String nomeUsuarioDestinatario = cursor.getString(cursor.getColumnIndex(atributos.NOMEUSUARIODESTINATARIO));
                String mensagemS = cursor.getString(cursor.getColumnIndex(atributos.MENSAGEM));

                mensagem.setLida(lida);
                mensagem.setMensagem(mensagemS);
                usuarioRemetente.setIdUsuario(idUsuarioRemetente);
                usuarioRemetente.setNome(nomeUsuarioRemetente);
                usuarioDestinatario.setIdUsuario(idUsuarioDestinatario);
                usuarioDestinatario.setNome(nomeUsuarioDestinatario);
                mensagem.setUsuarioRemetente(usuarioRemetente);
                listaUsuarioDestinatario.add(usuarioDestinatario);
                mensagem.setUsuariosDestino(listaUsuarioDestinatario);


                try{
                    Date dataMensagem = formatData.parse(cursor.getString(cursor.getColumnIndex(atributos.DATADAMENSAGEM)));
                    mensagem.setDataEnvio(dataMensagem);
                }catch (Exception ex){
                    Log.e("ERROCONVERTERDATAMENSA",ex.getMessage());
                }

                listaMensagem.add(mensagem);

                while(cursor.moveToNext()){
                    mensagem = new Mensagem();
                    usuarioRemetente = new Usuario();
                    usuarioDestinatario = new Usuario();
                    listaUsuarioDestinatario = new ArrayList<Usuario>();

                    mensagem.setIdMensagem(cursor.getInt(cursor.getColumnIndex(atributos.ID)));

                    idUsuarioRemetente = cursor.getInt(cursor.getColumnIndex(atributos.IDUSUARIOREMETENTE));
                    idUsuarioDestinatario = cursor.getInt(cursor.getColumnIndex(atributos.IDUSUARIODESTINATARIO));
                    lida = cursor.getInt(cursor.getColumnIndex(atributos.LIDA)) == 1 ? true : false;
                    nomeUsuarioRemetente = cursor.getString(cursor.getColumnIndex(atributos.NOMEUSUARIOREMETENTE));
                    nomeUsuarioDestinatario = cursor.getString(cursor.getColumnIndex(atributos.NOMEUSUARIODESTINATARIO));
                    mensagemS = cursor.getString(cursor.getColumnIndex(atributos.MENSAGEM));

                    mensagem.setLida(lida);
                    mensagem.setMensagem(mensagemS);
                    usuarioRemetente.setIdUsuario(idUsuarioRemetente);
                    usuarioRemetente.setNome(nomeUsuarioRemetente);
                    usuarioDestinatario.setIdUsuario(idUsuarioDestinatario);
                    usuarioDestinatario.setNome(nomeUsuarioDestinatario);
                    mensagem.setUsuarioRemetente(usuarioRemetente);
                    listaUsuarioDestinatario.add(usuarioDestinatario);
                    mensagem.setUsuariosDestino(listaUsuarioDestinatario);


                    try{
                        Date dataMensagem = formatData.parse(cursor.getString(cursor.getColumnIndex(atributos.DATADAMENSAGEM)));
                        mensagem.setDataEnvio(dataMensagem);
                    }catch (Exception ex){
                        Log.e("ERROCONVERTERDATAMENSA",ex.getMessage());
                    }
                    listaMensagem.add(mensagem);
                }

            }

            if (cursor != null) {
                cursor.close();
            }

        }catch(Exception ex){
            Log.e("ERROLISTAR",ex.getMessage());
            return null;
        }

        listaMensagem = (List<Mensagem>)inverterOrdemLista(listaMensagem);
        return listaMensagem;
    }

    private List<?> inverterOrdemLista(List<?> lista) {
        List<Object> listaInvertida = new ArrayList<Object>();

        if(lista != null) {
            for (Object o : lista) {
                listaInvertida.add(0, o);
            }
        }

        return listaInvertida;
    }

    public List<Mensagem> listarConversa(long idUsuarioA, long idUsuarioB){

        List<Mensagem> listaMensagem = new ArrayList<Mensagem>();
        Cursor cursor = null;

        try {




            this.database = this.dbHelper.getReadableDatabase();


            String where = "("+atributos.IDUSUARIOREMETENTE+" = ? AND "+atributos.IDUSUARIODESTINATARIO+" = ? ) OR ("+atributos.IDUSUARIODESTINATARIO+" = ? AND "+atributos.IDUSUARIOREMETENTE+" = ?)";

            String[] colunas = new String[] { atributos.ID,atributos.DATADAMENSAGEM,atributos.IDUSUARIODESTINATARIO,atributos
                    .IDUSUARIOREMETENTE,atributos.LIDA,atributos.MENSAGEM,atributos.NOMEUSUARIODESTINATARIO,atributos.NOMEUSUARIOREMETENTE};

            String argumentos[] = new String[] { String.valueOf(idUsuarioA),String.valueOf(idUsuarioB),String.valueOf(idUsuarioA),String.valueOf(idUsuarioB)};

            cursor = database.query(TABLE_NAME,colunas,where,argumentos,null,null,null);



            if (cursor != null && cursor.moveToFirst()) {
                Mensagem mensagem = new Mensagem();
                Usuario usuarioRemetente = new Usuario();
                Usuario usuarioDestinatario = new Usuario();
                List<Usuario> listaUsuarioDestinatario = new ArrayList<Usuario>();

                mensagem.setIdMensagem(cursor.getInt(cursor.getColumnIndex(atributos.ID)));

                int idUsuarioRemetente = cursor.getInt(cursor.getColumnIndex(atributos.IDUSUARIOREMETENTE));
                int idUsuarioDestinatario = cursor.getInt(cursor.getColumnIndex(atributos.IDUSUARIODESTINATARIO));
                Boolean lida = cursor.getInt(cursor.getColumnIndex(atributos.LIDA)) == 1 ? true : false;
                String nomeUsuarioRemetente = cursor.getString(cursor.getColumnIndex(atributos.NOMEUSUARIOREMETENTE));
                String nomeUsuarioDestinatario = cursor.getString(cursor.getColumnIndex(atributos.NOMEUSUARIODESTINATARIO));
                String mensagemS = cursor.getString(cursor.getColumnIndex(atributos.MENSAGEM));

                mensagem.setLida(lida);
                mensagem.setMensagem(mensagemS);
                usuarioRemetente.setIdUsuario(idUsuarioRemetente);
                usuarioRemetente.setNome(nomeUsuarioRemetente);
                usuarioDestinatario.setIdUsuario(idUsuarioDestinatario);
                usuarioDestinatario.setNome(nomeUsuarioDestinatario);
                mensagem.setUsuarioRemetente(usuarioRemetente);
                listaUsuarioDestinatario.add(usuarioDestinatario);
                mensagem.setUsuariosDestino(listaUsuarioDestinatario);


                try{
                    Date dataMensagem = formatData.parse(cursor.getString(cursor.getColumnIndex(atributos.DATADAMENSAGEM)));
                    mensagem.setDataEnvio(dataMensagem);
                }catch (Exception ex){
                    Log.e("ERROCONVERTERDATAMENSA",ex.getMessage());
                }

                listaMensagem.add(mensagem);

                while(cursor.moveToNext()){
                    mensagem = new Mensagem();
                    usuarioRemetente = new Usuario();
                    usuarioDestinatario = new Usuario();
                    listaUsuarioDestinatario = new ArrayList<Usuario>();

                    mensagem.setIdMensagem(cursor.getInt(cursor.getColumnIndex(atributos.ID)));

                    idUsuarioRemetente = cursor.getInt(cursor.getColumnIndex(atributos.IDUSUARIOREMETENTE));
                    idUsuarioDestinatario = cursor.getInt(cursor.getColumnIndex(atributos.IDUSUARIODESTINATARIO));
                    lida = cursor.getInt(cursor.getColumnIndex(atributos.LIDA)) == 1 ? true : false;
                    nomeUsuarioRemetente = cursor.getString(cursor.getColumnIndex(atributos.NOMEUSUARIOREMETENTE));
                    nomeUsuarioDestinatario = cursor.getString(cursor.getColumnIndex(atributos.NOMEUSUARIODESTINATARIO));
                    mensagemS = cursor.getString(cursor.getColumnIndex(atributos.MENSAGEM));

                    mensagem.setLida(lida);
                    mensagem.setMensagem(mensagemS);
                    usuarioRemetente.setIdUsuario(idUsuarioRemetente);
                    usuarioRemetente.setNome(nomeUsuarioRemetente);
                    usuarioDestinatario.setIdUsuario(idUsuarioDestinatario);
                    usuarioDestinatario.setNome(nomeUsuarioDestinatario);
                    mensagem.setUsuarioRemetente(usuarioRemetente);
                    listaUsuarioDestinatario.add(usuarioDestinatario);
                    mensagem.setUsuariosDestino(listaUsuarioDestinatario);


                    try{
                        Date dataMensagem = formatData.parse(cursor.getString(cursor.getColumnIndex(atributos.DATADAMENSAGEM)));
                        mensagem.setDataEnvio(dataMensagem);
                    }catch (Exception ex){
                        Log.e("ERROCONVERTERDATAMENSA",ex.getMessage());
                    }
                    listaMensagem.add(mensagem);
                }

            }

            if (cursor != null) {
                cursor.close();
            }

        }catch(Exception ex){
            Log.e("ERROLISTAR",ex.getMessage());
            return null;
        }


        return listaMensagem;
    }


    /**
     * Lista as mensagens do dia da data passada no parâmetro
     * @param data
     * @return
     */
    public List<Mensagem> listarMensagemDoDiaDaData(Date data){
        List<Mensagem> listaMensagem = new ArrayList<Mensagem>();
        Cursor cursor = null;

        try {

            calendario.setTime(data);
            calendario.set(Calendar.HOUR_OF_DAY,0);
            calendario.set(Calendar.MILLISECOND,0);
            calendario.set(Calendar.SECOND,0);

            Date dataInicioDia = calendario.getTime();

            calendario.setTime(data);

            calendario.set(Calendar.HOUR_OF_DAY,23);
            calendario.set(Calendar.MILLISECOND,59);
            calendario.set(Calendar.SECOND,59);

            Date dataFimDia = calendario.getTime();

            this.database = this.dbHelper.getReadableDatabase();

            String dataStringInicio = formatData.format(dataInicioDia);
            String dataStringFim = formatData.format(dataFimDia);

            String where = atributos.DATADAMENSAGEM+" BETWEEN ? AND ?";

            String[] colunas = new String[] { atributos.ID,atributos.DATADAMENSAGEM,atributos.IDUSUARIODESTINATARIO,atributos
            .IDUSUARIOREMETENTE,atributos.LIDA,atributos.MENSAGEM,atributos.NOMEUSUARIODESTINATARIO,atributos.NOMEUSUARIOREMETENTE};

            String argumentos[] = new String[] { dataStringInicio,dataStringFim};

            cursor = database.query(TABLE_NAME,colunas,where,argumentos,null,null,null);



            if (cursor != null && cursor.moveToFirst()) {
                Mensagem mensagem = new Mensagem();
                Usuario usuarioRemetente = new Usuario();
                Usuario usuarioDestinatario = new Usuario();
                List<Usuario> listaUsuarioDestinatario = new ArrayList<Usuario>();

                mensagem.setIdMensagem(cursor.getInt(cursor.getColumnIndex(atributos.ID)));

                int idUsuarioRemetente = cursor.getInt(cursor.getColumnIndex(atributos.IDUSUARIOREMETENTE));
                int idUsuarioDestinatario = cursor.getInt(cursor.getColumnIndex(atributos.IDUSUARIODESTINATARIO));
                Boolean lida = cursor.getInt(cursor.getColumnIndex(atributos.LIDA)) == 1 ? true : false;
                String nomeUsuarioRemetente = cursor.getString(cursor.getColumnIndex(atributos.NOMEUSUARIOREMETENTE));
                String nomeUsuarioDestinatario = cursor.getString(cursor.getColumnIndex(atributos.NOMEUSUARIODESTINATARIO));
                String mensagemS = cursor.getString(cursor.getColumnIndex(atributos.MENSAGEM));

                mensagem.setLida(lida);
                mensagem.setMensagem(mensagemS);
                usuarioRemetente.setIdUsuario(idUsuarioRemetente);
                usuarioRemetente.setNome(nomeUsuarioRemetente);
                usuarioDestinatario.setIdUsuario(idUsuarioDestinatario);
                usuarioDestinatario.setNome(nomeUsuarioDestinatario);
                mensagem.setUsuarioRemetente(usuarioRemetente);
                listaUsuarioDestinatario.add(usuarioDestinatario);
                mensagem.setUsuariosDestino(listaUsuarioDestinatario);


                try{
                    Date dataMensagem = formatData.parse(cursor.getString(cursor.getColumnIndex(atributos.DATADAMENSAGEM)));
                    mensagem.setDataEnvio(dataMensagem);
                }catch (Exception ex){
                    Log.e("ERROCONVERTERDATAMENSA",ex.getMessage());
                }

                listaMensagem.add(mensagem);

                while(cursor.moveToNext()){
                    mensagem = new Mensagem();
                    usuarioRemetente = new Usuario();
                    usuarioDestinatario = new Usuario();
                    listaUsuarioDestinatario = new ArrayList<Usuario>();

                    mensagem.setIdMensagem(cursor.getInt(cursor.getColumnIndex(atributos.ID)));

                    idUsuarioRemetente = cursor.getInt(cursor.getColumnIndex(atributos.IDUSUARIOREMETENTE));
                    idUsuarioDestinatario = cursor.getInt(cursor.getColumnIndex(atributos.IDUSUARIODESTINATARIO));
                    lida = cursor.getInt(cursor.getColumnIndex(atributos.LIDA)) == 1 ? true : false;
                    nomeUsuarioRemetente = cursor.getString(cursor.getColumnIndex(atributos.NOMEUSUARIOREMETENTE));
                    nomeUsuarioDestinatario = cursor.getString(cursor.getColumnIndex(atributos.NOMEUSUARIODESTINATARIO));
                    mensagemS = cursor.getString(cursor.getColumnIndex(atributos.MENSAGEM));

                    mensagem.setLida(lida);
                    mensagem.setMensagem(mensagemS);
                    usuarioRemetente.setIdUsuario(idUsuarioRemetente);
                    usuarioRemetente.setNome(nomeUsuarioRemetente);
                    usuarioDestinatario.setIdUsuario(idUsuarioDestinatario);
                    usuarioDestinatario.setNome(nomeUsuarioDestinatario);
                    mensagem.setUsuarioRemetente(usuarioRemetente);
                    listaUsuarioDestinatario.add(usuarioDestinatario);
                    mensagem.setUsuariosDestino(listaUsuarioDestinatario);


                    try{
                        Date dataMensagem = formatData.parse(cursor.getString(cursor.getColumnIndex(atributos.DATADAMENSAGEM)));
                        mensagem.setDataEnvio(dataMensagem);
                    }catch (Exception ex){
                        Log.e("ERROCONVERTERDATAMENSA",ex.getMessage());
                    }
                    listaMensagem.add(mensagem);
                }

            }

            if (cursor != null) {
                cursor.close();
            }

        }catch(Exception ex){
            Log.e("ERROLISTAR",ex.getMessage());
            return null;
        }


        return listaMensagem;
    }


    public List<Mensagem> listarTudo(){
        List<Mensagem> listaMensagem = new ArrayList<Mensagem>();
        Cursor cursor = null;

        try {



            this.database = this.dbHelper.getReadableDatabase();


            String[] colunas = new String[] { atributos.ID,atributos.DATADAMENSAGEM,atributos.IDUSUARIODESTINATARIO,atributos
                    .IDUSUARIOREMETENTE,atributos.LIDA,atributos.MENSAGEM,atributos.NOMEUSUARIODESTINATARIO,atributos.NOMEUSUARIOREMETENTE};



            cursor = database.query(TABLE_NAME,colunas,null,null,null,null,null);



            if (cursor != null && cursor.moveToFirst()) {
                Mensagem mensagem = new Mensagem();
                Usuario usuarioRemetente = new Usuario();
                Usuario usuarioDestinatario = new Usuario();
                List<Usuario> listaUsuarioDestinatario = new ArrayList<Usuario>();

                mensagem.setIdMensagem(cursor.getInt(cursor.getColumnIndex(atributos.ID)));

                int idUsuarioRemetente = cursor.getInt(cursor.getColumnIndex(atributos.IDUSUARIOREMETENTE));
                int idUsuarioDestinatario = cursor.getInt(cursor.getColumnIndex(atributos.IDUSUARIODESTINATARIO));
                Boolean lida = cursor.getInt(cursor.getColumnIndex(atributos.LIDA)) == 1 ? true : false;
                String nomeUsuarioRemetente = cursor.getString(cursor.getColumnIndex(atributos.NOMEUSUARIOREMETENTE));
                String nomeUsuarioDestinatario = cursor.getString(cursor.getColumnIndex(atributos.NOMEUSUARIODESTINATARIO));
                String mensagemS = cursor.getString(cursor.getColumnIndex(atributos.MENSAGEM));

                mensagem.setLida(lida);
                mensagem.setMensagem(mensagemS);
                usuarioRemetente.setIdUsuario(idUsuarioRemetente);
                usuarioRemetente.setNome(nomeUsuarioRemetente);
                usuarioDestinatario.setIdUsuario(idUsuarioDestinatario);
                usuarioDestinatario.setNome(nomeUsuarioDestinatario);
                mensagem.setUsuarioRemetente(usuarioRemetente);
                listaUsuarioDestinatario.add(usuarioDestinatario);
                mensagem.setUsuariosDestino(listaUsuarioDestinatario);


                try{
                    Date dataMensagem = formatData.parse(cursor.getString(cursor.getColumnIndex(atributos.DATADAMENSAGEM)));
                    mensagem.setDataEnvio(dataMensagem);
                }catch (Exception ex){
                    Log.e("ERROCONVERTERDATAMENSA",ex.getMessage());
                }

                listaMensagem.add(mensagem);

                while(cursor.moveToNext()){
                    mensagem = new Mensagem();
                    usuarioRemetente = new Usuario();
                    usuarioDestinatario = new Usuario();
                    listaUsuarioDestinatario = new ArrayList<Usuario>();

                    mensagem.setIdMensagem(cursor.getInt(cursor.getColumnIndex(atributos.ID)));

                    idUsuarioRemetente = cursor.getInt(cursor.getColumnIndex(atributos.IDUSUARIOREMETENTE));
                    idUsuarioDestinatario = cursor.getInt(cursor.getColumnIndex(atributos.IDUSUARIODESTINATARIO));
                    lida = cursor.getInt(cursor.getColumnIndex(atributos.LIDA)) == 1 ? true : false;
                    nomeUsuarioRemetente = cursor.getString(cursor.getColumnIndex(atributos.NOMEUSUARIOREMETENTE));
                    nomeUsuarioDestinatario = cursor.getString(cursor.getColumnIndex(atributos.NOMEUSUARIODESTINATARIO));
                    mensagemS = cursor.getString(cursor.getColumnIndex(atributos.MENSAGEM));

                    mensagem.setLida(lida);
                    mensagem.setMensagem(mensagemS);
                    usuarioRemetente.setIdUsuario(idUsuarioRemetente);
                    usuarioRemetente.setNome(nomeUsuarioRemetente);
                    usuarioDestinatario.setIdUsuario(idUsuarioDestinatario);
                    usuarioDestinatario.setNome(nomeUsuarioDestinatario);
                    mensagem.setUsuarioRemetente(usuarioRemetente);
                    listaUsuarioDestinatario.add(usuarioDestinatario);
                    mensagem.setUsuariosDestino(listaUsuarioDestinatario);


                    try{
                        Date dataMensagem = formatData.parse(cursor.getString(cursor.getColumnIndex(atributos.DATADAMENSAGEM)));
                        mensagem.setDataEnvio(dataMensagem);
                    }catch (Exception ex){
                        Log.e("ERROCONVERTERDATAMENSA",ex.getMessage());
                    }
                    listaMensagem.add(mensagem);
                }

            }

            if (cursor != null) {
                cursor.close();
            }

        }catch(Exception ex){
            Log.e("ERROLISTAR",ex.getMessage());
            return null;
        }


        return listaMensagem;
    }




}
