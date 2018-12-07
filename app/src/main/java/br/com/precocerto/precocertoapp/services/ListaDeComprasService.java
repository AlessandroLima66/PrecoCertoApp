package br.com.precocerto.precocertoapp.services;

import java.util.List;

import br.com.precocerto.precocertoapp.dto.ListaDeCompra;

import br.com.precocerto.precocertoapp.dto.listaDeMercados;
import br.com.precocerto.precocertoapp.model.ProdutoCompra;
import retrofit2.Call;
import retrofit2.http.Body;

import retrofit2.http.POST;

public interface ListaDeComprasService {

    @POST("recomendacao")
    Call<listaDeMercados> getListadeCompras(@Body List<ProdutoCompra> listaDeCompras);
}
