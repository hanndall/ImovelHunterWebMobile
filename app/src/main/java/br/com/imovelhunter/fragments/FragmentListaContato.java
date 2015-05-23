package br.com.imovelhunter.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import br.com.imovelhunter.adapters.AdapterContatos;
import br.com.imovelhunter.dominio.Usuario;
import br.com.imovelhunter.imovelhunterwebmobile.R;

/**
 * Created by Washington Luiz on 20/05/2015.
 */
public class FragmentListaContato extends Fragment{

    private List<Usuario> listaContatos;

    private ListView listView;

    private AdapterContatos adapterContatos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.listaContatos = new ArrayList<Usuario>();

        this.adapterContatos = new AdapterContatos(this.listaContatos);

        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmeng_lista_contatos,container,false);

        listView = (ListView)view.findViewById(R.id.listView);

        listView.setAdapter(this.adapterContatos);

        if(getActivity() instanceof OnClickItemListListener){
            this.onClickItemListListener = (OnClickItemListListener)getActivity();
        }
        if(getActivity() instanceof OnHoldItemListListener){
            this.onHoldItemListListener = (OnHoldItemListListener)getActivity();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(onClickItemListListener != null){
                    onClickItemListListener.clickLista(listaContatos.get(i));
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                if(onHoldItemListListener != null){
                    onHoldItemListListener.holdLista(listaContatos.get(i));
                    return true;
                }

                return false;
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public List<Usuario> getListaContatos() {
        return listaContatos;
    }

    public AdapterContatos getAdapterContatos() {
        return adapterContatos;
    }

    private OnClickItemListListener onClickItemListListener;

    public interface OnClickItemListListener{
        public void clickLista(Usuario usuario);
    }

    private OnHoldItemListListener onHoldItemListListener;
    public interface OnHoldItemListListener{
        public void holdLista(Usuario usuario);
    }

    public void setOnClickItemListListener(OnClickItemListListener onClickItemListListener) {
        this.onClickItemListListener = onClickItemListListener;
    }

    public void setOnHoldItemListListener(OnHoldItemListListener onHoldItemListListener) {
        this.onHoldItemListListener = onHoldItemListListener;
    }


}
