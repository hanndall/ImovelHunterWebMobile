package br.com.imovelhunter.imovelhunterwebmobile;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;

import br.com.imovelhunter.enums.ParametrosSessao;
import br.com.imovelhunter.listeners.OnFinishTask;
import br.com.imovelhunter.tasks.TaskCadastroGCM;
import br.com.imovelhunter.util.SessionUtil;
import br.com.imovelhunter.web.WebImp;


public class MainActivity extends ActionBarActivity implements OnFinishTask {

    private String chaveGcm;

    private String serial;

    private Handler hand;

    private Intent intentMapa;


    /*
    * ActionBar bar = getActionBar();
        if (bar != null) {
            bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FF9305")));
        }

        if(getResources().getBoolean(R.bool.tablet)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        else if(getResources().getBoolean(R.bool.smart)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    * */

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //carregar a tela de splash aqui

        ActionBar bar = getActionBar();
        bar.hide();
        if (bar != null) {
            bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#315e8a")));
        }

        Intent intent = getIntent();

        this.chaveGcm = intent.getStringExtra("gcm");

        this.serial = this.getSerialNumber();

        SessionUtil.setObject(ParametrosSessao.GCM,chaveGcm);
        SessionUtil.setObject(ParametrosSessao.SERIAL,serial);

        new TaskCadastroGCM(1,this).execute(new WebImp(),this.chaveGcm,this.serial);

        this.intentMapa = new Intent(this,MapaActivity.class);
        intentMapa.putExtra("gcm",this.chaveGcm);
        intentMapa.putExtra("serial",this.serial);

        this.hand = new Handler();
    }

    private Runnable irParaOMapa = new Runnable() {
        @Override
        public void run() {
            SessionUtil.setObject(ParametrosSessao.WEB,new WebImp());
            startActivity(intentMapa);
            MainActivity.this.finish();
        }
    };

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
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
        this.hand.postDelayed(this.irParaOMapa,3000);
    }
}
