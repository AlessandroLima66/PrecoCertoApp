package br.com.precocerto.precocertoapp.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.precocerto.precocertoapp.R;
import br.com.precocerto.precocertoapp.bo.ProdutoComparado;
import br.com.precocerto.precocertoapp.model.ProdutoCompra;
import br.com.precocerto.precocertoapp.util.MoedaUtil;

public class ListaComparacaoAdapter extends BaseAdapter {

    private Context context;
    List<ProdutoComparado> produtosComparados;

    public ListaComparacaoAdapter(Context context, List<ProdutoComparado> produtosComparados) {
        this.context = context;
        this.produtosComparados = produtosComparados;
    }

    @Override
    public int getCount() {
        return produtosComparados.size();
    }

    @Override
    public Object getItem(int item) {
        return produtosComparados.get(item);
    }

    @Override
    public long getItemId(int posicao) {
        return 0;
    }

    @Override
    public View getView(int posicao, View view, ViewGroup parent) {

        View viewCriada =  LayoutInflater.from(context)
                .inflate(R.layout.item_comparacao, parent,false);

        ProdutoComparado produtoComparado = produtosComparados.get(posicao);
        mostraItens(viewCriada, produtoComparado);

        return viewCriada;
    }

    private void mostraItens(View viewCriada, ProdutoComparado produtoComparado) {
        TextView nome_produto = viewCriada.findViewById(R.id.item_comparacao_nome_produto);
        nome_produto.setText(produtoComparado.getProdutoCompra().getNome());


        TextView nome_produto_cupom = viewCriada.findViewById(R.id.item_comparacao_nome_produto_cupom);
        nome_produto_cupom.setText(produtoComparado.getProdutoCupom().getNome());


        TextView valor_total = viewCriada.findViewById(R.id.item_comparacao_valor_total);
        String valorTotalFormatado = MoedaUtil
                .formataParaExibicao(produtoComparado.getProdutoCompra().getValorTotal());
        valor_total.setText(valorTotalFormatado);


        TextView valor_total_item_cupom = viewCriada.findViewById(R.id.item_comparacao_valor_total_item_cupom);
        String valorTotalFormatado2 = MoedaUtil
                .formataParaExibicao(produtoComparado.getProdutoCupom().getValorTotal());
        valor_total_item_cupom.setText(valorTotalFormatado2);

        if (produtoComparado.isDivergencia()) {

            nome_produto.setTextColor(Color.RED);
            nome_produto_cupom.setTextColor(Color.RED);
//            CardView comparacao_cardvie = viewCriada.findViewById(R.id.item_comparacao_cardview);
//            comparacao_cardvie.setCardBackgroundColor(Color.parseColor("#c8ff4081"));
        }

    }


}
