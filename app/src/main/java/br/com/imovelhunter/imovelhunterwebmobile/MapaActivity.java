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
import android.os.Handler;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import br.com.imovelhunter.adapters.MapaInfoWindowAdapter;
import br.com.imovelhunter.dialogs.DialogAlerta;
import br.com.imovelhunter.dominio.Cliente;
import br.com.imovelhunter.dominio.Imovel;
import br.com.imovelhunter.dominio.Localizacao;
import br.com.imovelhunter.dominio.Usuario;
import br.com.imovelhunter.enums.Parametros;
import br.com.imovelhunter.enums.ParametrosSessao;
import br.com.imovelhunter.enums.ParametrosSessaoJson;
import br.com.imovelhunter.listeners.OnFinishTask;
import br.com.imovelhunter.tasks.TaskListarImoveis;
import br.com.imovelhunter.util.GpsUtil;
import br.com.imovelhunter.util.NetUtil;
import br.com.imovelhunter.util.SessionUtilJson;
import br.com.imovelhunter.web.Web;
import br.com.imovelhunter.web.WebImp;


public class MapaActivity extends ActionBarActivity implements OnFinishTask,DialogAlerta.RespostaSim{

    private double latitudeVindoDeTelaMensagem =0 ;
    private double longitudeVindoDeTelaMensagem =0 ;
    private GoogleMap map;

    private LatLng latLng;

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

    private final int CLIQUE_CHAT = 5;

    private Cliente cliente;

    private ImageView lupa;

    private Usuario usuarioLogado;

    private NetUtil netUtil;

    private Handler handlerListarImoveisInicio;

    //private Object usuarioLogado; definir o tipo do usuarioLogado


    //Picasso.with(context).load("url").into(imoveview);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#315e8a")));
        }

        this.netUtil = new NetUtil(this);

        this.handlerListarImoveisInicio = new Handler();

        this.lupa = (ImageView)this.findViewById(R.id.imageViewFiltrar);

        this.lupa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(netUtil.verificaInternet()) {
                    Intent in = new Intent(MapaActivity.this, FiltroActivity.class);
                    startActivityForResult(in, CLIQUE_LUPA);
                }
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



/*

    latitudeVindoDeTelaMensagem = getIntent().getStringExtra("LATITUDE");
       longitudeVindoDeTelaMensagem =  getIntent().getStringExtra("LONGITUDE");

*/
        try {

            Bundle b = getIntent().getExtras();
            latitudeVindoDeTelaMensagem = b.getDouble("LATITUDE");
            longitudeVindoDeTelaMensagem = b.getDouble("LONGITUDE");

        }catch (Exception ex){

        }

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

               if(latitudeVindoDeTelaMensagem!=0 && longitudeVindoDeTelaMensagem!=0){


                    latLng = new LatLng(latitudeVindoDeTelaMensagem,longitudeVindoDeTelaMensagem);///

                    latitudeVindoDeTelaMensagem =-1;
                    longitudeVindoDeTelaMensagem=-1;
                    map.setOnMarkerClickListener(clicouMarcadorMapa);

                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                    map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                    map.setOnMyLocationChangeListener(null);
                }else {
                    latLng = new LatLng(location.getLatitude(), location.getLongitude());

                    map.setOnMarkerClickListener(clicouMarcadorMapa);

                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                    map.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);
                    map.setOnMyLocationChangeListener(null);
                }
            }
        });


        if(netUtil.verificaInternet()) {
            //Manda listar todos os imoveis
            new TaskListarImoveis(1, this).execute(this.web);
        }else{
            this.handlerListarImoveisInicio.postDelayed(runListarImoveisInternet,5000);
        }

    }

    private Runnable runListarImoveisInternet = new Runnable() {
        @Override
        public void run() {
            if(netUtil.verificaInternet()){
                new TaskListarImoveis(1,MapaActivity.this).execute(web);
            }else{
                handlerListarImoveisInicio.postDelayed(runListarImoveisInternet,5000);
            }
        }
    };

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

            if(netUtil.verificaInternet()) {
                Imovel imovelSelecionado = mapImovel.get(marker);

                Intent intent = new Intent(MapaActivity.this, DetalheImovelActivity.class);

                intent.putExtra(ParametrosSessao.IMOVEL_SELECIONADO.name(), imovelSelecionado);

                startActivityForResult(intent, CLIQUE_MARCADOR);

                return false;
            }
            return false;
        }

    };



    private MenuItem.OnMenuItemClickListener clickMenuLogin = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if(netUtil.verificaInternet()) {
                Intent intent = new Intent(MapaActivity.this, LoginActivity.class);

                intent.putExtra(ParametrosSessao.GCM.name(), gcm);
                intent.putExtra(ParametrosSessao.SERIAL.name(), serial);

                startActivityForResult(intent, CLIQUE_TELA_LOGIN);
                return true;
            }
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

    private MenuItem.OnMenuItemClickListener clickMenuChat = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            if(netUtil.verificaInternet()){
                Intent intent = new Intent(MapaActivity.this,ListaContatoActivity.class);
                startActivityForResult(intent,CLIQUE_CHAT);
                return true;
            }

            return false;
        }
    };

    private MenuItem.OnMenuItemClickListener clickMenuCadastro = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            Intent intent = new Intent(MapaActivity.this,CadastroClienteActivity.class);

            intent.putExtra(Parametros.SERIAL_DISPOSITIVO.name(),serial);

            startActivityForResult(intent,CLIQUE_TELA_CADASTRO);
            return true;
        }
    };
//
    private MenuItem.OnMenuItemClickListener clikMenuNotificacao = new  MenuItem.OnMenuItemClickListener(){

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        Intent intent = new Intent(MapaActivity.this,NotificationActivity.class);
        startActivity(intent);
        finish();//Testando aqui
       return true;
    }
};




    private MenuItem menuItemLogin;
    private MenuItem menuCadastrar;
    private MenuItem menuCadastroInteresse;
    private MenuItem menuChat;
    private MenuItem menuNotificacao;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mapa_activity_menu, menu);

        this.menuItemLogin = menu.getItem(0);
        this.menuCadastrar = menu.getItem(1);
        this.menuCadastroInteresse = menu.getItem(2);
        this.menuChat = menu.getItem(3);
        this.menuNotificacao = menu.getItem(4);

        this.menuItemLogin.setOnMenuItemClickListener(this.clickMenuLogin);

        this.menuCadastrar.setOnMenuItemClickListener(this.clickMenuCadastro);

        this.menuChat.setOnMenuItemClickListener(this.clickMenuChat);

        this.menuNotificacao.setOnMenuItemClickListener(this.clikMenuNotificacao);

        this.menuNotificacao.setVisible(false);
        this.menuChat.setVisible(false);
        this.menuCadastroInteresse.setVisible(false);

        if(SessionUtilJson.getInstance(this).containsName(ParametrosSessaoJson.USUARIO_LOGADO)){
            this.usuarioLogado = (Usuario)SessionUtilJson.getInstance(this).getJsonObject(ParametrosSessaoJson.USUARIO_LOGADO,Usuario.class);
            this.menuChat.setVisible(true);
            this.menuCadastroInteresse.setVisible(true);
            menuItemLogin.setTitle("DESLOGAR");
            menuItemLogin.setOnMenuItemClickListener(clickMenuLogout);
            menuCadastrar.setVisible(false);
            menuNotificacao.setVisible(true);
        }

        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CLIQUE_TELA_LOGIN){
            if(resultCode != 1){
                return;
            }
            this.usuarioLogado = (Usuario)data.getSerializableExtra(ParametrosSessao.USUARIO_LOGADO.name());
            Toast.makeText(this,"Usuário logado com sucesso",Toast.LENGTH_LONG).show();
            menuItemLogin.setTitle("DESLOGAR");
            menuItemLogin.setOnMenuItemClickListener(clickMenuLogout);
            menuCadastrar.setVisible(false);
            menuChat.setVisible(true);
            menuCadastroInteresse.setVisible(true);
            menuNotificacao.setVisible(true);
            SessionUtilJson.getInstance(this).setJsonObject(ParametrosSessaoJson.USUARIO_LOGADO,usuarioLogado);
        }else if(requestCode == CLIQUE_TELA_CADASTRO){
            if(resultCode == 1){
                cliente = (Cliente)data.getSerializableExtra("clienteCadastrado");
                Toast.makeText(MapaActivity.this,"Cliente cadastrado com sucesso",Toast.LENGTH_LONG).show();
            }
        }else if(requestCode == CLIQUE_LUPA){
            if(resultCode == 1){
                List<Imovel> listaNova = (List<Imovel>)SessionUtilJson.getInstance(this).getJsonArrayObject(ParametrosSessaoJson.IMOVEIS_FILTRO,Imovel.class);
                SessionUtilJson.getInstance(this).removeObject(ParametrosSessaoJson.IMOVEIS_FILTRO);
                if(listaNova != null) {
                    listaImovel = listaNova;
                }
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
            SessionUtilJson.getInstance(this).removeObject(ParametrosSessaoJson.USUARIO_LOGADO);
            Toast.makeText(MapaActivity.this,"Usuário deslogou",Toast.LENGTH_LONG).show();
            menuItemLogin.setOnMenuItemClickListener(clickMenuLogin);
            menuItemLogin.setTitle("LOGAR");
            menuCadastrar.setVisible(true);
            menuChat.setVisible(false);
            menuCadastroInteresse.setVisible(false);
            //////////////////
        }
    }
}
