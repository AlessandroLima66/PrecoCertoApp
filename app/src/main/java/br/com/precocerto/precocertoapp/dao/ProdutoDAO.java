package br.com.precocerto.precocertoapp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import br.com.precocerto.precocertoapp.model.Produto;

public class ProdutoDAO extends SQLiteOpenHelper{

    public ProdutoDAO(Context context) {
        super(context, "Produtos", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE Produtos (" +
                "id INTEGER PRIMARY KEY," +
                "nome TEXT NOT NULL," +
                "codigoDeBarras TEXT NOT NULL,"+
                "quantidade INTEGER," +
                "valorUnitario REAL," +
                "valorTotal REAL," +
                "caminhoFoto TEXT);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insere(Produto produto) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues dados = pegaDadosDoProduto(produto);
        db.insert("Produtos", null, dados);
    }

    public List<Produto> buscaProdutos() {
        String sql = "SELECT * from Produtos";
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(sql, null);

        List<Produto> produtos = new ArrayList<>();
        while(c.moveToNext()){
            Produto produto = new Produto();
            produto.setId(c.getLong(c.getColumnIndex("id")));
            produto.setNome(c.getString(c.getColumnIndex("nome")));
            produto.setCodigoDeBarras(c.getString(c.getColumnIndex("codigoDeBarras")));
            produto.setQuantidade(c.getInt(c.getColumnIndex("quantidade")));
            produto.setValorUnitario(c.getDouble(c.getColumnIndex("valorUnitario")));
            produto.setValorTotal(c.getDouble(c.getColumnIndex("valorTotal")));
            produto.setCaminhoFoto(c.getString(c.getColumnIndex("caminhoFoto")));
            produtos.add(produto);
        }
        c.close();
        return produtos;
    }

    public void deleta(Produto produto) {
        SQLiteDatabase db = getWritableDatabase();
        String[] params = {produto.getId().toString()};
        db.delete("Produtos", "id = ?", params);
    }

    public void altera(Produto produto) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues dados = pegaDadosDoProduto(produto);

        String[] params = {produto.getId().toString()};
        db.update("Produtos", dados, "id = ?", params);
    }


    @NonNull
    private ContentValues pegaDadosDoProduto(Produto produto) {
        ContentValues dados = new ContentValues();
        dados.put("nome", produto.getNome());
        dados.put("codigoDeBarras", produto.getCodigoDeBarras());
        dados.put("quantidade", produto.getQuantidade());
        dados.put("valorUnitario", String.valueOf(produto.getValorUnitario()));
        dados.put("valorTotal", String.valueOf(produto.getValorTotal()));
        dados.put("caminhoFoto", produto.getCaminhoFoto());
        return dados;
    }
}
