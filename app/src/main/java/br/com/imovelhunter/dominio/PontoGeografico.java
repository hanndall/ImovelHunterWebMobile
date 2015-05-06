package br.com.imovelhunter.dominio;


import java.io.Serializable;

import br.com.imovelhunter.util.ObjetoJSON;

public class PontoGeografico extends ObjetoJSON<PontoGeografico> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2078039141469619425L;


	private long idPontoGeografico;
	

	private double longitude;
	

	private double latitude;

	public long getIdPontoGeografico() {
		return idPontoGeografico;
	}

	public void setIdPontoGeografico(long idPontoGeografico) {
		this.idPontoGeografico = idPontoGeografico;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
}
