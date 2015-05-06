package br.com.imovelhunter.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.Map;

import br.com.imovelhunter.dominio.Imovel;
import br.com.imovelhunter.imovelhunterwebmobile.R;


public class MapaInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Context context;

    private View view;

    private ImageView imagem;

    private TextView proprietario;

    private TextView cep;

    private TextView numero;

    private TextView endereco;

    private TextView cidade;

    private TextView bairro;

    private TextView preco;

    private Map<Marker,Imovel> mapImovel;

    private Button botaoLigar;

    private Button botaoEnviarMensagem;

    private Imovel imovel;

    public MapaInfoWindowAdapter(Context contexto,Map<Marker,Imovel> mapImovel){
        //Fazer o layout e jogar ali no lugar do zero ex: R.layout.layoutjanelinha
        this.context = contexto;
        this.mapImovel = mapImovel;
        int idDoLayou = R.layout.layout_info_window_imovel;
        if(idDoLayou != 0) {
            this.view = LayoutInflater.from(contexto).inflate(idDoLayou, null);
            this.imagem = (ImageView)this.view.findViewById(R.id.imageView);
            this.proprietario = (TextView)this.view.findViewById(R.id.textViewProprietario);
            this.cep = (TextView)this.view.findViewById(R.id.textViewCep);
            this.numero = (TextView)this.view.findViewById(R.id.textViewNumero);
            this.endereco = (TextView)this.view.findViewById(R.id.textViewEndereco);
            this.cidade = (TextView)this.view.findViewById(R.id.textViewCidade);
            this.bairro = (TextView)this.view.findViewById(R.id.textViewBairro);
            this.preco = (TextView)this.view.findViewById(R.id.textViewPreco);
            this.botaoLigar = (Button)this.view.findViewById(R.id.buttonLigar);
            this.botaoEnviarMensagem = (Button)this.view.findViewById(R.id.buttonEnviarMensagem);
            this.botaoLigar.setVisibility(View.GONE);

            this.botaoEnviarMensagem.setOnClickListener(this.clickBotaoEnviarMensagem);
        }
    }

    private View.OnClickListener clickBotaoEnviarMensagem = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(context,imovel.getAnunciante().getNome(),Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {

        this.imovel = this.mapImovel.get(marker);

        if (this.imovel.getImagens() != null && this.imovel.getImagens().size() > 0){
            Picasso.with(this.context).load(this.imovel.getImagens().get(0).getCaminhoImagem()).into(this.imagem);
        }

        this.proprietario.setText(this.imovel.getNomeDoProprietario());
        this.cep.setText(this.imovel.getCep());
        this.numero.setText(this.imovel.getNumeroDoImovel());
        this.endereco.setText(this.imovel.getLogradouro());
        this.cidade.setText(this.imovel.getCidade());
        this.bairro.setText(this.imovel.getBairro());
        this.preco.setText(String.valueOf(this.imovel.getPreco()));

        return this.view;
    }
}
