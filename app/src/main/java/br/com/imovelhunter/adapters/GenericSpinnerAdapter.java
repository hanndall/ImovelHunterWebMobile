package br.com.imovelhunter.adapters;

import android.content.Context;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.imovelhunter.imovelhunterwebmobile.R;
import br.com.imovelhunter.util.ReflectUtil;

/**
 * Created by Washington Luiz on 21/04/2015.
 */
public class GenericSpinnerAdapter<T> extends BaseAdapter {

    private List<T> lista;
    private String nomeCampo;
    private String idCampo;
    ReflectUtil refUtil;


    public GenericSpinnerAdapter(List<T> lista,String idCampo,String nomeCampo){
        this.refUtil = new ReflectUtil();
        this.lista = lista;
        this.nomeCampo = nomeCampo;
        this.idCampo = idCampo;
    }


    @Override
    public int getCount() {
        return this.lista.size();
    }

    @Override
    public Object getItem(int i) {
        return this.lista.get(i);
    }

    @Override
    public long getItemId(int i) {
        try {
            T iV = this.lista.get(i);
            long id = (Long)this.refUtil.get(iV,this.idCampo);
            return Integer.valueOf(String.valueOf(id));
        }
        catch(Exception ex){
            Log.e("ADAPTERERRO",ex.getMessage());
            return 0;
        }
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        T item = this.lista.get(i);
        String nome = null;
        ViewHolder vh = null;
        try {
            nome = (String)this.refUtil.get(item,this.nomeCampo);
        } catch (NoSuchFieldException e) {
            Log.e("ADAPTERERRO",e.getMessage());
            return null;
        } catch (IllegalAccessException e) {
            Log.e("ADAPTERERRO",e.getMessage());
            return null;
        }

        if(view == null){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_simples_inf,null,false);
            vh = new ViewHolder();

            vh.nome = (TextView)view.findViewById(R.id.textView);

            vh.nome.setText(nome);

            view.setTag(vh);
        }else{
            vh = (ViewHolder)view.getTag();

            vh.nome.setText(nome);
        }

        return view;
    }

    private class ViewHolder{
        TextView nome;
    }

}
