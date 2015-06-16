package br.com.imovelhunter.tasks;

import android.os.AsyncTask;

import java.io.IOException;

import br.com.imovelhunter.dominio.Perfil;
import br.com.imovelhunter.exceptions.MensagensException;
import br.com.imovelhunter.listeners.OnFinishTask;
import br.com.imovelhunter.web.Web;

/**
 * Created by aluno on 04/05/2015.
 */
public class TaskCadastrarPerfilImovel extends AsyncTask<Object,Object,Object[]>{

    private int requestCode;

    private OnFinishTask onFinishTask;



    public TaskCadastrarPerfilImovel(int requestCode, OnFinishTask onFinishTask) {
        this.requestCode = requestCode;
        this.onFinishTask = onFinishTask;
    }

    @Override
    protected Object[] doInBackground(Object... params) {
        Web web = (Web)params[0];

        Perfil perfil = (Perfil)params[1];



        try{
            return new Object[]{web.cadastrarPerfilImovel(perfil)};
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (MensagensException e) {
            e.printStackTrace();
            return new Object[]{null,e.getMessage()};
        }
    }

    @Override
    protected void onPostExecute(Object[] objects) {
        super.onPostExecute(objects);
        if(objects == null){
            onFinishTask.finish(requestCode,0,null);
        }else{
            if(objects[0] instanceof MensagensException) {
                onFinishTask.finish(requestCode, 2, ((MensagensException) objects[0]).getMessage());
            }else{
                onFinishTask.finish(requestCode, 1,objects[0]);
            }
        }
    }
}