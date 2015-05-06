package br.com.imovelhunter.imovelhunterwebmobile;


import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
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
import br.com.imovelhunter.dominio.Filtro;
import br.com.imovelhunter.dominio.Imovel;
import br.com.imovelhunter.dominio.Uf;
import br.com.imovelhunter.enums.ParametrosSessao;
import br.com.imovelhunter.listeners.OnFinishTask;
import br.com.imovelhunter.tasks.TaskFiltrarImovel;
import br.com.imovelhunter.tasks.TaskListarBairrosPorCidade;
import br.com.imovelhunter.tasks.TaskListarCidadesPorUf;
import br.com.imovelhunter.tasks.TaskListarUf;
import br.com.imovelhunter.util.SessionUtil;


public class FiltroActivity extends Activity implements OnFinishTask{

    private ArrayAdapter<CharSequence> vAluguel;
    private ArrayAdapter<CharSequence> vVendas;
    private ArrayAdapter<CharSequence> tipoImovelAdapter;
    private ArrayAdapter<CharSequence> quartosAdapter;

    private ProgressDialog progress;

    private RadioButton rd;
    private RadioButton re;

    private Spinner spinnerValores;
    private Spinner spinnerIipoImovel;
    private Spinner spinnerQuartos;



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
    private final int REQUEST_FILTRAR = 4;

    private Button btnConfirmar;
    private Button btnVoltar;

    private Uf uf;
    private Cidades cidade;
    private Bairros bairro;

    private RadioGroup radioGroup;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtro);

        ActionBar bar = getActionBar();
        if (bar != null) {
            bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#315e8a")));
        }


        this.progress = new ProgressDialog(this);
        this.progress.setIcon(R.drawable.imovelhunterimgicone);
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
                new TaskListarCidadesPorUf(REQUEST_LISTAR_CIDADE_POR_UF,FiltroActivity.this).execute(SessionUtil.getObject(ParametrosSessao.WEB),uf);
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
                new TaskListarBairrosPorCidade(REQUEST_LISTAR_BAIRRO_POR_CIDADE,FiltroActivity.this).execute(SessionUtil.getObject(ParametrosSessao.WEB), cidade);
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
        new TaskListarUf(REQUEST_LISTAR_UF,this).execute(SessionUtil.getObject(ParametrosSessao.WEB));

        this.btnConfirmar = (Button)this.findViewById(R.id.btnConfirmar);
        this.btnVoltar = (Button)this.findViewById(R.id.btnVoltar);


        this.btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validarCampos()) {
                    Filtro filtro = new Filtro();

                    filtro.setBairro(bairro.getDs_bairro_nome());
                    filtro.setCidade(cidade.getDs_cidade_nome());
                    filtro.setUf(uf.getDs_uf_sigla());

                    if (!rd.isChecked()) {
                        filtro.setSituacao("ALUGAR");
                    } else {
                        filtro.setSituacao("COMPRAR");
                    }

                    filtro.setValorDesejado((String) spinnerValores.getSelectedItem());
                    filtro.setTipoDeImovel((String) spinnerIipoImovel.getSelectedItem());
                    filtro.setQtdQuarto((String) spinnerQuartos.getSelectedItem());

                    progress.show();
                    new TaskFiltrarImovel(REQUEST_FILTRAR, FiltroActivity.this).execute(SessionUtil.getObject(ParametrosSessao.WEB), filtro);

                }
            }
        });

        this.btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    private boolean validarCampos(){
        if(listaUf.size() == 0){
            this.progress.show();
            new TaskListarUf(REQUEST_LISTAR_UF,this).execute(SessionUtil.getObject(ParametrosSessao.WEB));
            return false;
        }else if(listaCidades.size() == 0){
            progress.show();
            new TaskListarCidadesPorUf(REQUEST_LISTAR_CIDADE_POR_UF,FiltroActivity.this).execute(SessionUtil.getObject(ParametrosSessao.WEB),uf);
            return false;
        }else if(listaBairro.size() == 0){
            progress.show();
            new TaskListarBairrosPorCidade(REQUEST_LISTAR_BAIRRO_POR_CIDADE,FiltroActivity.this).execute(SessionUtil.getObject(ParametrosSessao.WEB), cidade);
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
        }else if(requestCode == REQUEST_FILTRAR){
            if(responseCode == 1){
                List<Imovel> listaNova = (List<Imovel>)data;

                if(listaNova.size() > 0){
                    SessionUtil.setObject(ParametrosSessao.IMOVEIS_FILTRO,listaNova);
                    setResult(1);
                    finish();
                }else{
                    Toast.makeText(this,"Nenhum imóvel encontrado",Toast.LENGTH_LONG).show();
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
