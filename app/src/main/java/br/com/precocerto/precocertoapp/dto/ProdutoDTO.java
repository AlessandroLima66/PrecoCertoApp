package br.com.precocerto.precocertoapp.dto;

import br.com.precocerto.precocertoapp.model.Produto;

public class ProdutoDTO {
    private String nomeProduto;
    private String codigoDeBarras;


    public ProdutoDTO(){
    }

    public ProdutoDTO(String nomeProduto, String codigoDeBarras) {
        this.nomeProduto = nomeProduto;
        this.codigoDeBarras = codigoDeBarras;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public String getCodigoDeBarras() {
        return codigoDeBarras;
    }

    public void setCodigoDeBarras(String codigoDeBarras) {
        this.codigoDeBarras = codigoDeBarras;
    }
}
