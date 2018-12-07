package br.com.precocerto.precocertoapp.services;

import br.com.precocerto.precocertoapp.dto.ProdutoDTO;
import br.com.precocerto.precocertoapp.dto.ProdutoMockado;
import br.com.precocerto.precocertoapp.model.Produto;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ProdutoService {

    @GET("consultaNome")
    Call<ProdutoDTO> getProduto(@Query("codigoDeBarras") String codigoDeBarras);
}
