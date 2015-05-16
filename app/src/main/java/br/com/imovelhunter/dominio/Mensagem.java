package br.com.imovelhunter.dominio;


import java.io.Serializable;
import java.util.Date;
import java.util.List;

import br.com.imovelhunter.util.ObjetoJSON;

public class Mensagem extends ObjetoJSON<Mensagem> implements Serializable {


	private long idMensagem;

	private String mensagem;

	private Date dataEnvio;

	private Usuario usuarioRemetente;

	private List<Usuario> usuariosDestino;

    private Boolean lida;

    public Mensagem(String json){
        super(json);
    }

    public Mensagem(){

    }
	

	public long getIdMensagem() {
		return idMensagem;
	}

	public void setIdMensagem(long idMensagem) {
		this.idMensagem = idMensagem;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public Date getDataEnvio() {
		return dataEnvio;
	}

	public void setDataEnvio(Date dataEnvio) {
		this.dataEnvio = dataEnvio;
	}

	public Usuario getUsuarioRemetente() {
		return usuarioRemetente;
	}

	public void setUsuarioRemetente(Usuario usuarioRemetente) {
		this.usuarioRemetente = usuarioRemetente;
	}

	public List<Usuario> getUsuariosDestino() {
		return usuariosDestino;
	}

	public void setUsuariosDestino(List<Usuario> usuariosDestino) {
		this.usuariosDestino = usuariosDestino;
	}


    public Boolean getLida() {
        return lida;
    }

    public void setLida(Boolean lida) {
        this.lida = lida;
    }
}
