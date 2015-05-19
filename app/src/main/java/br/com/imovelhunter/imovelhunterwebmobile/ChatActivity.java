package br.com.imovelhunter.imovelhunterwebmobile;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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
import br.com.imovelhunter.tasks.TaskEnviarMensagem;
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

    private TaskEnviarMensagem taskEnviarMensagem;

    private MensagemDAO mensagemDAO;

    private Web web;

    private Intent in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        GcmBroadcastReceiver.setEscutadorDeMensagem(this);

        this.in = getIntent();

        this.mensagemDAO = new MensagemDAO(getApplicationContext());

        this.web = new WebImp();

        meuUsuario = (Usuario)SessionUtilJson.getInstance(this).getJsonObject(ParametrosSessaoJson.USUARIO_LOGADO,Usuario.class);
        usuarioChatAtual = (Usuario)in.getSerializableExtra(ParametrosSessao.USUARIO_CHAT_ATUAL.name());
        this.listaMensagem = new ArrayList<Mensagem>();


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

        android.support.v7.app.ActionBar bar = getSupportActionBar();
        bar.hide();
        if (bar != null) {
            bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#315e8a")));
        }




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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present..
        getMenuInflater().inflate(R.menu.chat, menu);

        this.menuItemBloqDesbloq = menu.getItem(0);

        this.menuItemBloqDesbloq.setOnMenuItemClickListener(cliqueBloquear);

        return true;
    }

    private MenuItem.OnMenuItemClickListener cliqueBloquear = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            return true;
        }
    };

    private MenuItem.OnMenuItemClickListener cliqueDesBloquear = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
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

        mensagemO.setLida(true);

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
                    .setSmallIcon(R.drawable.imovelhunterimgicone)
                    .setContentTitle("Imovel Hunter")
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

            }else{
                Toast.makeText(this,"Erro ao enviar a mensagem",Toast.LENGTH_LONG).show();
            }
        }
    }
}
