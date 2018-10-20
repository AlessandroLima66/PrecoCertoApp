package br.com.precocerto.precocertoapp.retrofit;

import br.com.precocerto.precocertoapp.services.ProdutoService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RetrofitInicializador {
    private final Retrofit retrofit;

    public RetrofitInicializador(){

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.addInterceptor(interceptor);

        //.baseUrl("http://192.168.1.32:8080/api/")
        retrofit = new Retrofit.Builder()
                .baseUrl("http://www.json-generator.com/")
                .addConverterFactory(JacksonConverterFactory.create())
                .client(client.build())
                .build();
    }

    public ProdutoService getProdutoService() {
        return retrofit.create(ProdutoService.class);
    }

}
