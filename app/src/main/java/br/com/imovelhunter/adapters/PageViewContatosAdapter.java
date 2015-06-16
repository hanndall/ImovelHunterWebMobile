package br.com.imovelhunter.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import br.com.imovelhunter.fragments.FragmentListaContato;

/**
 * Created by Washington Luiz on 20/05/2015.
 */
public class PageViewContatosAdapter extends FragmentPagerAdapter {

    private FragmentListaContato fragmentListaContatoAdicionados;
    private FragmentListaContato fragmentListaContatoBloqueados;
    private FragmentListaContato fragmentListaContatoMensagens;

    private final String[] flip = new String[]{"Mensagens","Contatos adicionados","Contatos bloqueados"};

    private final FragmentListaContato[] fragmentos;

    public PageViewContatosAdapter(FragmentManager fm,FragmentListaContato fragmentListaContatoAdicionados,FragmentListaContato fragmentListaContatoBloqueados,FragmentListaContato fragmentListaContatoMensagens) {
        super(fm);
        this.fragmentListaContatoAdicionados = fragmentListaContatoAdicionados;
        this.fragmentListaContatoBloqueados = fragmentListaContatoBloqueados;
        this.fragmentListaContatoMensagens = fragmentListaContatoMensagens;

        fragmentos = new FragmentListaContato[]{fragmentListaContatoMensagens,fragmentListaContatoAdicionados,fragmentListaContatoBloqueados};
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return flip[position];
    }

    @Override
    public Fragment getItem(int i) {
        return fragmentos[i];
    }

    @Override
    public int getCount() {
        return fragmentos.length;
    }


}
