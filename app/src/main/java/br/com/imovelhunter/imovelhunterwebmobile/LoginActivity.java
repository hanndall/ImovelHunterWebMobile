package br.com.imovelhunter.imovelhunterwebmobile;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

import java.nio.charset.Charset;

import br.com.imovelhunter.dominio.Anunciante;
import br.com.imovelhunter.dominio.Cliente;
import br.com.imovelhunter.dominio.Usuario;
import br.com.imovelhunter.enums.ParametrosSessao;
import br.com.imovelhunter.listeners.OnFinishTask;
import br.com.imovelhunter.tasks.TaskLogar;
import br.com.imovelhunter.web.Web;
import br.com.imovelhunter.web.WebImp;


public class LoginActivity extends ActionBarActivity implements OnFinishTask {

    private EditText editTextLogin;
    private EditText editTextSenha;
    private Button buttonLogar;

    private ProgressDialog progress;

    private Web web;

    private String gcm;

    private String serial;

    private TaskLogar taskLogar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        android.support.v7.app.ActionBar bar = getSupportActionBar();
        bar.hide();
        if (bar != null) {
            bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#315e8a")));
        }

        Intent in = getIntent();

        this.web = new WebImp();

        gcm = (String)in.getSerializableExtra(ParametrosSessao.GCM.name());
        serial = (String)in.getSerializableExtra(ParametrosSessao.SERIAL.name());

        this.progress = new ProgressDialog(this);
        this.progress.setIcon(R.drawable.imovelhunterimgicone);
        this.progress.setMessage("Processando");
        this.progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                if(taskLogar != null){
                    taskLogar.cancel(true);
                }
            }
        });

        this.editTextLogin = (EditText)this.findViewById(R.id.editTextLogin);
        this.editTextSenha = (EditText)this.findViewById(R.id.editTextSenha);

        this.buttonLogar = (Button)this.findViewById(R.id.buttonLogar);
        this.buttonLogar.setOnClickListener(this.cliqueBotaoLogar);

    }

    private View.OnClickListener cliqueBotaoLogar = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            progress.show();
            taskLogar = new TaskLogar(1,LoginActivity.this);
            taskLogar.execute(web,gcm,serial,editTextLogin.getText().toString(),editTextSenha.getText().toString());
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.login, menu);
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
        if(requestCode == 1){
            if(responseCode == 0){
                Toast.makeText(this,"Erro ao fazer o login",Toast.LENGTH_LONG).show();
            }else if(responseCode == 1){
                if(data != null) {
                    Usuario usuario = (Usuario)data;
                    Intent in = new Intent();
                    in.putExtra(ParametrosSessao.USUARIO_LOGADO.name(),usuario);

                    setResult(1,in);
                    finish();
                }else{
                    Toast.makeText(this,"Usuário não encontrado",Toast.LENGTH_LONG).show();
                }
            }else if(responseCode == 2){
                String mensagem = (String)data;
                Charset.forName("UTF-8").encode(mensagem);
                Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show();
            }
        }
        progress.dismiss();
    }
}
