package br.com.precocerto.precocertoapp.services;

import br.com.precocerto.precocertoapp.dto.ProdutoSync;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ProdutoService {


    /*
    @GET("produto/{codigoDeBarras}")
    Call<String> procuraCodigoDeBarras(
            @Query("codigoDeBarras") String codigoDeBarras
    );
     */


    //@GET("produto/{codigoDeBarras}")
    @GET("api/json/get/cflucAgKle?indent=2")
    Call<ProdutoSync> procuraCodigoDeBarras();
}
