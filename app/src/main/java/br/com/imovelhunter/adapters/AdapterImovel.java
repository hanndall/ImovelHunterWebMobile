package br.com.imovelhunter.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import br.com.imovelhunter.dominio.Imovel;
import br.com.imovelhunter.imovelhunterwebmobile.R;

/**
 * Created by FAGNER on 13/05/2015.
 */
public class AdapterImovel extends BaseAdapter {

    private List<Imovel> mListaImoveis;

    public AdapterImovel(List<Imovel> mListaImoveis){
        this.mListaImoveis = mListaImoveis;

    }
    @Override
    public int getCount() {
        return mListaImoveis.size();
    }

    @Override
    public Object getItem(int position) {
        return mListaImoveis.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Imovel imovel = mListaImoveis.get(i);

        ViewHolder holder;
        if(view == null){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_lista_imovel, null);
            holder = new ViewHolder();
            holder.preco = (TextView)view.findViewById(R.id.txtItemPreco);
            holder.numero = (TextView) view.findViewById(R.id.txtItemNumero);
            holder.bairro = (TextView) view.findViewById(R.id.txtItemBairro);
            holder.situacao = (TextView) view.findViewById(R.id.txtItemSituacao);
            holder.tipo = (TextView) view.findViewById(R.id.txtItemTipo);
            holder.imagem = (ImageView) view.findViewById(R.id.item_imagem_imovel);
            view.setTag(holder);
        }
        else {
            holder = (ViewHolder)view.getTag();
        }

        holder.bairro.setText(imovel.getBairro());
        holder.tipo.setText(imovel.getTipoImovel().toString());
        holder.numero.setText(imovel.getNumeroDoImovel()+" - ");
        holder.situacao.setText(imovel.getSituacaoImovel().toString());
        holder.preco.setText(Double.toString(imovel.getPreco()));

        if(imovel.getImagens() != null && imovel.getImagens().size() > 0) {
            String caminhoImagem = "http://ec2-54-68-17-181.us-west-2.compute.amazonaws.com/imovelhunterweb/servidor/imagens/" + imovel.getImagens().get(0).getCaminhoImagem();
            Picasso.with(viewGroup.getContext()).load(caminhoImagem).into(holder.imagem);
        }else{
            holder.imagem.setImageDrawable(viewGroup.getContext().getResources().getDrawable(R.drawable.icone));
        }


        return view;

    }

    static class ViewHolder {
        TextView preco;
        TextView numero;
        TextView bairro;
        TextView situacao;
        TextView tipo;
        ImageView imagem;

    }
}
