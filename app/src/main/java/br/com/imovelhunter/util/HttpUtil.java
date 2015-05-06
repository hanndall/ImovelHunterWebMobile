package br.com.imovelhunter.util;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by BOTTOMUP 05 on 26/11/2014.
 */
public class HttpUtil {

    private String url;

    private HttpPost httppost;
    private HttpClient httpclient;
    List<NameValuePair> nameValuePairs;

    public HttpUtil(String url){
        this.url = url;
        this.httppost = new HttpPost(this.url);
        this.nameValuePairs = new ArrayList<NameValuePair>();
        this.httpclient = new DefaultHttpClient();
    }


    //#################
    //####publics#####
    //#################

    public void put(String chave,String valor){
        nameValuePairs.add(new BasicNameValuePair(chave,valor));
    }



    public void clear(){
        this.nameValuePairs.clear();
    }


    public void setUrl(String url){
        this.url = url;
    }



    public String enviarRequest() throws IOException {
        String downloadedString = null;
        try{
            //add data
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);

            InputStream in = response.getEntity().getContent();
            StringBuilder stringbuilder = new StringBuilder();
            BufferedReader bfrd = new BufferedReader(new InputStreamReader(in),1024);
            String line;
            while((line = bfrd.readLine()) != null)
                stringbuilder.append(line);

            downloadedString = stringbuilder.toString();

        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return downloadedString;

    }





    //##################
    //####ferramentas###
    //##################


    public List<?> jsonArrayToList(String jsonArray,Class<?> classe) throws JSONException, IllegalAccessException, InstantiationException {
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

    public String listaParaJson(List<? extends ObjetoJSON<?>> lista) {
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





    //#################
    //####privates#####
    //#################

    /*private String montarParametros(){
        Iterator<String> chaves = this.parametros.keySet().iterator();
        StringBuffer buffer = new StringBuffer();

        Boolean removerUltimo = false;
        while(chaves.hasNext()){
            removerUltimo = true;
            String chave = chaves.next();
            String valor = this.parametros.get(chave);

            buffer.append(chave);
            buffer.append("=");
            buffer.append(valor);
            buffer.append("&");
        }

        if(removerUltimo){
            buffer.deleteCharAt(buffer.length() - 1);
        }


        return buffer.toString();
    }*/

}
