package br.com.precocerto.precocertoapp.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.view.UCropView;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.com.precocerto.precocertoapp.BuildConfig;
import br.com.precocerto.precocertoapp.R;
import br.com.precocerto.precocertoapp.bo.ParserCupomFiscal;
import br.com.precocerto.precocertoapp.bo.CupomPosicaoOCR;
import br.com.precocerto.precocertoapp.model.ProdutoLista;
import br.com.precocerto.precocertoapp.ui.adapter.ListaFinalizarCompraAdapter;
import br.com.precocerto.precocertoapp.util.BitMapUtil;


public class InformaCupomActivity extends AppCompatActivity {

    static final int CODIGO_CAMERA = 123;
    private final int requestMode = 1;
    private List<ProdutoLista> cupom;
    private List<CupomPosicaoOCR> posicoesOCR = new ArrayList<>();
    private ParserCupomFiscal parseCupomProduto = new ParserCupomFiscal();
    private String caminhoDaFoto;
    private File arquivoFoto;
    private String caminhoNovaFoto;
    private Bitmap bitmap;
    private ListView informa_cupom_ListView;
    private Button botao_comparar;
    private Button botao_foto;
    private UCropView uCropView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informa_cupom);

        botao_comparar = findViewById(R.id.informa_cupom_botao_comparar);
        botao_comparar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!cupom.isEmpty()) {
                    Intent intentComparacao = new Intent(InformaCupomActivity.this, ComparacaoActivity.class);
                    intentComparacao.putExtra("cupom",(Serializable)cupom);
                    startActivityForResult(intentComparacao,666);
                }
            }
        });

        botao_foto = findViewById(R.id.informa_cupom_botao_foto);
        botao_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cupom.clear();
                parseCupomProduto = new ParserCupomFiscal();
                posicoesOCR.clear();
                bitmap.recycle();
                tirarFoto();
            }
        });


        tirarFoto();
    }

    private void tirarFoto() {
        caminhoDaFoto = getExternalFilesDir(null) + "/" + System.currentTimeMillis() + ".jpg";
        arquivoFoto = new File(caminhoDaFoto);

        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT,
                FileProvider.getUriForFile(InformaCupomActivity.this,
                        BuildConfig.APPLICATION_ID + ".provider", arquivoFoto));

        startActivityForResult(intentCamera, CODIGO_CAMERA);
    }

    private void startCrop(@NonNull Uri uri) {
        caminhoNovaFoto = System.currentTimeMillis() + ".jpg";

        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), caminhoNovaFoto)));
        uCrop = advancedConfig(uCrop);
        uCrop.start(InformaCupomActivity.this);
    }


    private UCrop advancedConfig(@NonNull UCrop uCrop) {
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setCompressionQuality(100);
        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(true);
        options.setCropGridColor(Color.GREEN);
        options.setCropGridColumnCount(1);
        options.setCropGridRowCount(6);
        options.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        return uCrop.withOptions(options);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CODIGO_CAMERA:
                    final Uri uriTeste = FileProvider.getUriForFile(InformaCupomActivity.this,
                            BuildConfig.APPLICATION_ID + ".provider", arquivoFoto);
                    startCrop(uriTeste);
                    break;

                case requestMode:
                    final Uri selectedUri = data.getData();
                    startCrop(selectedUri);
                    break;

                case UCrop.REQUEST_CROP:
                    final Uri resultUri = UCrop.getOutput(data);
                    bitmap = BitMapUtil.devolveBitmap(resultUri.getPath());
                    mostraImagem(bitmap);
                    runTextRecognition();
                    break;
            }
        }else
            if(resultCode == 666 || resultCode == 0){
                finish();
            }
    }

    private void mostraImagem(Bitmap bitmap) {
        try {
            uCropView = findViewById(R.id.informa_cupom_imagem);
            uCropView.getCropImageView().setImageBitmap(bitmap);
            uCropView.getCropImageView().setRotateEnabled(false);
            uCropView.getOverlayView().setShowCropFrame(false);
            uCropView.getOverlayView().setShowCropGrid(false);
            uCropView.getOverlayView().setDimmedColor(Color.TRANSPARENT);
            uCropView.getCropImageView().setAdjustViewBounds(true);
        } catch (Exception e) {
            Log.e("ERRO", "setImageUri", e);
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void runTextRecognition() {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionTextDetector detector = FirebaseVision.getInstance()
                .getVisionTextDetector();

        detector.detectInImage(image)
                .addOnSuccessListener(
                        new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText texts) {
                                processTextRecognitionResult(texts);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("onFailure", "runTextRecognition");
                                e.printStackTrace();
                            }
                        });
    }

    private void processTextRecognitionResult(FirebaseVisionText texts) {
        List<FirebaseVisionText.Block> blocks = texts.getBlocks();

        if (blocks.size() == 0) {
            Toast.makeText(this, "Nenhum texto foi enontrado na imagem", Toast.LENGTH_LONG).show();
            return;
        }

        for (int i = 0; i < blocks.size(); i++) {
            List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
            for (int j = 0; j < lines.size(); j++) {
                posicoesOCR.add(new CupomPosicaoOCR(new RectF(lines.get(j).getBoundingBox()), lines.get(j).getText()));
            }
        }

        marcaPosicaoBitmap(posicoesOCR);
        cupom = parseCupomProduto.parseListaDeProdutos(posicoesOCR);
        carregaLista();
    }

    private void marcaPosicaoBitmap(List<CupomPosicaoOCR> cupom) {
        final Paint rectPaint = new Paint();
        rectPaint.setColor(Color.RED);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(4.0f);

        Bitmap bitmapMarcado = bitmap.copy(this.bitmap.getConfig(), true);
        Canvas canvas = new Canvas(bitmapMarcado);

        for (int i = 0; i < cupom.size(); i++) {
            canvas.drawRect(cupom.get(i).getPosicao(), rectPaint);
        }

        mostraImagem(bitmapMarcado);
    }

    private void carregaLista() {
        if(!cupom.isEmpty()) {
            informa_cupom_ListView = findViewById(R.id.informa_cupom_ListView);
            informa_cupom_ListView.setAdapter(new ListaFinalizarCompraAdapter(this, cupom));
            botao_comparar.setVisibility(View.VISIBLE);
        }else {
            Toast.makeText(this, "Favor tirar outra foto", Toast.LENGTH_LONG).show();
            botao_comparar.setVisibility(View.INVISIBLE);
        }
    }

}
