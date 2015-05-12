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

import br.com.imovelhunter.dao.MensagemDAO;
import br.com.imovelhunter.dominio.Mensagem;
import br.com.imovelhunter.listeners.EscutadorDeMensagem;


public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

    private static EscutadorDeMensagem escutadorDeMensagem;

    private MensagemDAO mensagemDAO;

    public static void setEscutadorDeMensagem(EscutadorDeMensagem escutadorDeMensagem){
        GcmBroadcastReceiver.escutadorDeMensagem = escutadorDeMensagem;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Explicitly specify that GcmIntentService will handle the intent.
        ComponentName comp = new ComponentName(context.getPackageName(),GcmIntentService.class.getName());

        if(mensagemDAO == null) {
            mensagemDAO = new MensagemDAO(context);
        }

        if(escutadorDeMensagem != null){
            Bundle bundle = intent.getExtras();

            escutadorDeMensagem.recebeuAlgo(bundle);
        }else {


            String mensagem = intent.getExtras().getString("mensagem");

            Mensagem mensagemO = new Mensagem();

            mensagemO.parse(mensagem);

            mensagemO.setLida(true);

            mensagemDAO.inserirMensagem(mensagemO);


            // Start the service, keeping the device awake while it is launching.
            startWakefulService(context, (intent.setComponent(comp)));
        }
        setResultCode(Activity.RESULT_OK);
    }


}
