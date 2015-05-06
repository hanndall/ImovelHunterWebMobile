package br.com.imovelhunter.dominio;

import java.io.Serializable;

import br.com.imovelhunter.util.ObjetoJSON;


public class Cidades extends ObjetoJSON<Cidades> implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7228501478539067824L;

	private long cd_cidade;

	private Uf uf;

	private String ds_cidade_nome;

	public long getCd_cidade() {
		return cd_cidade;
	}

	public void setCd_cidade(long cd_cidade) {
		this.cd_cidade = cd_cidade;
	}

	public Uf getUf() {
		return uf;
	}

	public void setUf(Uf uf) {
		this.uf = uf;
	}

	public String getDs_cidade_nome() {
		return ds_cidade_nome;
	}

	public void setDs_cidade_nome(String ds_cidade_nome) {
		this.ds_cidade_nome = ds_cidade_nome;
	}


}
