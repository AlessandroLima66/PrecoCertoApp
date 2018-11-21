package br.com.precocerto.precocertoapp.services;

import br.com.precocerto.precocertoapp.dto.ProdutoMockado;
import br.com.precocerto.precocertoapp.model.Produto;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ProdutoService {

    @GET("produto")
    Call<Produto> getProduto(@Query("codigoDeBarras") String codigoDeBarras);


    @GET("api/json/get/bPynKVortu?indent=2")
    Call<ProdutoMockado> produtosMock();
}
