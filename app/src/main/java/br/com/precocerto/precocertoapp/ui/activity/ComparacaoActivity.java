package br.com.precocerto.precocertoapp.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.precocerto.precocertoapp.R;
import br.com.precocerto.precocertoapp.dao.ProdutoDAO;
import br.com.precocerto.precocertoapp.model.Produto;
import br.com.precocerto.precocertoapp.bo.ProdutoComparado;
import br.com.precocerto.precocertoapp.model.ProdutoCompra;
import br.com.precocerto.precocertoapp.model.ProdutoLista;
import br.com.precocerto.precocertoapp.ui.adapter.ListaComparacaoAdapter;

public class ComparacaoActivity extends AppCompatActivity {
    private List<ProdutoLista> listaCupom = new ArrayList<>();
    private List<ProdutoLista> listaProduto = new ArrayList<>();
    private Map<String, ProdutoCompra> listaProdutoMap = new HashMap<>();
    private List<ProdutoComparado> produtosComparados = new ArrayList<>();
    private Button botao_finalizar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comparacao);

//        listaCupom = (List<ProdutoLista>) getIntent().getSerializableExtra("cupom");
//        converteParaMap();
//        carregaLista();
        mockdados();
        comparaLista();
        exibeLista();

        botao_finalizar = findViewById(R.id.comparacao_botao_finalizar);
        botao_finalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void converteParaMap(){
        for (int i = 0; i < listaCupom.size(); i++) {
            listaProdutoMap.put(listaCupom.get(i).getCodigoDeBarras(),listaCupom.get(i));
        }
    }

    @Override
    public void onBackPressed() {
        setResult(666);
        super.onBackPressed();
    }

    private void carregaLista() {
        ProdutoDAO dao = new ProdutoDAO(this);
        listaProduto = dao.buscaProdutos();
        dao.close();
    }

    private void comparaLista() {
        ProdutoCompra produtoVazio = new ProdutoCompra("---", "0000000000000", Integer.valueOf(0), Double.valueOf(0), Double.valueOf(0));
        for (int i = 0; i < listaProduto.size(); i++) {
            String codigoDeBarras = listaProduto.get(i).getCodigoDeBarras();
            if (listaProdutoMap.containsKey(codigoDeBarras)) {
                ProdutoComparado produtoComparado = new ProdutoComparado(listaProduto.get(i), listaProdutoMap.get(codigoDeBarras));
                if (produtoComparado.temDivergencia()) {
                    produtoComparado.setDivergencia(true);
                }
                produtosComparados.add(produtoComparado);
                listaProdutoMap.remove(codigoDeBarras);
            } else {
                ProdutoComparado produtoComparadoErro = new ProdutoComparado(listaProduto.get(i), produtoVazio);
                produtoComparadoErro.setDivergencia(true);
                produtosComparados.add(produtoComparadoErro);
            }
        }

        Set<String> chaves = listaProdutoMap.keySet();
        for (String chave : chaves) {
            if (chave != null) {
                ProdutoComparado produtoComparadoErro = new ProdutoComparado(produtoVazio, listaProdutoMap.get(chave));
                produtoComparadoErro.setDivergencia(true);
                produtosComparados.add(produtoComparadoErro);
            }
        }
    }

        private void exibeLista () {
            ListView listaComparacao = findViewById(R.id.comparacao_ListView);
            listaComparacao.setAdapter(new ListaComparacaoAdapter(this, produtosComparados));
        }

        private void mockdados (){
            List<ProdutoLista> produtos = new ArrayList<>();
            produtos.add(new ProdutoLista("Leite UHT Integral Piracanjuba 1L", "7898215151890", null, Integer.valueOf(12), Double.valueOf(2.49), Double.valueOf(29.88), "" ));
            produtos.add(new ProdutoLista("Suco em Pó Sabor Maracujá TANG 25g", "7622300861261", null, Integer.valueOf(1), Double.valueOf(0.99), Double.valueOf(0.99), ""));
            produtos.add(new ProdutoLista("Chocolate Bis Xtra LACTA 45g", "7622300988470", null, Integer.valueOf(1), Double.valueOf(2.49), Double.valueOf(2.49), ""));
            produtos.add(new ProdutoLista("Cerveja Itaipava Lata 473 ml", "7897395020217",null,  Integer.valueOf(8), Double.valueOf(3.49), Double.valueOf(27.92), ""));
            produtos.add(new ProdutoLista("Biscoito PassaTempo Nestle Recheado", "7891000051351",null,  Integer.valueOf(1), Double.valueOf(1.39), Double.valueOf(1.39), ""));

            listaCupom = produtos;
            converteParaMap();
            produtos.remove(produtos.size() -1);
            produtos.add(new ProdutoLista("Biscoito PassaTempo Nestle Recheado", "7891000051351",null,  Integer.valueOf(1), Double.valueOf(1.99), Double.valueOf(1.99), ""));
            listaProduto = produtos;
        }
    }
