package br.com.imovelhunter.imovelhunterwebmobile;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.imovelhunter.adapters.AdapterImovel;
import br.com.imovelhunter.dominio.Anunciante;
import br.com.imovelhunter.dominio.Imovel;
import br.com.imovelhunter.enums.ParametrosSessao;
import br.com.imovelhunter.listeners.OnFinishTask;
import br.com.imovelhunter.tasks.TaskListarImoveisAnunciante;
import br.com.imovelhunter.util.NetUtil;
import br.com.imovelhunter.web.Web;
import br.com.imovelhunter.web.WebImp;


public class ListaImoveisActivity extends ActionBarActivity implements OnFinishTask {


    private List<Imovel> mListaImoveis;

    private ListView listView;

    private AdapterImovel adapterImovel;

    private Anunciante anuncianteLogado;

    private NetUtil netUtil;

    private Web web;

    private final int CLIQUE_ITEM_LISTA = 0;

    private TaskListarImoveisAnunciante taskListarImoveisAnunciante;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_imoveis);


        android.support.v7.app.ActionBar bar = getSupportActionBar();
        bar.hide();
        if (bar != null) {
            bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#315e8a")));
        }

        if(getResources().getBoolean(R.bool.smart)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }else{
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }


        listView = (ListView)this.findViewById(R.id.listaImoveis);

        this.netUtil = new NetUtil(this);

        this.web = new WebImp();

        this.netUtil = new NetUtil(this);

        Intent intent = getIntent();
        anuncianteLogado = (Anunciante)intent.getSerializableExtra("anunciantelogado");

        mListaImoveis = new ArrayList<Imovel>();
        adapterImovel = new AdapterImovel(mListaImoveis);
        listView.setAdapter(adapterImovel);


        if(!this.netUtil.verificaInternet()){
            finish();
            return;
        }



        new TaskListarImoveisAnunciante(1,this).execute(web,anuncianteLogado);


        //setar o onclick da lista de imoveis

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ListaImoveisActivity.this,DetalheImovelActivity.class);
                intent.putExtra(ParametrosSessao.IMOVEL_SELECIONADO.name(),mListaImoveis.get(position));
                startActivityForResult(intent,CLIQUE_ITEM_LISTA);
            }
        });

    }


    @Override
    public void finish(int requestCode, int responseCode, Object data) {

        if(requestCode == 1){
            if(responseCode == 0){
                Toast.makeText(this, "Erro na conexão com o servidor", Toast.LENGTH_LONG).show();
            }
            if(responseCode == 1){
                this.mListaImoveis.clear();
                this.mListaImoveis.addAll((List<Imovel>)data);
                adapterImovel.notifyDataSetChanged();
            }
            if(responseCode == 2){
                String mensagem = (String)data;
                Toast.makeText(this,mensagem,Toast.LENGTH_LONG).show();
            }
        }
    }




    public void atualizar(){
        this.mListaImoveis = (List<Imovel>)getIntent().getSerializableExtra("listaimovel");
        adapterImovel = new AdapterImovel(mListaImoveis);
        listView.setAdapter(adapterImovel);
        adapterImovel.notifyDataSetChanged();
    }
}
