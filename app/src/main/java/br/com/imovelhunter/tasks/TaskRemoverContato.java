package br.com.imovelhunter.tasks;

import java.io.IOException;

import br.com.imovelhunter.dominio.Usuario;
import br.com.imovelhunter.exceptions.MensagensException;
import br.com.imovelhunter.listeners.OnFinishTask;
import br.com.imovelhunter.web.Web;

/**
 * Created by Washington Luiz on 17/05/2015.
 */
public class TaskRemoverContato extends GenericTask {
    public TaskRemoverContato(int requestCode, OnFinishTask onFinishTask) {
        super(requestCode, onFinishTask);
    }

    @Override
    protected Object[] doInBackGround(Object... params) throws IOException, MensagensException {
        Web web = (Web)params[0];

        Usuario usuarioRemovedor = (Usuario)params[1];
        Usuario usuarioRemovido = (Usuario)params[2];

        return new Object[]{web.removerContato(usuarioRemovedor,usuarioRemovido)};
    }
}
