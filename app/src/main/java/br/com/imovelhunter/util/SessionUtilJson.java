package br.com.imovelhunter.util;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

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

    public Object getJsonObject(ParametrosSessaoJson parametrosSessao,Class<? extends ObjetoJSON<?>> typeClass){
        manageFile.setFileName(parametrosSessao.name());
        if(manageFile.existFile()){
            try {
                Object objClass = typeClass.newInstance();
                String resp = manageFile.ReadFile();
                ((ObjetoJSON)objClass).parse(resp);
                return objClass;
            }catch(Exception ex){
                ex.printStackTrace();
                return null;
            }
        }else{
            return null;
        }
    }

    public String getJsonArrayObject(ParametrosSessaoJson parametrosSessao){
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

    public List<?> getJsonArrayObject(ParametrosSessaoJson parametrosSessao,Class<? extends ObjetoJSON<?>> classe){
        manageFile.setFileName(parametrosSessao.name());
        if(manageFile.existFile()){
            try {
                String resp = manageFile.ReadFile();
                return this.jsonArrayToList(resp,classe);
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

    public void setJsonArrayObject(ParametrosSessaoJson parametrosSessao,List<? extends ObjetoJSON<?>> list){
        manageFile.setFileName(parametrosSessao.name());
        String string = this.listaParaJson(list);
        manageFile.WriteFile(string);
    }


    public boolean containsName(ParametrosSessaoJson parametrosSessao){
        manageFile.setFileName(parametrosSessao.name());
        return manageFile.existFile();
    }

    public void removeObject(ParametrosSessaoJson parametrosSessao){
        manageFile.setFileName(parametrosSessao.name());
        manageFile.deleteFile();
    }



    //##################
    //####ferramentas###
    //##################


    private List<?> jsonArrayToList(String jsonArray,Class<?> classe) throws JSONException, IllegalAccessException, InstantiationException {
        List<Object> lista = new ArrayList<Object>();

        JSONArray array = new JSONArray(jsonArray);

        int tam = array.length();

        for(int i = 0 ; i < tam ; i++){

            Object objeto = classe.newInstance();

            ((ObjetoJSON)objeto).parse(array.getJSONObject(i).toString());

            lista.add(objeto);

        }

        return lista;
    }

    private String listaParaJson(List<? extends ObjetoJSON<?>> lista) {
        StringBuffer stb = new StringBuffer();

        stb.append("[");

        int tam = 0;
        if(lista != null && (tam = lista.size()) > 0){

            int cont = 0;
            Boolean fim = false;

            for(Object o : lista){

                if(cont + 1 == tam){
                    fim = true;
                }


                if(fim){

                    stb.append(o.toString());

                }else{

                    stb.append(o.toString());
                    stb.append(",");

                }

                cont++;
            }


        }

        stb.append("]");


        return stb.toString();
    }


}
