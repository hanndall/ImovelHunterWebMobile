package br.com.imovelhunter.tasks;

import android.os.AsyncTask;

import java.io.IOException;

import br.com.imovelhunter.listeners.OnFinishTask;
import br.com.imovelhunter.web.Web;

public class TaskCadastroGCM extends AsyncTask<Object,Object,Boolean> {

    private int requestCode;

    private OnFinishTask onFinishTask;

    public TaskCadastroGCM(int requestCode,OnFinishTask onFinishTask){
        this.requestCode = requestCode;
        this.onFinishTask = onFinishTask;
    }

    @Override
    protected Boolean doInBackground(Object... params) {
        Web web = (Web)params[0];

        String chaveGcm = (String)params[1];

        String serialDispositivo = (String)params[2];

        try {
            return web.cadastrarGCM(chaveGcm,serialDispositivo);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

        onFinishTask.finish(requestCode,aBoolean ? 1 : 0,null);

    }

}
