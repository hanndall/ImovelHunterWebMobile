package br.com.imovelhunter.dominio;


import java.io.Serializable;
import java.util.Date;

import br.com.imovelhunter.util.ObjetoJSON;

public class Bloqueio extends ObjetoJSON<Bloqueio> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7816181546141658762L;

	private long idBloqueio;
	

	private Date dataBloqueio;
	

	private Date dataVencimento;
	

	private Usuario usuarioBloqueador;
	

	private Usuario usuarioBloqueado;

	public long getIdBloqueio() {
		return idBloqueio;
	}

	public void setIdBloqueio(long idBloqueio) {
		this.idBloqueio = idBloqueio;
	}

	public Date getDataBloqueio() {
		return dataBloqueio;
	}

	public void setDataBloqueio(Date dataBloqueio) {
		this.dataBloqueio = dataBloqueio;
	}

	public Date getDataVencimento() {
		return dataVencimento;
	}

	public void setDataVencimento(Date dataVencimento) {
		this.dataVencimento = dataVencimento;
	}

	public Usuario getUsuarioBloqueador() {
		return usuarioBloqueador;
	}

	public void setUsuarioBloqueador(Usuario usuarioBloqueador) {
		this.usuarioBloqueador = usuarioBloqueador;
	}

	public Usuario getUsuarioBloqueado() {
		return usuarioBloqueado;
	}

	public void setUsuarioBloqueado(Usuario usuarioBloqueado) {
		this.usuarioBloqueado = usuarioBloqueado;
	}
	
	
	
	
	
}
