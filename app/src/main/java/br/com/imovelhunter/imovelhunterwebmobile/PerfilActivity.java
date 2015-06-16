package br.com.imovelhunter.imovelhunterwebmobile;


import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.imovelhunter.adapters.GenericSpinnerAdapter;
import br.com.imovelhunter.dominio.Bairros;
import br.com.imovelhunter.dominio.Cidades;
import br.com.imovelhunter.dominio.Imovel;
import br.com.imovelhunter.dominio.Perfil;
import br.com.imovelhunter.dominio.Uf;
import br.com.imovelhunter.dominio.Usuario;
import br.com.imovelhunter.enums.ParametrosSessao;
import br.com.imovelhunter.enums.ParametrosSessaoJson;
import br.com.imovelhunter.enums.SituacaoImovel;
import br.com.imovelhunter.enums.TipoImovel;
import br.com.imovelhunter.listeners.OnFinishTask;
import br.com.imovelhunter.tasks.TaskCadastrarPerfilImovel;
import br.com.imovelhunter.tasks.TaskListarBairrosPorCidade;
import br.com.imovelhunter.tasks.TaskListarCidadesPorUf;
import br.com.imovelhunter.tasks.TaskListarUf;
import br.com.imovelhunter.util.SessionUtilJson;
import br.com.imovelhunter.web.Web;
import br.com.imovelhunter.web.WebImp;


public class PerfilActivity extends ActionBarActivity implements OnFinishTask {

    private ArrayAdapter<CharSequence> vAluguel;
    private ArrayAdapter<CharSequence> vVendas;
    private ArrayAdapter<CharSequence> tipoImovelAdapter;
    private ArrayAdapter<CharSequence> quartosAdapter;

    private ProgressDialog progress;

    private RadioButton rd;
    private RadioButton re;
    private Perfil perfilGlobal;

    private Spinner spinnerValores;
    private Spinner spinnerIipoImovel;
    private Spinner spinnerQuartos;

    private Usuario usuarioLogado;

    private Spinner spinnerUf;

    private List<Uf> listaUf = new ArrayList<Uf>();
    private GenericSpinnerAdapter<Uf> adapterUf = new GenericSpinnerAdapter<Uf>(listaUf,"cd_uf","ds_uf_sigla");


    private Spinner spinnerCidade;

    private List<Cidades> listaCidades = new ArrayList<Cidades>();
    private GenericSpinnerAdapter<Cidades> adapterCidades = new GenericSpinnerAdapter<Cidades>(listaCidades,"cd_cidade","ds_cidade_nome");

    private Spinner spinnerBairro;

    private List<Bairros> listaBairro = new ArrayList<Bairros>();
    private GenericSpinnerAdapter<Bairros> adapterBairros = new GenericSpinnerAdapter<Bairros>(listaBairro,"cd_bairro","ds_bairro_nome");

    private final int REQUEST_LISTAR_UF = 1;
    private final int REQUEST_LISTAR_CIDADE_POR_UF = 2;
    private final int REQUEST_LISTAR_BAIRRO_POR_CIDADE = 3;
    private final int REQUEST_CADASTRAR_PERFIL = 4;
    private final int REQUEST_TELA_LISTA_PERFIL = 5;

    private Button btnConfirmar;


    private Uf uf;
    private Cidades cidade;
    private Bairros bairro;

    private Web web;

    private RadioGroup radioGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_perfil);
        setContentView(R.layout.activity_cadastro_perfil);

        android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#315e8a")));
        }

        if(getResources().getBoolean(R.bool.smart)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }else{
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        this.usuarioLogado = (Usuario)SessionUtilJson.getInstance(this).getJsonObject(ParametrosSessaoJson.USUARIO_LOGADO,Usuario.class);

        web = new WebImp();
        this.progress = new ProgressDialog(this);
        this.progress.setIcon(R.drawable.icone);
        this.progress.setMessage("Processando");

        vAluguel =  ArrayAdapter.createFromResource(this, R.array.valores_aluguel, R.layout.spinner_item);
        vVendas =   ArrayAdapter.createFromResource(this, R.array.valores_vendas, R.layout.spinner_item);
        tipoImovelAdapter = ArrayAdapter.createFromResource(this, R.array.tipo_imovel, R.layout.spinner_item);
        quartosAdapter = ArrayAdapter.createFromResource(this, R.array.qtd_quartos, R.layout.spinner_item);



        //Definindo a spinner das UF

        spinnerUf =(Spinner) findViewById(R.id.spinnerUf);
        spinnerUf.setAdapter(adapterUf);

        //Definindo a spinner dos bairros

        spinnerBairro = (Spinner) findViewById(R.id.spinnerBairro);
        spinnerBairro.setAdapter(adapterBairros);

        //Definindo a spinner de quartos
        spinnerQuartos =(Spinner) findViewById(R.id.spinnerQuartos);
        spinnerQuartos.setAdapter(quartosAdapter);
        //Definindo a spinner dos valores
        spinnerValores = (Spinner) findViewById(R.id.spinner);
        spinnerValores.setAdapter(vAluguel);
        //Definindo a spinner dos tipos de imoveis
        spinnerIipoImovel = (Spinner) findViewById(R.id.spinnerTipo);
        spinnerIipoImovel.setAdapter(tipoImovelAdapter);

        //Definindo a spinner das cidades
        spinnerCidade = (Spinner) findViewById(R.id.spinnerCidade);
        spinnerCidade.setAdapter(adapterCidades);

        //Declarando uma spinner para cada radial Button, Radial Direito e Radial Esquerdo
        rd = (RadioButton) findViewById(R.id.rdButtonComprar);
        rd.setSelected(true);
        re = (RadioButton) findViewById(R.id.rdButtonAlugar);


        this.radioGroup = (RadioGroup)this.findViewById(R.id.rdradiogroup);

        this.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rdButtonAlugar){
                    spinnerValores.setAdapter(vAluguel);
                }else{
                    spinnerValores.setAdapter(vVendas);
                }
            }
        });




        this.spinnerUf.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                uf = listaUf.get(position);
                progress.show();

                new TaskListarCidadesPorUf(REQUEST_LISTAR_CIDADE_POR_UF,PerfilActivity.this).execute(web,uf);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        this.spinnerCidade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cidade = listaCidades.get(position);
                progress.show();
                new TaskListarBairrosPorCidade(REQUEST_LISTAR_BAIRRO_POR_CIDADE,PerfilActivity.this).execute(web, cidade);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        this.spinnerBairro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bairro = listaBairro.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        this.progress.show();

        new TaskListarUf(REQUEST_LISTAR_UF,this).execute(web);

        this.btnConfirmar = (Button)this.findViewById(R.id.btnCadastrar);




        this.btnConfirmar.setOnClickListener(new View.OnClickListener() {




        public void onClick(View v) {


            Perfil p = new Perfil();

            p.setBairro(((Bairros)spinnerBairro.getSelectedItem()).getDs_bairro_nome());
            p.setCidade(((Cidades)spinnerCidade.getSelectedItem()).getDs_cidade_nome());
            p.setUf(((Uf)spinnerUf.getSelectedItem()).getDs_uf_sigla());




            if(!rd.isChecked()) {
                p.setSituacaoImovel(SituacaoImovel.LOCACAO);
            }else{
                p.setSituacaoImovel(SituacaoImovel.VENDA);
            }

            switch (spinnerQuartos.getSelectedItemPosition()){
                case 1:
                    p.setQtQuartos(0);
                    break;
                case 2:
                    p.setQtQuartos(1);
                    break;
                case 3:
                    p.setQtQuartos(2);
                    break;
                case 4:
                    p.setQtQuartos(3);
                    break;
                case 5:
                    p.setQtQuartos(4);
                    break;
                case 6:
                    p.setQtQuartos(5);
                    break;
            }


            switch (spinnerValores.getSelectedItemPosition()){
                case 1:
                    p.setValor(0);
                    break;
                case 2:
                    p.setValor(1);
                    break;
                case 3:
                    p.setValor(2);
                    break;
                case 4:
                    p.setValor(3);
                    break;
                case 5:
                    p.setValor(4);
                    break;
                case 6:
                    p.setValor(5);
                    break;
                case 7:
                    p.setValor(6);
                    break;
                case 8:
                    p.setValor(7);
                    break;
            }

            switch (spinnerIipoImovel.getSelectedItemPosition()){
                case 1:
                    p.setTipo(TipoImovel.CASA);
                    break;
                case 2:
                    p.setTipo(TipoImovel.APARTAMENTO);
                    break;
                case 3:
                    p.setTipo(TipoImovel.APARTAMENTO_DUPLEX);
                    break;
                case 4:
                    p.setTipo(TipoImovel.TERRENO);
                    break;
                case 5:
                    p.setTipo(TipoImovel.AREA);
                    break;
                case 6:
                    p.setTipo(TipoImovel.BARRACO);
                    break;
                case 7:
                    p.setTipo(TipoImovel.CHACARA);
                    break;
                case 8:
                    p.setTipo(TipoImovel.COBERTURA);
                    break;
                case 9:
                    p.setTipo(TipoImovel.FLAT);
                    break;
                case 10:
                    p.setTipo(TipoImovel.GALPAO);
                    break;
                case 11:
                    p.setTipo(TipoImovel.KITNET);
                    break;
                case 12:
                    p.setTipo(TipoImovel.LOFT);
                    break;
                case 13:
                    p.setTipo(TipoImovel.PREDIO);
                    break;
                case 14:
                    p.setTipo(TipoImovel.SALA);
                    break;
                case 15:
                    p.setTipo(TipoImovel.SALAO);
                    break;
                case 16:
                    p.setTipo(TipoImovel.SITIO);
                    break;
            }

            p.setUsuario(usuarioLogado);




            new TaskCadastrarPerfilImovel(REQUEST_CADASTRAR_PERFIL,PerfilActivity.this).execute(web, p);

              //new TaskCadastrarCliente(1,CadastroClienteActivity.this).execute(web,cliente,SessionUtil.getObject("serial"));
            }
           }
        );





    }

    private MenuItem menuListaPerfis;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.perfil_menu, menu);

        this.menuListaPerfis = menu.getItem(0);

        this.menuListaPerfis.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                Intent in = new Intent(PerfilActivity.this,ListaPerfilActivity.class);

                startActivityForResult(in,REQUEST_TELA_LISTA_PERFIL);

                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_lista_perfil) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private boolean validarCampos(){
        if(listaUf.size() == 0){
            this.progress.show();
            new TaskListarUf(REQUEST_LISTAR_UF,PerfilActivity.this).execute(web);
            return false;
        }else if(listaCidades.size() == 0){
            progress.show();
            new TaskListarCidadesPorUf(REQUEST_LISTAR_CIDADE_POR_UF,PerfilActivity.this).execute(web,uf);
            return false;
        }else if(listaBairro.size() == 0){
            progress.show();
            new TaskListarBairrosPorCidade(REQUEST_LISTAR_BAIRRO_POR_CIDADE,PerfilActivity.this).execute(web, cidade);
            return false;
        }

        return true;
    }


    @Override
    public void finish(int requestCode, int responseCode, Object data) {

        if (requestCode == REQUEST_LISTAR_UF){
            if(responseCode == 1){
                List<Uf> listaNova = (List<Uf>)data;
                this.listaUf.addAll(listaNova);
                this.adapterUf.notifyDataSetChanged();
            }else if(responseCode == 0){
                Toast.makeText(this,"Erro na conexão com o servidor",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this,(String)data,Toast.LENGTH_LONG).show();
            }
        }else if(requestCode == REQUEST_LISTAR_CIDADE_POR_UF){
            if(responseCode == 1){
                List<Cidades> listaNova = (List<Cidades>)data;
                this.listaCidades.clear();
                this.listaCidades.addAll(listaNova);
                this.adapterCidades.notifyDataSetChanged();
            }else if(responseCode == 0){
                Toast.makeText(this,"Erro na conexão com o servidor",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this,(String)data,Toast.LENGTH_LONG).show();
            }
        }else if(requestCode == REQUEST_LISTAR_BAIRRO_POR_CIDADE){
            if(responseCode == 1){
                List<Bairros> listaNova = (List<Bairros>)data;
                this.listaBairro.clear();
                this.listaBairro.addAll(listaNova);
                this.adapterBairros.notifyDataSetChanged();
            }else if(responseCode == 0){
                Toast.makeText(this,"Erro na conexão com o servidor",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this,(String)data,Toast.LENGTH_LONG).show();
            }
        }else if(requestCode == REQUEST_CADASTRAR_PERFIL){
            if(responseCode == 1){
                if((Boolean)data){
                    Toast.makeText(this,"Cadastrado com sucesso",Toast.LENGTH_LONG).show();
                }
            }else if(responseCode == 0){
                Toast.makeText(this,"Erro na conexão com o servidor",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this,(String)data,Toast.LENGTH_LONG).show();
            }
        }


        this.progress.dismiss();
    }
}
