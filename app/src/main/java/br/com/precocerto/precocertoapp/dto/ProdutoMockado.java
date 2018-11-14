package br.com.precocerto.precocertoapp.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

import br.com.precocerto.precocertoapp.model.Produto;

@JsonDeserialize
public class ProdutoMockado {

    private List<Produto> listaProdutos;

    public List<Produto> getListaProdutos() {
        return listaProdutos;
    }

    public void setListaProdutos(List<Produto> listaProdutos) {
        this.listaProdutos = listaProdutos;
    }
}
