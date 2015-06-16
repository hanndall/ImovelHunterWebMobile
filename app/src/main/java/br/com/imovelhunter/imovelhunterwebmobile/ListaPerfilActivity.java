package br.com.imovelhunter.imovelhunterwebmobile;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.imovelhunter.adapters.AdapterListPerfil;
import br.com.imovelhunter.dialogs.DialogAlerta;
import br.com.imovelhunter.dominio.Perfil;
import br.com.imovelhunter.dominio.Usuario;
import br.com.imovelhunter.enums.ParametrosSessaoJson;
import br.com.imovelhunter.listeners.OnFinishTask;
import br.com.imovelhunter.tasks.TaskListarPerfisCadastrado;
import br.com.imovelhunter.tasks.TaskRemoverPerfilCadastrado;
import br.com.imovelhunter.util.NetUtil;
import br.com.imovelhunter.util.SessionUtilJson;
import br.com.imovelhunter.web.Web;
import br.com.imovelhunter.web.WebImp;


public class ListaPerfilActivity extends ActionBarActivity implements OnFinishTask,DialogAlerta.RespostaSim {

    private ListView listView;
    private List<Perfil> listaPerfil;
    private AdapterListPerfil adapter;
    private TaskListarPerfisCadastrado taskListarPerfisCadastrado;
    private TaskRemoverPerfilCadastrado taskRemoverPerfilCadastrado;
    private Usuario usuarioLogado;
    private NetUtil netUtil;
    private Web web;
    private Perfil perfilSelecionado;
    private DialogAlerta dialog;



    private final int REQUEST_LISTAR_PERFIL = 1;
    private final int REQUEST_REMOVER_PERFIL = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_perfil);


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

        this.usuarioLogado = (Usuario) SessionUtilJson.getInstance(this).getJsonObject(ParametrosSessaoJson.USUARIO_LOGADO,Usuario.class);

        if(this.usuarioLogado == null){
            Toast.makeText(this,"Usuário não está logado",Toast.LENGTH_LONG).show();
            finish();
            return;
        }


        this.netUtil = new NetUtil(this);

        this.listaPerfil = new ArrayList<Perfil>();
        this.adapter = new AdapterListPerfil(this.listaPerfil);

        this.listView = (ListView)this.findViewById(R.id.listView);
        this.listView.setAdapter(this.adapter);

        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(taskRemoverPerfilCadastrado == null || !taskRemoverPerfilCadastrado.getStatus().equals(AsyncTask.Status.RUNNING)) {
                    if (netUtil.verificaInternet()) {
                        perfilSelecionado = listaPerfil.get(i);
                        dialog = new DialogAlerta(ListaPerfilActivity.this, "Deseja remover o perfil cadastrado?", "", REQUEST_REMOVER_PERFIL, "SIM", "NÃO");
                        dialog.exibirDialog();
                    }
                }
            }
        });


        if(this.netUtil.verificaInternet()){
            this.web = new WebImp();

            this.taskListarPerfisCadastrado = new TaskListarPerfisCadastrado(REQUEST_LISTAR_PERFIL,this);

            this.taskListarPerfisCadastrado.execute(web,this.usuarioLogado);

        }else{
            finish();
            return;
        }

    }

    private MenuItem menuAtualiza;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lista_perfil, menu);

        this.menuAtualiza = menu.getItem(0);

        this.menuAtualiza.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                if(netUtil.verificaInternet()){

                    if(taskListarPerfisCadastrado == null || !taskListarPerfisCadastrado.getStatus().equals(AsyncTask.Status.RUNNING)) {

                        taskListarPerfisCadastrado = new TaskListarPerfisCadastrado(REQUEST_LISTAR_PERFIL, ListaPerfilActivity.this);

                        taskListarPerfisCadastrado.execute(web, usuarioLogado);
                    }

                }

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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish(int requestCode, int responseCode, Object data) {
        if(requestCode == REQUEST_LISTAR_PERFIL){
            if(responseCode == 1){

                List<Perfil> lista = (List<Perfil>)data;
                this.listaPerfil.clear();
                this.listaPerfil.addAll(lista);
                this.adapter.notifyDataSetChanged();


            }else if(responseCode == 0){
                Toast.makeText(this,"Falha na conexão com o servidor",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this,(String)data,Toast.LENGTH_LONG).show();
            }
        }else if(requestCode == REQUEST_REMOVER_PERFIL){
            if(responseCode == 1){

                this.listaPerfil.remove(perfilSelecionado);
                this.adapter.notifyDataSetChanged();


            }else if(responseCode == 0){
                Toast.makeText(this,"Falha na conexão com o servidor",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this,(String)data,Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void respondeuSim(int requestCode) {
        if(REQUEST_REMOVER_PERFIL == requestCode){
            if(this.perfilSelecionado != null) {
                this.taskRemoverPerfilCadastrado = new TaskRemoverPerfilCadastrado(REQUEST_REMOVER_PERFIL, this);
                this.taskRemoverPerfilCadastrado.execute(this.web, this.perfilSelecionado);
            }else{
                Toast.makeText(this,"Nenhum perfil selecionado",Toast.LENGTH_LONG).show();
            }
        }
    }
}
