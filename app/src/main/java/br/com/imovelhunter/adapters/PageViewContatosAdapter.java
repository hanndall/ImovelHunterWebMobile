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

    public PageViewContatosAdapter(FragmentManager fm,FragmentListaContato fragmentListaContatoAdicionados,FragmentListaContato fragmentListaContatoBloqueados) {
        super(fm);
        this.fragmentListaContatoAdicionados = fragmentListaContatoAdicionados;
        this.fragmentListaContatoBloqueados = fragmentListaContatoBloqueados;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return position == 0 ? "Contatos adicionados" : "Contatos bloqueados";
    }

    @Override
    public Fragment getItem(int i) {
        return i == 0 ? this.fragmentListaContatoAdicionados : this.fragmentListaContatoBloqueados;
    }

    @Override
    public int getCount() {
        return 2;
    }


}
