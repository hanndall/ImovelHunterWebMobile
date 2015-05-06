package br.com.imovelhunter.tasks;

import android.os.AsyncTask;

import java.io.IOException;

import br.com.imovelhunter.exceptions.MensagensException;
import br.com.imovelhunter.listeners.OnFinishTask;
import br.com.imovelhunter.web.Web;

/**
 * Created by BOTTOMUP 05 on 07/04/2015.
 */
public class TaskLogar extends AsyncTask<Object,Object,Object[]> {

    private int requestCode;

    private OnFinishTask onFinishTask;

    public TaskLogar(int requestCode,OnFinishTask onFinishTask){
        this.requestCode = requestCode;
        this.onFinishTask = onFinishTask;
    }

    @Override
    protected Object[] doInBackground(Object... params) {
        Web web = (Web)params[0];

        String gcm = (String)params[1];

        String serial = (String)params[2];

        String login = (String)params[3];

        String senha = (String)params[4];

        try {
            return new Object[]{web.logar(gcm,serial,login,senha)};
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (MensagensException e) {
            e.printStackTrace();
            return new Object[]{e};
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
