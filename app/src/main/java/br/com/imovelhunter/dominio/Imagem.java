package br.com.imovelhunter.dominio;


import java.io.Serializable;

import br.com.imovelhunter.util.ObjetoJSON;

public class Imagem extends ObjetoJSON<Imagem> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3305441889753631178L;


	private long idImagem;
	
	private String nome;
	
	private String descricao;
	
	private String caminhoImagem;

	private Imovel imovel;

	public Imagem(){
		
	}
	
	public Imagem(String caminhoImagem, Imovel imovel){
		this.caminhoImagem = caminhoImagem;
		this.imovel = imovel;
	}

	public Imagem(String nome, String descricao, String caminhoImagem,
			Imovel imovel) {
		this.nome = nome;
		this.descricao = descricao;
		this.caminhoImagem = caminhoImagem;
		this.imovel = imovel;
	}

	public long getIdImagem() {
		return idImagem;
	}

	public void setIdImagem(long idImagem) {
		this.idImagem = idImagem;
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

	public String getCaminhoImagem() {
		return caminhoImagem;
	}

	public void setCaminhoImagem(String caminhoImagem) {
		this.caminhoImagem = caminhoImagem;
	}

	public Imovel getImovel() {
		return imovel;
	}

	public void setImovel(Imovel imovel) {
		this.imovel = imovel;
	}
	
	
	
}
