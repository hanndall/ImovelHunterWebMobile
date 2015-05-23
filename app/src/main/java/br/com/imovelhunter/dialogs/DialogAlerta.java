package br.com.imovelhunter.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by BOTTOMUP 05 on 22/01/2015.
 */
public class DialogAlerta {

    private Context contexto;

    private String titulo;

    private String mensagem;

    private int requestCode;

    private String respSim;

    private String respNao;

    public DialogAlerta(Context contexto, String titulo, String mensagem, int requestCode, String respSim, String respNao){
        this.respSim = respSim;

        this.respNao = respNao;

        this.contexto = contexto;

        this.titulo = titulo;

        this.mensagem = mensagem;

        this.requestCode = requestCode;

        if(this.contexto instanceof RespostaSim){
            this.respostaSim = (RespostaSim)this.contexto;
        }

        if(this.contexto instanceof  RespostaNao){
            this.respostaNao = (RespostaNao)this.contexto;
        }
    }

    public DialogAlerta(Context contexto, String titulo, String mensagem, int requestCode){

        this.respSim = "Sim";

        this.respNao = "Não";

        this.contexto = contexto;

        this.titulo = titulo;

        this.mensagem = mensagem;

        this.requestCode = requestCode;

        if(this.contexto instanceof RespostaSim){
            this.respostaSim = (RespostaSim)this.contexto;
        }

        if(this.contexto instanceof  RespostaNao){
            this.respostaNao = (RespostaNao)this.contexto;
        }

    }

    public void exibirDialog(){

        new AlertDialog.Builder(this.contexto)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(DialogAlerta.this.titulo)
                .setMessage(DialogAlerta.this.mensagem)
                .setPositiveButton(this.respSim, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(respostaSim != null){
                            respostaSim.respondeuSim(DialogAlerta.this.requestCode);
                        }

                    }

                })
                .setNegativeButton(this.respNao, null).setNegativeButton(this.respNao,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(respostaNao != null){
                            respostaNao.respondeuNao(DialogAlerta.this.requestCode);
                        }
                    }
                }).show();

    }


    private RespostaSim respostaSim;

    private RespostaNao respostaNao;

    public interface RespostaSim{
        public void respondeuSim(int requestCode);
    }

    public void setRespostaSim(RespostaSim respostaSim){
        this.respostaSim = respostaSim;
    }

    public interface  RespostaNao{
        public void respondeuNao(int requestCode);
    }

    public void setRespostaNao(RespostaNao respostaNao) {
        this.respostaNao = respostaNao;
    }
}
