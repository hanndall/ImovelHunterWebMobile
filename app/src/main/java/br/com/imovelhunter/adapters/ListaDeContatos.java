package br.com.imovelhunter.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.imovelhunter.dominio.Bloqueio;
import br.com.imovelhunter.dominio.Mensagem;
import br.com.imovelhunter.dominio.Usuario;
import br.com.imovelhunter.imovelhunterwebmobile.R;

/**
 * Created by Fabio Souza on 08/05/2015.
 */
public class ListaDeContatos extends BaseAdapter {

    private List<Usuario> listaContatos;

    public  ListaDeContatos(List<Usuario> listaContatos){
        this.listaContatos = listaContatos;
    }

    @Override
    public int getCount() {
        return listaContatos.size();
    }

    @Override
    public Object getItem(int position) {
        return listaContatos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder vb = null;
        if (convertView == null){
            vb = new ViewHolder();

            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contato, null, false);
            vb.txt = (TextView)convertView.findViewById(R.id.txtNomeContato);
            vb.txtMe = (TextView)convertView.findViewById(R.id.txtStatus);
            vb.txtUltima = (TextView)convertView.findViewById(R.id.txtUltimaMsg);
        }

        convertView.setTag(vb);
        return convertView;

    }

    private class ViewHolder{

        TextView txt;
        TextView txtMe;
        TextView txtUltima;

    }
}
