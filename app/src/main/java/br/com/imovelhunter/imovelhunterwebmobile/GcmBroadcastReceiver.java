/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.com.imovelhunter.imovelhunterwebmobile;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.google.android.gms.maps.model.internal.e;

import java.util.Date;
import java.util.List;

import br.com.imovelhunter.dao.MensagemDAO;
import br.com.imovelhunter.dao.NotificacaoDAO;
import br.com.imovelhunter.dominio.Imovel;
import br.com.imovelhunter.dominio.Mensagem;
import br.com.imovelhunter.dominio.Notificacao;
import br.com.imovelhunter.dominio.Usuario;
import br.com.imovelhunter.enums.ParametrosSessaoJson;
import br.com.imovelhunter.listeners.EscutadorDeMensagem;
import br.com.imovelhunter.util.SessionUtilJson;


public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

    private static EscutadorDeMensagem escutadorDeMensagem;

    private static NotificationActivity.OnRecebeuNotificacao onRecebeuNotificacao;

    private static ListaContatoActivity.OnMensageListener onMessageListener;

    private MensagemDAO mensagemDAO;

    private NotificacaoDAO notificacaoDAO;

    private Usuario usuarioLogado;

    public static void setEscutadorDeMensagem(EscutadorDeMensagem escutadorDeMensagem){
        GcmBroadcastReceiver.escutadorDeMensagem = escutadorDeMensagem;
    }

    public static void setOnRecebeuNotificacao(NotificationActivity.OnRecebeuNotificacao onRecebeuNotificacao){
        GcmBroadcastReceiver.onRecebeuNotificacao = onRecebeuNotificacao;
    }

    public static void setOnMessageListener(ListaContatoActivity.OnMensageListener onMessageListener){
        GcmBroadcastReceiver.onMessageListener = onMessageListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Explicitly specify that GcmIntentService will handle the intent.
        ComponentName comp = new ComponentName(context.getPackageName(),GcmIntentService.class.getName());

        if(SessionUtilJson.getInstance(context).containsName(ParametrosSessaoJson.USUARIO_LOGADO)){
            usuarioLogado = (Usuario)SessionUtilJson.getInstance(context).getJsonObject(ParametrosSessaoJson.USUARIO_LOGADO,Usuario.class);
        }

        if(mensagemDAO == null) {
            mensagemDAO = new MensagemDAO(context);
        }

        if(notificacaoDAO == null){
            notificacaoDAO = new NotificacaoDAO(context);
        }

        if(escutadorDeMensagem != null){
            Bundle bundle = intent.getExtras();

            String mensagemS = bundle.getString("mensagem");

            if(!mensagemS.contains("idImovel")){ //É uma mensagem do chat
                Mensagem m = new Mensagem();
                m.parse(mensagemS);
                m.setLida(false);
                if(usuarioLogado != null && m.getUsuariosDestino().get(0).getIdUsuario() == usuarioLogado.getIdUsuario()) {
                    escutadorDeMensagem.recebeuAlgo(bundle);
                }else{
                    mensagemDAO.inserirMensagem(m);
                }

            }else{ //É um perfil de imóvel

                Notificacao not = new Notificacao();
                Imovel imovel = new Imovel();
                imovel.parse(mensagemS);

                this.parseNotificacao(not,imovel);
                not.setDataNotificacao(new Date());
                notificacaoDAO.inserir(not);

                if(usuarioLogado != null && usuarioLogado.getIdUsuario() == imovel.getIdUsuarioNotificacao()) {
                    if(onRecebeuNotificacao != null){
                        onRecebeuNotificacao.recebeuNotificacao();
                    }else {
                        // Start the service, keeping the device awake while it is launching.
                        startWakefulService(context, (intent.setComponent(comp)));
                    }

                }


            }

        }else {


            String mensagem = intent.getExtras().getString("mensagem");

            boolean notificar = true;

            if(!mensagem.contains("idImovel")) { // É mensagem de chat
                Mensagem mensagemO = new Mensagem();

                mensagemO.parse(mensagem);

                mensagemO.setLida(false);

                mensagemDAO.inserirMensagem(mensagemO);

                if(onMessageListener != null && usuarioLogado != null && usuarioLogado.getIdUsuario() == mensagemO.getUsuariosDestino().get(0).getIdUsuario()){
                    onMessageListener.atualizar();
                }

                if(usuarioLogado == null || mensagemO.getUsuariosDestino().get(0).getIdUsuario() != usuarioLogado.getIdUsuario()){
                    notificar = false;
                }

            }else{ //É imóvel

                Notificacao not = new Notificacao();
                Imovel imovel = new Imovel();
                imovel.parse(mensagem);
                this.parseNotificacao(not,imovel);
                not.setDataNotificacao(new Date());
                notificacaoDAO.inserir(not);


                if(usuarioLogado != null  && usuarioLogado.getIdUsuario() == imovel.getIdUsuarioNotificacao()) {
                    if(onRecebeuNotificacao != null){
                        notificar = false;
                        onRecebeuNotificacao.recebeuNotificacao();
                    }

                }else{
                    notificar = false;
                }



            }

            if(notificar) {
                // Start the service, keeping the device awake while it is launching.
                startWakefulService(context, (intent.setComponent(comp)));
            }
        }
        setResultCode(Activity.RESULT_OK);
    }


    private void parseNotificacao(Notificacao not, Imovel imovel){
        not.setPreco(imovel.getPreco());
        not.setDataNotificacao(new Date());
        not.setSituacao(imovel.getSituacaoImovel().name());
        not.setLatitude(imovel.getPontoGeografico().getLatitude());
        not.setLongitude(imovel.getPontoGeografico().getLongitude());
        not.setRua(imovel.getLogradouro());
        if(imovel.getImagens() != null && imovel.getImagens().size() > 0) {
            not.setImagem(imovel.getImagens().get(0).getCaminhoImagem());
        }
        not.setNumero(imovel.getNumeroDoImovel());
        not.setIdImovel(Integer.valueOf(String.valueOf(imovel.getIdImovel())));
        not.setRua(imovel.getLogradouro());
        not.setTipo(imovel.getTipoImovel().name());
        not.setIdUsuario(Integer.valueOf(String.valueOf(imovel.getIdUsuarioNotificacao())));
    }


}

