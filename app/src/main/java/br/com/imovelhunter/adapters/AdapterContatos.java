package br.com.imovelhunter.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.imovelhunter.dominio.Usuario;
import br.com.imovelhunter.imovelhunterwebmobile.R;

/**
 * Created by Washington Luiz on 20/05/2015.
 */
public class AdapterContatos extends BaseAdapter {

    private List<Usuario> lista;

    public AdapterContatos(List<Usuario> lista){
        this.lista = lista;
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
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Usuario usuario = this.lista.get(i);
        ViewHolder vh = null;

        if(view == null){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_contato,null);
            vh = new ViewHolder();

            vh.nomeContato = (TextView)view.findViewById(R.id.textView);

            vh.nomeContato.setText(usuario.getNomeUsuario());

            view.setTag(vh);
        }else{
            vh = (ViewHolder)view.getTag();

            vh.nomeContato.setText(usuario.getNomeUsuario());
        }

        return view;
    }

    private class ViewHolder{

        TextView nomeContato;

    }

}
