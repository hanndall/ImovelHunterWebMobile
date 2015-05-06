package br.com.imovelhunter.enums;

public enum TipoImovel {
	CASA 				("Casa"), 
	APARTAMENTO 		("Apartamento"), 
	APARTAMENTO_DUPLEX 	("Apartamento Duplex"), 
	TERRENO 			("Terreno"), 
	AREA 				("�rea"), 
	BARRACO	 			("Barraco"),
	CHACARA  			("Ch�cara"),
	COBERTURA  			("Cobertura"),
	FLAT  				("Flat"),
	GALPAO  			("Galp�o"),
	KITNET  			("Kitnet"),
	LOFT  				("Loft"),
	PREDIO  			("Pr�dio"),
	SALA  				("Sala"),
	SALAO  				("Sal�o"),
	SITIO  				("S�tio");
	
	private TipoImovel(String nome){
		this.nome = nome;
	}
	
	private final String nome;

	public String getNome() {
		return nome;
	}
	
}
