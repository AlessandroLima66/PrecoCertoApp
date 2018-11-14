package br.com.precocerto.precocertoapp.bo;

import br.com.precocerto.precocertoapp.model.Produto;
import br.com.precocerto.precocertoapp.model.ProdutoCompra;

public class ProdutoComparado {
    private ProdutoCompra produtoCompra;
    private ProdutoCompra produtoCupom;
    private boolean divergencia;

    public ProdutoComparado() {
    }

    public ProdutoComparado(ProdutoCompra produtoCompra, ProdutoCompra produtoCupom) {
        this.produtoCompra = produtoCompra;
        this.produtoCupom = produtoCupom;
    }

    public ProdutoCompra getProdutoCompra() {
        return produtoCompra;
    }

    public void setProdutoCompra(ProdutoCompra produtoCompra) {
        this.produtoCompra = produtoCompra;
    }

    public ProdutoCompra getProdutoCupom() {
        return produtoCupom;
    }

    public void setProdutoCupom(ProdutoCompra produtoCupom) {
        this.produtoCupom = produtoCupom;
    }

    public boolean isDivergencia() {
        return divergencia;
    }

    public void setDivergencia(boolean divergencia) {
        this.divergencia = divergencia;
    }

    public boolean temDivergencia() {
        if (produtoCompra.getValorTotal().compareTo(produtoCupom.getValorTotal()) != 0) {
            return true;
        }
        return false;
    }
}
