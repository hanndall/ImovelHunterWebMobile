package br.com.imovelhunter.tasks;

import android.os.AsyncTask;

import java.io.IOException;

import br.com.imovelhunter.dominio.Cliente;
import br.com.imovelhunter.exceptions.MensagensException;
import br.com.imovelhunter.listeners.OnFinishTask;
import br.com.imovelhunter.web.Web;

/**
 * Created by BOTTOMUP 05 on 09/04/2015.
 */
public class TaskCadastrarCliente extends AsyncTask<Object,Object,Object[]> {


    private int requestCode;

    private OnFinishTask onFinishTask;

    public TaskCadastrarCliente(int requestCode,OnFinishTask onFinishTask){
        this.requestCode = requestCode;
        this.onFinishTask = onFinishTask;
    }

    @Override
    protected Object[] doInBackground(Object... params) {
        Web web = (Web)params[0];

        Cliente cliente = (Cliente)params[1];

        String serial = (String)params[2];

        try {
            cliente = web.cadastraCliente(cliente,serial);
            return new Object[]{cliente};
        } catch (IOException e) {
            e.printStackTrace();
            return null;
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
