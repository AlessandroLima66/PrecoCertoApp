package br.com.precocerto.precocertoapp.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.precocerto.precocertoapp.R;

public class ListaCupomOCRAdapter extends BaseAdapter {

    private Context context;
    private List<String> cupom;

    public ListaCupomOCRAdapter(Context context, List<String> cupom) {
        this.context = context;
        this.cupom = cupom;
    }

    @Override
    public int getCount() {
        return cupom.size();
    }

    @Override
    public Object getItem(int posicao) {
        return cupom.get(posicao);
    }

    @Override
    public long getItemId(int posicao) {
        return 0;
    }

    @Override
    public View getView(int posicao, View view, ViewGroup parent) {

        View viewCriada =  LayoutInflater.from(context)
                .inflate(R.layout.item_cupom_ocr, parent,false);

        String item = cupom.get(posicao);
        mostraItens(viewCriada, item);

        return viewCriada;
    }

    private void mostraItens(View viewCriada, String item) {
        TextView cupom_ocr_texto = viewCriada.findViewById(R.id.item_cupom_ocr_texto);
        cupom_ocr_texto.setText(item);
    }
}
