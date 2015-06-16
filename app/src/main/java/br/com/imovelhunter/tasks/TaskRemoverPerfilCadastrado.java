package br.com.imovelhunter.tasks;

import java.io.IOException;

import br.com.imovelhunter.dominio.Perfil;
import br.com.imovelhunter.dominio.Usuario;
import br.com.imovelhunter.exceptions.MensagensException;
import br.com.imovelhunter.listeners.OnFinishTask;
import br.com.imovelhunter.web.Web;

/**
 * Created by Washington Luiz on 07/06/2015.
 */
public class TaskRemoverPerfilCadastrado extends GenericTask {

    public TaskRemoverPerfilCadastrado(int requestCode, OnFinishTask onFinishTask) {
        super(requestCode, onFinishTask);
    }

    @Override
    protected Object[] doInBackGround(Object... params) throws IOException, MensagensException {
        Web web = (Web)params[0];

        Perfil perfil = (Perfil)params[1];

        return new Object[]{web.removePerfilCadastrado(perfil)};
    }
}
