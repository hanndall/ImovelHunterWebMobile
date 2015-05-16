package br.com.imovelhunter.dominio;

import java.io.Serializable;
import java.util.Date;

import br.com.imovelhunter.enums.TipoUsuario;
import br.com.imovelhunter.util.ObjetoJSON;


public class Anunciante extends ObjetoJSON<Anunciante> implements Serializable {


	private long idAnunciante;	
	

	private String nome;
	

	private String sobreNome;

    private String telefone;

	private String email;
	

	private Date dataDeNascimento;
	

	private Date dataDeVencimento;
	


	private String creci;
	

	private Date dataDeCriacao;
	

	private String cpf;
	

	private String login;
	

	private String senha;
	

	private TipoUsuario tipoUsuario;
	
	public Anunciante(){
		this.tipoUsuario = TipoUsuario.USUARIO;
	}

	public long getIdAnunciante() {
		return idAnunciante;
	}

	public void setIdAnunciante(long idAnunciante) {
		this.idAnunciante = idAnunciante;
	}	

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getSobreNome() {
		return sobreNome;
	}

	public void setSobreNome(String sobreNome) {
		this.sobreNome = sobreNome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getDataDeNascimento() {
		return dataDeNascimento;
	}

	public void setDataDeNascimento(Date dataDeNascimento) {
		this.dataDeNascimento = dataDeNascimento;
	}

	public Date getDataDeVencimento() {
		return dataDeVencimento;
	}

	public void setDataDeVencimento(Date dataDeVencimento) {
		this.dataDeVencimento = dataDeVencimento;
	}

	public String getCreci() {
		return creci;
	}

	public void setCreci(String creci) {
		this.creci = creci;
	}

	public Date getDataDeCriacao() {
		return dataDeCriacao;
	}

	public void setDataDeCriacao(Date dataDeCriacao) {
		this.dataDeCriacao = dataDeCriacao;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public TipoUsuario getTipoUsuario() {
		return tipoUsuario;
	}

	public void setTipoUsuario(TipoUsuario tipoUsuario) {
		this.tipoUsuario = tipoUsuario;
	}

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
	
	
}
