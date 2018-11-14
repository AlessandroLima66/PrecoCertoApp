package br.com.precocerto.precocertoapp.model;

public class ProdutoLista extends ProdutoCompra{
    private Long id;
    private String caminhoFoto;

    public ProdutoLista(){
        super();
    }

    public ProdutoLista(String nome, String codigoDeBarras, Long id, Integer quantidade, Double valorUnitario, Double valorTotal, String caminhoFoto) {
        super(nome, codigoDeBarras, quantidade, valorUnitario, valorTotal);
        this.caminhoFoto = caminhoFoto;
        this.id = id;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
    }
}
