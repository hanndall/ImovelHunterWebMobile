package br.com.imovelhunter.enums;

public enum SituacaoImovel {
	VENDA ("Venda"), 
	LOCACAO ("Loca��o"), 
	VENDIDO ("Vendido"), 
	LOCADO ("Locado"), 
	DESATIVADO ("Desativado");
	
	private SituacaoImovel(String nome){
		this.nome = nome;
	}
	
	private final String nome;

	public String getNome() {
		return nome;
	}
	
}
