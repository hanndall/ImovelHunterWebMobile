package br.com.imovelhunter.dominio;


import java.io.Serializable;
import java.util.Date;

import br.com.imovelhunter.enums.TipoUsuario;
import br.com.imovelhunter.util.ObjetoJSON;

public class Cliente extends ObjetoJSON<Cliente> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -901566217226420067L;


	private long idCliente;
	

	private String login;
	

	private String senha;
	

	private String nome;
	

	private String sobreNome;
	

	private String email;

	private Date dataDeNascimento;
	

	private TipoUsuario tipoUsuario;

	public long getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(long idCliente) {
		this.idCliente = idCliente;
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

	public TipoUsuario getTipoUsuario() {
		return tipoUsuario;
	}

	public void setTipoUsuario(TipoUsuario tipoUsuario) {
		this.tipoUsuario = tipoUsuario;
	}
	

}
