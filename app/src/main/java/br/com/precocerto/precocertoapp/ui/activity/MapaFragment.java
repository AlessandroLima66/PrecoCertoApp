package br.com.precocerto.precocertoapp.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.precocerto.precocertoapp.R;
import br.com.precocerto.precocertoapp.dto.ListaDeCompra;
import br.com.precocerto.precocertoapp.dto.listaDeMercados;
import br.com.precocerto.precocertoapp.model.ProdutoCompra;
import br.com.precocerto.precocertoapp.retrofit.RetrofitInicializador;
import br.com.precocerto.precocertoapp.util.MoedaUtil;
import retrofit2.Callback;
import retrofit2.Response;

public class MapaFragment extends SupportMapFragment implements OnMapReadyCallback {
    private static final int REQUEST_CHECK_SETTINGS = 613;
    private List<ListaDeCompra> listaComprasMercados;
    private GoogleMap GoogleMap;
    private AlertDialog dialogLocalizacao;
    private FusedLocationProviderClient mFusedLocationClient;
    protected Location mLastLocation;
    private LatLng posicaoAtual;
    private List<ProdutoCompra> listaCompras = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle data = getArguments();
        listaCompras = (List<ProdutoCompra>) data.getSerializable("ListaCompras");
        getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap GoogleMap) {
        this.GoogleMap = GoogleMap;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        getPermissoes();
    }

    private void getPermissoes() {
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            VerificarGPSAtivo();
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    VerificarGPSAtivo();
                } else {
                    mostraAlertDialogEnderecoObrigatorio();
                }
                break;
            }
        }
    }

    private void VerificarGPSAtivo() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(new LocationRequest());
        SettingsClient client = LocationServices.getSettingsClient(getActivity());
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getLastLocation();
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException ignored) {
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    getLastLocation();
                    break;
                case Activity.RESULT_CANCELED:
                    mostraAlertDialogEnderecoObrigatorio();
                    break;
            }
        }
    }

    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        final LocationRequest locationRequest  = LocationRequest.create()
                .setNumUpdates(1)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(0);

        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                mLastLocation = locationResult.getLastLocation();
                posicaoAtual = pegaCoordenadaLatLng();
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(posicaoAtual, 13f);
                GoogleMap.moveCamera(update);
                setMarcadorHome(posicaoAtual, "Onde Estou");
                pegaDados();
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                if(!locationAvailability.isLocationAvailable()){
                    getLastLocation();
                }
            }
        },null);
    }

    private void mostraAlertDialogEnderecoObrigatorio() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setCancelable(false);
        dialogBuilder.setTitle("Atenção");
        dialogBuilder.setIcon(R.drawable.ic_maps);
        dialogBuilder.setMessage("Para mostrar a lista de mercados com o valor da sua compra, precisamos da sua localização.\n\nVocê pode nos ajudar? :)");

        dialogBuilder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getPermissoes();
            }
        });

        dialogBuilder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Retorna para a antivity anterior
                getActivity().onBackPressed();
            }
        });

        dialogLocalizacao = dialogBuilder.create();
        dialogLocalizacao.show();
    }

    private LatLng pegaCoordenadaLatLng() {
        try {
            Geocoder geocoder = new Geocoder (getContext());
            List<Address> resultados =
                    geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(),3);

            if (!resultados.isEmpty()) {
                LatLng posicao = new LatLng(resultados.get(0).getLatitude(), resultados.get(0).getLongitude());
                return posicao;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void encontraMercados(){
        for (int i = 0; i < listaComprasMercados.size();i++ ){
            String enderecoMercado = listaComprasMercados.get(i).getEnderecoMercado();
            String nomeMercado = listaComprasMercados.get(i).getNomeMercado();

            LatLng latLngMercado = pegaCoordenadaDoEndereco(enderecoMercado);
            if (latLngMercado != null) {
                setMarcadorMercados(latLngMercado, nomeMercado, Double.valueOf(0));
            }
        }
    }

    private void setMarcadorMercados(LatLng coordenada, String titulo, Double valorCompra) {
            if (coordenada != null) {
                MarkerOptions marcador = new MarkerOptions();
                marcador.position(coordenada);
                marcador.title(titulo);
                marcador.snippet("Total Compra= " + MoedaUtil.formataParaExibicao(valorCompra));
                GoogleMap.addMarker(marcador);
            }
    }

    private void setMarcadorHome(LatLng coordenada, String titulo) {
        if (coordenada != null) {
            MarkerOptions marcador = new MarkerOptions();
            marcador.position(coordenada);
            marcador.title(titulo);
            marcador.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_home));
            GoogleMap.addMarker(marcador).showInfoWindow();
        }
    }

    private LatLng pegaCoordenadaDoEndereco(String endereco) {
        try {
            Geocoder geocoder = new Geocoder (getContext());
            List<Address> resultados =
                    geocoder.getFromLocationName(endereco, 1);

            if (!resultados.isEmpty()) {
                LatLng posicao = new LatLng(resultados.get(0).getLatitude(), resultados.get(0).getLongitude());
                return posicao;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void pegaDados(){
        //List<ProdutoCompra> produtosCompra = new ArrayList<>();
        //produtosCompra.add(new ProdutoCompra("Biscoito PassaTempo Recheado Chocolate 130g", "7896512909787", Integer.valueOf(2), null, null));
        //produtosCompra.add(new ProdutoCompra("Achocolatado Toddynho 200 ML", "7894321722016", Integer.valueOf(2), null, null));
        //produtosCompra.add(new ProdutoCompra("Suco Pronto Su Fresh Nectar Abacaxi", "7898192034063", Integer.valueOf(4), null, null));
        //produtosCompra.add(new ProdutoCompra("Arroz Tipo 1 1kg Camil", "7896006711117",  Integer.valueOf(1), null, null));
        //produtosCompra.add(new ProdutoCompra("Feijao Camil Preto", "7896006751106",  Integer.valueOf(1), null, null));


        retrofit2.Call<listaDeMercados> call= new RetrofitInicializador().pegaListaDeCompras().getListadeCompras(listaCompras);

        call.enqueue(new Callback<listaDeMercados>() {
            @Override
            public void onResponse(retrofit2.Call<listaDeMercados> call, Response<listaDeMercados> response) {
                listaDeMercados body = response.body();
                listaComprasMercados = body.getListaDeMercados();
                encontraMercados();
            }

            @Override
            public void onFailure(retrofit2.Call<listaDeMercados> call, Throwable t) {
                Toast.makeText(getContext(),"ERRO", Toast.LENGTH_LONG).show();
            }
        });
    }
}
