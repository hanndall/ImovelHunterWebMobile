package br.com.imovelhunter.imovelhunterwebmobile;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

import br.com.imovelhunter.dominio.Anunciante;
import br.com.imovelhunter.dominio.Caracteristica;
import br.com.imovelhunter.dominio.Imagem;
import br.com.imovelhunter.dominio.Imovel;
import br.com.imovelhunter.dominio.Usuario;
import br.com.imovelhunter.enums.Parametros;
import br.com.imovelhunter.enums.ParametrosSessao;
import br.com.imovelhunter.enums.ParametrosSessaoJson;
import br.com.imovelhunter.listeners.OnFinishTask;
import br.com.imovelhunter.tasks.TaskBuscarUsuarioPeloAnunciante;
import br.com.imovelhunter.util.SessionUtilJson;
import br.com.imovelhunter.web.Web;
import br.com.imovelhunter.web.WebImp;


public class DetalheImovelActivity extends ActionBarActivity implements OnFinishTask {

    private ImageView imagem;

    private TextView proprietario;

    private TextView numero;

    private TextView endereco;

    private TextView cidade;

    private TextView bairro;

    private TextView preco;

    private Button botaoLigar;

    private Button botaoEnviarMensagem;

    private TextView textViewSemImagens;

    /////////////////////////////////////////

    private TextView anunciante;

    private TextView nQuartos;

    private TextView nSuites;

    private TextView mCubicos;

    private TextView situacao;

    private TextView nAmbientes;

    private TextView nBanheiros;

    private TextView nSalas;

    private TextView complemento;

    private TextView caracteristicas;

    /////////////////////////////////////////

    private Imovel imovel;

    private int indexFoto;

    private int tamListImagens;

    private Button botEsquerda;

    private Button botDireita;

    private List<Imagem> listaImagemImovel;

    private final int REQUEST_BUSCAR_USUARIO_PELO_ANUNCIANTE = 1;
    private final int REQUEST_ABRIU_CHAT = 2;

    private Anunciante anuncianteObject;

    private Usuario usuarioObject;

    private TaskBuscarUsuarioPeloAnunciante taskBuscarUsuarioPeloAnunciante;

    private Usuario usuarioLogado;

    private Intent intentPassado;

    private Web web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_detalhe_imovel);
        setContentView(R.layout.detalhe_imovel_anunciante_fragment);

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
        this.web = new WebImp();

        this.intentPassado = getIntent();

        this.usuarioLogado = (Usuario)SessionUtilJson.getInstance(this).getJsonObject(ParametrosSessaoJson.USUARIO_LOGADO,Usuario.class);

        this.imagem = (ImageView)this.findViewById(R.id.imageView);
        this.proprietario = (TextView)this.findViewById(R.id.textViewProprietario);
        this.numero = (TextView)this.findViewById(R.id.textViewNumero);
        this.endereco = (TextView)this.findViewById(R.id.textViewEndereco);
        this.cidade = (TextView)this.findViewById(R.id.textViewCidade);
        this.bairro = (TextView)this.findViewById(R.id.textViewBairro);
        this.preco = (TextView)this.findViewById(R.id.textViewPreco);

        /////////////////////////////////////////////////////////////////////
        this.anunciante = (TextView)this.findViewById(R.id.textViewAnunciante);
        this.nQuartos = (TextView)this.findViewById(R.id.textViewQuartos);
        this.mCubicos = (TextView)this.findViewById(R.id.textViewMetrosCubicos);
        this.situacao = (TextView)this.findViewById(R.id.textViewSituacao);
        this.nAmbientes = (TextView)this.findViewById(R.id.textViewAmbientes);
        this.nBanheiros = (TextView)this.findViewById(R.id.textViewBanheiros);
        this.nSalas = (TextView)this.findViewById(R.id.textViewSalas);
        this.complemento = (TextView)this.findViewById(R.id.textViewComplemento);
        this.caracteristicas = (TextView)this.findViewById(R.id.textViewCaracteristicas);
        /////////////////////////////////////////////////////////////////////

        this.botaoLigar = (Button)this.findViewById(R.id.buttonLigar);
        this.botaoEnviarMensagem = (Button)this.findViewById(R.id.buttonEnviarMensagem);
        this.textViewSemImagens = (TextView)this.findViewById(R.id.textViewSemImagens);
        this.botaoLigar.setVisibility(View.VISIBLE);

        this.botaoEnviarMensagem.setOnClickListener(this.clickBotaoEnviarMensagem);
        this.botaoEnviarMensagem.setVisibility(View.GONE);

        this.botEsquerda = (Button)this.findViewById(R.id.button_esquerda);
        this.botDireita = (Button)this.findViewById(R.id.button_direita);

        this.botEsquerda.setOnClickListener(this.clickBotEsquerdo);
        this.botDireita.setOnClickListener(this.clickBotDireito);
        this.botaoLigar.setOnClickListener(this.clickBotaoLigar);

        this.indexFoto = intentPassado.getIntExtra(ParametrosSessao.INDEX_FOTO.name(),0);
        this.tamListImagens = 0;

        this.imovel = (Imovel)intentPassado.getSerializableExtra(ParametrosSessao.IMOVEL_SELECIONADO.name());
        this.setImovel();

        this.anuncianteObject = this.imovel.getAnunciante();
        if(this.anuncianteObject != null) {
            this.taskBuscarUsuarioPeloAnunciante = new TaskBuscarUsuarioPeloAnunciante(REQUEST_BUSCAR_USUARIO_PELO_ANUNCIANTE, this);
            this.taskBuscarUsuarioPeloAnunciante.execute(web, anuncianteObject);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putSerializable(ParametrosSessao.IMOVEL_SELECIONADO.name(),this.imovel);
        outState.putInt(ParametrosSessao.INDEX_FOTO.name(),this.indexFoto);


        super.onSaveInstanceState(outState);




    }

    private View.OnClickListener clickBotEsquerdo = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            indexFoto--;
            carregaImagem();
        }
    };

    private View.OnClickListener clickBotDireito = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            indexFoto++;
            carregaImagem();
        }
    };

    private View.OnClickListener clickBotaoLigar = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ligarProAnunciante();
        }
    };


    private void ligarProAnunciante() {

        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + this.imovel.getAnunciante().getTelefone()));

        startActivity(intent);
    }


    private void carregaImagem(){

        if(this.indexFoto <= 0){
            this.indexFoto = 0;
            this.botEsquerda.setVisibility(View.INVISIBLE);
            this.botEsquerda.setOnClickListener(null);
        }else{
            this.botEsquerda.setVisibility(View.VISIBLE);
            this.botEsquerda.setOnClickListener(this.clickBotEsquerdo);
        }

        if(this.indexFoto + 1 >= this.tamListImagens){
            this.indexFoto = this.tamListImagens - 1;
            this.botDireita.setVisibility(View.INVISIBLE);
            this.botDireita.setOnClickListener(null);
        }else{
            this.botDireita.setVisibility(View.VISIBLE);
            this.botDireita.setOnClickListener(this.clickBotDireito);
        }

        Picasso.with(this).load("http://ec2-54-68-17-181.us-west-2.compute.amazonaws.com/imovelhunterweb/servidor/imagens/"+this.listaImagemImovel.get(this.indexFoto).getCaminhoImagem()).into(this.imagem);

    }

    private View.OnClickListener clickBotaoEnviarMensagem = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(usuarioLogado != null) {
                Intent in = new Intent(DetalheImovelActivity.this, ChatActivity.class);

                in.putExtra(ParametrosSessao.USUARIO_CHAT_ATUAL.name(),usuarioObject);

                startActivityForResult(in, REQUEST_ABRIU_CHAT);
            }else{
                Toast.makeText(DetalheImovelActivity.this,"VocÃª precisa estÃ¡ logado para enviar uma mensagem",Toast.LENGTH_LONG).show();
            }
        }
    };

    public void setImovel(){

        if ((this.listaImagemImovel = this.imovel.getImagens()) != null && (this.tamListImagens = this.listaImagemImovel.size()) > 0){
            this.textViewSemImagens.setVisibility(View.GONE);
            this.imagem.setVisibility(View.VISIBLE);

            Picasso.with(this).load("http://ec2-54-68-17-181.us-west-2.compute.amazonaws.com/imovelhunterweb/servidor/imagens/"+this.listaImagemImovel.get(this.indexFoto).getCaminhoImagem()).into(this.imagem);

            if(this.indexFoto == 0){
                this.botEsquerda.setVisibility(View.INVISIBLE);
                this.botEsquerda.setOnClickListener(null);
            }else{
                this.botEsquerda.setVisibility(View.VISIBLE);
                this.botEsquerda.setOnClickListener(this.clickBotEsquerdo);
            }

            if(this.tamListImagens == 1){
                this.botDireita.setVisibility(View.INVISIBLE);
                this.botDireita.setOnClickListener(null);
            }else{
                this.botDireita.setVisibility(View.VISIBLE);
                this.botDireita.setOnClickListener(this.clickBotDireito);
            }



            carregaImagem();

        }else{
            this.textViewSemImagens.setVisibility(View.VISIBLE);
            this.botEsquerda.setVisibility(View.GONE);
            this.botDireita.setVisibility(View.GONE);
            this.imagem.setVisibility(View.GONE);
            this.tamListImagens = 0;
        }

        this.proprietario.setText("Proprietario: "+this.imovel.getNomeDoProprietario());
        this.numero.setText(this.imovel.getNumeroDoImovel());
        this.endereco.setText(this.imovel.getLogradouro());
        this.cidade.setText(this.imovel.getCidade());
        this.bairro.setText(this.imovel.getBairro());
        this.preco.setText("PreÃ§o:R$"+String.valueOf(this.imovel.getPreco()));


        /////////////////////////////////////////////////////////////////////
        this.anunciante.setText("Anunciante: "+this.imovel.getAnunciante().getNome());
        this.nQuartos.setText(String.valueOf(this.imovel.getNumeroDeQuartos())+"Quarto(s)");
        this.mCubicos.setText(String.valueOf(this.imovel.getAreaTotal())+"MÂ²");
        this.situacao.setText("SituaÃ§ao: "+this.imovel.getSituacaoImovel().name());
        this.nAmbientes.setText(String.valueOf(this.imovel.getAmbientes())+"Ambiente(s)");
        this.nBanheiros.setText(String.valueOf(this.imovel.getNumeroDeBanheiros())+"Banheiro(s)");
        this.nSalas.setText(String.valueOf(this.imovel.getNumeroDeSalas())+" Sala(s)");
        this.complemento.setText(this.imovel.getComplemento().toString());
        this.caracteristicas.setText("");

        List<Caracteristica> listaCaracteristicasDoImovel = this.imovel.getCaracteristicas();
        if(listaCaracteristicasDoImovel != null){
            StringBuffer stb = new StringBuffer();
            for(Caracteristica c : listaCaracteristicasDoImovel){
                stb.append(c.getNome());
                stb.append("\r\n");
            }
            this.caracteristicas.setText(stb.toString());
        }
        /////////////////////////////////////////////////////////////////////


    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.detalhe_imovel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish(int requestCode, int responseCode, Object data) {
        if(REQUEST_BUSCAR_USUARIO_PELO_ANUNCIANTE == requestCode){

            if(responseCode == 0){
                //ERRO

            }else if(responseCode == 1){
                //OK
                usuarioObject = (Usuario)data;
                if(usuarioObject != null && usuarioObject.getChaveGCM() != null && usuarioObject.getChaveGCM().length() > 0 && usuarioLogado != null && usuarioLogado.getIdUsuario() != usuarioObject.getIdUsuario()){
                    botaoEnviarMensagem.setVisibility(View.VISIBLE);
                }

            }else if(responseCode == 2){
                //Exception
            }
        }
    }
}
