package br.com.imovelhunter.tasks;

import java.io.IOException;

import br.com.imovelhunter.dominio.Mensagem;
import br.com.imovelhunter.exceptions.MensagensException;
import br.com.imovelhunter.listeners.OnFinishTask;
import br.com.imovelhunter.web.Web;

/**
 * Created by BOTTOMUP 05 on 05/05/2015.
 */
public class TaskEnviarMensagem extends GenericTask {
    public TaskEnviarMensagem(int requestCode, OnFinishTask onFinishTask) {
        super(requestCode, onFinishTask);
    }

    @Override
    protected Object[] doInBackGround(Object... params) throws IOException, MensagensException {

        try {
            Web web = (Web) params[0];
            Mensagem mensagem = (Mensagem) params[1];

            return new Object[]{web.enviarMensagem(mensagem)};
        }
        catch(Exception ex){
            throw new MensagensException(ex.getMessage());
        }

    }

    public void setOnExceptionMessage(OnExceptionMessage onExceptionMessage){
        this.onExceptionMessage = onExceptionMessage;
    }

    private OnExceptionMessage onExceptionMessage;
    public interface OnExceptionMessage{
        public void exception(Exception ex);
    }
}
