package br.com.precocerto.precocertoapp.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.precocerto.precocertoapp.R;
import br.com.precocerto.precocertoapp.model.ProdutoCompra;
import br.com.precocerto.precocertoapp.model.ProdutoLista;
import br.com.precocerto.precocertoapp.ui.activity.PesquisaProdutosActivity;

public class ListaPesquisaProdutosaAdapter extends BaseAdapter {


    private Context context;
    private List<ProdutoCompra> produtos;

    public ListaPesquisaProdutosaAdapter(Context context,List<ProdutoCompra> produtos) {
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
                .inflate(R.layout.item_pesquisa_produto, parent,false);

        ProdutoCompra produto = produtos.get(posicao);
        mostraItem(viewCriada, produto);

        return viewCriada;
    }

    private void mostraItem(View viewCriada, ProdutoCompra produto) {

        TextView produto_nome = viewCriada.findViewById(R.id.item_pesquisa_produto_nome);
        produto_nome.setText(produto.getNome());

        TextView produto_quantidade = viewCriada.findViewById(R.id.item_pesquisa_produto_quantidade);
        produto_quantidade.setText(String.valueOf(produto.getQuantidade()));
    }
}
