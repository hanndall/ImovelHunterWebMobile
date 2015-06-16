package br.com.imovelhunter.imovelhunterwebmobile;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.com.imovelhunter.adapters.PageViewContatosAdapter;
import br.com.imovelhunter.dao.MensagemDAO;
import br.com.imovelhunter.dialogs.DialogAlerta;
import br.com.imovelhunter.dominio.Mensagem;
import br.com.imovelhunter.dominio.Usuario;
import br.com.imovelhunter.enums.ParametrosSessao;
import br.com.imovelhunter.enums.ParametrosSessaoJson;
import br.com.imovelhunter.fragments.FragmentListaContato;
import br.com.imovelhunter.listeners.OnFinishTask;
import br.com.imovelhunter.tasks.TaskBloquearContato;
import br.com.imovelhunter.tasks.TaskDesbloquearContato;
import br.com.imovelhunter.tasks.TaskListarContatosBloqueados;
import br.com.imovelhunter.tasks.TaskListarContatosDoUsuario;
import br.com.imovelhunter.tasks.TaskRemoverContato;
import br.com.imovelhunter.tasks.TaskUsuarioEBloqueado;
import br.com.imovelhunter.util.NetUtil;
import br.com.imovelhunter.util.SessionUtilJson;
import br.com.imovelhunter.web.Web;
import br.com.imovelhunter.web.WebImp;


public class ListaContatoActivity extends ActionBarActivity implements ViewPager.OnPageChangeListener,OnFinishTask,DialogAlerta.RespostaNao,DialogAlerta.RespostaSim{

    private ViewPager viewPager;

    private PageViewContatosAdapter pageViewContatosAdapter;

    private FragmentManager fm;

    private FragmentListaContato fragmentListaContatoAdicionados;

    private FragmentListaContato fragmentListaContatoBloqueados;

    private FragmentListaContato fragmentListaMensagem;

    private DialogAlerta dialogAlerta;

    private Handler handler;

    private ProgressDialog progress;

    private Web web;

    private Usuario usuarioLogado;

    private Usuario usuarioSelecionado;

    private Boolean usuarioSelecionadoBloqueado;

    private NetUtil netUtil;

    private List<Usuario> listaContatos;
    private List<Usuario> listaBloqueados;
    private List<Usuario> listaMensagensUsuario;

    private MensagemDAO mensagemDAO;

    //############
    //##REQUESTS##
    private final int REQUEST_LISTAR_CONTATOS_ADICIONADOS = 1;
    private final int REQUEST_LISTAR_CONTATOS_BLOQUEADOS = 2;
    private final int REQUEST_BLOQUEAR_CONTATO = 3;
    private final int REQUEST_EXCLUIR_CONTATO = 4;
    private final int REQUEST_TELA_CHAT = 5;
    private final int REQUEST_SEGUROU_ADICIONADOS = 6;
    private final int REQUEST_SEGUROU_BLOQUEADOS = 7;
    private final int REQUEST_CLICOU_CONTATO_BLOQUEADO = 8;
    private final int REQUEST_DESBLOQUEAR_CONTATO = 9;
    private final int REQUEST_VERIFICA_SE_CONTATO_BLOQUEADO = 10;
    //##REQUESTS##
    //############

    //#########
    //##TASKS##
    private TaskListarContatosDoUsuario taskListarContatosDoUsuario;
    private TaskListarContatosBloqueados taskListarContatosBloqueados;
    private TaskBloquearContato taskBloquearContato;
    private TaskRemoverContato taskRemoverContato;
    private TaskDesbloquearContato taskDesbloquearContato;
    private TaskUsuarioEBloqueado taskUsuarioEBloqueado;
    //##TASKS##
    //#########

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_contato);

        if(getResources().getBoolean(R.bool.smart)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }else{
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        android.support.v7.app.ActionBar bar = getSupportActionBar();
        bar.hide();
        if (bar != null) {
            bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#315e8a")));
        }



        this.netUtil = new NetUtil(this);

        if(!this.netUtil.verificaInternet()){
            finish();
            return;
        }

        this.mensagemDAO = new MensagemDAO(this);

        if(SessionUtilJson.getInstance(this).containsName(ParametrosSessaoJson.USUARIO_LOGADO)){
            this.usuarioLogado = (Usuario)SessionUtilJson.getInstance(this).getJsonObject(ParametrosSessaoJson.USUARIO_LOGADO,Usuario.class);
        }else{
            Toast.makeText(this,"Usuário não está logado",Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        this.web = new WebImp();

        this.progress = new ProgressDialog(this);
        this.progress.setIcon(R.drawable.icone);
        this.progress.setMessage("Processando");
        this.progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                if(taskListarContatosDoUsuario != null && taskListarContatosDoUsuario.getStatus().equals(AsyncTask.Status.RUNNING)){
                    taskListarContatosDoUsuario.cancel(true);
                }
                if(taskListarContatosBloqueados != null && taskListarContatosBloqueados.getStatus().equals(AsyncTask.Status.RUNNING)){
                    taskListarContatosBloqueados.cancel(true);
                }
                if(taskBloquearContato != null && taskBloquearContato.getStatus().equals(AsyncTask.Status.RUNNING)){
                    taskBloquearContato.cancel(true);
                }
                if(taskRemoverContato != null && taskRemoverContato.getStatus().equals(AsyncTask.Status.RUNNING)){
                    taskRemoverContato.cancel(true);
                }
                if(taskDesbloquearContato != null && taskDesbloquearContato.getStatus().equals(AsyncTask.Status.RUNNING)){
                    taskDesbloquearContato.cancel(true);
                }
                if(taskUsuarioEBloqueado != null && taskUsuarioEBloqueado.getStatus().equals(AsyncTask.Status.RUNNING)){
                    taskUsuarioEBloqueado.cancel(true);
                }
            }
        });

        fm = getSupportFragmentManager();

        this.handler = new Handler();

        this.fragmentListaContatoAdicionados = (FragmentListaContato)fm.findFragmentByTag("fragmentListaContatoAdicionados");
        this.fragmentListaContatoBloqueados = (FragmentListaContato)fm.findFragmentByTag("fragmentListaContatoBloqueados");
        this.fragmentListaMensagem = (FragmentListaContato)fm.findFragmentByTag("fragmentListaMensagem");

        if(this.fragmentListaContatoAdicionados == null){
            this.fragmentListaContatoAdicionados = new FragmentListaContato();
        }

        if(this.fragmentListaContatoBloqueados == null){
            this.fragmentListaContatoBloqueados = new FragmentListaContato();
        }

        if(this.fragmentListaMensagem ==  null){
            this.fragmentListaMensagem = new FragmentListaContato();
        }

        this.fragmentListaContatoAdicionados.setOnClickItemListListener(this.onClickItemListListenerFragmentAdicionados);
        this.fragmentListaContatoAdicionados.setOnHoldItemListListener(this.onHoldItemListListenerFragmentAdicionados);

        this.fragmentListaContatoBloqueados.setOnClickItemListListener(this.onClickItemListListenerFragmentBloquados);
        this.fragmentListaContatoBloqueados.setOnHoldItemListListener(this.onHoldItemListListenerFragmentBloquados);

        this.fragmentListaMensagem.setOnClickItemListListener(this.onClickItemListListenerFragmentMensagens);

        this.viewPager = (ViewPager)findViewById(R.id.pageviewcontatos);
        this.pageViewContatosAdapter = new PageViewContatosAdapter(fm,fragmentListaContatoAdicionados,fragmentListaContatoBloqueados,fragmentListaMensagem);

        this.viewPager.setAdapter(pageViewContatosAdapter);
        this.viewPager.setOnPageChangeListener(this);



        //Listar os contatos adicionados e os bloqueados
        this.taskListarContatosDoUsuario = new TaskListarContatosDoUsuario(REQUEST_LISTAR_CONTATOS_ADICIONADOS,this);
        this.taskListarContatosBloqueados = new TaskListarContatosBloqueados(REQUEST_LISTAR_CONTATOS_BLOQUEADOS,this);

        this.taskListarContatosDoUsuario.execute(web,usuarioLogado);
        this.taskListarContatosBloqueados.execute(web,usuarioLogado);

        this.mapUsuarios = new HashMap<Long,Usuario>();

        GcmBroadcastReceiver.setOnMessageListener(onMessageListener);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        listarMensagens();

    }



    private Map<Long,Usuario> mapUsuarios;
    private void listarMensagens(){
        this.mapUsuarios.clear();
        List<Mensagem> mnsg = mensagemDAO.listarMensgensNaoLidas(usuarioLogado.getIdUsuario());

        for(Mensagem m : mnsg){
            mapUsuarios.put(Long.valueOf(m.getUsuarioRemetente().getIdUsuario()),m.getUsuarioRemetente());
        }

        Iterator<Long> chaves = mapUsuarios.keySet().iterator();
        List<Usuario> usuarios = new ArrayList<Usuario>();
        while(chaves.hasNext()){
            Usuario u = mapUsuarios.get(chaves.next());
            usuarios.add(u);
        }

        fragmentListaMensagem.getListaContatos().clear();
        fragmentListaMensagem.getListaContatos().addAll(usuarios);
        this.listaMensagensUsuario = fragmentListaMensagem.getListaContatos();
        fragmentListaMensagem.getAdapterContatos().notifyDataSetChanged();
        fragmentListaContatoAdicionados.getAdapterContatos().notifyDataSetChanged();
        fragmentListaContatoBloqueados.getAdapterContatos().notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        if(taskListarContatosDoUsuario != null && taskListarContatosDoUsuario.getStatus().equals(AsyncTask.Status.RUNNING)){
            taskListarContatosDoUsuario.cancel(true);
        }
        if(taskListarContatosBloqueados != null && taskListarContatosBloqueados.getStatus().equals(AsyncTask.Status.RUNNING)){
            taskListarContatosBloqueados.cancel(true);
        }
        if(taskBloquearContato != null && taskBloquearContato.getStatus().equals(AsyncTask.Status.RUNNING)){
            taskBloquearContato.cancel(true);
        }
        if(taskRemoverContato != null && taskRemoverContato.getStatus().equals(AsyncTask.Status.RUNNING)){
            taskRemoverContato.cancel(true);
        }
        if(taskDesbloquearContato != null && taskDesbloquearContato.getStatus().equals(AsyncTask.Status.RUNNING)){
            taskDesbloquearContato.cancel(true);
        }
        if(taskUsuarioEBloqueado != null && taskUsuarioEBloqueado.getStatus().equals(AsyncTask.Status.RUNNING)){
            taskUsuarioEBloqueado.cancel(true);
        }

        GcmBroadcastReceiver.setOnMessageListener(null);

        super.onDestroy();
    }

    //##############
    //####Menu######
     @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_lista_contato, menu);
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
    //####Menu######
    //##############


    //####################
    //#####ViewPager######
    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int i) {

    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
    //#####ViewPager######
    //#####################

    //############
    //##Observer##

    public interface OnMensageListener{
        public void atualizar();
    }

    private OnMensageListener onMessageListener = new OnMensageListener() {
        @Override
        public void atualizar() {
            listarMensagens();
        }
    };

    //##Observer##
    //############

    //########################
    //##STARTACTIVITY RESULT##
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_TELA_CHAT){
            //Listar os contatos adicionados e os bloqueados
            this.taskListarContatosDoUsuario = new TaskListarContatosDoUsuario(REQUEST_LISTAR_CONTATOS_ADICIONADOS,this);
            this.taskListarContatosBloqueados = new TaskListarContatosBloqueados(REQUEST_LISTAR_CONTATOS_BLOQUEADOS,this);

            this.taskListarContatosDoUsuario.execute(web,usuarioLogado);
            this.taskListarContatosBloqueados.execute(web,usuarioLogado);

            listarMensagens();
        }

    }
    //##STARTACTIVITY RESULT##
    //########################

    //#################################
    //##Implementações das interfaces##
    private FragmentListaContato.OnClickItemListListener onClickItemListListenerFragmentMensagens = new FragmentListaContato.OnClickItemListListener(){

        @Override
        public void clickLista(Usuario usuario) {
            Intent intent = new Intent(ListaContatoActivity.this,ChatActivity.class);
            intent.putExtra(ParametrosSessao.USUARIO_CHAT_ATUAL.name(),usuario);
            ListaContatoActivity.this.startActivityForResult(intent,REQUEST_TELA_CHAT);
        }
    };


    /////////////////////////////////////
    /////////////////////////////////////
    private FragmentListaContato.OnClickItemListListener onClickItemListListenerFragmentAdicionados = new FragmentListaContato.OnClickItemListListener(){

        @Override
        public void clickLista(Usuario usuario) {
            Intent intent = new Intent(ListaContatoActivity.this,ChatActivity.class);
            intent.putExtra(ParametrosSessao.USUARIO_CHAT_ATUAL.name(),usuario);
            ListaContatoActivity.this.startActivityForResult(intent,REQUEST_TELA_CHAT);
        }
    };
    private FragmentListaContato.OnHoldItemListListener onHoldItemListListenerFragmentAdicionados = new FragmentListaContato.OnHoldItemListListener(){

        @Override
        public void holdLista(Usuario usuario) {
            progress.show();
            usuarioSelecionado = usuario;
            taskUsuarioEBloqueado = new TaskUsuarioEBloqueado(REQUEST_VERIFICA_SE_CONTATO_BLOQUEADO,ListaContatoActivity.this);
            taskUsuarioEBloqueado.execute(web,usuarioLogado,usuarioSelecionado);
        }
    };
    /////////////////////////////////////
    /////////////////////////////////////
    private FragmentListaContato.OnClickItemListListener onClickItemListListenerFragmentBloquados = new FragmentListaContato.OnClickItemListListener(){

        @Override
        public void clickLista(Usuario usuario) {
            usuarioSelecionado = usuario;
            dialogAlerta = new DialogAlerta(ListaContatoActivity.this,"Opções","",REQUEST_CLICOU_CONTATO_BLOQUEADO,"Desbloquear","Voltar");
            dialogAlerta.exibirDialog();
        }
    };
    private FragmentListaContato.OnHoldItemListListener onHoldItemListListenerFragmentBloquados = new FragmentListaContato.OnHoldItemListListener(){

        @Override
        public void holdLista(Usuario usuario) {

        }
    };
    //##Implementações das interfaces##
    //#################################


    //#########
    //##Tasks##
    @Override
    public void finish(int requestCode, int responseCode, Object data) {
        if(requestCode == REQUEST_LISTAR_CONTATOS_ADICIONADOS){
            if(responseCode == 0){
                Toast.makeText(this,"Erro ao listar os contatos do usuário",Toast.LENGTH_LONG).show();
            }else if(responseCode == 1){
                listaContatos = (List<Usuario>)data;
                this.fragmentListaContatoAdicionados.getListaContatos().clear();
                this.fragmentListaContatoAdicionados.getListaContatos().addAll(listaContatos);
                this.listaContatos = this.fragmentListaContatoAdicionados.getListaContatos();
                this.fragmentListaContatoAdicionados.getAdapterContatos().notifyDataSetChanged();

            }else if(responseCode == 2){
                Toast.makeText(this,new String((String)data),Toast.LENGTH_LONG).show();
            }
        }else if(requestCode == REQUEST_LISTAR_CONTATOS_BLOQUEADOS){
            if(responseCode == 0){
                Toast.makeText(this,"Erro ao listar os contatos bloqueados do usuário",Toast.LENGTH_LONG).show();
            }else if(responseCode == 1){
                listaBloqueados = (List<Usuario>)data;
                this.fragmentListaContatoBloqueados.getListaContatos().clear();
                this.fragmentListaContatoBloqueados.getListaContatos().addAll(listaBloqueados);
                this.listaBloqueados = this.fragmentListaContatoBloqueados.getListaContatos();
                this.fragmentListaContatoBloqueados.getAdapterContatos().notifyDataSetChanged();

            }else if(responseCode == 2){
                Toast.makeText(this,new String((String)data),Toast.LENGTH_LONG).show();
            }
        }else if(requestCode == REQUEST_BLOQUEAR_CONTATO){
            if(responseCode == 0){
                Toast.makeText(this,"Erro ao tentar bloquear o contato",Toast.LENGTH_LONG).show();
            }else if(responseCode == 1){
                this.taskListarContatosBloqueados = new TaskListarContatosBloqueados(REQUEST_LISTAR_CONTATOS_BLOQUEADOS,this);
                this.taskListarContatosBloqueados.execute(web,usuarioLogado);
            }else if(responseCode == 2){
                Toast.makeText(this,new String((String)data),Toast.LENGTH_LONG).show();
            }
        }else if(requestCode == REQUEST_DESBLOQUEAR_CONTATO){
            if(responseCode == 0){
                Toast.makeText(this,"Erro ao tentar desbloquear o contato",Toast.LENGTH_LONG).show();
            }else if(responseCode == 1){
                this.taskListarContatosBloqueados = new TaskListarContatosBloqueados(REQUEST_LISTAR_CONTATOS_BLOQUEADOS,this);
                this.taskListarContatosBloqueados.execute(web,usuarioLogado);
            }else if(responseCode == 2){
                Toast.makeText(this,new String((String)data),Toast.LENGTH_LONG).show();
            }
        }else if(requestCode == REQUEST_EXCLUIR_CONTATO){
            if(responseCode == 0){
                Toast.makeText(this,"Erro ao tentar excluir o contato",Toast.LENGTH_LONG).show();
            }else if(responseCode == 1){
                this.taskListarContatosDoUsuario = new TaskListarContatosDoUsuario(REQUEST_LISTAR_CONTATOS_ADICIONADOS,this);
                this.taskListarContatosDoUsuario.execute(web,usuarioLogado);
            }else if(responseCode == 2){
                Toast.makeText(this,new String((String)data),Toast.LENGTH_LONG).show();
            }
        }else if(requestCode == REQUEST_VERIFICA_SE_CONTATO_BLOQUEADO){
            if(responseCode == 0){
                Toast.makeText(this,"Erro ao tentar verificar se o contato é bloqueado",Toast.LENGTH_LONG).show();
            }else if(responseCode == 1){
                usuarioSelecionadoBloqueado = (Boolean)data;
                if(usuarioSelecionadoBloqueado){
                    dialogAlerta = new DialogAlerta(ListaContatoActivity.this,"Opções","",REQUEST_SEGUROU_ADICIONADOS,"Remover contato","Desbloquear contato");
                    dialogAlerta.exibirDialog();
                }else{
                    dialogAlerta = new DialogAlerta(ListaContatoActivity.this,"Opções","",REQUEST_SEGUROU_ADICIONADOS,"Remover contato","Bloquear contato");
                    dialogAlerta.exibirDialog();
                }
            }else if(responseCode == 2){
                Toast.makeText(this,new String((String)data),Toast.LENGTH_LONG).show();
            }
            progress.dismiss();
        }
    }
    //##Tasks##
    //#########


    //################
    //##DialogAlerta##
    @Override
    public void respondeuNao(int requestCode) {
        if(requestCode == REQUEST_SEGUROU_ADICIONADOS){
            if(usuarioSelecionado != null) {
                if(usuarioSelecionadoBloqueado){
                    taskDesbloquearContato = new TaskDesbloquearContato(REQUEST_DESBLOQUEAR_CONTATO,this);
                    taskDesbloquearContato.execute(web,usuarioLogado,usuarioSelecionado);
                }else {
                    taskBloquearContato = new TaskBloquearContato(REQUEST_BLOQUEAR_CONTATO, this);
                    taskBloquearContato.execute(web, usuarioLogado, usuarioSelecionado);
                }
            }else{
                Toast.makeText(this,"Usuário não selecionado",Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void respondeuSim(int requestCode) {
        if(requestCode == REQUEST_SEGUROU_ADICIONADOS){
            if(usuarioSelecionado != null) {
                taskRemoverContato = new TaskRemoverContato(REQUEST_EXCLUIR_CONTATO, this);
                taskRemoverContato.execute(web,usuarioLogado,usuarioSelecionado);
            }else{
                Toast.makeText(this,"Usuário não selecionado",Toast.LENGTH_LONG).show();
            }
        }
        if(requestCode == REQUEST_CLICOU_CONTATO_BLOQUEADO){
            if(usuarioSelecionado != null) {
                taskDesbloquearContato = new TaskDesbloquearContato(REQUEST_DESBLOQUEAR_CONTATO,this);
                taskDesbloquearContato.execute(web,usuarioLogado,usuarioSelecionado);
            }else{
                Toast.makeText(this,"Usuário não selecionado",Toast.LENGTH_LONG).show();
            }
        }
    }
    //##DialogAlerta##
    //################
}
