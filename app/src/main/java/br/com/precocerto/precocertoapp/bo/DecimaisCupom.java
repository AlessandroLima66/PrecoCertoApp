package br.com.precocerto.precocertoapp.bo;

public class DecimaisCupom {
    private Double valorUnitario;
    private Double valorTotal;

    public DecimaisCupom() {
    }

    public DecimaisCupom(Double valorUnitario, Double valorTotal) {
        this.valorUnitario = valorUnitario;
        this.valorTotal = valorTotal;
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

    @Override
    public String toString() {
        return "valorUnitario= " + valorUnitario +
                " valorTotal= " + valorTotal;
    }
}
