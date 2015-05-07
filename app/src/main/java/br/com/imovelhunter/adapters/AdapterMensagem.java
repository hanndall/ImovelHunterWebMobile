package br.com.imovelhunter.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.imovelhunter.dominio.Mensagem;
import br.com.imovelhunter.dominio.Usuario;
import br.com.imovelhunter.imovelhunterwebmobile.R;

/**
 * Created by BOTTOMUP 05 on 05/05/2015.
 */
public class AdapterMensagem extends BaseAdapter {

    private Usuario meuUsuario;
    // teste commit
    private List<Mensagem> listaMensagem;

    public AdapterMensagem(List<Mensagem> listaMensagem,Usuario meuUsuario){
        this.listaMensagem = listaMensagem;
        this.meuUsuario = meuUsuario;
    }

    @Override
    public int getCount() {
        return listaMensagem.size();
    }

    @Override
    public Object getItem(int position) {
        return listaMensagem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder vh = null;

        Mensagem mensagem = listaMensagem.get(position);

        if(view == null){
            vh = new ViewHolder();

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mensagem, null, false);

            vh.txt = (TextView)view.findViewById(R.id.textView);
            vh.txtMe = (TextView)view.findViewById(R.id.textViewMe);


            if(mensagem.getUsuarioRemetente().getIdUsuario() == meuUsuario.getIdUsuario()){
                vh.txt.setVisibility(View.INVISIBLE);
                vh.txtMe.setVisibility(View.VISIBLE);
                vh.txtMe.setText(mensagem.getUsuarioRemetente().getNomeUsuario()+":"+mensagem.getMensagem());
            }else{
                vh.txt.setVisibility(View.VISIBLE);
                vh.txtMe.setVisibility(View.INVISIBLE);
                vh.txt.setText(mensagem.getUsuarioRemetente().getNomeUsuario()+":"+mensagem.getMensagem());
            }



            view.setTag(vh);
        }else{
            vh = (ViewHolder)view.getTag();

            if(mensagem.getUsuarioRemetente().getIdUsuario() == meuUsuario.getIdUsuario()){
                vh.txt.setVisibility(View.INVISIBLE);
                vh.txtMe.setVisibility(View.VISIBLE);
                vh.txtMe.setText(mensagem.getUsuarioRemetente().getNomeUsuario()+":"+mensagem.getMensagem());
            }else{
                vh.txt.setVisibility(View.VISIBLE);
                vh.txtMe.setVisibility(View.INVISIBLE);
                vh.txt.setText(mensagem.getUsuarioRemetente().getNomeUsuario()+":"+mensagem.getMensagem());
            }





        }


        return view;
    }

    private class ViewHolder{

        TextView txt;
        TextView txtMe;

    }

}
