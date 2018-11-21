package br.com.precocerto.precocertoapp.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import br.com.precocerto.precocertoapp.R;

public class MapsActivity extends AppCompatActivity {

    private static final int REQUEST_CHECK_SETTINGS = 613;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction tx = manager.beginTransaction();
        tx.replace(R.id.activity_maps_frame, new MapaFragment());
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



}
