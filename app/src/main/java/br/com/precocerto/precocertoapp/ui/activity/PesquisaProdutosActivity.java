package br.com.precocerto.precocertoapp.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.shawnlin.numberpicker.NumberPicker;

import java.util.ArrayList;
import java.util.List;

import br.com.precocerto.precocertoapp.R;
import br.com.precocerto.precocertoapp.model.ProdutoCompra;
import br.com.precocerto.precocertoapp.ui.adapter.ListaPesquisaProdutosaAdapter;

public class PesquisaProdutosActivity extends AppCompatActivity {
    private Button botao_lista;
    private Button botao_localizar;
    private AutoCompleteTextView produto_autoCompleteTextView;
    private ListView pesquisa_produto_listView;
    private List<ProdutoCompra> produtos = new ArrayList<>();

    private static final String[] CATEGORIES = new String[]{
            "Biscoito PassaTempo Recheado Chocolate 130g",
            "Achocolatado Toddynho 200 ML",
            "Suco Pronto Su Fresh Nectar Abacaxi",
            "Arroz Tipo 1 1kg Camil",
            "Feijao Camil Preto"
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesquisa_produtos);

        preparaView();
        preparaAutoComplete();
        registerForContextMenu(pesquisa_produto_listView);

        botao_lista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        botao_localizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentLocalizacao = new Intent(PesquisaProdutosActivity.this, MapsActivity.class);
                startActivity(intentLocalizacao);
            }
        });

        produto_autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = produto_autoCompleteTextView.getText().toString();
                produtos.add(new ProdutoCompra(text, "", Integer.valueOf(1), null, null));
                produto_autoCompleteTextView.setText(null);
                fechaKeyboard(PesquisaProdutosActivity.this,produto_autoCompleteTextView);
                carregaLista();
            }
        });

        pesquisa_produto_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mostraAlertEditarItem(produtos.get(position));
            }
        });
    }

    public void fechaKeyboard(Context context, View editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        final ProdutoCompra produtoCompra = (ProdutoCompra) pesquisa_produto_listView.getItemAtPosition(info.position);

        MenuItem itemExcluir = menu.add("Excluir");

        itemExcluir.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                produtos.remove(info.position);
                carregaLista();
                return false;
            }
        });
    }

    private void mostraAlertEditarItem(final ProdutoCompra produto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = PesquisaProdutosActivity.this.getLayoutInflater();
        View layout = inflater.inflate(R.layout.layout_dialog_quantidade, null);

        TextView nome_produto = layout.findViewById(R.id.dialog_quantidade_nome_produto);
        nome_produto.setText(produto.getNome());

        final NumberPicker numberPicker = layout.findViewById(R.id.dialog_quantidade_number_picker);
        numberPicker.setValue(produto.getQuantidade());

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                produto.setQuantidade(numberPicker.getValue());
                carregaLista();
            }
        });

        builder.setView(layout);
        builder.show();
    }

    private void preparaAutoComplete() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, CATEGORIES);
        AutoCompleteTextView nameTV = (AutoCompleteTextView)
                findViewById(R.id.pesquisa_produto_autoCompleteTextView);

        nameTV.setAdapter(adapter);
    }

    private void preparaView() {
        botao_lista = findViewById(R.id.pesquisa_produto_botao_lista);
        botao_localizar = findViewById(R.id.pesquisa_produto_botao_localizar);
        pesquisa_produto_listView = findViewById(R.id.pesquisa_produto_listView);
        produto_autoCompleteTextView = findViewById(R.id.pesquisa_produto_autoCompleteTextView);
    }


    private void carregaLista() {
        pesquisa_produto_listView.setAdapter(new ListaPesquisaProdutosaAdapter(this, produtos));
    }
}
