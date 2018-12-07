package br.com.precocerto.precocertoapp.retrofit;

import br.com.precocerto.precocertoapp.model.Produto;
import br.com.precocerto.precocertoapp.services.FinalCompraService;
import br.com.precocerto.precocertoapp.services.ListaDeComprasService;
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

        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.25.50:8080/")
                .addConverterFactory(JacksonConverterFactory.create())
                .client(client.build())
                .build();
    }

    public ProdutoService getProdutoService() {
        return retrofit.create(ProdutoService.class);
    }

    public FinalCompraService getFinalCompraService(){
        return retrofit.create(FinalCompraService.class);
    }

    public ListaDeComprasService pegaListaDeCompras(){
        return retrofit.create(ListaDeComprasService.class);
    }
}
