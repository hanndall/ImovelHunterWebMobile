/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.imovelhunter.imovelhunterwebmobile;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import br.com.imovelhunter.dominio.Imovel;
import br.com.imovelhunter.dominio.Mensagem;
import br.com.imovelhunter.dominio.Notificacao;
import br.com.imovelhunter.dominio.Usuario;
import br.com.imovelhunter.enums.Parametros;
import br.com.imovelhunter.enums.ParametrosSessao;
import br.com.imovelhunter.enums.ParametrosSessaoJson;
import br.com.imovelhunter.util.SessionUtilJson;


/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;



    public GcmIntentService() {
        super("GcmIntentService");
    }
    public static final String TAG = "Aplicação gcm";
    private Mensagem mensagem;

    private Usuario usuarioLogado;

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if(SessionUtilJson.getInstance(getApplicationContext()).containsName(ParametrosSessaoJson.USUARIO_LOGADO)){
            usuarioLogado = (Usuario)SessionUtilJson.getInstance(getApplicationContext()).getJsonObject(ParametrosSessaoJson.USUARIO_LOGADO,Usuario.class);
        }


        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM will be
             * extended in the future with new message types, just ignore any message types you're
             * not interested in, or that you don't recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " + extras.toString());

            // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                String mensagem = extras.getString("mensagem");

                if(!mensagem.contains("idImovel")) {
                    Mensagem mensagemO = new Mensagem();

                    mensagemO.parse(mensagem);

                    this.mensagem = mensagemO;

                    this.mensagem.setLida(false);

                    if(usuarioLogado != null && usuarioLogado.getIdUsuario() == this.mensagem.getUsuariosDestino().get(0).getIdUsuario()) {
                        sendNotification(mensagemO.getMensagem());
                    }
                }else{
                    Imovel imovel = new Imovel();
                    imovel.parse(mensagem);
                    String stringImovel = "Imovel encontrado em "+imovel.getEstado()+" - "+imovel.getBairro();
                    sendNotificationImovel(stringImovel);
                }




            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    /*
    private void sendNotification(Mensagem msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent in = new Intent(this, MensagensActivity.class);

        in.putExtra("mensagem",msg);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,in
                , 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_stat_gcm)
        .setContentTitle("Aplicação android")
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(msg.getMensagem()))
        .setContentText(msg.getMensagem());

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

        this.vibrar();
    }
    */

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent in = new Intent(this,ChatActivity.class);

        Bundle extras = new Bundle();
        extras.putSerializable(Parametros.MENSAGEM_JSON.name(),this.mensagem);
        extras.putSerializable(ParametrosSessao.USUARIO_CHAT_ATUAL.name(), this.mensagem.getUsuarioRemetente());

        in.putExtras(extras);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,in
                , 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.icone)
                        .setContentTitle("Imovel Hunter - "+mensagem.getUsuarioRemetente().getNomeUsuario())
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setAutoCancel(true);



        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

        this.vibrar();
    }


    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotificationImovel(String msg) {
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent in = new Intent(this,NotificationActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,in
                , 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icone)
                .setContentTitle("Imovel Hunter")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(msg))
                .setContentText(msg);

        mBuilder.setAutoCancel(true);



        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

        this.vibrar();
    }


    private void vibrar()
    {
        Vibrator rr = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long milliseconds = 1000;
        rr.vibrate(milliseconds);
    }


}
