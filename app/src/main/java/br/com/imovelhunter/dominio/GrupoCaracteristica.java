package br.com.imovelhunter.dominio;


import java.io.Serializable;
import java.util.List;

import br.com.imovelhunter.util.ObjetoJSON;

public class GrupoCaracteristica extends ObjetoJSON<GrupoCaracteristica> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1867080893607777384L;


	private long idGrupoCaracteristica;

	private String nome;

	private String descricao;

	private List<Caracteristica> caracteristicas;

	public long getIdGrupoCaracteristica() {
		return idGrupoCaracteristica;
	}

	public void setIdGrupoCaracteristica(long idGrupoCaracteristica) {
		this.idGrupoCaracteristica = idGrupoCaracteristica;
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

	public List<Caracteristica> getCaracteristicas() {
		return caracteristicas;
	}

	public void setCaracteristicas(List<Caracteristica> caracteristicas) {
		this.caracteristicas = caracteristicas;
	}
	
	
	
}
