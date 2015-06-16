package br.com.imovelhunter.dominio;

import java.io.Serializable;
import java.util.List;

import br.com.imovelhunter.enums.SituacaoImovel;
import br.com.imovelhunter.enums.TipoImovel;
import br.com.imovelhunter.util.ObjetoJSON;

public class Imovel extends ObjetoJSON<Imovel> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 369610495863986782L;


	private long idImovel;

	private List<Imagem> imagens;
	

	private String numeroDoImovel;


	private int numeroDeBanheiros;
	

	private int numeroDeQuartos;
	

	private int ambientes;
	

	private String nomeDoProprietario;
	

	private String descricao;
	

	private int numeroDeSuites;
	

	private SituacaoImovel situacaoImovel;
	

	private double areaTotal;
	

	private double preco;
	

	private String codigoDeReferencia;
	

	private int numeroDeSalas;
	

	private String logradouro; 
	

	private String bairro;
	

	private String cidade;
	

	private String estado;
	

	private String cep;
	

	private String pais;
	

	private String complemento;
	

	private TipoImovel tipoImovel;


	private Anunciante anunciante;


    private long idUsuarioNotificacao;

	private List<Caracteristica> caracteristicas;
	

	private PontoGeografico pontoGeografico;
	

	private List<PontoGeografico> cobertura;	
	
	public long getIdImovel() {
		return idImovel;
	}

	public void setIdImovel(long idImovel) {
		this.idImovel = idImovel;
	}

	public String getNumeroDoImovel() {
		return numeroDoImovel;
	}

	public void setNumeroDoImovel(String numeroDoImovel) {
		this.numeroDoImovel = numeroDoImovel;
	}

	public int getNumeroDeBanheiros() {
		return numeroDeBanheiros;
	}

	public void setNumeroDeBanheiros(int numeroDeBanheiros) {
		this.numeroDeBanheiros = numeroDeBanheiros;
	}

	public int getNumeroDeQuartos() {
		return numeroDeQuartos;
	}

	public void setNumeroDeQuartos(int numeroDeQuartos) {
		this.numeroDeQuartos = numeroDeQuartos;
	}

	public int getAmbientes() {
		return ambientes;
	}

	public void setAmbientes(int ambientes) {
		this.ambientes = ambientes;
	}

	public String getNomeDoProprietario() {
		return nomeDoProprietario;
	}

	public void setNomeDoProprietario(String nomeDoProprietario) {
		this.nomeDoProprietario = nomeDoProprietario;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public int getNumeroDeSuites() {
		return numeroDeSuites;
	}

	public void setNumeroDeSuites(int numeroDeSuites) {
		this.numeroDeSuites = numeroDeSuites;
	}

	public SituacaoImovel getSituacaoImovel() {
		return situacaoImovel;
	}

	public void setSituacaoImovel(SituacaoImovel situacaoImovel) {
		this.situacaoImovel = situacaoImovel;
	}

	public double getAreaTotal() {
		return areaTotal;
	}

	public void setAreaTotal(double areaTotal) {
		this.areaTotal = areaTotal;
	}

	public double getPreco() {
		return preco;
	}

	public void setPreco(double preco) {
		this.preco = preco;
	}

	public String getCodigoDeReferencia() {
		return codigoDeReferencia;
	}

	public void setCodigoDeReferencia(String codigoDeReferencia) {
		this.codigoDeReferencia = codigoDeReferencia;
	}

	public int getNumeroDeSalas() {
		return numeroDeSalas;
	}

	public void setNumeroDeSalas(int numeroDeSalas) {
		this.numeroDeSalas = numeroDeSalas;
	}

	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getCep() {
		return this.cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getPais() {
		return pais;
	}

	public void setPais(String pais) {
		this.pais = pais;
	}
	
	
	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}	
	
	public Anunciante getAnunciante() {
		return anunciante;
	}

	public void setAnunciante(Anunciante anunciante) {
		this.anunciante = anunciante;
	}

	public List<Caracteristica> getCaracteristicas() {
		return caracteristicas;
	}

	public void setCaracteristicas(List<Caracteristica> caracteristicas) {
		this.caracteristicas = caracteristicas;
	}

	public PontoGeografico getPontoGeografico() {
		return pontoGeografico;
	}

	public void setPontoGeografico(PontoGeografico pontoGeografico) {
		this.pontoGeografico = pontoGeografico;
	}

	public List<PontoGeografico> getCobertura() {
		return cobertura;
	}

	public void setCobertura(List<PontoGeografico> cobertura) {
		this.cobertura = cobertura;
	}	
	
	public TipoImovel getTipoImovel() {
		return tipoImovel;
	}

	public void setTipoImovel(TipoImovel tipoImovel) {
		this.tipoImovel = tipoImovel;
	}

	public List<Imagem> getImagens() {
		return imagens;
	}

	public void setImagens(List<Imagem> imagens) {
		this.imagens = imagens;
	}


    public long getIdUsuarioNotificacao() {
        return idUsuarioNotificacao;
    }

    public void setIdUsuarioNotificacao(long idUsuarioNotificacao) {
        this.idUsuarioNotificacao = idUsuarioNotificacao;
    }
}
