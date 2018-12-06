package br.com.precocerto.precocertoapp.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.com.precocerto.precocertoapp.R;
import br.com.precocerto.precocertoapp.model.ProdutoCompra;
import br.com.precocerto.precocertoapp.model.ProdutoLista;

public class MapsActivity extends AppCompatActivity {

    private static final int REQUEST_CHECK_SETTINGS = 613;
    private List<ProdutoCompra> listaCompras = new ArrayList<>();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        listaCompras = (List<ProdutoCompra>) getIntent().getSerializableExtra("ListaCompras");
        preparaFragmentMaps();
    }

    private void preparaFragmentMaps() {
        Fragment myFrag = new MapaFragment();
        Bundle data = new Bundle();
        data.putSerializable("ListaCompras", (Serializable) listaCompras);
        myFrag.setArguments(data);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction tx = manager.beginTransaction();
        tx.replace(R.id.activity_maps_frame, myFrag);
        tx.commit();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.activity_maps_frame);
        fragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.activity_maps_frame);
        fragment.onRequestPermissionsResult(requestCode, permissions,  grantResults);
    }

    public List<ProdutoCompra> getListaCompras(){
        return listaCompras;
    }



}
