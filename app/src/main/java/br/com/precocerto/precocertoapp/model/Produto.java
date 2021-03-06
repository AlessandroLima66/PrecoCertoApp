package br.com.precocerto.precocertoapp.model;

import java.io.Serializable;

public class Produto implements Serializable {
    private String nomeProduto;
    private String codigoDeBarras;


    public Produto(){
    }

    public Produto(String nomeProduto, String codigoDeBarras) {
        this.nomeProduto = nomeProduto;
        this.codigoDeBarras = codigoDeBarras;
    }

    public String getNome() {
        return nomeProduto;
    }

    public void setNome(String nome) {
        this.nomeProduto = nome;
    }

    public String getCodigoDeBarras() {
        return codigoDeBarras;
    }

    public void setCodigoDeBarras(String codigoDeBarras) {
        this.codigoDeBarras = codigoDeBarras;
    }
}
