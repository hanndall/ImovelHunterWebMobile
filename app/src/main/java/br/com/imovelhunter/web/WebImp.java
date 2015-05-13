package br.com.imovelhunter.web;


import org.json.JSONException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import br.com.imovelhunter.dominio.Anunciante;
import br.com.imovelhunter.dominio.Bairros;
import br.com.imovelhunter.dominio.Bloqueio;
import br.com.imovelhunter.dominio.Cidades;
import br.com.imovelhunter.dominio.Cliente;
import br.com.imovelhunter.dominio.Filtro;
import br.com.imovelhunter.dominio.Imovel;
import br.com.imovelhunter.dominio.Mensagem;
import br.com.imovelhunter.dominio.Uf;
import br.com.imovelhunter.dominio.Usuario;
import br.com.imovelhunter.enums.Parametros;
import br.com.imovelhunter.enums.Requisicao;
import br.com.imovelhunter.exceptions.MensagensException;
import br.com.imovelhunter.util.HttpUtil;

public class WebImp implements Web {

    private HttpUtil httpUtil;

    private String url = "http://ec2-54-68-17-181.us-west-2.compute.amazonaws.com/imovelhunterwebservice/servico";
    //private String url = "http://192.168.25.11:8080/imovelhunterwebservice/servico";


    public WebImp(){
        Charset.forName("UTF-8").encode(this.url);
        this.httpUtil = new HttpUtil(this.url);
    }

    //##################
    //##Implementações##
    //##################

    @Override
    public List<Imovel> listarImoveis() throws IOException, MensagensException {
        this.httpUtil.clear();
        this.httpUtil.put("requisicao", Requisicao.LISTAR_IMOVEIS.name());

        String resp = this.httpUtil.enviarRequest();
        if(resp.contains("ERRO")){
            throw new MensagensException(resp.split(";")[1]);
        }else{
            try {
                return (List<Imovel>)this.httpUtil.jsonArrayToList(resp,Imovel.class);
            } catch (Exception e) {
                e.printStackTrace();
                throw new MensagensException("Erro ao dar o parse da lista de imóvel");
            }
        }
    }

    @Override
    public Boolean cadastrarGCM(String chaveGCM,String serialDispositivo) throws IOException {
        this.httpUtil.clear();
        this.httpUtil.put("requisicao", Requisicao.CADASTRA_GCM.name());

        this.httpUtil.put(Parametros.CHAVE_GCM.name(),chaveGCM);
        this.httpUtil.put(Parametros.SERIAL_DISPOSITIVO.name(),serialDispositivo);

        String resp = this.httpUtil.enviarRequest();

        if(resp.contains("OK")){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public Usuario logar(String chaveGCM, String serialDispositivo, String login, String senha) throws IOException, MensagensException {
        this.httpUtil.clear();
        this.httpUtil.put("requisicao", Requisicao.LOGAR.name());


        this.httpUtil.put(Parametros.SERIAL_DISPOSITIVO.name(),serialDispositivo);
        this.httpUtil.put(Parametros.LOGIN.name(),login);
        this.httpUtil.put(Parametros.SENHA.name(),senha);
        this.httpUtil.put(Parametros.CHAVE_GCM.name(),chaveGCM);

        String resp = this.httpUtil.enviarRequest();

        if(resp.contains("ERRO")){
            throw new MensagensException(resp.split(";")[1]);
        }else{
            Usuario usuario = new Usuario();
            usuario.parse(resp);
            return  usuario;
        }
    }

    @Override
    public Cliente cadastraCliente(Cliente cliente, String serialDispositivo) throws IOException {
        this.httpUtil.clear();
        this.httpUtil.put("requisicao", Requisicao.CADASTRA_CLIENTE.name());

        this.httpUtil.put(Parametros.CLIENTE_JSON.name(),cliente.toString());
        this.httpUtil.put(Parametros.SERIAL_DISPOSITIVO.name(),serialDispositivo);

        String resp = this.httpUtil.enviarRequest();

        Cliente clienteCad = new Cliente();

        clienteCad.parse(resp);

        cliente.setIdCliente(clienteCad.getIdCliente());

        return cliente;
    }

    @Override
    public List<Uf> listarUfs() throws IOException, MensagensException {
        this.httpUtil.clear();
        this.httpUtil.put("requisicao", Requisicao.LISTAR_UFS.name());

        String resp = this.httpUtil.enviarRequest();

        if(resp.contains("ERRO")){
            throw new MensagensException(resp.split(";")[1]);
        }else{
            try {
                return (List<Uf>)this.httpUtil.jsonArrayToList(resp,Uf.class);
            } catch (Exception e) {
                e.printStackTrace();
                throw new MensagensException("Erro ao dar o parse da lista de Uf");
            }
        }

    }

    @Override
    public List<Cidades> listarCidadesPelaUf(Uf uf) throws IOException, MensagensException {
        this.httpUtil.clear();
        this.httpUtil.put("requisicao", Requisicao.LISTAR_CIDADE_PELA_UF.name());

        this.httpUtil.put(Parametros.UF_JSON.name(),uf.toString());

        String resp = this.httpUtil.enviarRequest();

        if(resp.contains("ERRO")){
            throw new MensagensException(resp.split(";")[1]);
        }else{
            try {
                return (List<Cidades>)this.httpUtil.jsonArrayToList(resp,Cidades.class);
            } catch (Exception e) {
                e.printStackTrace();
                throw new MensagensException("Erro ao dar o parse da lista de Cidades");
            }
        }

    }

    @Override
    public List<Bairros> listarBairrosPelaCidade(Cidades cidade) throws IOException, MensagensException {
        this.httpUtil.clear();
        this.httpUtil.put("requisicao", Requisicao.LISTAR_BAIRRO_PELA_CIDADE.name());

        this.httpUtil.put(Parametros.CIDADE_JSON.name(),cidade.toString());

        String resp = this.httpUtil.enviarRequest();

        if(resp.contains("ERRO")){
            throw new MensagensException(resp.split(";")[1]);
        }else{
            try {
                return (List<Bairros>)this.httpUtil.jsonArrayToList(resp,Bairros.class);
            } catch (Exception e) {
                e.printStackTrace();
                throw new MensagensException("Erro ao dar o parse da lista de Cidades");
            }
        }
    }

    @Override
    public List<Imovel> enviarFiltro(Filtro filtro) throws IOException, MensagensException {
        this.httpUtil.clear();
        this.httpUtil.put("requisicao", Requisicao.FILTRAR_IMOVEIS.name());

        this.httpUtil.put(Parametros.FILTRO_JSON.name(),filtro.toString());

        String resp = this.httpUtil.enviarRequest();

        if(resp.contains("ERRO")){
            throw new MensagensException(resp.split(";")[1]);
        }else{
            try {
                return (List<Imovel>)this.httpUtil.jsonArrayToList(resp,Imovel.class);
            } catch (Exception e) {
                e.printStackTrace();
                throw new MensagensException("Erro ao dar o parse da lista de Imoveis");
            }
        }
    }

    @Override
    public Usuario buscarUsuarioPeloAnunciante(Anunciante anunciante) throws IOException, MensagensException {
        this.httpUtil.clear();
        this.httpUtil.put("requisicao", Requisicao.BUSCAR_USUARIO_POR_ANUNCIANTE.name());

        this.httpUtil.put(Parametros.ANUNCIANTE_JSON.name(),anunciante.toString());

        String resp = this.httpUtil.enviarRequest();

        if(resp.contains("ERRO")){
            throw new MensagensException(resp.split(";")[1]);
        }else{
            try {
                Usuario u = new Usuario();
                u.parse(resp);
                return u;
            } catch (Exception e) {
                e.printStackTrace();
                throw new MensagensException("Erro ao dar o parse no objeto Usuario");
            }
        }


    }

    @Override
    public Boolean enviarMensagem(Mensagem mensagem) throws IOException, MensagensException {
        this.httpUtil.clear();
        this.httpUtil.put("requisicao", Requisicao.ENVIAR_MENSAGEM.name());

        this.httpUtil.put(Parametros.MENSAGEM_JSON.name(),mensagem.toString());

        String resp = this.httpUtil.enviarRequest();

        if(resp.contains("ERRO")){
            throw new MensagensException(resp.split(";")[1]);
        }else{
            return true;
        }

    }

    @Override
    public List<Bloqueio> meusUsuarioBloqueado(Usuario usuario) throws IOException, MensagensException {
        return null;
    }

    @Override
    public List<Usuario> buscarContatosDoUsarario(Usuario usuario) throws IOException, MensagensException {
        return null;
    }


}
