package com.example.myapplication.exercise;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class NotaFiscalServiceJava {

    public String formatarIds(List<NotaFiscalJava> notas) {
        if (notas == null || notas.isEmpty()) {
            return "";
        }
        List<String> ids = notas.stream()
                .map(n -> n.getId().toString())
                .collect(Collectors.toList());
        return formatarListaComE(ids);
    }

    public List<NotaFiscalJava> filtrarPorValorMinimo(List<NotaFiscalJava> notas, double valorMinimo) {
        return notas.stream()
                .filter(n -> n.getValor() >= valorMinimo)
                .collect(Collectors.toList());
    }

    public String formatarNomeValor(List<NotaFiscalJava> notas) {
        if (notas == null || notas.isEmpty()) {
            return "";
        }
        List<String> formatados = notas.stream()
                .map(n -> String.format(Locale.forLanguageTag("pt-BR"), "%s - R$ %.2f", n.getCliente(), n.getValor()))
                .collect(Collectors.toList());
        return formatarListaComE(formatados);
    }

    private String formatarListaComE(List<String> itens) {
        int size = itens.size();
        if (size == 0) return "";
        if (size == 1) return itens.get(0);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size - 1; i++) {
            sb.append(itens.get(i));
            if (i < size - 2) {
                sb.append(", ");
            }
        }
        sb.append(" e ").append(itens.get(size - 1));
        return sb.toString();
    }
}
