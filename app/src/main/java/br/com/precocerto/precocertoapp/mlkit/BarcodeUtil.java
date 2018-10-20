package br.com.precocerto.precocertoapp.mlkit;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;

import java.util.List;


public class BarcodeUtil {

    private Bitmap bitmap;
    private Context context;
    private String codigoDeBarras;

    public BarcodeUtil(Context context, Bitmap bitmap) {
        this.context = context;
        this.bitmap = bitmap;
    }

    public void runBarcodeRecognition() {

        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

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

                        Toast.makeText(context,"ERRO ao tenatar localizar um código de barras",Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void processBarcodeResult(List<FirebaseVisionBarcode> barcodes) {
        if (!barcodes.isEmpty())
            codigoDeBarras =  barcodes.get(0).getRawValue();
        else
            codigoDeBarras =  "Nenhum texto foi encontrado";

        carregaCodigoDeBarras();
    }

    private void carregaCodigoDeBarras() {

    }
}
