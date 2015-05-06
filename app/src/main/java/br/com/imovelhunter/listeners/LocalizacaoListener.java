package br.com.imovelhunter.listeners;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class LocalizacaoListener implements LocationListener {

    public double longitude;

    public double latitude;

    private static LocalizacaoListener locListener;

    private OnLocationChange eventoMudancaLocalizacao;

    private LocalizacaoListener(){

    }

    public static LocalizacaoListener getInstance(){

        if(locListener == null){
            locListener = new LocalizacaoListener();
        }

        return locListener;
    }

    @Override
    public void onLocationChanged(Location location) {

        longitude = location.getLongitude();
        latitude = location.getLatitude();

        if(this.eventoMudancaLocalizacao != null){
            this.eventoMudancaLocalizacao.mudouCoordenada(this.longitude,this.latitude);
        }




    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public void setOnLocationChange(OnLocationChange eventoMudancaLocalizacao){
        this.eventoMudancaLocalizacao = eventoMudancaLocalizacao;
    }

    public interface OnLocationChange{
        public void mudouCoordenada(double longitude, double latitude);
    }
}
