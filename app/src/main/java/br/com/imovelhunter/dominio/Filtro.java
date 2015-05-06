package br.com.imovelhunter.dominio;

import java.io.Serializable;

import br.com.imovelhunter.util.ObjetoJSON;

/**
 * Created by Washington Luiz on 21/04/2015.
 */
public class Filtro extends ObjetoJSON<Filtro> implements Serializable {

    private String situacao;

    private String valorDesejado;

    private String tipoDeImovel;

    private String uf;

    private String cidade;

    private String bairro;

    private String qtdQuarto;




    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }


    public String getTipoDeImovel() {
        return tipoDeImovel;
    }

    public void setTipoDeImovel(String tipoDeImovel) {
        this.tipoDeImovel = tipoDeImovel;
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

    public String getValorDesejado() {
        return valorDesejado;
    }

    public void setValorDesejado(String valorDesejado) {
        this.valorDesejado = valorDesejado;
    }

    public String getQtdQuarto() {
        return qtdQuarto;
    }

    public void setQtdQuarto(String qtdQuarto) {
        this.qtdQuarto = qtdQuarto;
    }
}
