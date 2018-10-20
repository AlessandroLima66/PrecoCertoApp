package br.com.precocerto.precocertoapp.services;

import java.util.List;

import br.com.precocerto.precocertoapp.model.Produto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ComparacaoService {

    @POST("api/json/get/cflucAgKle?indent=2")
    Call<Produto> ComparaListas(@Body List<Produto> produtos, @Body List<String> cupom);
}
