package br.com.precocerto.precocertoapp.model;

import java.io.Serializable;

public class Produto implements Serializable {
    private String nome;
    private String codigoDeBarras;


    public Produto(){
    }

    public Produto(String nome, String codigoDeBarras) {
        this.nome = nome;
        this.codigoDeBarras = codigoDeBarras;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCodigoDeBarras() {
        return codigoDeBarras;
    }

    public void setCodigoDeBarras(String codigoDeBarras) {
        this.codigoDeBarras = codigoDeBarras;
    }
}
