package br.com.precocerto.precocertoapp.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import br.com.precocerto.precocertoapp.R;
import br.com.precocerto.precocertoapp.dao.ProdutoDAO;
import br.com.precocerto.precocertoapp.model.Produto;
import br.com.precocerto.precocertoapp.model.ProdutoComparado;
import br.com.precocerto.precocertoapp.ui.adapter.ListaComparacaoAdapter;
import br.com.precocerto.precocertoapp.ui.adapter.ListaDeComprasAdapter;

public class ComparacaoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comparacao);

        carregaLista();
    }

    private void carregaLista(){
        List<ProdutoComparado> produtosComparados = new ArrayList<>();

        produtosComparados.add(new ProdutoComparado(
                new Produto("Biscoito PassaTempo Recheado Chocolate 130g", "7896512909787", Integer.valueOf(2), Double.valueOf(1.99), Double.valueOf(3.98), "teste"),
                new Produto("001 PassaTempo Recheado", "7896512909787", Integer.valueOf(2), Double.valueOf(1.99), Double.valueOf(3.998), "teste"),
                false));

        produtosComparados.add(new ProdutoComparado(
                new Produto("teste2", "654321", Integer.valueOf(2), Double.valueOf(2.0), Double.valueOf(4.0), "teste"),
                new Produto("teste2", "654321", Integer.valueOf(2), Double.valueOf(2.0), Double.valueOf(4.0), "teste"),
                false));

        produtosComparados.add(new ProdutoComparado(
                new Produto("teste3 erro", "789", Integer.valueOf(4), Double.valueOf(1.0), Double.valueOf(4.0), "teste"),
                new Produto("teste2 erro", "654321", Integer.valueOf(3), Double.valueOf(2.0), Double.valueOf(6.0), "teste"),
                true));

        produtosComparados.add(new ProdutoComparado(
                new Produto("---", "---", Integer.valueOf(0), Double.valueOf(0), Double.valueOf(0), "---"),
                new Produto("teste2", "654321", Integer.valueOf(3), Double.valueOf(2.20), Double.valueOf(6.60), "teste"),
                true));

        produtosComparados.add(new ProdutoComparado(
                new Produto("teste3", "789", Integer.valueOf(8), Double.valueOf(1.0), Double.valueOf(8.0), "teste"),
                new Produto("---", "---", Integer.valueOf(0), Double.valueOf(0), Double.valueOf(0), "---"),
                true));

        ListView listaComparacao = findViewById(R.id.comparacao_ListView);
        listaComparacao.setAdapter(new ListaComparacaoAdapter(this, produtosComparados));
    }
}
