package br.com.precocerto.precocertoapp.model;

import java.io.Serializable;

public class Produto implements Serializable {

    private Long id;
    private String nome;
    private String codigoDeBarras;
    private Integer quantidade;
    private Double valorUnitario;
    private Double valorTotal;
    private String caminhoFoto;

    public Produto(){

    }

    public Produto(String nome, String codigoDeBarras, Integer quantidade, Double valorUnitario, Double valorTotal, String caminhoFoto) {
        this.nome = nome;
        this.codigoDeBarras = codigoDeBarras;
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario;
        this.valorTotal = valorTotal;
        this.caminhoFoto = caminhoFoto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Double getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(Double valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public Double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
    }

}
