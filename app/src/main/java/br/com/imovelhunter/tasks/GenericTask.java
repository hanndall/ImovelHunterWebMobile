package br.com.imovelhunter.tasks;

import android.os.AsyncTask;

import java.io.IOException;

import br.com.imovelhunter.dominio.Filtro;
import br.com.imovelhunter.exceptions.MensagensException;
import br.com.imovelhunter.listeners.OnFinishTask;
import br.com.imovelhunter.web.Web;

/**
 * Created by BOTTOMUP 05 on 28/04/2015.
 */
public abstract class GenericTask extends AsyncTask<Object,Object,Object[]> {

    private int requestCode;

    private OnFinishTask onFinishTask;

    public GenericTask(int requestCode,OnFinishTask onFinishTask){
        this.requestCode = requestCode;
        this.onFinishTask = onFinishTask;
    }


    @Override
    protected Object[] doInBackground(Object... params) {
        try {
            return this.doInBackGround(params);
        }catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (MensagensException e) {
            e.printStackTrace();
            return new Object[]{null,e.getMessage()};
        }
    }

    protected abstract Object[] doInBackGround(Object... params) throws IOException,MensagensException;


    @Override
    protected void onPostExecute(Object[] results) {
        super.onPostExecute(results);

        if(results == null){
            //Erro quando foi enviar a requisição
            onFinishTask.finish(requestCode,0,null);
        }else{
            if(results[0] == null){
                //Mensagem de erro
                onFinishTask.finish(requestCode,2,results[1]);
            }else{
                //Tudo ocorreu bem
                onFinishTask.finish(requestCode,1,results[0]);
            }
        }
    }


}
