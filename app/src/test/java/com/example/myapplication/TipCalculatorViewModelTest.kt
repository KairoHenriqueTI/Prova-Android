package com.example.myapplication

import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TipCalculatorViewModelTest {

    private lateinit var viewModel: TipCalculatorViewModel

    @Before
    fun setup() {
        viewModel = TipCalculatorViewModel()
    }

    // ============= TESTES DE ADIÇÃO DE DÍGITOS =============

    @Test
    fun testAddDigitSingle() = runTest {
        viewModel.addDigit("5")
        assertEquals("5", viewModel.amountInput.first())
    }

    @Test
    fun testAddMultipleDigits() = runTest {
        viewModel.addDigit("1")
        viewModel.addDigit("2")
        viewModel.addDigit("3")
        assertEquals("123", viewModel.amountInput.first())
    }

    @Test
    fun testAddDigitWithDecimal() = runTest {
        viewModel.addDigit("1")
        viewModel.addDigit("0")
        viewModel.addDigit("0")
        viewModel.addDigit(".")
        viewModel.addDigit("5")
        viewModel.addDigit("0")
        assertEquals("100.50", viewModel.amountInput.first())
    }

    @Test
    fun testPreventMultipleDecimalPoints() = runTest {
        viewModel.addDigit("1")
        viewModel.addDigit(".")
        viewModel.addDigit("5")
        viewModel.addDigit(".")  // Deve ser ignorado
        assertEquals("1.5", viewModel.amountInput.first())
    }

    @Test
    fun testPreventDecimalAtStart() = runTest {
        viewModel.addDigit(".")  // Deve ser ignorado
        assertEquals("", viewModel.amountInput.first())
    }

    @Test
    fun testPreventLeadingZero() = runTest {
        viewModel.addDigit("0")
        viewModel.addDigit("5")  // Deve substituir o 0
        assertEquals("5", viewModel.amountInput.first())
    }

    @Test
    fun testAllowZeroAfterDecimal() = runTest {
        viewModel.addDigit("1")
        viewModel.addDigit(".")
        viewModel.addDigit("0")
        assertEquals("1.0", viewModel.amountInput.first())
    }

    @Test
    fun testMaxInputLength() = runTest {
        // Tentar adicionar 11 dígitos (máximo é 10)
        for (i in 1..11) {
            viewModel.addDigit("1")
        }
        val input = viewModel.amountInput.first()
        assertEquals(10, input.length)
        assertEquals("1111111111", input)
    }

    @Test
    fun testRejectNegativeSign() = runTest {
        viewModel.addDigit("1")
        viewModel.addDigit("-")  // Deve ser ignorado
        assertEquals("1", viewModel.amountInput.first())
    }

    // ============= TESTES DE DELEÇÃO =============

    @Test
    fun testDeleteSingleDigit() = runTest {
        viewModel.addDigit("1")
        viewModel.addDigit("2")
        viewModel.addDigit("3")
        viewModel.deleteDigit()
        assertEquals("12", viewModel.amountInput.first())
    }

    @Test
    fun testDeleteAllDigits() = runTest {
        viewModel.addDigit("1")
        viewModel.addDigit("2")
        viewModel.deleteDigit()
        viewModel.deleteDigit()
        assertEquals("", viewModel.amountInput.first())
    }

    @Test
    fun testDeleteFromEmpty() = runTest {
        viewModel.deleteDigit()
        assertEquals("", viewModel.amountInput.first())
    }

    @Test
    fun testDeleteDecimalPoint() = runTest {
        viewModel.addDigit("1")
        viewModel.addDigit(".")
        viewModel.addDigit("5")
        viewModel.deleteDigit()
        assertEquals("1.", viewModel.amountInput.first())
    }

    // ============= TESTES DE PERCENTUAL PERSONALIZADO =============

    @Test
    fun testUpdateCustomTipPercentValid() = runTest {
        viewModel.updateCustomTipPercent(20f)
        assertEquals(20f, viewModel.customTipPercent.first())
    }

    @Test
    fun testUpdateCustomTipPercentAtMin() = runTest {
        viewModel.updateCustomTipPercent(0f)
        assertEquals(0f, viewModel.customTipPercent.first())
    }

    @Test
    fun testUpdateCustomTipPercentAtMax() = runTest {
        viewModel.updateCustomTipPercent(30f)
        assertEquals(30f, viewModel.customTipPercent.first())
    }

    @Test
    fun testRejectCustomTipPercentAboveMax() = runTest {
        viewModel.updateCustomTipPercent(31f)  // Deve manter o anterior
        assertEquals(18f, viewModel.customTipPercent.first())  // DEFAULT_TIP_PERCENT
    }

    @Test
    fun testRejectCustomTipPercentBelowMin() = runTest {
        viewModel.updateCustomTipPercent(-1f)  // Deve manter o anterior
        assertEquals(18f, viewModel.customTipPercent.first())  // DEFAULT_TIP_PERCENT
    }

    // ============= TESTES DE CÁLCULO DE VALOR =============

    @Test
    fun testGetAmountEmpty() {
        val amount = viewModel.getFormattedAmount()
        assertEquals("$0.00", amount)  // Formato padrão do Locale
    }

    @Test
    fun testGetAmountWithDot() {
        viewModel.addDigit(".")
        val amount = viewModel.getFormattedAmount()
        assertEquals("$0.00", amount)
    }

    @Test
    fun testGetAmountSimple() = runTest {
        // 100 centavos = R$ 1.00
        viewModel.addDigit("1")
        viewModel.addDigit("0")
        viewModel.addDigit("0")
        val amount = viewModel.getFormattedAmount()
        assertEquals("$1.00", amount)
    }

    @Test
    fun testGetAmountWithDecimal() = runTest {
        // 34.56 (conforme o exercício) = R$ 0.3456 centavos -> mas mantém o valor
        viewModel.addDigit("3")
        viewModel.addDigit("4")
        viewModel.addDigit(".")
        viewModel.addDigit("5")
        viewModel.addDigit("6")
        val amount = viewModel.getFormattedAmount()
        // 3456 centavos / 100 = 34.56
        assertEquals("$34.56", amount)
    }

    // ============= TESTES DE CÁLCULO DE GORJETA 15% =============

    @Test
    fun testGetFormattedTip15EmptyAmount() {
        val tip = viewModel.getFormattedTip15()
        assertEquals("$0.00", tip)
    }

    @Test
    fun testGetFormattedTip15WithAmount() = runTest {
        // 3456 centavos = 34.56
        // 15% de 34.56 = 5.184
        viewModel.addDigit("3")
        viewModel.addDigit("4")
        viewModel.addDigit("5")
        viewModel.addDigit("6")
        val tip = viewModel.getFormattedTip15()
        assertEquals("$5.18", tip)  // Arredondado para 2 casas decimais
    }

    @Test
    fun testGetFormattedTip15ZeroAmount() {
        val tip = viewModel.getFormattedTip15()
        assertEquals("$0.00", tip)
    }

    // ============= TESTES DE CÁLCULO DE GORJETA PERSONALIZADA =============

    @Test
    fun testGetFormattedCustomTipDefault() = runTest {
        // 3456 centavos = 34.56
        // 18% de 34.56 = 6.2208
        viewModel.addDigit("3")
        viewModel.addDigit("4")
        viewModel.addDigit("5")
        viewModel.addDigit("6")
        val tip = viewModel.getFormattedCustomTip()
        assertEquals("$6.22", tip)  // Arredondado
    }

    @Test
    fun testGetFormattedCustomTipAfterSliderChange() = runTest {
        viewModel.addDigit("3")
        viewModel.addDigit("4")
        viewModel.addDigit("5")
        viewModel.addDigit("6")
        viewModel.updateCustomTipPercent(20f)
        val tip = viewModel.getFormattedCustomTip()
        // 20% de 34.56 = 6.912
        assertEquals("$6.91", tip)  // Arredondado
    }

    @Test
    fun testGetFormattedCustomTipZeroPercent() = runTest {
        viewModel.addDigit("3")
        viewModel.addDigit("4")
        viewModel.addDigit("5")
        viewModel.addDigit("6")
        viewModel.updateCustomTipPercent(0f)
        val tip = viewModel.getFormattedCustomTip()
        assertEquals("$0.00", tip)
    }

    // ============= TESTES DE CÁLCULO DE TOTAL 15% =============

    @Test
    fun testGetFormattedTotal15Empty() {
        val total = viewModel.getFormattedTotal15()
        assertEquals("$0.00", total)
    }

    @Test
    fun testGetFormattedTotal15WithAmount() = runTest {
        // 3456 centavos = 34.56
        // Total com 15% = 34.56 + 5.184 = 39.744
        viewModel.addDigit("3")
        viewModel.addDigit("4")
        viewModel.addDigit("5")
        viewModel.addDigit("6")
        val total = viewModel.getFormattedTotal15()
        assertEquals("$39.74", total)  // Conforme exercício: $39.74
    }

    // ============= TESTES DE CÁLCULO DE TOTAL PERSONALIZADO =============

    @Test
    fun testGetFormattedTotalCustomDefault() = runTest {
        // 3456 centavos = 34.56
        // Total com 18% = 34.56 + 6.2208 = 40.7808
        viewModel.addDigit("3")
        viewModel.addDigit("4")
        viewModel.addDigit("5")
        viewModel.addDigit("6")
        val total = viewModel.getFormattedTotalCustom()
        assertEquals("$40.78", total)  // Conforme exercício: $41.47? Vamos verificar
    }

    @Test
    fun testGetFormattedTotalCustomAfterSliderChange() = runTest {
        // 3456 centavos = 34.56
        // Total com 20% = 34.56 + 6.912 = 41.472
        viewModel.addDigit("3")
        viewModel.addDigit("4")
        viewModel.addDigit("5")
        viewModel.addDigit("6")
        viewModel.updateCustomTipPercent(20f)
        val total = viewModel.getFormattedTotalCustom()
        assertEquals("$41.47", total)  // Conforme exercício
    }

    // ============= TESTES DE INTEGRAÇÃO =============

    @Test
    fun testCompleteFlowExerciseExample() = runTest {
        // Simular: digitar 3456 (34.56), mudar slider para 20%
        viewModel.addDigit("3")
        viewModel.addDigit("4")
        viewModel.addDigit("5")
        viewModel.addDigit("6")
        
        assertEquals("$34.56", viewModel.getFormattedAmount())
        assertEquals("$5.18", viewModel.getFormattedTip15())
        assertEquals("$39.74", viewModel.getFormattedTotal15())
        
        viewModel.updateCustomTipPercent(20f)
        assertEquals(20f, viewModel.customTipPercent.first())
        assertEquals("$6.91", viewModel.getFormattedCustomTip())
        assertEquals("$41.47", viewModel.getFormattedTotalCustom())
    }

    @Test
    fun testAddDeleteAndRecalculate() = runTest {
        viewModel.addDigit("1")
        viewModel.addDigit("0")
        viewModel.addDigit("0")
        viewModel.deleteDigit()
        viewModel.deleteDigit()
        viewModel.addDigit("5")
        viewModel.addDigit("0")
        
        assertEquals("15", viewModel.amountInput.first())
        assertEquals("$0.15", viewModel.getFormattedAmount())
    }

    @Test
    fun testSliderMovementDoesNotAffectAmount() = runTest {
        viewModel.addDigit("3")
        viewModel.addDigit("4")
        viewModel.addDigit("5")
        viewModel.addDigit("6")
        
        val amountBefore = viewModel.getFormattedAmount()
        
        viewModel.updateCustomTipPercent(10f)
        viewModel.updateCustomTipPercent(25f)
        viewModel.updateCustomTipPercent(5f)
        
        val amountAfter = viewModel.getFormattedAmount()
        assertEquals(amountBefore, amountAfter)
    }

    @Test
    fun testMultipleInputsSequence() = runTest {
        // Primeiro valor
        viewModel.addDigit("1")
        viewModel.addDigit("0")
        viewModel.addDigit("0")
        
        var amount = viewModel.getFormattedAmount()
        assertEquals("$1.00", amount)
        
        // Deletar e inserir novo
        viewModel.deleteDigit()
        viewModel.deleteDigit()
        viewModel.deleteDigit()
        viewModel.addDigit("2")
        viewModel.addDigit("5")
        viewModel.addDigit("0")
        
        amount = viewModel.getFormattedAmount()
        assertEquals("$2.50", amount)
    }

    @Test
    fun testFormattingConsistency() = runTest {
        viewModel.addDigit("5")
        val formatted = viewModel.getFormattedAmount()
        // 5 centavos = 0.05
        assertEquals("$0.05", formatted)
    }

    @Test
    fun testLargeValue() = runTest {
        // Máximo: 10 dígitos = 9999999999 centavos = 99999999.99
        for (i in 1..10) {
            viewModel.addDigit("9")
        }
        val amount = viewModel.getFormattedAmount()
        assertEquals("$99,999,999.99", amount)
    }

    @Test
    fun testZeroInputHandling() = runTest {
        viewModel.addDigit("0")
        assertEquals("$0.00", viewModel.getFormattedAmount())
    }

    @Test
    fun testDecimalEdgeCases() = runTest {
        // 0.99 = 99 centavos? Não, porque o input é em centavos
        // Se digitar "99", são 99 centavos = 0.99
        viewModel.addDigit("0")
        viewModel.addDigit(".")
        viewModel.addDigit("9")
        viewModel.addDigit("9")
        
        val amount = viewModel.getFormattedAmount()
        // 0.99 centavos = 0.0099
        assertEquals("$0.01", amount)  // Arredondado
    }
}
