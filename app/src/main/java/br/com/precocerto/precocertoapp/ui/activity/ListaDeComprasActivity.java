package br.com.precocerto.precocertoapp.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.PlaceTypes;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.File;
import java.util.List;

import br.com.precocerto.precocertoapp.BuildConfig;
import br.com.precocerto.precocertoapp.R;
import br.com.precocerto.precocertoapp.dao.ProdutoDAO;
import br.com.precocerto.precocertoapp.model.Produto;
import br.com.precocerto.precocertoapp.model.ProdutoCompra;
import br.com.precocerto.precocertoapp.model.ProdutoLista;
import br.com.precocerto.precocertoapp.ui.adapter.ListaDeComprasAdapter;
import br.com.precocerto.precocertoapp.util.MoedaUtil;

public class ListaDeComprasActivity extends AppCompatActivity {

    static final int CODIGO_CAMERA = 123;

    public static final String MENSAGEM_LISTA_DE_COMPRAS_VAZIA = "Sua lista de compras está vazia!";
    private final CharSequence[] OPCOES_MENU = {"Pesquisar Produtos", "Limpar Lista de Compras"};
    private final String TITLE_DIALOG_MENU = "Menu";
    private final String TITLE_DIALOG_FINALIZAR = "Finalizar Compra";
    private final String MENSAGEM_DIALOG_FINALIZAR = ("Tem certeza que deseja finalizar sua compra?");
    private final String BOTAO_POSITIVO = ("Sim");
    private final String BOTAO_NEGATIVO = ("Não");

    private String caminhoDaFoto;
    private Button botao_adiciona;
    private Button botao_finalizar_compra;
    private Button botao_menu;
    private TextView valor_total_compra;
    private AlertDialog dialogMenu;
    private AlertDialog dialogFinalizar;
    private Double valorTotalCompra = Double.valueOf(0);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_compras);

        botao_adiciona = findViewById(R.id.lista_compras_botao_adiciona);
        botao_adiciona.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getPermissoes();
                Intent resumoProdutos = new Intent(ListaDeComprasActivity.this, DetalheDoProdutoActivity.class);
                startActivity(resumoProdutos);

            }
        });

        botao_finalizar_compra = findViewById(R.id.lista_compras_botao_finalizar_compra);
        botao_finalizar_compra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               mostraAlertDialogFinalizarCompra();
            }
        });

        botao_menu = findViewById(R.id.lista_compras_botao_menu);
        botao_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostraAlertDialogMenu();
            }
        });
    }

    private void mostraAlertDialogMenu() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setIcon(R.drawable.ic_menu_black);
        dialogBuilder.setTitle(TITLE_DIALOG_MENU);
        dialogBuilder.setItems(OPCOES_MENU, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    Intent intentPesquisaProdutos = new Intent(ListaDeComprasActivity.this, PesquisaProdutosActivity.class);
                    startActivity(intentPesquisaProdutos);
                } else {
                    limparLista();
                    carregaLista();
                }
            }
        });
        dialogMenu = dialogBuilder.create();
        dialogMenu.show();
    }

    private void mostraAlertDialogFinalizarCompra() {
        if (!(valorTotalCompra.doubleValue() <= 0)) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setTitle(TITLE_DIALOG_FINALIZAR);
            dialogBuilder.setIcon(R.drawable.ic_finalizar_brack);
            dialogBuilder.setMessage(MENSAGEM_DIALOG_FINALIZAR);
            dialogBuilder.setPositiveButton(BOTAO_POSITIVO, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intentFinalizarCompra = new Intent(ListaDeComprasActivity.this, FinalizarCompraActivity.class);
                    startActivity(intentFinalizarCompra);
                }
            });

            dialogBuilder.setNegativeButton(BOTAO_NEGATIVO, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            dialogFinalizar = dialogBuilder.create();
            dialogFinalizar.show();
        } else
            Toast.makeText(ListaDeComprasActivity.this, MENSAGEM_LISTA_DE_COMPRAS_VAZIA, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregaLista();
    }

    private void carregaLista() {
        ProdutoDAO dao = new ProdutoDAO(this);
        List<ProdutoLista> produtos = dao.buscaProdutos();
        dao.close();

        ListView listaDeCompras = findViewById(R.id.lista_compras_ListView);
        listaDeCompras.setAdapter(new ListaDeComprasAdapter(this, produtos));

        somaTotalDaCompra(produtos);
    }

    private void limparLista() {
        if (!(valorTotalCompra.doubleValue() <= 0)) {
            ProdutoDAO dao = new ProdutoDAO(this);
            dao.dropAll();
        }
    }

    private void somaTotalDaCompra(List<ProdutoLista> produtos) {
        valorTotalCompra = Double.valueOf(0);
        for (int i = 0; i < produtos.size(); i++) {

            valorTotalCompra += produtos.get(i).getValorTotal();
        }

        valor_total_compra = findViewById(R.id.lista_compras_valor_total_compra);
        String valor = MoedaUtil.formataParaExibicao(valorTotalCompra);
        valor_total_compra.setText(valor);
    }

//    private void getPermissoes() {
//        if (ActivityCompat.checkSelfPermission(this,
//                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//
//            tirarFoto();
//
//        } else {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.CAMERA}, 1);
//        }
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case 1: {
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    tirarFoto();
//                } else {
//                    Toast.makeText(this, "A permissão de câmera é necessaria", Toast.LENGTH_LONG).show();
//                }
//                break;
//            }
//        }
//    }

//    private void tirarFoto() {
//        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//        caminhoDaFoto = getExternalFilesDir(null) + "/" + System.currentTimeMillis() + ".jpg";
//        File arquivoFoto = new File(caminhoDaFoto);
//
//        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT,
//                FileProvider.getUriForFile(ListaDeComprasActivity.this,
//                        BuildConfig.APPLICATION_ID + ".provider", arquivoFoto));
//
//        startActivityForResult(intentCamera, CODIGO_CAMERA);
//    }

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
