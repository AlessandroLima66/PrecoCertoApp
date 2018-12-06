package br.com.precocerto.precocertoapp.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
import br.com.precocerto.precocertoapp.dto.ProdutoMockado;
import br.com.precocerto.precocertoapp.model.Produto;
import br.com.precocerto.precocertoapp.model.ProdutoLista;
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
    private AlertDialog dialogCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_produto);

        preparaView();
        getPermissoes();

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
                tirarFoto();
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

    private void getPermissoes() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            tirarFoto();
        } else {
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
                    mostraAlertDialogCamera();
                }
                break;
            }
        }
    }

    private void mostraAlertDialogCamera() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setTitle("Atenção");
        dialogBuilder.setIcon(R.drawable.ic_leitor_codigo_barras_2);
        dialogBuilder.setMessage("Para adicionar produtos, precisamos do acesso a câmera do dispositivo.\n\nVocê pode nos ajudar? :)");

        dialogBuilder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getPermissoes();
            }
        });

        dialogBuilder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        dialogCamera = dialogBuilder.create();
        dialogCamera.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private void procuraProduto() {
//        Call<Produto> call = new RetrofitInicializador().getProdutoService().getProduto(codigoDeBarras);
//
//        call.enqueue(new Callback<Produto>() {
//            @Override
//            public void onResponse(retrofit2.Call<Produto> call, Response<Produto> response) {
//                Produto produto = response.body();
//                codigo_de_barras.setText(produto.getCodigoDeBarras());
//                nome_produto.setText(produto.getNome());
//            }
//
//            @Override
//            public void onFailure(retrofit2.Call<Produto> call, Throwable t) {
//                Log.e("onFailure chamado", t.getMessage());
//                Toast.makeText(DetalheDoProdutoActivity.this,"Nenhum produto foi encontrato",Toast.LENGTH_LONG).show();
//            }
//        });

        Call<ProdutoMockado> call = new RetrofitInicializador().getProdutoService().produtosMock();
        call.enqueue(new Callback<ProdutoMockado>() {
            @Override
            public void onResponse(Call<ProdutoMockado> call, Response<ProdutoMockado> response) {
                ProdutoMockado produtoMockado = response.body();
                List<Produto> produtosMock = produtoMockado.getListaProdutos();
                produtosMock.add(new Produto("Agua Crystal 1 Litro","&7894900530025"));

                for (int i = 0; i < produtosMock.size(); i++) {
                    if (produtosMock.get(i).getCodigoDeBarras().equals("&" + codigoDeBarras)) {
                        nome_produto.setText(produtosMock.get(i).getNome());
                        return;
                    }
                }
                Toast.makeText(DetalheDoProdutoActivity.this, "Nenhum produto foi encontrato", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ProdutoMockado> call, Throwable t) {
                Log.e("onFailure chamado", t.getMessage());
                Toast.makeText(DetalheDoProdutoActivity.this, "ERRO", Toast.LENGTH_LONG).show();
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
            Toast.makeText(this, "Favor tirar outra foto", Toast.LENGTH_LONG).show();
        }

        codigo_de_barras.setText(codigoDeBarras);
    }

    private void tirarFoto() {
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
        } else {
            finish();
        }
    }

    private void adicionaProduto() {

        if (codigoDeBarras.equals("Nenhum código foi encontrado")) {
            Toast.makeText(DetalheDoProdutoActivity.this, "O campo Código de Barras é obrigatório", Toast.LENGTH_LONG).show();
            return;
        }


        nomeProduto = String.valueOf(nome_produto.getText());
        if (nomeProduto.equals("---") | nomeProduto.equals("")) {
            Toast.makeText(DetalheDoProdutoActivity.this, "O campo Nome do Produto é obrigatório", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            quantidadeProduto = Integer.valueOf(quantidade_produto.getText().toString());
            if (quantidadeProduto <= 0 | quantidadeProduto > 100) {
                if (quantidadeProduto <= 0) {
                    Toast.makeText(DetalheDoProdutoActivity.this, "O campo Quantidade deve ser maior que zero", Toast.LENGTH_LONG).show();
                } else if (quantidadeProduto > 100) {
                    Toast.makeText(DetalheDoProdutoActivity.this, "O campo Quantidade não deve ser maior que 100", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }catch (NumberFormatException n){
            Toast.makeText(DetalheDoProdutoActivity.this, "O campo Quantidade deve ser maior que zero", Toast.LENGTH_LONG).show();
        }

        try {
            valorUnitario = Double.valueOf(String.valueOf(valor_unitario.getText()));
            if (valorUnitario <= 0 | valorUnitario > 1000000) {
                if(valorUnitario <= 0){
                    Toast.makeText(DetalheDoProdutoActivity.this, "O campo Valor Unitário deve ser maior que 0", Toast.LENGTH_LONG).show();
                }else
                    if (valorUnitario > 1000000){
                        Toast.makeText(DetalheDoProdutoActivity.this, "O campo Valor unitario não deve ser maior que 1000000", Toast.LENGTH_LONG).show();
                    }
                    return;
            }
        }catch (NumberFormatException n){
            Toast.makeText(DetalheDoProdutoActivity.this, "O campo Valor Unitario deve ser maior que 0", Toast.LENGTH_LONG).show();
            return;
        }


        valorTotal = valorUnitario * quantidadeProduto;

        ProdutoLista produto = new ProdutoLista(nomeProduto, codigoDeBarras, null, quantidadeProduto, valorUnitario, valorTotal, caminhoFoto);
        ProdutoDAO dao = new ProdutoDAO(this);
        dao.insere(produto);
        dao.close();

        finish();
    }
}
