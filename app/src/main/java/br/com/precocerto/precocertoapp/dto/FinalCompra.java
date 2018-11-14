package br.com.precocerto.precocertoapp.dto;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.precocerto.precocertoapp.model.Produto;
import br.com.precocerto.precocertoapp.model.ProdutoCompra;
import br.com.precocerto.precocertoapp.model.ProdutoLista;

public class FinalCompra {
    private String nomeMercado;
    private String enderecoMercado;
    private String dataHoraCompra;
    private List<ProdutoCompra> listaDeCompra = new ArrayList<>();

    public String getNomeMercado() {
        return nomeMercado;
    }

    public void setNomeMercado(String nomeMercado) {
        this.nomeMercado = nomeMercado;
    }

    public String getEnderecoMercado() {
        return enderecoMercado;
    }

    public void setEnderecoMercado(String enderecoMercado) {
        this.enderecoMercado = enderecoMercado;
    }

    public String getDataHoraCompra() {
        return dataHoraCompra;
    }

    public void setDataHoraCompra(Date dataHoraCompra) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
        this.dataHoraCompra = simpleDateFormat.format(dataHoraCompra);
    }

    public List<ProdutoCompra> getListaDeCompra() {
        return listaDeCompra;
    }

    public void setListaDeCompra(List<ProdutoLista> listaDeCompra) {
        for(int i = 0; i < listaDeCompra.size(); i++){
            ProdutoCompra p = new ProdutoCompra();
            p.setNome(listaDeCompra.get(i).getNome());
            p.setCodigoDeBarras(listaDeCompra.get(i).getCodigoDeBarras());
            p.setQuantidade(listaDeCompra.get(i).getQuantidade());
            p.setValorUnitario(listaDeCompra.get(i).getValorUnitario());
            p.setValorTotal(listaDeCompra.get(i).getValorTotal());
            this.listaDeCompra.add(p);
        }
    }
}
