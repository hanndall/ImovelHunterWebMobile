package br.com.imovelhunter.tasks;

import java.io.IOException;

import br.com.imovelhunter.dominio.Usuario;
import br.com.imovelhunter.exceptions.MensagensException;
import br.com.imovelhunter.listeners.OnFinishTask;
import br.com.imovelhunter.web.Web;

/**
 * Created by Washington Luiz on 19/05/2015.
 */
public class TaskUsuarioEBloqueado extends GenericTask {

    public TaskUsuarioEBloqueado(int requestCode, OnFinishTask onFinishTask) {
        super(requestCode, onFinishTask);
    }

    @Override
    protected Object[] doInBackGround(Object... params) throws IOException, MensagensException {
        Web web = (Web)params[0];

        Usuario usuarioBloqueador = (Usuario)params[1];
        Usuario usuarioBloqueado = (Usuario)params[2];

        return new Object[]{web.usuarioEBloqueado(usuarioBloqueador,usuarioBloqueado)};
    }
}