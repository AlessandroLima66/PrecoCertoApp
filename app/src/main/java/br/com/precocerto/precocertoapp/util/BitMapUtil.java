package br.com.precocerto.precocertoapp.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;

import java.io.IOException;
import java.io.InputStream;

public class BitMapUtil {

    public static Bitmap devolveBitmap(String caminhoDaFoto) {
        if (caminhoDaFoto != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(caminhoDaFoto);
            return bitmap;
        }
        return null;
    }


    public static Bitmap devolveBitmapRotacionado(String caminhoFoto){
        try {

            ExifInterface exif = new ExifInterface(caminhoFoto);

            String orientacao = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
            int codigoOrientacao = Integer.parseInt(orientacao);

            switch (codigoOrientacao) {
                case ExifInterface.ORIENTATION_NORMAL:
                    return rotacionadoBitmap(caminhoFoto, 0);
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return rotacionadoBitmap(caminhoFoto, 90);
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return rotacionadoBitmap(caminhoFoto, 180);
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return rotacionadoBitmap(caminhoFoto, 270);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Bitmap rotacionadoBitmap(String caminhoFoto, int angulo) {

        if (caminhoFoto != null) {
            // Abre o bitmap a partir do caminho da foto
            Bitmap bitmap = devolveBitmap(caminhoFoto);

            // Prepara a operação de rotação com o ângulo escolhido
            Matrix matrix = new Matrix();
            matrix.postRotate(angulo);

            // Cria um novo bitmap a partir do original já com a rotação aplicada
            return Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(), bitmap.getHeight(),
                    matrix, true);
        }

        return null;
    }

    public static Bitmap toGrayscale(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    public static Bitmap toBinarizacaoPorLimearFixo(Bitmap src) {
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
        // color information
        int A, R, G, B;
        int pixel;

        // scan through all pixels
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                int gray = (int) (0.2989 * R + 0.5870 * G + 0.1140 * B);

                // use 128 as threshold, above -> white, below -> black
                if (gray > 128)
                    gray = 255;
                else
                    gray = 0;
                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, gray, gray, gray));
            }
        }
        return bmOut;
    }
}
