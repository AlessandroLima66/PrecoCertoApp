package br.com.precocerto.precocertoapp.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.precocerto.precocertoapp.BuildConfig;
import br.com.precocerto.precocertoapp.R;
import br.com.precocerto.precocertoapp.util.BitMapUtil;

import static br.com.precocerto.precocertoapp.ui.activity.PermissoesActivity.REQUEST_STORAGE_READ_ACCESS_PERMISSION;

public class InformaCupomActivity extends AppCompatActivity {

    static final int CODIGO_CAMERA = 123;
    private final int requestMode = 1;
    private String caminhoDaFoto;
    private PermissoesActivity permissoes = new PermissoesActivity();
    private File arquivoFoto;
    private String caminhoNovaFoto;
    private Bitmap bitmap;
    private Bitmap novoBitmap;
    private ListView cupom_ListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informa_cupom);

        tirarFoto();
//        pickFromGallery();

        Button informa_cupom = findViewById(R.id.informa_cupom_button);
        informa_cupom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentFinalizar = new Intent(InformaCupomActivity.this, ComparacaoActivity.class);
                startActivity(intentFinalizar);
            }
        });

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

        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), caminhoNovaFoto)) );
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

                    Bitmap bitmap = BitMapUtil.devolveBitmap(resultUri.getPath());
                    novoBitmap = BitMapUtil.toGrayscale(bitmap);
                    bitmap.recycle();

                    try {
                        UCropView uCropView = findViewById(R.id.informa_cupom_imagem);
                        uCropView.getCropImageView().setImageUri(resultUri, null);
                        uCropView.getCropImageView().setRotateEnabled(false);
                        uCropView.getOverlayView().setShowCropFrame(false);
                        uCropView.getOverlayView().setShowCropGrid(false);
                        uCropView.getOverlayView().setDimmedColor(Color.TRANSPARENT);
//                        uCropView.getCropImageView().setAdjustViewBounds(true);
                    } catch (Exception e) {
                        Log.e("ERRO", "setImageUri", e);
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    runTextRecognition();
                    break;
            }
        }
    }


    private void pickFromGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            permissoes.requestPermissionMeu(Manifest.permission.READ_EXTERNAL_STORAGE,
                    "A permissão de leitura de armazenamento é necessária para escolher arquivos.",
                    REQUEST_STORAGE_READ_ACCESS_PERMISSION);
        } else {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
                    .setType("image/*")
                    .addCategory(Intent.CATEGORY_OPENABLE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                String[] mimeTypes = {"image/jpeg", "image/png"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }

            startActivityForResult(Intent.createChooser(intent, "Selecione a imagem"), requestMode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE_READ_ACCESS_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickFromGallery();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private void runTextRecognition() {

        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(novoBitmap);
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
        List<String> cupom = new ArrayList<>();

        List<FirebaseVisionText.Block> blocks = texts.getBlocks();

        if (blocks.size() == 0) {
            Toast.makeText(this,"Nenhum texto foi enontrado na imagem", Toast.LENGTH_LONG).show();
            return;
        }

        String text ="";

        for (int i = 0; i < blocks.size(); i++) {

            text += blocks.get(i).getText() + "\n";

            List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
            for (int j = 0; j < lines.size(); j++) {
                cupom.add(lines.get(j).getText());
            }
        }

        Toast.makeText(this,"blocks\n" + text, Toast.LENGTH_LONG).show();
        List<String> cupomTratado = trataLista(cupom);
        carregaLista(cupomTratado);
    }

    private List<String> trataLista(List<String> cupom) {
        List<String> indice = new ArrayList<>();
        List<String> preco = new ArrayList<>();

        for (int i = 0; i< cupom.size(); i++){
            if(cupom.get(i).substring(0,3).matches("^[0oO]{1,2}[0-9]{1,2}")){
                indice.add(cupom.get(i));
            }
        }

        for (int i = 0; i< cupom.size(); i++){
            if (cupom.get(i).substring(0, 4).matches("^[0-9iI]+\\sun")){
                preco.add(cupom.get(i));
            }
        }

        for (int posicao = 1, j = 0; j < preco.size(); j++){
            indice.add(posicao, preco.get(j));
            posicao +=2;
        }

        return indice;
    }


    private void carregaLista(List<String> cupom) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cupom);
        cupom_ListView = findViewById(R.id.informa_cupom_ListView);
        cupom_ListView.setAdapter(adapter);
    }
}
