package br.com.imovelhunter.dominio;

import java.io.Serializable;

import br.com.imovelhunter.util.ObjetoJSON;


public class Uf extends ObjetoJSON<Cliente> implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6049937385283437683L;


	private long cd_uf;

	private String ds_uf_sigla;

	private String ds_uf_nome;

	public long getCd_uf() {
		return cd_uf;
	}

	public void setCd_uf(long cd_uf) {
		this.cd_uf = cd_uf;
	}

	public String getDs_uf_sigla() {
		return ds_uf_sigla;
	}

	public void setDs_uf_sigla(String ds_uf_sigla) {
		this.ds_uf_sigla = ds_uf_sigla;
	}

	public String getDs_uf_nome() {
		return ds_uf_nome;
	}

	public void setDs_uf_nome(String ds_uf_nome) {
		this.ds_uf_nome = ds_uf_nome;
	}
	
	

}
