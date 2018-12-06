package br.com.precocerto.precocertoapp.dao;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.precocerto.precocertoapp.model.Produto;

public class ListaProdutosDAO {

    private Context context;

    public ListaProdutosDAO(Context context) {
        this.context = context;
    }

    private String pegaJSONAsset(){
        String json = null;
        try {
            InputStream is = context.getAssets().open("listaProdutos.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer,"UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }

    public List<Produto> getlistaProdutos(){
        String json = pegaJSONAsset();
        List<Produto> listaProdutos = new ArrayList<>();


        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("listaProdutos");
            for (int i =0; i< jsonArray.length(); i++){
                JSONObject jb = (JSONObject) jsonArray.get(i);
                Produto produto = new Produto();
                produto.setNome(jb.getString("nome"));
                produto.setCodigoDeBarras(jb.getString("codigoDeBarras"));
                listaProdutos.add(produto);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listaProdutos;
    }
}
