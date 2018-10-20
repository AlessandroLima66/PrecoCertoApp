package br.com.precocerto.precocertoapp.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import br.com.precocerto.precocertoapp.BuildConfig;
import br.com.precocerto.precocertoapp.R;
import br.com.precocerto.precocertoapp.dao.ProdutoDAO;
import br.com.precocerto.precocertoapp.model.Produto;
import br.com.precocerto.precocertoapp.ui.adapter.ListaDeComprasAdapter;
import br.com.precocerto.precocertoapp.util.MoedaUtil;

public class ListaDeComprasActivity extends AppCompatActivity {

    static final int CODIGO_CAMERA = 123;
    private String caminhoDaFoto;
    private Button botao_adiciona;
    private Button botao_finalizar_compra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_compras);

        botao_adiciona = findViewById(R.id.lista_compras_botao_adiciona);
        botao_adiciona.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPermissoes();
            }
        });

        botao_finalizar_compra = findViewById(R.id.lista_compras_botao_finalizar_compra);
        botao_finalizar_compra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentFinalizar = new Intent(ListaDeComprasActivity.this, InformaCupomActivity.class);
                startActivity(intentFinalizar);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        carregaLista();
    }

    private void carregaLista(){
        ProdutoDAO dao = new ProdutoDAO(this);
        List<Produto> produtos = dao.buscaProdutos();
        dao.close();

        ListView listaDeCompras = findViewById(R.id.lista_compras_ListView);
        listaDeCompras.setAdapter(new ListaDeComprasAdapter(this, produtos));

        somaTotalDaCompra(produtos);
    }

    private void somaTotalDaCompra(List<Produto> produtos) {

        Double total = Double.valueOf(0);
        for(int i = 0; i < produtos.size(); i++){
            total += produtos.get(i).getValorTotal();
        }

        TextView valorTotalCompra = findViewById(R.id.lista_compras_valor_total_compra);
        String valor = MoedaUtil.formataParaExibicao(total);
        valorTotalCompra.setText(valor);
    }

    private void getPermissoes() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

            tirarFoto();

        } else{
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    tirarFoto();
                } else {
                    Toast.makeText(this, "A permissão de câmera é necessaria", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    private void tirarFoto() {
        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        caminhoDaFoto = getExternalFilesDir(null) + "/" + System.currentTimeMillis() + ".jpg";
        File arquivoFoto = new File(caminhoDaFoto);

        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT,
                FileProvider.getUriForFile(ListaDeComprasActivity.this,
                        BuildConfig.APPLICATION_ID + ".provider", arquivoFoto));

        startActivityForResult(intentCamera, CODIGO_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CODIGO_CAMERA) {
                Intent resumoProdutos = new Intent(ListaDeComprasActivity.this, DetalheDoProdutoActivity.class);
                resumoProdutos.putExtra("caminhoDaFoto", caminhoDaFoto);
                startActivity(resumoProdutos);
            }
        }
    }

}
