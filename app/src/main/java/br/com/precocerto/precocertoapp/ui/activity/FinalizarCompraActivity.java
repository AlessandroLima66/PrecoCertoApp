package br.com.precocerto.precocertoapp.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Date;
import java.util.List;

import br.com.precocerto.precocertoapp.R;
import br.com.precocerto.precocertoapp.dao.ProdutoDAO;
import br.com.precocerto.precocertoapp.dto.FinalCompra;
import br.com.precocerto.precocertoapp.model.ProdutoLista;
import br.com.precocerto.precocertoapp.retrofit.RetrofitInicializador;
import br.com.precocerto.precocertoapp.ui.adapter.ListaFinalizarCompraAdapter;
import br.com.precocerto.precocertoapp.util.MoedaUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FinalizarCompraActivity extends AppCompatActivity {
    private static final int PLACE_PICKER_REQUEST = 456;
    private static final int REQUEST_CHECK_SETTINGS = 613;

    private AlertDialog dialogFinalizar;
    private Button botao_lista;
    private Button botao_comparar;
    private TextView nome_mercado;
    private TextView endereco_mercado;
    private TextView compra_data;
    private TextView compra_total;
    private ListView compra_ListView;

    private Double valorTotalCompra = Double.valueOf(0);
    private FinalCompra finalCompra = new FinalCompra();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalizar_compra);

        botao_lista = findViewById(R.id.finalizar_compra_botao_lista);
        botao_lista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        botao_comparar = findViewById(R.id.finalizar_compra_botao_comparar);
        botao_comparar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentInformaCupom = new Intent(FinalizarCompraActivity.this, InformaCupomActivity.class);
                startActivity(intentInformaCupom);
            }
        });

        preparaView();
        VerificarGPSAtivo();
    }

    private void preparaView() {
        nome_mercado = findViewById(R.id.finalizar_compra_nome_mercado);
        endereco_mercado = findViewById(R.id.finalizar_compra_endereco_mercado);
        compra_data = findViewById(R.id.finalizar_compra_data);
        compra_total = findViewById(R.id.finalizar_compra_total);
    }

    private void carregaCupom() {
        ProdutoDAO dao = new ProdutoDAO(this);
        List<ProdutoLista> produtos = dao.buscaProdutos();
        dao.close();

        finalCompra.setListaDeCompra(produtos);

        compra_ListView = findViewById(R.id.finalizar_compra_ListView);
        compra_ListView.setAdapter(new ListaFinalizarCompraAdapter(this, produtos));
        somaTotalDaCompra(produtos);
    }

    private void somaTotalDaCompra(List<ProdutoLista> produtos) {
        for (int i = 0; i < produtos.size(); i++) {
            valorTotalCompra += produtos.get(i).getValorTotal();
        }
        compra_total.setText(MoedaUtil.formataParaExibicao(valorTotalCompra));
    }

    private void setHorarioAtual() {
        compra_data.setText(finalCompra.getDataHoraCompra());
    }

    private void setEnderecoMercado() {
        endereco_mercado.setText(finalCompra.getEnderecoMercado());
    }

    private void setNomeMercado() {
        nome_mercado.setText( finalCompra.getNomeMercado());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (data != null) {
                Place place = PlacePicker.getPlace(this, data);
                finalCompra.setNomeMercado(place.getName().toString());
                finalCompra.setEnderecoMercado(place.getAddress().toString());
                finalCompra.setDataHoraCompra(new Date());

                setNomeMercado();
                setEnderecoMercado();
                setHorarioAtual();
                carregaCupom();
                salvaListaDeCompras();
            } else {
                mostraAlertDialogEnderecoObrigatorio();
            }
        }

        if (requestCode == REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    localAtual();
                    break;
                case Activity.RESULT_CANCELED:
                    mostraAlertDialogEnderecoObrigatorio();
                    break;
            }
        }
    }

    private void mostraAlertDialogEnderecoObrigatorio() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setTitle("Atenção");
        dialogBuilder.setIcon(R.drawable.ic_finalizar_brack);
        dialogBuilder.setMessage("Para finalizar sua compra, precisamos identificar o mercado que você está.\n\nVocê pode nos ajudar? :)");

        dialogBuilder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                VerificarGPSAtivo();
            }
        });

        dialogBuilder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        dialogFinalizar = dialogBuilder.create();
        dialogFinalizar.show();
    }

    public void localAtual() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    private void VerificarGPSAtivo() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(new LocationRequest());
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                localAtual();
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(FinalizarCompraActivity.this, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException ignored) {
                    }
                }
            }
        });
    }

    private void salvaListaDeCompras() {
        Call<FinalCompra> call = new RetrofitInicializador().getFinalCompraService().PersisteCompra(finalCompra);

        call.enqueue(new Callback<FinalCompra>() {
            @Override
            public void onResponse(Call<FinalCompra> call, Response<FinalCompra> response) {
                //Logal Sucesso
            }

            @Override
            public void onFailure(Call<FinalCompra> call, Throwable t) {
                //Logar Erro
            }
        });
    }


}