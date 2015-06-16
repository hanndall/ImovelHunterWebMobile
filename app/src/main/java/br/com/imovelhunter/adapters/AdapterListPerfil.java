package br.com.imovelhunter.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.imovelhunter.dominio.Perfil;
import br.com.imovelhunter.enums.SituacaoImovel;
import br.com.imovelhunter.enums.TipoImovel;
import br.com.imovelhunter.imovelhunterwebmobile.R;

/**
 * Created by Washington Luiz on 07/06/2015.
 */
public class AdapterListPerfil extends BaseAdapter{

    private List<Perfil> listaPerfil;

    public AdapterListPerfil(List<Perfil> listaPerfil){
        this.listaPerfil = listaPerfil;
    }

    @Override
    public int getCount() {
        return this.listaPerfil.size();
    }

    @Override
    public Object getItem(int i) {
        return this.listaPerfil.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Perfil p = this.listaPerfil.get(i);
        ViewHolder vh = null;

        if(view == null){
            vh = new ViewHolder();
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_perfil_lista,null);

            vh.situacaoImovel = (TextView)view.findViewById(R.id.textViewSituacaoImovel);
            vh.uf = (TextView)view.findViewById(R.id.textViewUf);
            vh.cidade = (TextView)view.findViewById(R.id.textViewCidade);
            vh.bairro = (TextView)view.findViewById(R.id.textViewBairro);
            vh.tipo = (TextView)view.findViewById(R.id.textViewTipo);
            vh.qtdQuartos = (TextView)view.findViewById(R.id.textViewQtdQuartos);
            vh.valor = (TextView)view.findViewById(R.id.textViewValor);

            view.setTag(vh);
        }else{
            vh = (ViewHolder)view.getTag();
        }

        this.parse(p,vh);

        return view;
    }

    private class ViewHolder{
        TextView situacaoImovel;
        TextView uf;
        TextView cidade;
        TextView bairro;
        TextView tipo;
        TextView qtdQuartos;
        TextView valor;
    }


    private void parse(Perfil p,ViewHolder vh){
        SituacaoImovel situacaoImovel = p.getSituacaoImovel();
        String uf = p.getUf();
        String cidade = p.getCidade();
        String bairro = p.getBairro();
        TipoImovel tipo = p.getTipo();
        Integer qtdQuartos = p.getQtQuartos();
        Double valor = p.getValor();

        boolean locacao = false;

        if(situacaoImovel != null){
            if(situacaoImovel.equals(SituacaoImovel.LOCACAO)){
                locacao = true;
                vh.situacaoImovel.setText("Situação: Alugar");
            }else if(situacaoImovel.equals(SituacaoImovel.VENDA)){
                vh.situacaoImovel.setText("Situação: Compra");
            }
            if(uf != null && uf.length() > 0){
                vh.uf.setText("Estado: "+uf);
            }
            if(cidade != null && cidade.length() > 0){
                vh.cidade.setText("Cidade: "+cidade);
            }
            if(bairro != null && bairro.length() > 0){
                vh.bairro.setText("Bairro: "+bairro);
            }
            if(tipo != null){
                vh.tipo.setText("Tipo de imóvel: "+tipo.name());
            }else{
                vh.tipo.setText("Tipo de imóvel: Todos os tipos");
            }

            if(qtdQuartos != null){
                if(qtdQuartos == 0){
                    vh.qtdQuartos.setText("Todos");
                }
                else if(qtdQuartos == 1){
                    vh.qtdQuartos.setText("1 quarto");
                }
                else if(qtdQuartos == 2){
                    vh.qtdQuartos.setText("2 quartos");
                }
                else if(qtdQuartos == 3){
                    vh.qtdQuartos.setText("3 quartos");
                }
                else if(qtdQuartos == 4){
                    vh.qtdQuartos.setText("4 quartos");
                }
                else if(qtdQuartos == 5){
                    vh.qtdQuartos.setText("5 ou mais quartos");
                }
            }

            if(valor != null){
                if(locacao){
                    String stringValor = String.valueOf(valor);
                    stringValor = stringValor.substring(0,stringValor.indexOf('.'));
                    int valorInt = Integer.valueOf(stringValor);
                    switch (valorInt){
                        case 0:
                            vh.valor.setText("Valor: Qualquer valor...");
                            break;
                        case 1:
                            vh.valor.setText("Valor: Ate R$ 1000");
                            break;
                        case 2:
                            vh.valor.setText("Valor: De R$ 1001 a R$ 1500");
                            break;
                        case 3:
                            vh.valor.setText("Valor: De R$ 1501 a R$ 2000");
                            break;
                        case 4:
                            vh.valor.setText("Valor: De R$ 2001 a R$ 2500");
                            break;
                        case 5:
                            vh.valor.setText("Valor: De R$ 2501 a R$ 3000");
                            break;
                        case 6:
                            vh.valor.setText("Valor: De R$ 3001 a R$ 5000");
                            break;
                        case 7:
                            vh.valor.setText("Valor: Acima de R$ 5000");
                            break;
                    }
                }else{
                    String stringValor = String.valueOf(valor);
                    stringValor = stringValor.substring(0,stringValor.indexOf('.'));
                    int valorInt = Integer.valueOf(stringValor);
                    switch (valorInt){
                        case 0:
                            vh.valor.setText("Valor: Qualquer valor...");
                            break;
                        case 1:
                            vh.valor.setText("Valor: Ate R$ 135 mil");
                            break;
                        case 2:
                            vh.valor.setText("Valor: De R$ 136 mil a R$ 250 mil");
                            break;
                        case 3:
                            vh.valor.setText("Valor: De R$ 251 mil a R$ 350");
                            break;
                        case 4:
                            vh.valor.setText("Valor: De R$ 350 mil a R$ 500 mil");
                            break;
                        case 5:
                            vh.valor.setText("Valor: De R$ 501 mil a R$ 700 mil");
                            break;
                        case 6:
                            vh.valor.setText("Valor: De R$ 701 mil a R$ 1 milhão");
                            break;
                        case 7:
                            vh.valor.setText("Valor: Acima de R$ 1 milhão");
                            break;
                    }
                }
            }
        }
    }
}
