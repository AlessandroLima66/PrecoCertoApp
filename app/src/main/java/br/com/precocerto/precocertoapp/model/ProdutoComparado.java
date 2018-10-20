package br.com.precocerto.precocertoapp.model;

public class ProdutoComparado{
    private Produto produtoCompra;
    private Produto produtoCupom;
    private boolean divergencia;

    ProdutoComparado(){

    }

    public ProdutoComparado(Produto produtoCompra, Produto produtoCupom, boolean divergencia){
        this.produtoCompra= produtoCompra;
        this.produtoCupom = produtoCupom;
        this.divergencia = divergencia;
    }

    public Produto getProdutoCompra() {
        return produtoCompra;
    }

    public void setProdutoCompra(Produto produtoCompra) {
        this.produtoCompra = produtoCompra;
    }

    public Produto getProdutoCupom() {
        return produtoCupom;
    }

    public void setProdutoCupom(Produto produtoCupom) {
        this.produtoCupom = produtoCupom;
    }

    public boolean isDivergencia() {
        return divergencia;
    }

    public void setDivergencia(boolean divergencia) {
        this.divergencia = divergencia;
    }

}
