package com.example.myapplication.exercise;

import java.util.Objects;

public class NotaFiscalJava {
    private Long id;
    private String cliente;
    private Double valor;

    public NotaFiscalJava(Long id, String cliente, Double valor) {
        this.id = id;
        this.cliente = cliente;
        this.valor = valor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotaFiscalJava that = (NotaFiscalJava) o;
        return Objects.equals(id, that.id) && Objects.equals(cliente, that.cliente) && Objects.equals(valor, that.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cliente, valor);
    }

    @Override
    public String toString() {
        return "NotaFiscalJava{" +
                "id=" + id +
                ", cliente='" + cliente + '\'' +
                ", valor=" + valor +
                '}';
    }
}
