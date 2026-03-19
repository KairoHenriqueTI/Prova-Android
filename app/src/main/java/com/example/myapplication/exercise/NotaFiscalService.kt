package com.example.myapplication.exercise

import java.util.*

class NotaFiscalService {

    fun formatarIds(notas: List<NotaFiscal>): String {
        if (notas.isEmpty()) return ""
        val ids = notas.map { it.id.toString() }
        return formatarListaComE(ids)
    }

    fun filtrarPorValorMinimo(notas: List<NotaFiscal>, valorMinimo: Double): List<NotaFiscal> {
        return notas.filter { it.valor >= valorMinimo }
    }

    fun formatarNomeValor(notas: List<NotaFiscal>): String {
        if (notas.isEmpty()) return ""
        val formatados = notas.map { String.format(Locale.forLanguageTag("pt-BR"), "%s - R$ %.2f", it.cliente, it.valor) }
        return formatarListaComE(formatados)
    }

    private fun formatarListaComE(itens: List<String>): String {
        return when (itens.size) {
            0 -> ""
            1 -> itens[0]
            else -> {
                val anteriorAoUltimo = itens.dropLast(1).joinToString(", ")
                "$anteriorAoUltimo e ${itens.last()}"
            }
        }
    }
}
