package br.com.precocerto.precocertoapp.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.precocerto.precocertoapp.R;
import br.com.precocerto.precocertoapp.model.Produto;
import br.com.precocerto.precocertoapp.model.ProdutoCompra;
import br.com.precocerto.precocertoapp.model.ProdutoLista;
import br.com.precocerto.precocertoapp.util.MoedaUtil;

public class ListaFinalizarCompraAdapter extends BaseAdapter {

    public static final String ESPACO = " ";
    public static final String UNIDADE = " UN ";
    public static final String VEZES = "X ";
    public static final String IGUAL = " = ";

    private Context context;
    private List<ProdutoLista> produtos;

    public ListaFinalizarCompraAdapter(Context context,List<ProdutoLista> produtos) {
        this.context = context;
        this.produtos = produtos;
    }

    @Override
    public int getCount() {
        return produtos.size();
    }

    @Override
    public Object getItem(int item) {
        return produtos.get(item);
    }

    @Override
    public long getItemId(int item) {
        return 0;
    }

    @Override
    public View getView(int posicao, View view, ViewGroup parent) {
        View viewCriada =  LayoutInflater.from(context)
                .inflate(R.layout.item_finalizar_compra, parent,false);

        ProdutoLista produto = produtos.get(posicao);
        mostraItem(viewCriada, produto, posicao);

        return viewCriada;
    }

    private void mostraItem(View viewCriada, ProdutoLista produto, int posicao){
        TextView finalizar_compra_linha_1 = viewCriada.findViewById(R.id.item_finalizar_compra_linha_1);
        StringBuffer linha_1 = new StringBuffer();
        linha_1.append(String.format("%03d", ++posicao).toString());
        linha_1.append(ESPACO);
        linha_1.append(produto.getCodigoDeBarras());
        linha_1.append(ESPACO);
        linha_1.append(produto.getNome());
        finalizar_compra_linha_1.setText(linha_1);
        TextView finalizar_compra_linha_2 = viewCriada.findViewById(R.id.item_finalizar_compra_linha_2);
        StringBuffer linha_2 = new StringBuffer();
        linha_2.append(produto.getQuantidade());
        linha_2.append(UNIDADE);
        linha_2.append(VEZES);
        linha_2.append(produto.getValorUnitario());
        linha_2.append(IGUAL);
        linha_2.append(produto.getValorTotal());
        finalizar_compra_linha_2.setText(linha_2);

        if( produto.getValorTotal() == 0 | produto.getCodigoDeBarras().equals("0")){
            int corErro = Color.parseColor("#FF6347");
            finalizar_compra_linha_1.setBackgroundColor(corErro);
            finalizar_compra_linha_2.setBackgroundColor(corErro);
        }
//        else if(produto.getQuantidade() == 0 | produto.getValorUnitario() == 0 ){
//            int corAtencao = Color.parseColor("#F0E68C");
//            finalizar_compra_linha_1.setBackgroundColor(corAtencao);
//            finalizar_compra_linha_2.setBackgroundColor(corAtencao);
//        }
    }
}
