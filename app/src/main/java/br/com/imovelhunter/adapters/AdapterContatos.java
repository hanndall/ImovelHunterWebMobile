package br.com.imovelhunter.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.imovelhunter.dao.MensagemDAO;
import br.com.imovelhunter.dominio.Mensagem;
import br.com.imovelhunter.dominio.Usuario;
import br.com.imovelhunter.enums.ParametrosSessaoJson;
import br.com.imovelhunter.imovelhunterwebmobile.R;
import br.com.imovelhunter.util.SessionUtilJson;

/**
 * Created by Washington Luiz on 20/05/2015.
 */
public class AdapterContatos extends BaseAdapter {

    private List<Usuario> lista;

    private MensagemDAO mensagemDAO;

    private Context context;

    private Usuario usuarioLogado;

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

            if(context == null) {
                context = viewGroup.getContext();
                mensagemDAO = new MensagemDAO(context);

                if(SessionUtilJson.getInstance(context).containsName(ParametrosSessaoJson.USUARIO_LOGADO)){
                    this.usuarioLogado = (Usuario)SessionUtilJson.getInstance(context).getJsonObject(ParametrosSessaoJson.USUARIO_LOGADO,Usuario.class);
                }
            }

            view = LayoutInflater.from(context).inflate(R.layout.item_contato,null);
            vh = new ViewHolder();

            vh.nomeContato = (TextView)view.findViewById(R.id.textView);
            vh.numeroDeMensagens = (TextView)view.findViewById(R.id.textView2);

            view.setTag(vh);
        }else{
            vh = (ViewHolder)view.getTag();
        }


        vh.nomeContato.setText(usuario.getNomeUsuario());
        vh.numeroDeMensagens.setText("");

        if(mensagemDAO != null){
            List<Mensagem> mensNaoLidas = mensagemDAO.listarMensagensNaoLidasDaConversa(usuarioLogado.getIdUsuario(),usuario.getIdUsuario());
            if(mensNaoLidas != null && mensNaoLidas.size() > 0){
                vh.numeroDeMensagens.setText(String.valueOf(mensNaoLidas.size()));
            }
        }


        return view;
    }

    private class ViewHolder{

        TextView nomeContato;
        TextView numeroDeMensagens;

    }

}
