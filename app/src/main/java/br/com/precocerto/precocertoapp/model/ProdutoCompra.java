package br.com.precocerto.precocertoapp.model;

public class ProdutoCompra extends Produto{
    private Integer quantidade;
    private Double valorUnitario;
    private Double valorTotal;

    public ProdutoCompra(){
        super();
    }

    public ProdutoCompra(Integer quantidade, Double valorUnitario, Double valorTotal) {
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario;
        this.valorTotal = valorTotal;
    }

    public ProdutoCompra(String nome, String codigoDeBarras, Integer quantidade, Double valorUnitario, Double valorTotal) {
        super(nome, codigoDeBarras);
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario;
        this.valorTotal = valorTotal;
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
}
