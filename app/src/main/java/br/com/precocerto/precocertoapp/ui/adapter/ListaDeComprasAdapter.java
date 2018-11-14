package br.com.precocerto.precocertoapp.ui.adapter;

import android.content.Context;
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

public class ListaDeComprasAdapter extends BaseAdapter {

    private Context context;
    private List<ProdutoLista> produtos;

    public ListaDeComprasAdapter(Context context, List<ProdutoLista> produtos) {
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
                .inflate(R.layout.item_compra, parent,false);

        ProdutoCompra produto = produtos.get(posicao);
        mostraNome(viewCriada, produto);
        mostraQuantidade(viewCriada, produto);
        mostraValorUnitario(viewCriada, produto);
        mostraValorTotal(viewCriada, produto);

        return viewCriada;
    }

    private void mostraNome(View viewCriada, Produto produto){
        TextView nome = viewCriada.findViewById(R.id.item_compra_nome);
        nome.setText(produto.getNome());
    }

    private void mostraQuantidade(View viewCriada, ProdutoCompra produto){
        TextView quantidade = viewCriada.findViewById(R.id.item_compra_quantidade);
        quantidade.setText(String.valueOf(produto.getQuantidade() + " un"));
    }

    private void mostraValorUnitario(View viewCriada, ProdutoCompra produto){
        TextView valorUnitario = viewCriada.findViewById(R.id.item_compra_valor_unitario);

        String valorUnitarioFormatado = MoedaUtil
                .formataParaExibicao(produto.getValorUnitario());

        valorUnitario.setText(valorUnitarioFormatado);
    }

    private  void mostraValorTotal(View viewCriada, ProdutoCompra produto){
        TextView valorTotal = viewCriada.findViewById(R.id.item_compra_valor_total);

        String valorTotalFormatado = MoedaUtil
                .formataParaExibicao(produto.getValorTotal());

        valorTotal.setText(valorTotalFormatado);
    }

}
