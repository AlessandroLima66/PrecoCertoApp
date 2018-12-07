package br.com.precocerto.precocertoapp.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.shawnlin.numberpicker.NumberPicker;

import java.util.ArrayList;
import java.util.List;

import br.com.precocerto.precocertoapp.R;
import br.com.precocerto.precocertoapp.dao.ProdutoDAO;
import br.com.precocerto.precocertoapp.model.Produto;
import br.com.precocerto.precocertoapp.model.ProdutoCompra;
import br.com.precocerto.precocertoapp.model.ProdutoLista;
import br.com.precocerto.precocertoapp.ui.adapter.ListaDeComprasAdapter;
import br.com.precocerto.precocertoapp.util.MoedaUtil;

public class ListaDeComprasActivity extends AppCompatActivity {

    public static final String MENSAGEM_LISTA_DE_COMPRAS_VAZIA = "Sua lista de compras está vazia!";
    private final CharSequence[] OPCOES_MENU = {"Pesquisar Produtos", "Limpar Lista de Compras"};
    private final String TITLE_DIALOG_MENU = "Menu";
    private final String TITLE_DIALOG_FINALIZAR = "Finalizar Compra";
    private final String MENSAGEM_DIALOG_FINALIZAR = ("Tem certeza que deseja finalizar sua compra?");
    private final String BOTAO_POSITIVO = ("Sim");
    private final String BOTAO_NEGATIVO = ("Não");

    private Button botao_adiciona;
    private Button botao_finalizar_compra;
    private Button botao_menu;
    private TextView valor_total_compra;
    private AlertDialog dialogMenu;
    private AlertDialog dialogFinalizar;
    private SwipeMenuListView listaDeCompras;
    private Double valorTotalCompra = Double.valueOf(0);
    private  List<ProdutoLista> produtos = new ArrayList<>();


        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_compras);

            preparaView();
            carregaLista();
            setSwipe();


        botao_adiciona.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resumoProdutos = new Intent(ListaDeComprasActivity.this, DetalheDoProdutoActivity.class);
                startActivity(resumoProdutos);

            }
        });

        botao_finalizar_compra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               mostraAlertDialogFinalizarCompra();
            }
        });

        botao_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostraAlertDialogMenu();
            }
        });
    }

    private void preparaView() {
        botao_adiciona = findViewById(R.id.lista_compras_botao_adiciona);
        botao_finalizar_compra = findViewById(R.id.lista_compras_botao_finalizar_compra);
        botao_menu = findViewById(R.id.lista_compras_botao_menu);
        listaDeCompras = findViewById(R.id.lista_compras_ListView);
    }

    private void mostraAlertDialogMenu() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setIcon(R.drawable.ic_menu_black);
        dialogBuilder.setTitle(TITLE_DIALOG_MENU);
        dialogBuilder.setItems(OPCOES_MENU, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    Intent intentPesquisaProdutos = new Intent(ListaDeComprasActivity.this, PesquisaProdutosActivity.class);
                    startActivity(intentPesquisaProdutos);
                } else {
                    limparLista();
                    carregaLista();
                }
            }
        });
        dialogMenu = dialogBuilder.create();
        dialogMenu.show();
    }

    private void mostraAlertDialogFinalizarCompra() {
        if (!(valorTotalCompra.doubleValue() <= 0)) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setTitle(TITLE_DIALOG_FINALIZAR);
            dialogBuilder.setIcon(R.drawable.ic_finalizar_brack);
            dialogBuilder.setMessage(MENSAGEM_DIALOG_FINALIZAR);
            dialogBuilder.setPositiveButton(BOTAO_POSITIVO, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intentFinalizarCompra = new Intent(ListaDeComprasActivity.this, FinalizarCompraActivity.class);
                    startActivity(intentFinalizarCompra);
                }
            });

            dialogBuilder.setNegativeButton(BOTAO_NEGATIVO, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            dialogFinalizar = dialogBuilder.create();
            dialogFinalizar.show();
        } else
            Toast.makeText(ListaDeComprasActivity.this, MENSAGEM_LISTA_DE_COMPRAS_VAZIA, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregaLista();
    }


    private void carregaLista() {
        ProdutoDAO dao = new ProdutoDAO(this);
        produtos = dao.buscaProdutos();
        dao.close();

        listaDeCompras.setAdapter(new ListaDeComprasAdapter(this, produtos));
        somaTotalDaCompra(produtos);
    }

    private void setSwipe(){
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem editarItem = new SwipeMenuItem(getApplicationContext());
                editarItem.setWidth(160);
                editarItem.setIcon(R.drawable.botao_editar);
                menu.addMenuItem(editarItem);

                SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
                deleteItem.setWidth(160);
                deleteItem.setIcon(R.drawable.botao_excluir);
                menu.addMenuItem(deleteItem);
            }
        };

        listaDeCompras.setSwipeDirection(SwipeMenuListView.DIRECTION_RIGHT);
        listaDeCompras.setMenuCreator(creator);
        listaDeCompras.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        mostraAlertEditarItem(produtos.get(position));
                        break;
                    case 1:
                        removeProduto(produtos.get(position));
                        carregaLista();
                        break;
                }
                return false;
            }
        });
    }

    private void mostraAlertEditarItem(final ProdutoLista produto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = ListaDeComprasActivity.this.getLayoutInflater();
        View layout = inflater.inflate(R.layout.layout_dialog_editar_produto, null);

        TextView nome_produto = layout.findViewById(R.id.dialog_editar_produto_nome_produto);
        nome_produto.setText(produto.getNome());

        final EditText produto_quantidade = layout.findViewById(R.id.dialog_editar_produto_quantidade);
        produto_quantidade.setText(String.valueOf(produto.getQuantidade()));

        final EditText valor_unitario = layout.findViewById(R.id.dialog_editar_produto_valor_unitario);
        valor_unitario.setText(Double.toString(produto.getValorUnitario()));

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                produto.setQuantidade(Integer.valueOf(produto_quantidade.getText().toString()));
                produto.setValorUnitario(Double.valueOf(String.valueOf(valor_unitario.getText())));
                produto.setValorTotal(produto.getQuantidade() * produto.getValorUnitario());
                alteraProduto(produto);
                carregaLista();
            }
        });

        builder.setView(layout);
        builder.show();
    }

    private void limparLista() {
        if (!(valorTotalCompra.doubleValue() <= 0)) {
            ProdutoDAO dao = new ProdutoDAO(this);
            dao.dropAll();
            dao.close();
        }
    }

    private void removeProduto(ProdutoLista produto) {
        ProdutoDAO dao = new ProdutoDAO(this);
        dao.deleta(produto);
        dao.close();
    }

    private void alteraProduto(ProdutoLista produto) {
        ProdutoDAO dao = new ProdutoDAO(this);
        dao.altera(produto);
        dao.close();
    }

    private void somaTotalDaCompra(List<ProdutoLista> produtos) {
        valorTotalCompra = Double.valueOf(0);
        for (int i = 0; i < produtos.size(); i++) {

            valorTotalCompra += produtos.get(i).getValorTotal();
        }

        valor_total_compra = findViewById(R.id.lista_compras_valor_total_compra);
        String valor = MoedaUtil.formataParaExibicao(valorTotalCompra);
        valor_total_compra.setText(valor);
    }

}
