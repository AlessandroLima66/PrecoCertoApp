package br.com.precocerto.precocertoapp.dto;

import java.util.List;

import br.com.precocerto.precocertoapp.model.ProdutoCompra;

public class ListaDeCompra {
    private String nomeMercado;
    private String enderecoMercado;
    private List<ProdutoCompra> listaDeCompra;

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

    public List<ProdutoCompra> getListaDeCompra() {
        return listaDeCompra;
    }

    public void setListaDeCompra(List<ProdutoCompra> listaDeCompra) {
        this.listaDeCompra = listaDeCompra;
    }
}
