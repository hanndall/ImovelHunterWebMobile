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
import br.com.imovelhunter.dominio.Perfil;
import br.com.imovelhunter.dominio.Uf;
import br.com.imovelhunter.dominio.Usuario;
import br.com.imovelhunter.enums.Parametros;
import br.com.imovelhunter.enums.Requisicao;
import br.com.imovelhunter.exceptions.MensagensException;
import br.com.imovelhunter.util.HttpUtil;
import br.com.imovelhunter.util.RemoverAcentuacao;

public class WebImp implements Web {

    private HttpUtil httpUtil;

    private RemoverAcentuacao removerAcentuacao;

    private String url = "http://ec2-54-68-17-181.us-west-2.compute.amazonaws.com/imovelhunterwebservice/servico";
    //private String url = "http://192.168.1.3:8080/imovelhunterwebservice/servico";


    public WebImp(){
        Charset.forName("UTF-8").encode(this.url);
        this.httpUtil = new HttpUtil(this.url);
        this.removerAcentuacao = new RemoverAcentuacao();
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
            if(resp.contains("idUsuario")) {
                Usuario usuario = new Usuario();
                usuario.parse(resp);
                if(usuario.getIdUsuario() == 0){
                    throw new MensagensException("Falha ao tentar logar, tente novamente");
                }
                return usuario;
            }else{
                throw new MensagensException("Falha ao tentar logar, tente novamente");
            }
        }
    }

    @Override
    public Cliente cadastraCliente(Cliente cliente, String serialDispositivo) throws IOException {
        this.httpUtil.clear();
        this.httpUtil.put("requisicao", Requisicao.CADASTRA_CLIENTE.name());

        this.httpUtil.put(Parametros.CLIENTE_JSON.name(),removerAcentuacao.removerAcentos(cliente.toString()));
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

        this.httpUtil.put(Parametros.FILTRO_JSON.name(),removerAcentuacao.removerAcentos(filtro.toString()));

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
    public Boolean bloquearUsuario(Usuario usuarioBloqueador, Usuario usuarioBloqueado) throws IOException, MensagensException {
        this.httpUtil.clear();
        this.httpUtil.put("requisicao",Requisicao.STATUS_BLOQUEIO.name());

        this.httpUtil.put(Parametros.USUARIO_BLOQUEADO.name(),usuarioBloqueado.toString());
        this.httpUtil.put(Parametros.USUARIO_BLOQUEADOR.name(),usuarioBloqueador.toString());

        String resp = this.httpUtil.enviarRequest();

        if(resp.contains("ERRO")){
            throw new MensagensException(resp.split(";")[1]);
        }else{
            return true;
        }

    }

    @Override
    public Boolean desbloquearUsuario(Usuario usuarioBloqueador, Usuario usuarioBloqueado) throws IOException, MensagensException {
        this.httpUtil.clear();
        this.httpUtil.put("requisicao", Requisicao.STATUS_DESBLOQUEIO.name());

        this.httpUtil.put(Parametros.USUARIO_BLOQUEADO.name(),usuarioBloqueado.toString());
        this.httpUtil.put(Parametros.USUARIO_BLOQUEADOR.name(),usuarioBloqueador.toString());

        String resp = this.httpUtil.enviarRequest();

        if(resp.contains("ERRO")){
            throw new MensagensException(resp.split(";")[1]);
        }else{
            return true;
        }
    }

    @Override
    public List<Usuario> listarContatosDoUsuario(Usuario usuario) throws IOException, MensagensException {
        this.httpUtil.clear();
        this.httpUtil.put("requisicao", Requisicao.LISTAR_CONTATOS_DO_USUARIO.name());

        this.httpUtil.put(Parametros.USUARIO_JSON.name(),usuario.toString());

        String resp = this.httpUtil.enviarRequest();

        if(resp.contains("ERRO")){
            throw new MensagensException(resp.split(";")[1]);
        }else{
            try {
                return (List<Usuario>)this.httpUtil.jsonArrayToList(resp,Usuario.class);
            } catch (Exception e) {
                e.printStackTrace();
                throw new MensagensException("Erro ao dar o parse da lista de Usuários");
            }
        }


    }

    @Override
    public List<Usuario> listarContatosBloqueadosDoUsuario(Usuario usuario) throws IOException, MensagensException {
        this.httpUtil.clear();
        this.httpUtil.put("requisicao", Requisicao.LISTAR_CONTATOS_BLOQUEADOS_DO_USUARIO.name());

        this.httpUtil.put(Parametros.USUARIO_JSON.name(),usuario.toString());

        String resp = this.httpUtil.enviarRequest();

        if(resp.contains("ERRO")){
            throw new MensagensException(resp.split(";")[1]);
        }else{
            try {
                return (List<Usuario>)this.httpUtil.jsonArrayToList(resp,Usuario.class);
            } catch (Exception e) {
                e.printStackTrace();
                throw new MensagensException("Erro ao dar o parse da lista de Usuários");
            }
        }
    }

    @Override
    public Boolean adicionarContato(Usuario usuarioAdicionador, Usuario usuarioAdicionado) throws IOException, MensagensException {
        this.httpUtil.clear();
        this.httpUtil.put("requisicao", Requisicao.ADICIONAR_USUARIO.name());

        this.httpUtil.put(Parametros.USUARIO_ADICIONADO.name(),usuarioAdicionado.toString());
        this.httpUtil.put(Parametros.USUARIO_ADICIONADOR.name(),usuarioAdicionador.toString());

        String resp = this.httpUtil.enviarRequest();

        if(resp.contains("ERRO")){
            throw new MensagensException(resp.split(";")[1]);
        }else{
            return true;
        }
    }

    @Override
    public Boolean removerContato(Usuario usuarioRemovedor, Usuario usuarioRemovido) throws IOException, MensagensException {
        this.httpUtil.clear();
        this.httpUtil.put("requisicao",Requisicao.REMOVER_USUARIO.name());

        this.httpUtil.put(Parametros.USUARIO_REMOVEDOR.name(),usuarioRemovedor.toString());
        this.httpUtil.put(Parametros.USUARIO_REMOVIDO.name(),usuarioRemovido.toString());

        String resp = this.httpUtil.enviarRequest();

        if(resp.contains("ERRO")){
            throw new MensagensException(resp.split(";")[1]);
        }else{
            return true;
        }
    }

    @Override
    public Boolean usuarioEAdicionado(Usuario usuarioAdicionador, Usuario usuarioAdicionado) throws IOException, MensagensException {
        this.httpUtil.clear();
        this.httpUtil.put("requisicao", Requisicao.USUARIO_E_ADICIONADO.name());

        this.httpUtil.put(Parametros.USUARIO_ADICIONADO.name(),usuarioAdicionado.toString());
        this.httpUtil.put(Parametros.USUARIO_ADICIONADOR.name(),usuarioAdicionador.toString());

        String resp = this.httpUtil.enviarRequest();

        if(resp.equals("TRUE")){
            return true;
        }else if(resp.equals("FALSE")){
            return false;
        }
        throw new MensagensException("Erro ao verificar status do contato");
    }

    @Override
    public Boolean usuarioEBloqueado(Usuario usuarioBloqueador, Usuario usuarioBloquado) throws IOException, MensagensException {
        this.httpUtil.clear();
        this.httpUtil.put("requisicao", Requisicao.USUARIO_E_BLOQUEADO.name());

        this.httpUtil.put(Parametros.USUARIO_BLOQUEADO.name(),usuarioBloquado.toString());
        this.httpUtil.put(Parametros.USUARIO_BLOQUEADOR.name(),usuarioBloqueador.toString());

        String resp = this.httpUtil.enviarRequest();

        if(resp.equals("TRUE")){
            return true;
        }else if(resp.equals("FALSE")){
            return false;
        }
        throw new MensagensException("Erro ao verificar status do contato");
    }

    @Override
    public Boolean cadastrarPerfilImovel(Perfil perfil) throws IOException, MensagensException {
        this.httpUtil.clear();
        this.httpUtil.put("requisicao", Requisicao.CADASTRA_PERFIL_IMOVEL.name());

        this.httpUtil.put(Parametros.PERFIL_IMOVEL_JSON.name(), perfil.toString());

        String resp = this.httpUtil.enviarRequest();


        if(resp.contains("OK")){
            return true;
        }else{
            throw new MensagensException(resp.split(";")[1]);
        }
    }

    @Override
    public List<Perfil> listarPerfisCadastrados(Usuario usuarioLogado) throws IOException, MensagensException {
        this.httpUtil.clear();
        this.httpUtil.put("requisicao", Requisicao.LISTAR_PERFIS_CADASTRADOS.name());

        this.httpUtil.put(Parametros.USUARIO_JSON.name(),usuarioLogado.toString());

        String resp = this.httpUtil.enviarRequest();

        if(resp.contains("ERRO")){
            throw new MensagensException(resp.split(";")[1]);
        }else{
            try {
                return (List<Perfil>)this.httpUtil.jsonArrayToList(resp,Perfil.class);
            } catch (Exception e) {
                e.printStackTrace();
                throw new MensagensException("Erro ao dar o parse da lista de perfis");
            }
        }
    }

    @Override
    public Boolean removePerfilCadastrado(Perfil perfil) throws IOException, MensagensException {
        this.httpUtil.clear();
        this.httpUtil.put("requisicao", Requisicao.REMOVER_PERFIL_CADASTRADO.name());

       this.httpUtil.put(Parametros.PERFIL_IMOVEL_JSON.name(),perfil.toString());

        String resp = this.httpUtil.enviarRequest();

        if(resp.equals("TRUE")){
            return true;
        }else if(resp.equals("FALSE")){
            return false;
        }
        throw new MensagensException("Falha ao remover perfil cadastrado");
    }

    @Override
    public List<Imovel> listarImovelAnunciante(Anunciante anunciante) throws IOException, MensagensException {
        this.httpUtil.clear();
        this.httpUtil.put("requisicao", Requisicao.LISTAR_IMOVEIS_ANUNCIANTE.name());
        this.httpUtil.put(Parametros.ANUNCIANTE_JSON.name(),anunciante.toString());

        String resp = this.httpUtil.enviarRequest();
        if(resp.contains("ERRO")){
            throw new MensagensException(resp.split(";")[1]);
        }else{
            try {
                List<Imovel> lista = (List<Imovel>)this.httpUtil.jsonArrayToList(resp,Imovel.class);
                return lista;

            } catch (Exception e) {
                e.printStackTrace();
                throw new MensagensException("Erro ao dar o parse da lista de imóvel do anunciante");
            }
        }
    }


}
