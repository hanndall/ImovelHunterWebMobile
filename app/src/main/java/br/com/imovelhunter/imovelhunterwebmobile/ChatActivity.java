package br.com.imovelhunter.imovelhunterwebmobile;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.imovelhunter.adapters.AdapterMensagem;
import br.com.imovelhunter.dao.MensagemDAO;
import br.com.imovelhunter.dominio.Mensagem;
import br.com.imovelhunter.dominio.Usuario;
import br.com.imovelhunter.enums.Parametros;
import br.com.imovelhunter.enums.ParametrosSessao;
import br.com.imovelhunter.enums.ParametrosSessaoJson;
import br.com.imovelhunter.listeners.EscutadorDeMensagem;
import br.com.imovelhunter.listeners.OnFinishTask;
import br.com.imovelhunter.tasks.TaskAdicionarContato;
import br.com.imovelhunter.tasks.TaskBloquearContato;
import br.com.imovelhunter.tasks.TaskDesbloquearContato;
import br.com.imovelhunter.tasks.TaskEnviarMensagem;
import br.com.imovelhunter.tasks.TaskRemoverContato;
import br.com.imovelhunter.tasks.TaskUsuarioEAdicionado;
import br.com.imovelhunter.tasks.TaskUsuarioEBloqueado;
import br.com.imovelhunter.util.SessionUtilJson;
import br.com.imovelhunter.web.Web;
import br.com.imovelhunter.web.WebImp;


public class ChatActivity extends ActionBarActivity implements EscutadorDeMensagem,OnFinishTask {

    private AdapterMensagem adapterMensagem;
    private List<Mensagem> listaMensagem;
    private Usuario meuUsuario;
    private Usuario usuarioChatAtual;

    private Button botao;
    private EditText editText;

    private ListView listView;

    private final int REQUEST_ENVIAR_MENSAGEM = 1;
    private final int REQUEST_BLOQUEAR_CONTATO = 2;
    private final int REQUEST_DESBLOQUEAR_CONTATO = 3;
    private final int REQUEST_ADICIONAR_CONTATO = 4;
    private final int REQUEST_REMOVER_CONTATO = 5;
    private final int REQUEST_VERIFICA_ADICIONADO = 6;
    private final int REQUEST_VERIFICA_BLOQUEADO = 7;

    private TaskEnviarMensagem taskEnviarMensagem;

    private MensagemDAO mensagemDAO;

    private Web web;

    private Intent in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        android.support.v7.app.ActionBar bar = getSupportActionBar();
        //bar.hide();
        if (bar != null) {
            bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#315e8a")));
        }

        if(getResources().getBoolean(R.bool.smart)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }else{
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }



        GcmBroadcastReceiver.setEscutadorDeMensagem(this);

        this.in = getIntent();

        this.mensagemDAO = new MensagemDAO(getApplicationContext());

        this.web = new WebImp();

        meuUsuario = (Usuario)SessionUtilJson.getInstance(this).getJsonObject(ParametrosSessaoJson.USUARIO_LOGADO,Usuario.class);
        usuarioChatAtual = (Usuario)in.getSerializableExtra(ParametrosSessao.USUARIO_CHAT_ATUAL.name());
        this.listaMensagem = new ArrayList<Mensagem>();

        if(bar != null){
            bar.setTitle(usuarioChatAtual.getNomeUsuario());
            //bar.setDisplayShowHomeEnabled(false); //Serve para fazer desaparecer o ícone
        }


        List<Mensagem> mensagensDoDia = mensagemDAO.listarConversa(meuUsuario.getIdUsuario(),usuarioChatAtual.getIdUsuario());

        listaMensagem.addAll(mensagensDoDia);

        this.adapterMensagem = new AdapterMensagem(this.listaMensagem,this.meuUsuario);

        this.listView = (ListView)this.findViewById(R.id.listView);

        this.listView.setAdapter(this.adapterMensagem);

        this.editText = (EditText)this.findViewById(R.id.editText);
        this.botao = (Button)this.findViewById(R.id.button);

        this.botao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mensagem = editText.getText().toString();

                editText.setText("");

                Mensagem novaMensagem = new Mensagem();

                novaMensagem.setUsuarioRemetente(meuUsuario);
                List<Usuario> lu = new ArrayList<Usuario>();
                lu.add(usuarioChatAtual);
                novaMensagem.setUsuariosDestino(lu);
                novaMensagem.setDataEnvio(new Date());
                novaMensagem.setMensagem(mensagem);
                novaMensagem.setLida(true);
                mensagemDAO.inserirMensagem(novaMensagem);
                novaMensagem.setLida(false);


                listaMensagem.add(novaMensagem);



                adapterMensagem.notifyDataSetChanged();



                taskEnviarMensagem = new TaskEnviarMensagem(1,ChatActivity.this);
                taskEnviarMensagem.execute(web,novaMensagem);
            }
        });




    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putSerializable(ParametrosSessao.USUARIO_CHAT_ATUAL.name(),usuarioChatAtual);

        super.onSaveInstanceState(outState);
    }



    @Override
    public void onBackPressed() {
        GcmBroadcastReceiver.setEscutadorDeMensagem(null);
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        GcmBroadcastReceiver.setEscutadorDeMensagem(null);
        super.onDestroy();
    }

    private MenuItem menuItemBloqDesbloq;
    private MenuItem menuItemAdicionarRemover;
    private MenuItem menuItemRemoverConversa;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present..
        getMenuInflater().inflate(R.menu.chat, menu);

        this.menuItemBloqDesbloq = menu.getItem(0);
        this.menuItemAdicionarRemover = menu.getItem(1);
        this.menuItemRemoverConversa = menu.getItem(2);

        this.menuItemBloqDesbloq.setVisible(false);
        this.menuItemAdicionarRemover.setVisible(false);

        taskUsuarioEBloqueado = new TaskUsuarioEBloqueado(REQUEST_VERIFICA_BLOQUEADO,this);
        taskUsuarioEBloqueado.execute(web,meuUsuario,usuarioChatAtual);

        taskUsuarioEAdicionado = new TaskUsuarioEAdicionado(REQUEST_VERIFICA_ADICIONADO,this);
        taskUsuarioEAdicionado.execute(web,meuUsuario,usuarioChatAtual);

        this.menuItemRemoverConversa.setOnMenuItemClickListener(this.cliqueRemoveConversa);

        return true;
    }

    private TaskBloquearContato taskBloquearContato;
    private TaskDesbloquearContato taskDesbloquearContato;

    private TaskAdicionarContato taskAdicionarContato;
    private TaskRemoverContato taskRemoverContato;

    private TaskUsuarioEAdicionado taskUsuarioEAdicionado;
    private TaskUsuarioEBloqueado taskUsuarioEBloqueado;

    private MenuItem.OnMenuItemClickListener cliqueRemoveConversa = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {

            if(mensagemDAO.removerConversa(meuUsuario.getIdUsuario(),usuarioChatAtual.getIdUsuario())){
                Toast.makeText(ChatActivity.this,"Conversa removida com sucesso",Toast.LENGTH_LONG).show();

                listaMensagem.clear();
                List<Mensagem> mensagensDoDia = mensagemDAO.listarConversa(meuUsuario.getIdUsuario(),usuarioChatAtual.getIdUsuario());
                listaMensagem.addAll(mensagensDoDia);
                adapterMensagem.notifyDataSetChanged();
            }else{
                Toast.makeText(ChatActivity.this,"Erro ao tentar remover a conversa ",Toast.LENGTH_LONG).show();
            }

            return true;
        }
    };



    private MenuItem.OnMenuItemClickListener cliqueAdicionarContato = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {

            menuItemAdicionarRemover.setOnMenuItemClickListener(null);

            taskAdicionarContato = new TaskAdicionarContato(REQUEST_ADICIONAR_CONTATO,ChatActivity.this);
            taskAdicionarContato.execute(web,meuUsuario,usuarioChatAtual);

            return true;
        }
    };

    private MenuItem.OnMenuItemClickListener cliqueRemoverContato = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {

            menuItemAdicionarRemover.setOnMenuItemClickListener(null);

            taskRemoverContato = new TaskRemoverContato(REQUEST_REMOVER_CONTATO,ChatActivity.this);
            taskRemoverContato.execute(web,meuUsuario,usuarioChatAtual);

            return false;
        }
    };

    private MenuItem.OnMenuItemClickListener cliqueBloquear = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {

            menuItemBloqDesbloq.setOnMenuItemClickListener(null);

            taskBloquearContato = new TaskBloquearContato(REQUEST_BLOQUEAR_CONTATO,ChatActivity.this);
            taskBloquearContato.execute(web,meuUsuario,usuarioChatAtual);


            return true;
        }
    };

    private MenuItem.OnMenuItemClickListener cliqueDesBloquear = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {

            menuItemBloqDesbloq.setOnMenuItemClickListener(null);

            taskDesbloquearContato = new TaskDesbloquearContato(REQUEST_DESBLOQUEAR_CONTATO,ChatActivity.this);
            taskDesbloquearContato.execute(web,meuUsuario,usuarioChatAtual);

            return true;
        }
    };

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
    public void recebeuAlgo(Object mensagem) {

        Bundle bundle = (Bundle)mensagem;

        String mensagemS = bundle.getString("mensagem");

        Mensagem mensagemO = new Mensagem();

        mensagemO.parse(mensagemS);

        mensagemO.setLida(false);

        if(mensagemO.getUsuarioRemetente().getIdUsuario() == usuarioChatAtual.getIdUsuario()){
            listaMensagem.add(mensagemO);

            adapterMensagem.notifyDataSetChanged();

            usuarioChatAtual.setChaveGCM(mensagemO.getUsuarioRemetente().getChaveGCM());
        }else{



            mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

            Intent intent = new Intent(this,ChatActivity.class);

            String msg = mensagemO.getMensagem();

            Bundle args = new Bundle();

            args.putSerializable(Parametros.MENSAGEM_JSON.name(),mensagemO);
            args.putSerializable(ParametrosSessao.USUARIO_CHAT_ATUAL.name(),mensagemO.getUsuarioRemetente());

            intent.putExtras(args);

            PendingIntent contentIntent = PendingIntent.getActivity(this, 0,intent
                    , 0);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.icone)
                    .setContentTitle("Imovel Hunter - "+mensagemO.getUsuarioRemetente().getNomeUsuario())
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(msg))
                    .setContentText(msg);

            mBuilder.setAutoCancel(true);



            mBuilder.setContentIntent(contentIntent);
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

            this.vibrar();

        }
        mensagemDAO.inserirMensagem(mensagemO);

    }

    private NotificationManager mNotificationManager;

    public static final int NOTIFICATION_ID = 1;

    private void vibrar()
    {
        Vibrator rr = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long milliseconds = 1000;
        rr.vibrate(milliseconds);
    }

    @Override
    public void finish(int requestCode, int responseCode, Object data) {
        if(REQUEST_ENVIAR_MENSAGEM == requestCode){
            if(responseCode == 1){

            }else if(responseCode == 2){
                Toast.makeText(this,(String)data,Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(this,"Falha ao enviar a mensagem, tente novamente",Toast.LENGTH_LONG).show();
            }
        }else if(REQUEST_BLOQUEAR_CONTATO == requestCode){
            if(responseCode == 1){
                this.menuItemBloqDesbloq.setTitle("Desbloquear contato");
                this.menuItemBloqDesbloq.setOnMenuItemClickListener(cliqueDesBloquear);
                this.menuItemBloqDesbloq.setVisible(true);
                Toast.makeText(this,"Contato bloqueado com successo",Toast.LENGTH_LONG).show();
            }else{
                this.menuItemBloqDesbloq.setOnMenuItemClickListener(cliqueBloquear);
                Toast.makeText(this,"Falha ao bloquear o contato, tente novamente",Toast.LENGTH_LONG).show();

            }

        }else if(REQUEST_DESBLOQUEAR_CONTATO == requestCode){
            if(responseCode == 1){
                this.menuItemBloqDesbloq.setTitle("Bloquear contato");
                this.menuItemBloqDesbloq.setOnMenuItemClickListener(cliqueBloquear);
                this.menuItemBloqDesbloq.setVisible(true);
                Toast.makeText(this,"Contato desbloqueado com sucesso",Toast.LENGTH_LONG).show();
            }else{
                this.menuItemBloqDesbloq.setOnMenuItemClickListener(cliqueDesBloquear);
                Toast.makeText(this,"Falha ao desbloquear o contato, tente novamente",Toast.LENGTH_LONG).show();
            }
        }else if(REQUEST_VERIFICA_ADICIONADO == requestCode){
            if(responseCode == 0){
                Toast.makeText(this,"Falha ao tentar verificar se o contato já está adicionado",Toast.LENGTH_LONG).show();
            }else if(responseCode == 1){
                Boolean adicionado = (Boolean)data;

                if(adicionado){
                    this.menuItemAdicionarRemover.setTitle("Remover contato");
                    this.menuItemAdicionarRemover.setOnMenuItemClickListener(this.cliqueRemoverContato);
                    this.menuItemAdicionarRemover.setVisible(true);
                }else{
                    this.menuItemAdicionarRemover.setTitle("Adicionar contato");
                    this.menuItemAdicionarRemover.setOnMenuItemClickListener(this.cliqueAdicionarContato);
                    this.menuItemAdicionarRemover.setVisible(true);
                }

            }else if(responseCode == 2){
                Toast.makeText(this,(String)data,Toast.LENGTH_LONG).show();
            }
        }else if(REQUEST_VERIFICA_BLOQUEADO == requestCode){
            if(responseCode == 0){
                Toast.makeText(this,"Falha ao tentar verificar se o contato já está bloqueado",Toast.LENGTH_LONG).show();
            }else if(responseCode == 1){
                Boolean bloqueado = (Boolean)data;

                if(bloqueado){
                    this.menuItemBloqDesbloq.setTitle("Desbloquear contato");
                    this.menuItemBloqDesbloq.setOnMenuItemClickListener(cliqueDesBloquear);
                    this.menuItemBloqDesbloq.setVisible(true);
                }else{
                    this.menuItemBloqDesbloq.setTitle("Bloquear contato");
                    this.menuItemBloqDesbloq.setOnMenuItemClickListener(cliqueBloquear);
                    this.menuItemBloqDesbloq.setVisible(true);
                }

            }else if(responseCode == 2){
                Toast.makeText(this,(String)data,Toast.LENGTH_LONG).show();
            }
        }else if(REQUEST_ADICIONAR_CONTATO == requestCode){
            if(responseCode == 0){
                this.menuItemAdicionarRemover.setOnMenuItemClickListener(this.cliqueAdicionarContato);
                Toast.makeText(this,"Falha ao tentar adicionar o contato a lista de contatos",Toast.LENGTH_LONG).show();
            }else if(responseCode == 1){
                this.menuItemAdicionarRemover.setTitle("Remover contato");
                this.menuItemAdicionarRemover.setOnMenuItemClickListener(this.cliqueRemoverContato);
                this.menuItemAdicionarRemover.setVisible(true);
                Toast.makeText(this,"Usuário adicionado a lista de contatos",Toast.LENGTH_LONG).show();
            }else if(responseCode == 2){
                Toast.makeText(this,(String)data,Toast.LENGTH_LONG).show();
            }
        }else if(REQUEST_REMOVER_CONTATO == requestCode){
            if(responseCode == 0){
                this.menuItemAdicionarRemover.setOnMenuItemClickListener(this.cliqueRemoverContato);
                Toast.makeText(this,"Falha ao tentar remover o contato da lista de contatos",Toast.LENGTH_LONG).show();
            }else if(responseCode == 1){
                this.menuItemAdicionarRemover.setTitle("Adicionar contato");
                this.menuItemAdicionarRemover.setOnMenuItemClickListener(this.cliqueAdicionarContato);
                this.menuItemAdicionarRemover.setVisible(true);
                Toast.makeText(this,"Usuário removido da lista de contatos",Toast.LENGTH_LONG).show();
            }else if(responseCode == 2){
                Toast.makeText(this,(String)data,Toast.LENGTH_LONG).show();
            }
        }
    }
}
