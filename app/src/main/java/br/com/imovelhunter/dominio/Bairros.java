package br.com.imovelhunter.dominio;

import java.io.Serializable;

import br.com.imovelhunter.util.ObjetoJSON;


public class Bairros extends ObjetoJSON<Bairros> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6472242751031257781L;


	private long cd_bairro;

	private Cidades cidade;
		

	private String ds_bairro_nome;

	public long getCd_bairro() {
		return cd_bairro;
	}

	public void setCd_bairro(long cd_bairro) {
		this.cd_bairro = cd_bairro;
	}

	public Cidades getCidade() {
		return cidade;
	}

	public void setCidade(Cidades cidade) {
		this.cidade = cidade;
	}

	public String getDs_bairro_nome() {
		return ds_bairro_nome;
	}

	public void setDs_bairro_nome(String ds_bairro_nome) {
		this.ds_bairro_nome = ds_bairro_nome;
	}
	
	
	


}
