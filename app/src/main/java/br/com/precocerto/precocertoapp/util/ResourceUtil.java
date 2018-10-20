package br.com.precocerto.precocertoapp.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

public class ResourceUtil {

    public static Drawable devolveDrawable(Context context, String drawableEmTexto) {
        return Drawable.createFromPath(drawableEmTexto);
    }

}
