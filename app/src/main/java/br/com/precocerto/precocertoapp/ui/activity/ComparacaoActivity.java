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

        listaCupom = (List<ProdutoLista>) getIntent().getSerializableExtra("cupom");
        converteParaMap();
        carregaLista();
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

        //Cupom 1
        //listaProduto.add(new Produto("SUCO PO 25G MARACUJA", "7622300861261", Integer.valueOf(1), Double.valueOf(0.99), Double.valueOf(0.99), ""));
        //listaProduto.add(new Produto("QJ PRATO QUATA FATIADO B", "0000000005042", Integer.valueOf(1), Double.valueOf(24.90), Double.valueOf(4.69), ""));
        //listaProduto.add(new Produto("BIS LACTA 45G XTRA +CHOC", "7622300988470", Integer.valueOf(1), Double.valueOf(2.49), Double.valueOf(2.49), ""));
        //listaProduto.add(new Produto("CERV. ITAIPAVA 473ML PILS", "7897395020217", Integer.valueOf(1), Double.valueOf(2.99), Double.valueOf(2.99), ""));
        //listaProduto.add(new Produto("SUCO POMARIS 100% 300ML", "78988047190227", Integer.valueOf(1), Double.valueOf(1.49), Double.valueOf(1.49), ""));

        //Cupom 2
//        listaProduto.add(new ProdutoCompra("PAD-PAO FRANCES INTEGRAL", "0000000006328", Integer.valueOf(1), Double.valueOf(17.90), Double.valueOf(7.02)));
//        listaProduto.add(new ProdutoCompra("BISNAGUINHA PANCO 300G", "7891203010605", Integer.valueOf(1), Double.valueOf(5.49), Double.valueOf(5.49)));
//        listaProduto.add(new ProdutoCompra("BISC PIRAQUE 300G INTEG", "7896024721358", Integer.valueOf(1), Double.valueOf(3.99), Double.valueOf(3.99)));
//        listaProduto.add(new ProdutoCompra("LEITE LONG 1L INTEGRAL", "7896183220006", Integer.valueOf(1), Double.valueOf(2.19), Double.valueOf(8.76)));
//        listaProduto.add(new ProdutoCompra("SUCO POMARIS 100% 300ML", "78988047190227", Integer.valueOf(1), Double.valueOf(1.49), Double.valueOf(1.49)));


//        listaProdutoMap.put("7622300861261", new Produto("SUCO PO 25G MARACUJA", "7622300861261", Integer.valueOf(1), Double.valueOf(0.99), Double.valueOf(0.99), ""));
//        listaProdutoMap.put("0000000005042", new Produto("QJ PRATO QUATA FATIADO B", "0000000005042", Integer.valueOf(1), Double.valueOf(24.90), Double.valueOf(3.69), ""));
//        listaProdutoMap.put("7622300988470", new Produto("BIS LACTA 45G XTRA +CHOC", "7622300988470", Integer.valueOf(1), Double.valueOf(2.49), Double.valueOf(2.49), ""));
//        listaProdutoMap.put("7897395020217", new Produto("CERV. ITAIPAVA 473ML PILS", "7897395020217", Integer.valueOf(1), Double.valueOf(2.99), Double.valueOf(2.99), ""));
//        listaProdutoMap.put("7991025102496", new Produto("IOG DADONE 900G MORANGO", "7991025102496", Integer.valueOf(1), Double.valueOf(5.99), Double.valueOf(5.99), ""));
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
    }
