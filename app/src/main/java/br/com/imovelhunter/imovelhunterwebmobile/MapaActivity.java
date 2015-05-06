package br.com.imovelhunter.imovelhunterwebmobile;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.imovelhunter.adapters.MapaInfoWindowAdapter;
import br.com.imovelhunter.dialogs.DialogAlerta;
import br.com.imovelhunter.dominio.Cliente;
import br.com.imovelhunter.dominio.Imovel;
import br.com.imovelhunter.dominio.Localizacao;
import br.com.imovelhunter.enums.ParametrosSessao;
import br.com.imovelhunter.listeners.OnFinishTask;
import br.com.imovelhunter.tasks.TaskListarImoveis;
import br.com.imovelhunter.util.GpsUtil;
import br.com.imovelhunter.util.SessionUtil;
import br.com.imovelhunter.web.Web;
import br.com.imovelhunter.web.WebImp;


public class MapaActivity extends ActionBarActivity implements GpsUtil.OnCoordenadaRecebida,OnFinishTask,DialogAlerta.RespostaSim{

    private GoogleMap map;

    private LatLng latLng;

    private GpsUtil gpsUtil;

    private ProgressDialog dialog;

    private List<Marker> marcadores;

    private MapaInfoWindowAdapter mapaInfoWindowAdapter;

    private String gcm;

    private String serial;

    private Web web;

    private List<Imovel> listaImovel;

    private Map<Marker,Imovel> mapImovel;

    private final int CLIQUE_MARCADOR = 0;

    private final int CLIQUE_TELA_LOGIN = 1;

    private final int CLIQUE_TELA_CADASTRO = 2;

    private final int CLIQUE_DESLOGAR = 3;

    private final int CLIQUE_LUPA = 4;

    private Cliente cliente;

    private ImageView lupa;


    //private Object usuarioLogado; definir o tipo do usuarioLogado


    //Picasso.with(context).load("url").into(imoveview);
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        ActionBar bar = getActionBar();
        if (bar != null) {
            bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#315e8a")));
        }

        this.lupa = (ImageView)this.findViewById(R.id.imageViewFiltrar);

        this.lupa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MapaActivity.this,FiltroActivity.class);
                startActivityForResult(in,CLIQUE_LUPA);
            }
        });


        this.mapImovel = new HashMap<Marker, Imovel>();

        this.web = new WebImp();

        Intent intent = getIntent();

        this.gcm = intent.getStringExtra("gcm");
        this.serial = intent.getStringExtra("serial");


        //this.usuarioLogado = intent.getSerializableExtra("usuarioLogado"); //Depois definir o tipo de objeto usuarioLogado
        //if(this.usuarioLogado == null){ definir o que será feito
        //}

        this.gpsUtil = new GpsUtil(this);
        this.gpsUtil.ativarListeners();


        this.marcadores = new ArrayList<Marker>();

        this.latLng = new LatLng(intent.getDoubleExtra("latitude",0.0),intent.getDoubleExtra("longitude",0.0));

        //Só aparecerá o mapa após pegar a coordenada
        //this.map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(this.latLng, 15));

        this.dialog = new ProgressDialog(this);
        //this.dialog.setIcon(getResources().getDrawable(R.drawable.ic_launcher)); definir ícone lá na hora
        this.dialog.setTitle("Processando...");
        dialog.setCancelable(false);
        this.dialog.setMessage("");


        int idMapa = R.id.map; // coloque aqui o id do mapa do layout ex : R.id.map
        this.map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(idMapa)).getMap();


        this.map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setCompassEnabled(true);

        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                latLng = new LatLng(location.getLatitude(),location.getLongitude());

                map.setOnMarkerClickListener(clicouMarcadorMapa);

                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                map.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);
                map.setOnMyLocationChangeListener(null);
            }
        });


        //Manda listar todos os imoveis
        new TaskListarImoveis(1,this).execute(this.web);

    }

    /**
     * Adiciona um ponto no mapa, podendo inserir título e ícone
     * @param latitude
     * @param longitude
     * @param titulo
     * @param idDrawableImagem - exemplo de id : R.drawable.icone
     */
    private Marker addMark(double latitude,double longitude,String titulo,int idDrawableImagem){
        LatLng latLng = new LatLng(latitude,longitude);
        Marker marcador = this.map.addMarker(new MarkerOptions().position(latLng).title(titulo));
        marcador.setIcon(BitmapDescriptorFactory.fromResource(idDrawableImagem));
        this.marcadores.add(marcador);
        return marcador;
    }

    /**
     * Limpa todos os marcadores do mapa
     */
    private void clearMarks(){
        this.map.clear();
        this.marcadores.clear();
    }

    private Boolean verificaInternet(){
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }else{
            Toast.makeText(this, "Sem conexão com a internet", Toast.LENGTH_LONG).show();
            return false;
        }
    }


    /**
     * Quando clicar em um ícone do mapa
     */
    private GoogleMap.OnMarkerClickListener clicouMarcadorMapa = new GoogleMap.OnMarkerClickListener() {

        @Override
        public boolean onMarkerClick(Marker marker) {

            Imovel imovelSelecionado = mapImovel.get(marker);

            SessionUtil.setObject(ParametrosSessao.IMOVEL_SELECIONADO,imovelSelecionado);

            Intent intent = new Intent(MapaActivity.this,DetalheImovelActivity.class);

            startActivityForResult(intent,CLIQUE_MARCADOR);

            return false;
        }

    };

    @Override
    @Deprecated
    public void chegouCoordenada(Localizacao localizacao) {

        this.latLng = new LatLng(localizacao.getLatitude(),localizacao.getLongitude());


        if(this.map == null) {

            int idMapa = R.id.map; // coloque aqui o id do mapa do layout ex : R.id.map
            this.map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(idMapa)).getMap();

            this.map.setOnMarkerClickListener(clicouMarcadorMapa);

            this.map.moveCamera(CameraUpdateFactory.newLatLngZoom(this.latLng, 15));
            this.map.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
            //Marca sua posição no mapa
            this.map.getUiSettings().setMyLocationButtonEnabled(true);
            this.map.getUiSettings().setCompassEnabled(true);
            this.map.setMyLocationEnabled(true);


            this.gpsUtil.desativarListener();

            //Manda listar todos os imoveis
            new TaskListarImoveis(1,this).execute(this.web);

        }
        this.gpsUtil.desativarListener();
    }

    private MenuItem menuItemLogin;
    private MenuItem menuCadastrar;

    private MenuItem.OnMenuItemClickListener clickMenuLogin = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            Intent intent = new Intent(MapaActivity.this,LoginActivity.class);
            startActivityForResult(intent,CLIQUE_TELA_LOGIN);
            return true;
        }
    };

    private MenuItem.OnMenuItemClickListener clickMenuLogout = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {

            new DialogAlerta(MapaActivity.this,"Deslogar","Deseja deslogar??",CLIQUE_DESLOGAR,"SIM","NÃO").exibirDialog();


            return true;
        }
    };

    private MenuItem.OnMenuItemClickListener clickMenuCadastro = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            Intent intent = new Intent(MapaActivity.this,CadastroClienteActivity.class);
            startActivityForResult(intent,CLIQUE_TELA_CADASTRO);
            return true;
        }
    };



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mapa_activity_menu, menu);

        this.menuItemLogin = menu.getItem(0);
        this.menuCadastrar = menu.getItem(1);

        this.menuItemLogin.setOnMenuItemClickListener(this.clickMenuLogin);

        this.menuCadastrar.setOnMenuItemClickListener(this.clickMenuCadastro);

        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CLIQUE_TELA_LOGIN){
            if(SessionUtil.getObject(ParametrosSessao.USUARIO_LOGADO) == null){
                return;
            }
            Toast.makeText(this,"Usuário logado com sucesso",Toast.LENGTH_LONG).show();
            menuItemLogin.setTitle("DESLOGAR");
            menuItemLogin.setOnMenuItemClickListener(clickMenuLogout);
            menuCadastrar.setVisible(false);
        }else if(requestCode == CLIQUE_TELA_CADASTRO){
            if(resultCode == 1){
                cliente = (Cliente)data.getSerializableExtra("clienteCadastrado");
                Toast.makeText(MapaActivity.this,"Cliente cadastrado com sucesso",Toast.LENGTH_LONG).show();
            }
        }else if(requestCode == CLIQUE_LUPA){
            if(resultCode == 1){
                List<Imovel> listaNova = (List<Imovel>)SessionUtil.getObject(ParametrosSessao.IMOVEIS_FILTRO);
                SessionUtil.removeObject(ParametrosSessao.IMOVEIS_FILTRO);
                listaImovel = listaNova;
                this.map.clear();
                this.mapImovel.clear();
                for(Imovel i : this.listaImovel){
                    this.mapImovel.put(this.addMark(i.getPontoGeografico().getLatitude(), i.getPontoGeografico().getLongitude(), i.getComplemento(), R.drawable.casinha), i);
                }
            }
        }

    }

    @Override
    public void finish(int requestCode, int responseCode, Object data) {
        if(requestCode == 1){
            if(responseCode == 0){
                Toast.makeText(this,"Erro na conexão com o servidor",Toast.LENGTH_LONG).show();
            }
            if(responseCode == 1){
                this.listaImovel = (List<Imovel>)data;
                //Fazer os paranauês de jogar no mapa
                this.map.clear();
                for(Imovel i : this.listaImovel){
                    this.mapImovel.put(this.addMark(i.getPontoGeografico().getLatitude(), i.getPontoGeografico().getLongitude(), i.getComplemento(), R.drawable.casinha), i);
                }
            }
            if(responseCode == 2){
                String mensagem = (String)data;
                Toast.makeText(this,mensagem,Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void respondeuSim(int requestCode) {
        if(requestCode == CLIQUE_DESLOGAR){
            //////////////////
            SessionUtil.removeObject(ParametrosSessao.USUARIO_LOGADO);
            Toast.makeText(MapaActivity.this,"Usuário deslogou",Toast.LENGTH_LONG).show();
            menuItemLogin.setOnMenuItemClickListener(clickMenuLogin);
            menuItemLogin.setTitle("LOGAR");
            menuCadastrar.setVisible(true);
            //////////////////
        }
    }
}
