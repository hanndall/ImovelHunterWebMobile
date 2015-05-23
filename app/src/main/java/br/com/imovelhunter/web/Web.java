package br.com.imovelhunter.web;


import java.io.IOException;
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
import br.com.imovelhunter.exceptions.MensagensException;

public interface Web {

    /**
     * Lista todos os imoveis do sistema
     * @return
     * @throws Exception
     */
    List<Imovel> listarImoveis() throws IOException, MensagensException;

    /**
     * Cadastra a chaveGCM e o serial do dispositivo no repositório
     * @param chaveGCM
     * @param serialDispositivo
     * @return
     * @throws java.io.IOException
     */
    Boolean cadastrarGCM(String chaveGCM, String serialDispositivo) throws IOException;

    /**
     * Loga no sistema, após logar, dá um cast de usuário ou cliente na camada acima
     * @param chaveGCM
     * @param serialDispositivo
     * @param login
     * @param senha
     * @return
     */
    Usuario logar(String chaveGCM, String serialDispositivo, String login, String senha) throws IOException, MensagensException;

    /**
     * Cadastra o cliente lá no repositório
     * @param cliente
     * @param serialDispositivo
     * @return
     */
    Cliente cadastraCliente(Cliente cliente, String serialDispositivo) throws IOException;

    /**
     * Método que pede todos os Uf's do Brasil para o web service
     * @return
     */
    List<Uf> listarUfs() throws IOException, MensagensException;

    /**
     * Método que lista todas as cidades de uma determinada uf
     * @param uf
     * @return
     * @throws java.io.IOException
     * @throws br.com.imovelhunter.exceptions.MensagensException
     */
    List<Cidades> listarCidadesPelaUf(Uf uf) throws IOException, MensagensException;

    /**
     * Método que lista os bairros da cidade
     * @param cidade
     * @return
     * @throws java.io.IOException
     * @throws br.com.imovelhunter.exceptions.MensagensException
     */
    List<Bairros> listarBairrosPelaCidade(Cidades cidade) throws IOException, MensagensException;

    /**
     * Envia o filtro para que o servidor trate e mande a lista de imóveis encontrados
     * @param filtro
     * @return
     * @throws IOException
     * @throws MensagensException
     */
    List<Imovel> enviarFiltro(Filtro filtro) throws IOException, MensagensException;

    /**
     * Busca o usuário pelo anunciante passado
     * @param anunciante
     * @return
     * @throws IOException
     * @throws MensagensException
     */
    Usuario buscarUsuarioPeloAnunciante(Anunciante anunciante) throws IOException, MensagensException;

    /**
     * Envia uma mensagem para algum usuário
     * @param mensagem
     * @return
     * @throws IOException
     * @throws MensagensException
     */
    Boolean enviarMensagem(Mensagem mensagem) throws IOException, MensagensException;

    /**
     * Método que serve para bloquear o contato
     * @param usuarioBloqueador
     * @param usuarioBloqueado
     * @return
     */
    Boolean bloquearUsuario(Usuario usuarioBloqueador,Usuario usuarioBloqueado) throws IOException, MensagensException;

    /**
     * Método que serve para desbloquear um contato
     * @param usuarioBloqueador
     * @param usuarioBloqueado
     * @return
     * @throws IOException
     * @throws MensagensException
     */
    Boolean desbloquearUsuario(Usuario usuarioBloqueador,Usuario usuarioBloqueado) throws IOException, MensagensException;

    /**
     * Lista os contatos do usuário
     * @param usuario
     * @return
     * @throws IOException
     * @throws MensagensException
     */
    List<Usuario> listarContatosDoUsuario(Usuario usuario) throws IOException, MensagensException;

    /**
     * Lista os contatos bloqueados do usuário
     * @param usuario
     * @return
     * @throws IOException
     * @throws MensagensException
     */
    List<Usuario> listarContatosBloqueadosDoUsuario(Usuario usuario) throws IOException, MensagensException;

    /**
     * Método que serve para adicionar um contato a lista de contatos
     * @param usuarioAdicionador
     * @param usuarioAdicionado
     * @return
     * @throws IOException
     * @throws MensagensException
     */
    Boolean adicionarContato(Usuario usuarioAdicionador,Usuario usuarioAdicionado) throws IOException, MensagensException;

    /**
     * Método que serve para remover um contato da sua lista de contatos
     * @param usuarioRemovedor
     * @param usuarioRemovido
     * @return
     * @throws IOException
     * @throws MensagensException
     */
    Boolean removerContato(Usuario usuarioRemovedor,Usuario usuarioRemovido) throws IOException, MensagensException;

    /**
     * Método que vai verificar se um determinado usuário já está na lista de contatos
     * @param usuarioAdicionador
     * @param usuarioAdicionado
     * @return
     * @throws IOException
     * @throws MensagensException
     */
    Boolean usuarioEAdicionado(Usuario usuarioAdicionador,Usuario usuarioAdicionado) throws IOException, MensagensException;

    /**
     * Método que vai verificar se um determinado usuário já está bloqueado na lista de bloqueados
     * @param usuarioBloqueador
     * @param usuarioBloquado
     * @return
     * @throws IOException
     * @throws MensagensException
     */
    Boolean usuarioEBloqueado(Usuario usuarioBloqueador,Usuario usuarioBloquado) throws IOException, MensagensException;



}

