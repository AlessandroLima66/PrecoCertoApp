package br.com.precocerto.precocertoapp.bo;

import android.graphics.RectF;
import android.support.annotation.NonNull;

import java.io.Serializable;

public class CupomPosicaoOCR implements Comparable<CupomPosicaoOCR> {
    private RectF coordenadas;
    private String texto;

    public CupomPosicaoOCR(RectF coordenadas, String texto) {
        this.coordenadas = coordenadas;
        this.texto = texto;
    }

    public RectF getPosicao() {
        return coordenadas;
    }

    public void setPosicao(RectF coordenadas) {
        this.coordenadas = coordenadas;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    @Override
    public int compareTo(@NonNull CupomPosicaoOCR cupom) {
        if (coordenadas.top > cupom.coordenadas.top) {
            return 1;
        }
        if (coordenadas.top < cupom.coordenadas.top) {
            return -1;
        }
        return 0;
    }

}
