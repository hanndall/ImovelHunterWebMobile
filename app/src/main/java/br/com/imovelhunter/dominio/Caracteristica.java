package br.com.imovelhunter.dominio;


import java.io.Serializable;

import br.com.imovelhunter.util.ObjetoJSON;

public class Caracteristica extends ObjetoJSON<Caracteristica> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7879951292243680814L;


	private long idCaracteristica;
	

	private String nome;
	

	private String descricao;

	public long getIdCaracteristica() {
		return idCaracteristica;
	}

	public void setIdCaracteristica(long idCaracteristica) {
		this.idCaracteristica = idCaracteristica;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	
}
