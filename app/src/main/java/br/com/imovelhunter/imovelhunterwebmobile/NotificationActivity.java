package br.com.imovelhunter.imovelhunterwebmobile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.imovelhunter.adapters.AdapterNotificacao;
import br.com.imovelhunter.dao.NotificacaoDAO;
import br.com.imovelhunter.dialogs.DialogAlerta;
import br.com.imovelhunter.dominio.Notificacao;


public class NotificationActivity extends ActionBarActivity {


    ListView listView;
    NotificacaoDAO notificacaoDAO;
    AdapterNotificacao adapter;
    AlertDialog alerta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_notification);


        notificacaoDAO = new NotificacaoDAO(getApplicationContext());
        ArrayList<Notificacao> notificacaoes = new ArrayList<Notificacao>();

        Notificacao ntf = new Notificacao();
        ntf.setRua("Rua vida longa");
        ntf.setSituacao("Venda");
        ntf.setTipo("Casa");
        ntf.setNumero("2424");
        ntf.setPreco(7745.22);
        ntf.setLatitude(-8.1200242);
        ntf.setLongitude(-34.896827);

        ntf.setCaminhoImagem("http://gizmodo.uol.com.br/wp-content/blogs.dir/8/files/2015/05/cubos-de-comida.jpg");

        notificacaoDAO.inserir(ntf);
        //notificacaoes.add(ntf);


        Notificacao ntf2 = new Notificacao();
        ntf2.setRua("RUa do paranaue");
        ntf2.setSituacao("Aluguel");
        ntf2.setTipo("Apt");
        ntf2.setNumero("87");
        ntf2.setPreco(111.22);
        ntf2.setLongitude(4445);
        ntf2.setLatitude(777777);
        ntf2.setCaminhoImagem("http://gizmodo.uol.com.br/wp-content/blogs.dir/8/files/2015/05/1258407504077605036-1260x710.jpg");

      //  notificacaoDAO.inserir(ntf2);

        Notificacao ntf3 = new Notificacao();
        ntf3.setSituacao("Venda");
        ntf3.setTipo("Cobertura");
        ntf3.setRua("Armando monteiro perdeu");
        ntf3.setNumero("13,");
        ntf3.setPreco(77785.22);
        ntf3.setCaminhoImagem("http://gizmodo.uol.com.br/wp-content/blogs.dir/8/files/2015/04/wtsg56vbh9d1ntfqsg7x-1260x579.jpg");

      //  notificacaoDAO.inserir(ntf3);

       // notificacaoDAO.excluir(2);


     //   notificacaoDAO.excluir(ntf2.getIdNotificacao());
        //  notificacaoes.add(ntf2);
        notificacaoes.add(ntf3);

        listView = (ListView) findViewById(R.id.listViewMensagem);
       adapter = new AdapterNotificacao(notificacaoDAO.listar());
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(NotificationActivity.this, MapaActivity.class);



                Bundle b = new Bundle();
                b.putDouble("LATITUDE",adapter.getmNotificacoes().get(i).getLatitude());
                b.putDouble("LONGITUDE",adapter.getmNotificacoes().get(i).getLongitude());

               intent.putExtras(b);

                startActivity(intent);

                finish(); // Em Teste
                // aqui vai as cordenadas do paraue imovel

            }


        });


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
listView.getId();
  dialogDeletarNotificacao(i);
                return false;

            }
        });
    }


    public void dialogDeletarNotificacao(final int currentItem){



            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(NotificationActivity.this);



            alertDialogBuilder.setTitle(this.getTitle() + " Deletar Notificação");

            alertDialogBuilder.setMessage("Pressione sim para excluir esta notificação");

            // set positive button: Yes message

            alertDialogBuilder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int id) {

                  //  int currentItem = listView.get
                    int  idNotificacao = adapter.getmNotificacoes().get(currentItem).getIdNotificacao();
                    notificacaoDAO.excluir(idNotificacao);

atualizarLista();

                  //  adapter.notifyDataSetChanged();
                  //  listView.setAdapter(adapter);


                }


            });

            // set negative button: No message

            alertDialogBuilder.setNegativeButton("Nao", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int id) {

                    // cancel the alert box and put a Toast to the user


                    Toast.makeText(NotificationActivity.this,"Negativo"+dialog, Toast.LENGTH_LONG).show();

                }

            });





            AlertDialog alertDialog = alertDialogBuilder.create();

            // show alert

            alertDialog.show();




    }


    public void atualizarLista(){
        NotificacaoDAO notificacaoDAO = new NotificacaoDAO(getApplicationContext());
        List<Notificacao>notificacaos = new ArrayList<Notificacao>();
        notificacaos = notificacaoDAO.listar();
        AdapterNotificacao adapterNotificacao = new AdapterNotificacao(notificacaos);
      listView.setAdapter(adapterNotificacao);
        adapterNotificacao.notifyDataSetChanged();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notification, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
     //   atualizarLista();
    }
}
