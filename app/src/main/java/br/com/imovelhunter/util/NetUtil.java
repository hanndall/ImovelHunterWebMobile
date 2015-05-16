package br.com.imovelhunter.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by Washington Luiz on 10/05/2015.
 */
public class NetUtil {

    private Context context;

    public NetUtil(Context context){
        this.context = context;
    }

    private Boolean verificaInternet(){
        ConnectivityManager cm = (ConnectivityManager)this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }else{
            Toast.makeText(this.context, "Sem conexão com a internet", Toast.LENGTH_LONG).show();
            return false;
        }
    }


}
