package br.com.imovelhunter.dominio;


import java.io.Serializable;

import br.com.imovelhunter.enums.TipoContato;
import br.com.imovelhunter.util.ObjetoJSON;

public class Contato extends ObjetoJSON<Contato> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6237409989577921090L;


	private long idContato;
	

	private Anunciante anunciante;

	private Imovel imovel;
	

	private String numero;
	

	private TipoContato tipoContato;

	public long getIdContato() {
		return idContato;
	}

	public void setIdContato(long idContato) {
		this.idContato = idContato;
	}

	public Anunciante getAnunciante() {
		return anunciante;
	}

	public void setAnunciante(Anunciante anunciante) {
		this.anunciante = anunciante;
	}

	public Imovel getImovel() {
		return imovel;
	}

	public void setImovel(Imovel imovel) {
		this.imovel = imovel;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public TipoContato getTipoContato() {
		return tipoContato;
	}

	public void setTipoContato(TipoContato tipoContato) {
		this.tipoContato = tipoContato;
	}
	
	
	
	
}
