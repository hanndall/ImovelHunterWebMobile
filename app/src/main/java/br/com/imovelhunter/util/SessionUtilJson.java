package br.com.imovelhunter.util;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import br.com.imovelhunter.enums.ParametrosSessao;
import br.com.imovelhunter.enums.ParametrosSessaoJson;

/**
 * Created by Washington Luiz on 08/05/2015.
 */
public class SessionUtilJson {

    private final static ManageFile manageFile = new ManageFile();

    private static SessionUtilJson session;


    private SessionUtilJson(){

    }



    public static SessionUtilJson getInstance(Context context){
        if(session == null){
            session = new SessionUtilJson();
        }
        manageFile.setContext(context);

        return session;
    }

    public String getJsonObject(ParametrosSessaoJson parametrosSessao){
        manageFile.setFileName(parametrosSessao.name());
        if(manageFile.existFile()){
            try {
                return manageFile.ReadFile();
            }catch(Exception ex){
                ex.printStackTrace();
                return null;
            }
        }else{
            return null;
        }
    }

    public void setJsonObject(ParametrosSessaoJson parametrosSessao,ObjetoJSON<?> objeto){
        manageFile.setFileName(parametrosSessao.name());
        manageFile.WriteFile(objeto.toString());
    }

    public boolean containsName(ParametrosSessaoJson parametrosSessao){
        manageFile.setFileName(parametrosSessao.name());
        return manageFile.existFile();
    }

    public void removeObject(ParametrosSessaoJson parametrosSessao){
        manageFile.setFileName(parametrosSessao.name());
        manageFile.deleteFile();
    }

}
