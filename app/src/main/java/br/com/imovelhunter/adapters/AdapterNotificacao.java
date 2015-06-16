package br.com.imovelhunter.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import br.com.imovelhunter.dominio.Imagem;
import br.com.imovelhunter.dominio.Notificacao;
import br.com.imovelhunter.imovelhunterwebmobile.R;

/**
 * Created by Rodrigo on 17/05/2015.
 */
public class AdapterNotificacao extends BaseAdapter {

    private List<Notificacao> mNotificacoes;

    public AdapterNotificacao(List<Notificacao> mNotificacoes) {

        this.mNotificacoes = mNotificacoes;
    }


    public List<Notificacao> getmNotificacoes() {
        return mNotificacoes;
    }

    @Override
    public int getCount() {
        return mNotificacoes.size();
    }

    @Override
    public Object getItem(int i) {
        return mNotificacoes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        Notificacao notificacao = mNotificacoes.get(i);

        ViewHolder holder;

        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_lista_notification, null);
            holder = new ViewHolder();
            holder.preco = (TextView)view.findViewById(R.id.txtItemPreco);
            holder.numero = (TextView) view.findViewById(R.id.txtItemNumero);
            holder.rua = (TextView) view.findViewById(R.id.txtItemRua);
            holder.situacao = (TextView) view.findViewById(R.id.txtItemSituacao);
            holder.tipo = (TextView) view.findViewById(R.id.txtItemTipo);
            holder.imagem = (ImageView) view.findViewById(R.id.item_imagem_imovel);
            view.setTag(holder);

        } else {
            holder = (ViewHolder)view.getTag();
        }

        ;

        if(notificacao.getImagem() != null) {
            Picasso.with(viewGroup.getContext()).load("http://ec2-54-68-17-181.us-west-2.compute.amazonaws.com/imovelhunterweb/servidor/imagens/" + notificacao.getImagem()).into(holder.imagem);
        }else{
            holder.imagem.setImageDrawable(viewGroup.getContext().getResources().getDrawable(R.drawable.icone));
        }

        holder.rua.setText(notificacao.getRua());
        holder.tipo.setText(notificacao.getTipo());
        holder.numero.setText(notificacao.getNumero()+" - ");
        holder.situacao.setText(notificacao.getSituacao());
        holder.preco.setText(Double.toString(notificacao.getPreco()));


        return view;
    }



    static class ViewHolder {
        TextView preco;
        TextView numero;
        TextView rua;
        TextView situacao;
        TextView tipo;
        ImageView imagem;
    }
}
