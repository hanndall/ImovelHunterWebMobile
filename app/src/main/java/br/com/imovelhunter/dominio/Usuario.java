package br.com.imovelhunter.dominio;


import java.io.Serializable;

import br.com.imovelhunter.util.ObjetoJSON;

public class Usuario extends ObjetoJSON<Usuario> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4633881873007954301L;


	private long idUsuario;

	private Anunciante anunciante;
	

	private Cliente cliente;

	private String chaveGCM;

	private String serialDispositivo;

    private String nome;
	
	

	public long getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(long idUsuario) {
		this.idUsuario = idUsuario;
	}

	public Anunciante getAnunciante() {
		return anunciante;
	}

	public void setAnunciante(Anunciante anunciante) {
		this.anunciante = anunciante;
	}	

	public String getChaveGCM() {
		return chaveGCM;
	}

	public void setChaveGCM(String chaveGCM) {
		this.chaveGCM = chaveGCM;
	}

	public String getSerialDispositivo() {
		return serialDispositivo;
	}

	public void setSerialDispositivo(String serialDispositivo) {
		this.serialDispositivo = serialDispositivo;
	}

    public String getNomeUsuario(){
        if(this.cliente != null){
            return this.cliente.getNome();
        }else if(this.anunciante != null){
            return  this.anunciante.getNome();
        }else{
            if(this.nome != null){
                return this.nome;
            }
            return "Anónimo";
        }
    }

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}


    public void setNome(String nome) {
        this.nome = nome;
    }
}
