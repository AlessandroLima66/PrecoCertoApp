package br.com.precocerto.precocertoapp.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import br.com.precocerto.precocertoapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;

import java.io.File;
import java.util.List;

import br.com.precocerto.precocertoapp.BuildConfig;
import br.com.precocerto.precocertoapp.dao.ProdutoDAO;
import br.com.precocerto.precocertoapp.dto.ProdutoSync;
import br.com.precocerto.precocertoapp.model.Produto;
import br.com.precocerto.precocertoapp.retrofit.RetrofitInicializador;
import br.com.precocerto.precocertoapp.util.BitMapUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetalheDoProdutoActivity extends AppCompatActivity {

    static final int CODIGO_CAMERA = 123;
    private Bitmap bitmapCodigoDeBarras;
    private String codigoDeBarras;
    private String caminhoFoto;
    private String nomeProduto;
    private Integer quantidadeProduto;
    private Double valorUnitario;
    private Double valorTotal;
    private ImageView imagem_codigo_barras;
    private TextView codigo_de_barras;
    private TextView nome_produto;
    private EditText quantidade_produto;
    private EditText valor_unitario;
    private Button botao_foto;
    private Button botao_adicionar_produto;
    private Button botao_procura_produto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_produto);

        preparaView();

        Intent intent = getIntent();
        caminhoFoto = intent.getStringExtra("caminhoDaFoto");
        bitmapCodigoDeBarras = BitMapUtil.devolveBitmapRotacionado(caminhoFoto);
        imagem_codigo_barras.setImageBitmap(bitmapCodigoDeBarras);

        runBarcodeRecognition();

        botao_adicionar_produto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adicionaProduto();
            }
        });

        botao_procura_produto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                procuraProduto();
            }
        });

        botao_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tiraFoto();
            }
        });
    }

    private void preparaView() {
        imagem_codigo_barras = findViewById(R.id.detalhe_produto_imagem_codigo_barras);
        codigo_de_barras = findViewById(R.id.detalhe_produto_codigo_de_barras);
        nome_produto = findViewById(R.id.detalhe_produto_nome_produto);
        quantidade_produto = findViewById(R.id.detalhe_produto_quantidade);
        valor_unitario = findViewById(R.id.detalhe_produto_valor_unitario);
        botao_foto = findViewById(R.id.detalhe_produto_botao_foto);
        botao_adicionar_produto = findViewById(R.id.detalhe_produto_botao_adicionar_produto);
        botao_procura_produto = findViewById(R.id.detalhe_produto_botao_procurar_produto);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    private void procuraProduto() {
        Call<ProdutoSync> call = new RetrofitInicializador().getProdutoService().procuraCodigoDeBarras();

        call.enqueue(new Callback<ProdutoSync>() {
            @Override
            public void onResponse(retrofit2.Call<ProdutoSync> call, Response<ProdutoSync> response) {
                ProdutoSync produtoSync = response.body();
                codigo_de_barras.setText(produtoSync.getCodigoDeBarras());
                nome_produto.setText(produtoSync.getNomeProduto());
            }

            @Override
            public void onFailure(retrofit2.Call<ProdutoSync> call, Throwable t) {
                Log.e("onFailure chamado", t.getMessage());
                Toast.makeText(DetalheDoProdutoActivity.this,"Nenhum produto foi encontrato",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void runBarcodeRecognition() {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmapCodigoDeBarras);

        FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance()
                .getVisionBarcodeDetector();

        Task<List<FirebaseVisionBarcode>> result = detector.detectInImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionBarcode> barcodes) {
                        processBarcodeResult(barcodes);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("onFailure", "runCodigoDeBarrasRecognition");
                        e.printStackTrace();
                    }
                });
    }

    private void processBarcodeResult(List<FirebaseVisionBarcode> barcodes) {

        if (!barcodes.isEmpty()) {
            codigoDeBarras = barcodes.get(0).getRawValue();
            botao_procura_produto.setVisibility(View.VISIBLE);
        } else {
            codigoDeBarras = "Nenhum código foi encontrado";
            Toast.makeText(this,"Favor tirar outra foto", Toast.LENGTH_LONG).show();
        }

        codigo_de_barras.setText(codigoDeBarras);
    }

    private void tiraFoto() {
        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        caminhoFoto = getExternalFilesDir(null) + "/" + System.currentTimeMillis() + ".jpg";
        File arquivoFoto = new File(caminhoFoto);

        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT,
                FileProvider.getUriForFile(DetalheDoProdutoActivity.this,
                        BuildConfig.APPLICATION_ID + ".provider", arquivoFoto));

        startActivityForResult(intentCamera, CODIGO_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CODIGO_CAMERA) {
                bitmapCodigoDeBarras = BitMapUtil.devolveBitmapRotacionado(caminhoFoto);
                imagem_codigo_barras.setImageBitmap(bitmapCodigoDeBarras);
                runBarcodeRecognition();
            }
        }
    }


    private void adicionaProduto(){

        nomeProduto = String.valueOf(nome_produto.getText());
        quantidadeProduto = Integer.valueOf(quantidade_produto.getText().toString());

        if(!String.valueOf(valor_unitario.getText()).equals("")) {
            valorUnitario = Double.valueOf(String.valueOf(valor_unitario.getText()));
        }else
            valorUnitario = null;

        if(codigoDeBarras.equals("Nenhum código foi encontrado")
                || codigoDeBarras.equals("---")
                || nomeProduto.equals("---")
                || valorUnitario == null){

            Toast.makeText(this, "Favor preencher todos os campos", Toast.LENGTH_LONG).show();
        }else {

            valorTotal = valorUnitario * quantidadeProduto;

            Produto produto = new Produto(nomeProduto, codigoDeBarras, quantidadeProduto, valorUnitario, valorTotal, caminhoFoto);
            ProdutoDAO dao = new ProdutoDAO(this);
            dao.insere(produto);
            dao.close();

            finish();
        }
    }

}
