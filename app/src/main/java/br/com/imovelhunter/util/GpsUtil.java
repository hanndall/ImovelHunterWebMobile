package br.com.imovelhunter.util;

import android.content.Context;
import android.content.Intent;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.widget.Toast;

import br.com.imovelhunter.dominio.Localizacao;
import br.com.imovelhunter.enums.TipoGps;
import br.com.imovelhunter.listeners.LocalizacaoListener;


public class GpsUtil implements LocalizacaoListener.OnLocationChange {

    private Context contexto;
    private LocalizacaoListener loc;
    private LocationManager manager;

    private OnCoordenadaRecebida eventoCoordenada;
    private OnGpsStateChange eventoMudouEstadoDoGps;

    private GpsStatus.Listener listenerGpsStatus;

    public final static int GPS_REQUISICAO = 1;

    public GpsUtil(Context context){
        this.contexto = context;

        //Caso o contexto implemente o evento do recebimento de coordenada, ele já implementa a interface.
        if(this.contexto instanceof OnCoordenadaRecebida){
            this.eventoCoordenada = (OnCoordenadaRecebida)this.contexto;
        }
        //Caso o contexto implemente o evento da mudança de estado do gps, ele irá avisar a quem estiver implementando
        if(this.contexto instanceof  OnGpsStateChange){
            this.eventoMudouEstadoDoGps = (OnGpsStateChange)this.contexto;
        }





        this.listenerGpsStatus = new GpsStatus.Listener() {
            @Override
            public void onGpsStatusChanged(int event) {
                if(event == GpsStatus.GPS_EVENT_STARTED){
                    if(eventoMudouEstadoDoGps != null)
                    eventoMudouEstadoDoGps.mudouEstado(true);
                }else if(event == GpsStatus.GPS_EVENT_STOPPED){
                    if(eventoMudouEstadoDoGps != null)
                    eventoMudouEstadoDoGps.mudouEstado(false);
                }else if(event == GpsStatus.GPS_EVENT_SATELLITE_STATUS){

                }
            }
        };



    }

    /**
     * Coloca o observador de estado do gps
     */
    public void ativarGpsEstadoListener(){
        this.manager.addGpsStatusListener(this.listenerGpsStatus);
    }

    /**
     * Remove o observador de estado do gps
     */
    public void removerGpsEstadoListener(){
        this.manager.removeGpsStatusListener(this.listenerGpsStatus);
    }

    /**
     * Ativa os listeners
     */
    public void ativarListeners(){

        this.manager = (LocationManager)this.contexto.getSystemService(Context.LOCATION_SERVICE);

        this.manager.addGpsStatusListener(this.listenerGpsStatus);

        loc = LocalizacaoListener.getInstance();

        loc.setOnLocationChange(this);

        //Verifica se tem internet
        if(this.verificaInternet()){

            //Usando gp pela internet
            this.manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,loc);




            if (this.manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {


                try {
                    //Tá funcionando
                    //new GetLocationTask("AvLiberdade440RecifePernambuco",this.contexto).execute();
                }
                catch(Exception ex){
                    System.out.println("Rua não foi encontrada");
                }


                //Toast.makeText(this.contexto,"Gps conectado", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this.contexto,"Gps desconectado", Toast.LENGTH_LONG).show();
            }


        }else{
            //Usando o gps pelo dispositivo do gps
            this.manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,loc);

            if (this.manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                try {
                    //Tá funcionando
                    //new GetLocationTask("AvLiberdade440RecifePernambuco",this.contexto).execute();
                }
                catch(Exception ex){
                    System.out.println("Rua não foi encontrada");
                }


                //Toast.makeText(this.contexto,"Gps conectado", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this.contexto,"Gps desconectado", Toast.LENGTH_LONG).show();
            }


        }




    }

    /**
     * Para de ficar observando se chega coordenada do gps
     */
    public void desativarListener(){
        //Retira o listener
        this.manager.removeUpdates(this.loc);
    }

    public void mudarTipoGps(TipoGps tipo){

        this.desativarListener();


        this.manager = (LocationManager)this.contexto.getSystemService(Context.LOCATION_SERVICE);

        this.manager.addGpsStatusListener(this.listenerGpsStatus);

        loc = LocalizacaoListener.getInstance();

        loc.setOnLocationChange(this);

        //Seta gps do tipo network
        if(tipo.equals(TipoGps.INTERNET)){

            //Usando gp pela internet
            this.manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,loc);


            if (this.manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                //Toast.makeText(this.contexto,"Gps conectado", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this.contexto,"Sem conexão com internet", Toast.LENGTH_LONG).show();
            }


        }else{
            //Usando o gps pelo dispositivo do gps
            this.manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,loc);

            if (this.manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                //Toast.makeText(this.contexto,"Gps conectado", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this.contexto,"Gps da internet desconectado", Toast.LENGTH_LONG).show();
            }
        }

    }


    /**
     * Verifica se tem internet
     * @return
     */
    private Boolean verificaInternet(){
        ConnectivityManager cm = (ConnectivityManager)this.contexto.getSystemService(Context.CONNECTIVITY_SERVICE);


        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }else{
            return false;
        }
    }





    /**
     * Liga o gps
     */
    public void ligarGPS(){

        try {

            //Acima da versão android 3.0
            if (android.os.Build.VERSION.SDK_INT > 11) {
                final Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
                intent.putExtra("enabled", true);
                this.contexto.sendBroadcast(intent);
            }

            //Abaixo da versão android 3.0
            else{
                String provider = Settings.Secure.getString(
                        this.contexto.getContentResolver(),
                        Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
                if (!provider.contains("gps")) { // if gps is enabled
                    final Intent poke = new Intent();
                    poke.setClassName("com.android.settings",
                            "com.android.settings.widget.SettingsAppWidgetProvider");
                    poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
                    poke.setData(Uri.parse("3"));
                    this.contexto.sendBroadcast(poke);
                }
            }
        }
        catch (Exception e) {
            Toast.makeText(this.contexto,"Erro ao tentar ligar o gps", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

    /**
     * Desliga o gps
     */
    public void desligarGPS(){
        try {
            //Acima da versão android 3.0
            if (android.os.Build.VERSION.SDK_INT > 11) {
                final Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
                intent.putExtra("enabled", false);
                this.contexto.sendBroadcast(intent);
            }

            //Abaixo da versão android 3.0
            else{
                String provider = Settings.Secure.getString(
                        this.contexto.getContentResolver(),
                        Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
                if (provider.contains("gps")) { // if gps is enabled
                    final Intent poke = new Intent();
                    poke.setClassName("com.android.settings","com.android.settings.widget.SettingsAppWidgetProvider");
                    poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
                    poke.setData(Uri.parse("3"));
                    this.contexto.sendBroadcast(poke);
                }
            }
        }
        catch (Exception e) {
            Toast.makeText(this.contexto,"Erro ao tentar desconectar o gps", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    /**
     * Listener que avisa quando recebe uma nova coordenada
     */
    public interface OnCoordenadaRecebida{
        public void chegouCoordenada(Localizacao localizacao);
    }

    /**
     * Listener que avisa quando o estado do gps é modificado
     */
    public interface OnGpsStateChange{
        public void mudouEstado(Boolean estado);
    }


    /**
     * Seta o listener, não está sendo utilizado pois já é passado no parâmetro do contexto assim que é instanciado, caso ele o implemente
     * @param listenerRecebeCoordenada
     */
    @Deprecated
    public void setOnCoordenadaRecebida(OnCoordenadaRecebida listenerRecebeCoordenada){
        this.eventoCoordenada = listenerRecebeCoordenada;
    }

    /**
     * Quando recebe a coordenada no listener do gps, ele avisa para o seu observador que por sua vez avisará para quem o estiver implementando
     * @param longitude
     * @param latitude
     */
    @Override
    public void mudouCoordenada(double longitude, double latitude) {

        if(this.eventoCoordenada != null){

            Localizacao loc = new Localizacao();
            loc.setLongitude(longitude);
            loc.setLatitude(latitude);
            this.eventoCoordenada.chegouCoordenada(loc);

        }

    }


}
