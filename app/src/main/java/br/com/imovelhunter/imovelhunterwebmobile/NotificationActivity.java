package br.com.imovelhunter.imovelhunterwebmobile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.imovelhunter.adapters.AdapterNotificacao;
import br.com.imovelhunter.dao.NotificacaoDAO;
import br.com.imovelhunter.dialogs.DialogAlerta;
import br.com.imovelhunter.dominio.Notificacao;
import br.com.imovelhunter.dominio.Usuario;
import br.com.imovelhunter.enums.Parametros;
import br.com.imovelhunter.enums.ParametrosSessaoJson;
import br.com.imovelhunter.fragments.FragmentListaContato;
import br.com.imovelhunter.util.SessionUtilJson;


public class NotificationActivity extends ActionBarActivity implements DialogAlerta.RespostaSim {


    private ListView listView;
    private AdapterNotificacao adapter;
    private List<Notificacao> listaNotificacaos;
    private DialogAlerta alerta;

    private Notificacao notificacaoSelecionada;

    private boolean vemMap;

    private Usuario usuarioLogado;

    private TextView texto;

    private final int REQUEST_SEGUROU_NOTIFICACAO = 1;

    private NotificacaoDAO notificacaoDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_notification);

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

        this.notificacaoDAO = new NotificacaoDAO(this);

        GcmBroadcastReceiver.setOnRecebeuNotificacao(this.recebeuNotificacao);

        if(!SessionUtilJson.getInstance(this).containsName(ParametrosSessaoJson.USUARIO_LOGADO)){
            Toast.makeText(this,"Usuário deve está logado",Toast.LENGTH_LONG).show();
            finish();
            return;
        }else{
            usuarioLogado = (Usuario)SessionUtilJson.getInstance(this).getJsonObject(ParametrosSessaoJson.USUARIO_LOGADO,Usuario.class);
        }


        Intent in = getIntent();
        vemMap = in.getBooleanExtra("map",false);

        this.listaNotificacaos = new ArrayList<Notificacao>();
        this.adapter = new AdapterNotificacao(this.listaNotificacaos);
        this.listView = (ListView)this.findViewById(R.id.listViewMensagem);
        this.listView.setAdapter(this.adapter);

        this.texto = (TextView)this.findViewById(R.id.textView2);


        List<Notificacao> list = this.notificacaoDAO.listarNotificacoesPeloUsuario(this.usuarioLogado);


        if(list != null && list.size() > 0){
            this.listaNotificacaos.clear();
            this.listaNotificacaos.addAll(list);
            this.adapter.notifyDataSetChanged();
            this.texto.setVisibility(View.GONE);
            this.listView.setVisibility(View.VISIBLE);
        }else{
            this.texto.setVisibility(View.VISIBLE);
            this.texto.setText("Nenhuma notificação recebida");
            this.listView.setVisibility(View.GONE);
        }

        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Notificacao not = listaNotificacaos.get(i);

                if(vemMap) {
                    Intent in = new Intent();

                    Bundle b = new Bundle();
                    b.putDouble("LATITUDE",not.getLatitude());
                    b.putDouble("LONGITUDE",not.getLongitude());

                    in.putExtras(b);

                    setResult(1, in);

                    finish();
                }else{

                    Intent in = new Intent(NotificationActivity.this,GcmActivity.class);

                    in.putExtra(Parametros.NOTIFICACAO.name(), not);

                    Bundle b = new Bundle();
                    b.putDouble("LATITUDE",not.getLatitude());
                    b.putDouble("LONGITUDE",not.getLongitude());
                    b.putBoolean("vemNotificacao", true);
                    b.putString("serial",getSerialNumber());

                    in.putExtras(b);

                    startActivity(in);

                    finish();
                }
            }
        });

        this.listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                notificacaoSelecionada = listaNotificacaos.get(i);

                alerta = new DialogAlerta(NotificationActivity.this, "Remover notificação?", "", REQUEST_SEGUROU_NOTIFICACAO, "Sim", "Não");
                alerta.exibirDialog();

                return true;
            }
        });

    }

    @Override
    protected void onDestroy() {
        GcmBroadcastReceiver.setOnRecebeuNotificacao(null);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_notification, menu);
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
    }

    @Override
    public void respondeuSim(int requestCode) {
        if(requestCode == REQUEST_SEGUROU_NOTIFICACAO){
            if(notificacaoDAO.removerNotificacao(notificacaoSelecionada)) {
                listaNotificacaos.remove(notificacaoSelecionada);
                adapter.notifyDataSetChanged();
                Toast.makeText(this, "Notificação removida com sucesso", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this, "Não foi possível remover a notificação", Toast.LENGTH_LONG).show();
            }
        }
    }

    private String getSerialNumber(){
        TelephonyManager telemamanger = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String getSimSerialNumber = telemamanger.getSimSerialNumber();

        if(getSimSerialNumber == null){
            getSimSerialNumber = telemamanger.getDeviceId();

            if(getSimSerialNumber == null){
                getSimSerialNumber = Build.SERIAL;
            }
        }

        return getSimSerialNumber;
    }

    private OnRecebeuNotificacao recebeuNotificacao = new OnRecebeuNotificacao() {
        @Override
        public void recebeuNotificacao() {

            List<Notificacao> list = null;

            list = notificacaoDAO.listarNotificacoesPeloUsuario(usuarioLogado);

            if(list != null && list.size() > 0){
                listaNotificacaos.clear();
                listaNotificacaos.addAll(list);
                adapter.notifyDataSetChanged();
                texto.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            }else{
                listView.setVisibility(View.GONE);
                texto.setVisibility(View.VISIBLE);
                texto.setText("Nenhuma notificação recebida");
            }
            Toast.makeText(NotificationActivity.this,"Nova notificação",Toast.LENGTH_LONG).show();
        }
    };

    public interface OnRecebeuNotificacao{
        public void recebeuNotificacao();
    }

}
