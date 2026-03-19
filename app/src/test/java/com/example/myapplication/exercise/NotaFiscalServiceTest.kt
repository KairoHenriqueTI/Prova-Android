package com.example.myapplication.exercise

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test

class NotaFiscalServiceTest {

    private lateinit var service: NotaFiscalService
    private lateinit var notaFiscal1: NotaFiscal
    private lateinit var notaFiscal2: NotaFiscal
    private lateinit var notaFiscal3: NotaFiscal

    @Before
    fun setup() {
        service = NotaFiscalService()
        notaFiscal1 = NotaFiscal(1L, "Cliente A", 100.0)
        notaFiscal2 = NotaFiscal(2L, "Cliente B", 250.50)
        notaFiscal3 = NotaFiscal(3L, "Cliente C", 75.75)
    }

    // ============= TESTES DE FORMATAÇÃO DE IDS =============

    @Test
    fun testFormatarIdsEmptyList() {
        val result = service.formatarIds(emptyList())
        assertEquals("", result)
    }

    @Test
    fun testFormatarIdsSingleItem() {
        val result = service.formatarIds(listOf(notaFiscal1))
        assertEquals("1", result)
    }

    @Test
    fun testFormatarIdsTwoItems() {
        val result = service.formatarIds(listOf(notaFiscal1, notaFiscal2))
        assertEquals("1 e 2", result)
    }

    @Test
    fun testFormatarIdsThreeItems() {
        val result = service.formatarIds(listOf(notaFiscal1, notaFiscal2, notaFiscal3))
        assertEquals("1, 2 e 3", result)
    }

    @Test
    fun testFormatarIdsFourItems() {
        val nota4 = NotaFiscal(4L, "Cliente D", 150.0)
        val result = service.formatarIds(listOf(notaFiscal1, notaFiscal2, notaFiscal3, nota4))
        assertEquals("1, 2, 3 e 4", result)
    }

    @Test
    fun testFormatarIdsPreserveOrder() {
        val result = service.formatarIds(listOf(notaFiscal3, notaFiscal1, notaFiscal2))
        assertEquals("3, 1 e 2", result)
    }

    // ============= TESTES DE FILTRO POR VALOR MÍNIMO =============

    @Test
    fun testFiltrarPorValorMinimoEmpty() {
        val result = service.filtrarPorValorMinimo(emptyList(), 100.0)
        assertEquals(0, result.size)
    }

    @Test
    fun testFiltrarPorValorMinimoZero() {
        val result = service.filtrarPorValorMinimo(listOf(notaFiscal1, notaFiscal2, notaFiscal3), 0.0)
        assertEquals(3, result.size)
    }

    @Test
    fun testFiltrarPorValorMinimoExato() {
        val result = service.filtrarPorValorMinimo(listOf(notaFiscal1, notaFiscal2, notaFiscal3), 100.0)
        assertEquals(2, result.size)
        assertTrue(result.contains(notaFiscal1))
        assertTrue(result.contains(notaFiscal2))
    }

    @Test
    fun testFiltrarPorValorMinimoAcima() {
        val result = service.filtrarPorValorMinimo(listOf(notaFiscal1, notaFiscal2, notaFiscal3), 200.0)
        assertEquals(1, result.size)
        assertTrue(result.contains(notaFiscal2))
    }

    @Test
    fun testFiltrarPorValorMinimoMuitoAlto() {
        val result = service.filtrarPorValorMinimo(listOf(notaFiscal1, notaFiscal2, notaFiscal3), 500.0)
        assertEquals(0, result.size)
    }

    @Test
    fun testFiltrarPorValorMinimoNegativo() {
        val result = service.filtrarPorValorMinimo(listOf(notaFiscal1, notaFiscal2, notaFiscal3), -50.0)
        assertEquals(3, result.size)
    }

    @Test
    fun testFiltrarPorValorMinimoDecimal() {
        val result = service.filtrarPorValorMinimo(listOf(notaFiscal1, notaFiscal2, notaFiscal3), 75.75)
        assertEquals(2, result.size)
        assertTrue(result.contains(notaFiscal2))
        assertTrue(result.contains(notaFiscal3))
    }

    // ============= TESTES DE FORMATAÇÃO NOME E VALOR =============

    @Test
    fun testFormatarNomeValorEmpty() {
        val result = service.formatarNomeValor(emptyList())
        assertEquals("", result)
    }

    @Test
    fun testFormatarNomeValorSingleItem() {
        val result = service.formatarNomeValor(listOf(notaFiscal1))
        assertEquals("Cliente A - R$ 100,00", result)
    }

    @Test
    fun testFormatarNomeValorTwoItems() {
        val result = service.formatarNomeValor(listOf(notaFiscal1, notaFiscal2))
        assertEquals("Cliente A - R$ 100,00 e Cliente B - R$ 250,50", result)
    }

    @Test
    fun testFormatarNomeValorThreeItems() {
        val result = service.formatarNomeValor(listOf(notaFiscal1, notaFiscal2, notaFiscal3))
        assertEquals("Cliente A - R$ 100,00, Cliente B - R$ 250,50 e Cliente C - R$ 75,75", result)
    }

    @Test
    fun testFormatarNomeValorFormatoCurrencia() {
        val nota = NotaFiscal(1L, "Test", 1234.56)
        val result = service.formatarNomeValor(listOf(nota))
        assertTrue(result.contains("R$"))
        assertTrue(result.contains("1.234,56"))
    }

    @Test
    fun testFormatarNomeValorPreserveOrder() {
        val result = service.formatarNomeValor(listOf(notaFiscal3, notaFiscal1, notaFiscal2))
        assertEquals("Cliente C - R$ 75,75, Cliente A - R$ 100,00 e Cliente B - R$ 250,50", result)
    }

    @Test
    fun testFormatarNomeValorLocaleCorreto() {
        // Verificar que está usando locale pt-BR (vírgula como separador decimal)
        val result = service.formatarNomeValor(listOf(notaFiscal2))
        assertTrue(result.contains(",50"))  // Vírgula no lugar do ponto
    }

    // ============= TESTES DE INTEGRAÇÃO =============

    @Test
    fun testIntegrationFilterAndFormat() {
        val notas = listOf(notaFiscal1, notaFiscal2, notaFiscal3)
        
        // Filtrar por valor mínimo 100
        val filtered = service.filtrarPorValorMinimo(notas, 100.0)
        
        // Formatar nomes e valores
        val formatted = service.formatarNomeValor(filtered)
        
        assertTrue(formatted.contains("Cliente A"))
        assertTrue(formatted.contains("Cliente B"))
        assertTrue(!formatted.contains("Cliente C"))
    }

    @Test
    fun testIntegrationFormatIdsAndValues() {
        val notas = listOf(notaFiscal1, notaFiscal2, notaFiscal3)
        
        val ids = service.formatarIds(notas)
        val nomes = service.formatarNomeValor(notas)
        
        assertTrue(ids.contains("1"))
        assertTrue(ids.contains("2"))
        assertTrue(ids.contains("3"))
        
        assertTrue(nomes.contains("Cliente A"))
        assertTrue(nomes.contains("Cliente B"))
        assertTrue(nomes.contains("Cliente C"))
    }

    @Test
    fun testComplexScenario() {
        val notas = listOf(
            NotaFiscal(1L, "Restaurante A", 150.00),
            NotaFiscal(2L, "Restaurante B", 320.50),
            NotaFiscal(3L, "Restaurante C", 80.25),
            NotaFiscal(4L, "Restaurante D", 200.00)
        )
        
        // Filtrar por valor mínimo 150
        val filtered = service.filtrarPorValorMinimo(notas, 150.0)
        assertEquals(3, filtered.size)
        
        // Formatar IDs
        val ids = service.formatarIds(filtered)
        assertEquals("1, 2 e 4", ids)
        
        // Formatar nomes e valores
        val nomes = service.formatarNomeValor(filtered)
        assertTrue(nomes.contains("Restaurante A"))
        assertTrue(nomes.contains("Restaurante B"))
        assertTrue(nomes.contains("Restaurante D"))
        assertTrue(!nomes.contains("Restaurante C"))
    }

    @Test
    fun testEdgeCaseZeroValue() {
        val notaZero = NotaFiscal(10L, "Cliente Zero", 0.0)
        val result = service.formatarNomeValor(listOf(notaZero))
        assertTrue(result.contains("R$"))
        assertTrue(result.contains("0,00"))
    }

    @Test
    fun testEdgeCaseLargeValue() {
        val notaGrande = NotaFiscal(999L, "Cliente Grande", 999999.99)
        val result = service.formatarNomeValor(listOf(notaGrande))
        assertTrue(result.contains("R$"))
        assertTrue(result.contains("999.999,99"))
    }

    @Test
    fun testEdgeCaseSpecialCharactersInName() {
        val notaEspecial = NotaFiscal(1L, "Cliente & Cia.", 100.0)
        val result = service.formatarNomeValor(listOf(notaEspecial))
        assertTrue(result.contains("Cliente & Cia."))
    }

    @Test
    fun testEdgeCaseUnicodeInName() {
        val notaUnicode = NotaFiscal(1L, "Açoguería São Paulo", 100.0)
        val result = service.formatarNomeValor(listOf(notaUnicode))
        assertTrue(result.contains("Açoguería São Paulo"))
    }

    @Test
    fun testLargeListFormatting() {
        val notas = (1..100).map { i ->
            NotaFiscal(i.toLong(), "Cliente $i", (i * 10.5))
        }
        
        val ids = service.formatarIds(notas)
        assertTrue(ids.contains("100"))  // Último ID
        
        val filtered = service.filtrarPorValorMinimo(notas, 500.0)
        assertTrue(filtered.size > 0)
    }

    @Test
    fun testNomeValorConsistencyAfterFilter() {
        val original = listOf(notaFiscal1, notaFiscal2, notaFiscal3)
        val filtered = service.filtrarPorValorMinimo(original, 100.0)
        
        val formatado = service.formatarNomeValor(filtered)
        
        // Os valores formatados devem corresponder aos objetos filtrados
        assertTrue(formatado.contains("100,00"))
        assertTrue(formatado.contains("250,50"))
        assertTrue(!formatado.contains("75,75"))
    }

    @Test
    fun testOrderPreservationInFilter() {
        val notas = listOf(notaFiscal3, notaFiscal1, notaFiscal2)
        val filtered = service.filtrarPorValorMinimo(notas, 75.0)
        
        // Deve manter a ordem: 3, 1, 2
        assertEquals(notaFiscal3, filtered[0])
        assertEquals(notaFiscal1, filtered[1])
        assertEquals(notaFiscal2, filtered[2])
    }
}
