package br.com.imovelhunter.imovelhunterwebmobile;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ProgressDialog;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import br.com.imovelhunter.dominio.Cliente;
import br.com.imovelhunter.enums.ParametrosSessao;
import br.com.imovelhunter.enums.TipoUsuario;
import br.com.imovelhunter.listeners.OnFinishTask;
import br.com.imovelhunter.tasks.TaskCadastrarCliente;
import br.com.imovelhunter.util.SessionUtil;
import br.com.imovelhunter.web.Web;


public class CadastroClienteActivity extends ActionBarActivity implements OnFinishTask {

    private EditText editTextNome;
    private EditText editTexSobrenome;
    private EditText editTexLogin;
    private EditText editTexSenha;
    private EditText editTexConfirmarSenha;
    private EditText editTexEmail;
    private EditText editTexDataDeNascimento;
    private Button buttonCadastrar;

    private ProgressDialog progress;

    private Cliente cliente;

    private Web web;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_cliente);

        ActionBar bar = getActionBar();
        bar.hide();
        if (bar != null) {
            bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#315e8a")));
        }

        this.progress = new ProgressDialog(this);
        this.progress.setIcon(R.drawable.imovelhunterimgicone);
        this.progress.setMessage("Processando");

        this.web = (Web)SessionUtil.getObject(ParametrosSessao.WEB);

        this.editTextNome = (EditText)this.findViewById(R.id.edtTextNome);
        this.editTexSobrenome = (EditText)this.findViewById(R.id.edtTextSobreNome);
        this.editTexLogin = (EditText)this.findViewById(R.id.edtTxtLogin);
        this.editTexSenha = (EditText)this.findViewById(R.id.edtTxtSenha);
        this.editTexConfirmarSenha = (EditText)this.findViewById(R.id.edTxtConfirmarSenha);
        this.editTexEmail = (EditText)this.findViewById(R.id.edtTxtEmail);
        this.editTexDataDeNascimento = (EditText)this.findViewById(R.id.edtTxtDataNascimento);
        this.buttonCadastrar = (Button)this.findViewById(R.id.buttonCadastrarCliente);

        this.buttonCadastrar.setOnClickListener(this.clickBotaoCadastrar);

    }

    private View.OnClickListener clickBotaoCadastrar = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(validarCampos()){
                progress.show();
                cliente = newCliente();

                new TaskCadastrarCliente(1,CadastroClienteActivity.this).execute(web,cliente,SessionUtil.getObject(ParametrosSessao.SERIAL));
            }

        }
    };

    private boolean validarCampos(){


        return true;
    }

    private SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    private Cliente newCliente(){
        Cliente cliente = new Cliente();



        cliente.setNome(this.editTextNome.getText().toString());
        cliente.setSobreNome(this.editTexSobrenome.getText().toString());
        cliente.setLogin(this.editTexLogin.getText().toString());
        cliente.setSenha(this.editTexSenha.getText().toString());
        cliente.setEmail(this.editTexEmail.getText().toString());
        try {
            cliente.setDataDeNascimento(format.parse(this.editTexDataDeNascimento.getText().toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return cliente;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cadastro_cliente, menu);
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
                Toast.makeText(this,"Erro ao realizar o cadastro",Toast.LENGTH_LONG).show();
            }else if(responseCode == 1){
                cliente = (Cliente)data;
                cliente.setTipoUsuario(TipoUsuario.USUARIO);
                Toast.makeText(this,"Cadastro realizado com sucesso",Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.putExtra("clienteCadastrado",cliente);
                setResult(1, intent);
                finish();
            }else if(responseCode == 2){
                Toast.makeText(this,(String)data,Toast.LENGTH_LONG).show();
            }
        }

        progress.dismiss();
    }
}
