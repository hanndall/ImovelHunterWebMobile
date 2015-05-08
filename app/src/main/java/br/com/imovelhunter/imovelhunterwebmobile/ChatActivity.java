package br.com.imovelhunter.imovelhunterwebmobile;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
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
import br.com.imovelhunter.enums.ParametrosSessao;
import br.com.imovelhunter.listeners.EscutadorDeMensagem;
import br.com.imovelhunter.listeners.OnFinishTask;
import br.com.imovelhunter.tasks.TaskEnviarMensagem;
import br.com.imovelhunter.util.SessionUtil;


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

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        this.mensagemDAO = new MensagemDAO(getApplicationContext());

        meuUsuario = (Usuario)SessionUtil.getObject(ParametrosSessao.USUARIO_LOGADO);
        usuarioChatAtual = (Usuario)SessionUtil.getObject(ParametrosSessao.USUARIO_CHAT_ATUAL);
        this.listaMensagem = new ArrayList<Mensagem>();

        List<Mensagem> mensagensDoDia = mensagemDAO.listarMensagemDoDiaDaData(new Date());

        listaMensagem.addAll(mensagensDoDia);

        this.adapterMensagem = new AdapterMensagem(this.listaMensagem,this.meuUsuario);

        GcmBroadcastReceiver.setEscutadorDeMensagem(this);

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
                //TODO fazer ele persistir no banco do aplicativo que você já leu essa mensagem antes de enviar com o atribudo de  lida false
                novaMensagem.setLida(true);
                mensagemDAO.inserirMensagem(novaMensagem);
                novaMensagem.setLida(false);


                listaMensagem.add(novaMensagem);



                adapterMensagem.notifyDataSetChanged();



                taskEnviarMensagem = new TaskEnviarMensagem(1,ChatActivity.this);
                taskEnviarMensagem.execute(SessionUtil.getObject(ParametrosSessao.WEB),novaMensagem);
            }
        });

        ActionBar bar = getActionBar();
        bar.hide();
        if (bar != null) {
            bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#315e8a")));
        }




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat, menu);
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
    public void recebeuAlgo(Object mensagem) {

        Bundle bundle = (Bundle)mensagem;

        String mensagemS = bundle.getString("mensagem");

        Mensagem mensagemO = new Mensagem();

        mensagemO.parse(mensagemS);

        mensagemO.setLida(true);

        listaMensagem.add(mensagemO);

        adapterMensagem.notifyDataSetChanged();

        mensagemDAO.inserirMensagem(mensagemO);

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
