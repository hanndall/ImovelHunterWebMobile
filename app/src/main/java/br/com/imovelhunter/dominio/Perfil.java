package br.com.imovelhunter.dominio;

/**
 * Created by Washington Luiz on 02/06/2015.
 */
import java.io.Serializable;

import br.com.imovelhunter.enums.SituacaoImovel;
import br.com.imovelhunter.enums.TipoImovel;
import br.com.imovelhunter.util.ObjetoJSON;


public class Perfil extends ObjetoJSON<Perfil> implements Serializable {


    private long idPerfil;

    private Usuario usuario;

    private SituacaoImovel situacaoImovel;

    private double valor;

    private TipoImovel tipo;

    private String uf;

    private String cidade;

    private String bairro;

    private Integer qtQuartos;


    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }



    public long getIdPerfil() {
        return idPerfil;
    }

    public void setIdPerfil(long idPerfil) {
        this.idPerfil = idPerfil;
    }

    public void setQtQuartos(Integer qtQuartos) {
        this.qtQuartos = qtQuartos;
    }


    public SituacaoImovel getSituacaoImovel() {
        return situacaoImovel;
    }

    public void setSituacaoImovel(SituacaoImovel situacaoImovel) {
        this.situacaoImovel = situacaoImovel;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public TipoImovel getTipo() {
        return tipo;
    }

    public void setTipo(TipoImovel tipo) {
        this.tipo = tipo;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public Integer getQtQuartos() {
        return qtQuartos;
    }

    public void setQtQuartos(int qtQuartos) {
        this.qtQuartos = qtQuartos;
    }
}
