package br.com.precocerto.precocertoapp.services;

import br.com.precocerto.precocertoapp.dto.FinalCompra;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface FinalCompraService {

    @POST("salvarProduto")
    Call<FinalCompra> PersisteCompra(@Body FinalCompra finalCompra);
}
