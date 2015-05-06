package br.com.imovelhunter.dominio;

import java.io.Serializable;

import br.com.imovelhunter.util.ObjetoJSON;


public class Notificacao extends ObjetoJSON<Notificacao> implements Serializable{

    private long idNotificacao;

    private String mensagemNotificacao;
    
    private Object tag;


    public long getIdNotificacao() {
        return idNotificacao;
    }

    public void setIdNotificacao(long idNotificacao) {
        this.idNotificacao = idNotificacao;
    }

    public String getMensagemNotificacao() {
        return mensagemNotificacao;
    }

    public void setMensagemNotificacao(String mensagemNotificacao) {
        this.mensagemNotificacao = mensagemNotificacao;
    }

	public Object getTag() {
		return tag;
	}

	public void setTag(Object tag) {
		this.tag = tag;
	}
}
