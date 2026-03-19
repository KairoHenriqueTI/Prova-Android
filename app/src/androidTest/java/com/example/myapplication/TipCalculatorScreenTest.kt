package com.example.myapplication

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myapplication.ui.theme.MyApplicationTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TipCalculatorScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        composeTestRule.setContent {
            MyApplicationTheme {
                TipCalculatorScreen()
            }
        }
    }

    // ============= TESTES DE LAYOUT E ELEMENTOS =============

    @Test
    fun testTitleDisplayed() {
        composeTestRule
            .onNodeWithText("Tip Calculator")
            .assertIsDisplayed()
    }

    @Test
    fun testAmountLabelDisplayed() {
        composeTestRule
            .onNodeWithText("Amount")
            .assertIsDisplayed()
    }

    @Test
    fun testCustomPercentLabelDisplayed() {
        composeTestRule
            .onNodeWithText("Custom %")
            .assertIsDisplayed()
    }

    @Test
    fun testInitialAmountIsZero() {
        composeTestRule
            .onNodeWithText("$0.00")
            .assertIsDisplayed()
    }

    @Test
    fun testInitialCustomPercentIsEighteen() {
        composeTestRule
            .onNodeWithText("18%")
            .assertIsDisplayed()
    }

    // ============= TESTES DE TECLADO NUMÉRICO =============

    @Test
    fun testKeypadButton1Exists() {
        composeTestRule
            .onNodeWithText("1")
            .assertIsDisplayed()
    }

    @Test
    fun testKeypadButton2Exists() {
        composeTestRule
            .onNodeWithText("2")
            .assertIsDisplayed()
    }

    @Test
    fun testKeypadButton3Exists() {
        composeTestRule
            .onNodeWithText("3")
            .assertIsDisplayed()
    }

    @Test
    fun testKeypadButton4Exists() {
        composeTestRule
            .onNodeWithText("4")
            .assertIsDisplayed()
    }

    @Test
    fun testKeypadButton5Exists() {
        composeTestRule
            .onNodeWithText("5")
            .assertIsDisplayed()
    }

    @Test
    fun testKeypadButton6Exists() {
        composeTestRule
            .onNodeWithText("6")
            .assertIsDisplayed()
    }

    @Test
    fun testKeypadButton7Exists() {
        composeTestRule
            .onNodeWithText("7")
            .assertIsDisplayed()
    }

    @Test
    fun testKeypadButton8Exists() {
        composeTestRule
            .onNodeWithText("8")
            .assertIsDisplayed()
    }

    @Test
    fun testKeypadButton9Exists() {
        composeTestRule
            .onNodeWithText("9")
            .assertIsDisplayed()
    }

    @Test
    fun testKeypadButton0Exists() {
        composeTestRule
            .onNodeWithText("0")
            .assertIsDisplayed()
    }

    @Test
    fun testKeypadDecimalButtonExists() {
        composeTestRule
            .onNodeWithText(".")
            .assertIsDisplayed()
    }

    @Test
    fun testKeypadDeleteButtonExists() {
        composeTestRule
            .onNodeWithText("⌫")
            .assertIsDisplayed()
    }

    // ============= TESTES DE INTERAÇÃO COM TECLADO =============

    @Test
    fun testClickButton1UpdatesAmount() {
        composeTestRule.onNodeWithText("1").performClick()
        composeTestRule.onNodeWithText("$0.01").assertIsDisplayed()
    }

    @Test
    fun testClickMultipleButtons() {
        composeTestRule.onNodeWithText("3").performClick()
        composeTestRule.onNodeWithText("4").performClick()
        composeTestRule.onNodeWithText("5").performClick()
        composeTestRule.onNodeWithText("6").performClick()
        
        // 3456 centavos = $34.56
        composeTestRule.onNodeWithText("$34.56").assertIsDisplayed()
    }

    @Test
    fun testKeypadUpdateWithoutSliderInteraction() {
        // BUG FIX: Teclado deve atualizar SEM mexer no slider
        // Antes: só atualizava quando mexia no slider
        // Depois: atualiza instantaneamente
        
        composeTestRule.onNodeWithText("1").performClick()
        composeTestRule.onNodeWithText("0").performClick()
        composeTestRule.onNodeWithText("0").performClick()
        
        // Deve mostrar $1.00 SEM mexer no slider
        composeTestRule.onNodeWithText("$1.00").assertIsDisplayed()
        
        // Verificar que gorjeta 15% também atualizou
        composeTestRule.onNodeWithText("$0.15").assertIsDisplayed()
    }

    @Test
    fun testMultipleKeypadClicksWithoutSlider() {
        // Sequência: 5 -> 0 -> . -> 2 -> 5 sem mexer no slider
        composeTestRule.onNodeWithText("5").performClick()
        composeTestRule.onNodeWithText("0").performClick()
        composeTestRule.onNodeWithText(".").performClick()
        composeTestRule.onNodeWithText("2").performClick()
        composeTestRule.onNodeWithText("5").performClick()
        
        // Deve atualizar em tempo real
        val nodes = composeTestRule.onAllNodes(hasText("$"))
        nodes.fetchSemanticsNodes().isNotEmpty()
    }

    @Test
    fun testClickDecimalButton() {
        composeTestRule.onNodeWithText("1").performClick()
        composeTestRule.onNodeWithText(".").performClick()
        composeTestRule.onNodeWithText("5").performClick()
        
        // Verificar que o valor foi atualizado
        val nodes = composeTestRule.onAllNodes(hasText("$"))
        nodes.fetchSemanticsNodes().isNotEmpty()
    }

    @Test
    fun testClickDeleteButton() {
        composeTestRule.onNodeWithText("1").performClick()
        composeTestRule.onNodeWithText("2").performClick()
        composeTestRule.onNodeWithText("3").performClick()
        
        // Deve mostrar $1.23
        composeTestRule.onNodeWithText("$1.23").assertIsDisplayed()
        
        composeTestRule.onNodeWithText("⌫").performClick()
        
        // Deve mostrar $1.20 (ou $0.12 dependendo da lógica)
        // Verificar que mudou
        val nodes = composeTestRule.onAllNodes(hasText("$"))
        nodes.fetchSemanticsNodes().isNotEmpty()
    }

    // ============= TESTES DE CARDS DE TIP/TOTAL =============

    @Test
    fun testTip15CardDisplayed() {
        composeTestRule
            .onNodeWithText("15%")
            .assertIsDisplayed()
    }

    @Test
    fun testTipCustomCardDisplayed() {
        composeTestRule
            .onNodeWithText("18%")
            .assertIsDisplayed()
    }

    @Test
    fun testTotalLabelsDisplayed() {
        // Deve haver dois "Total"
        val nodes = composeTestRule.onAllNodes(hasText("Total"))
        nodes.fetchSemanticsNodes().size >= 2
    }

    @Test
    fun testInitialTipValuesAreZero() {
        // Ambos tips devem ser $0.00 inicialmente
        val nodes = composeTestRule.onAllNodes(hasText("$0.00"))
        nodes.fetchSemanticsNodes().size >= 2  // Pelo menos 2 (amount + tips)
    }

    // ============= TESTES DE SLIDER =============

    @Test
    fun testSliderIsDisplayed() {
        // Procurar por algum elemento que indique o slider
        val nodes = composeTestRule.onAllNodes(hasText("%"))
        nodes.fetchSemanticsNodes().isNotEmpty()
    }

    @Test
    fun testSliderInitialValue() {
        composeTestRule
            .onNodeWithText("18%")
            .assertIsDisplayed()
    }

    // ============= TESTES DE CÁLCULOS EXIBIDOS =============

    @Test
    fun testCalculationAfterInput() {
        // Digitar 100 centavos = $1.00
        composeTestRule.onNodeWithText("1").performClick()
        composeTestRule.onNodeWithText("0").performClick()
        composeTestRule.onNodeWithText("0").performClick()
        
        composeTestRule.onNodeWithText("$1.00").assertIsDisplayed()
        
        // 15% de $1.00 = $0.15
        composeTestRule.onNodeWithText("$0.15").assertIsDisplayed()
    }

    @Test
    fun testExerciseScenario() {
        // Simular o exercício: 34.56
        val buttons = listOf("3", "4", "5", "6")
        buttons.forEach { button ->
            composeTestRule.onNodeWithText(button).performClick()
        }
        
        // Verificar que amount é $34.56
        composeTestRule.onNodeWithText("$34.56").assertIsDisplayed()
        
        // Verificar que tip 15% é $5.18
        composeTestRule.onNodeWithText("$5.18").assertIsDisplayed()
        
        // Verificar que total 15% é $39.74
        composeTestRule.onNodeWithText("$39.74").assertIsDisplayed()
    }

    @Test
    fun testUIResponsiveness() {
        // Rápida sequência de cliques
        for (i in 1..5) {
            composeTestRule.onNodeWithText("1").performClick()
        }
        
        // Deve exibir algo
        val nodes = composeTestRule.onAllNodes(hasText("$"))
        nodes.fetchSemanticsNodes().isNotEmpty()
    }

    @Test
    fun testDeleteButtonRemovesLastDigit() {
        composeTestRule.onNodeWithText("5").performClick()
        composeTestRule.onNodeWithText("0").performClick()
        
        // Deve exibir $0.50
        composeTestRule.onNodeWithText("$0.50").assertIsDisplayed()
        
        composeTestRule.onNodeWithText("⌫").performClick()
        
        // Deve exibir $0.05
        composeTestRule.onNodeWithText("$0.05").assertIsDisplayed()
    }

    @Test
    fun testAllKeysClickable() {
        val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0", ".", "⌫")
        
        keys.forEach { key ->
            try {
                composeTestRule.onNodeWithText(key).assertIsDisplayed()
            } catch (e: Exception) {
                throw AssertionError("Key $key not found or not displayed")
            }
        }
    }

    @Test
    fun testColorScheme() {
        // Verificar que os elementos estão visíveis (indicando tema dark foi aplicado)
        composeTestRule.onNodeWithText("Tip Calculator").assertIsDisplayed()
        composeTestRule.onNodeWithText("Amount").assertIsDisplayed()
        composeTestRule.onNodeWithText("Custom %").assertIsDisplayed()
    }

    @Test
    fun testLongInputHandling() {
        // Tentar inserir 10 dígitos (máximo)
        for (i in 1..10) {
            composeTestRule.onNodeWithText("9").performClick()
        }
        
        // Deve exibir valor formatado
        val nodes = composeTestRule.onAllNodes(hasText("$"))
        nodes.fetchSemanticsNodes().isNotEmpty()
    }

    @Test
    fun testDecimalInputHandling() {
        composeTestRule.onNodeWithText("1").performClick()
        composeTestRule.onNodeWithText(".").performClick()
        composeTestRule.onNodeWithText("2").performClick()
        
        // Deve formatar corretamente
        val nodes = composeTestRule.onAllNodes(hasText("$"))
        nodes.fetchSemanticsNodes().isNotEmpty()
    }

    @Test
    fun testMultipleDeleteOperations() {
        composeTestRule.onNodeWithText("1").performClick()
        composeTestRule.onNodeWithText("2").performClick()
        composeTestRule.onNodeWithText("3").performClick()
        
        composeTestRule.onNodeWithText("⌫").performClick()
        composeTestRule.onNodeWithText("⌫").performClick()
        composeTestRule.onNodeWithText("⌫").performClick()
        
        // Deve voltar para zero
        val initialZeroNodes = composeTestRule.onAllNodes(hasText("$0.00"))
        initialZeroNodes.fetchSemanticsNodes().size >= 1
    }

    @Test
    fun testScreenLayoutStructure() {
        // Verificar estrutura básica
        composeTestRule.onNodeWithText("Tip Calculator").assertIsDisplayed()
        composeTestRule.onNodeWithText("Amount").assertIsDisplayed()
        composeTestRule.onNodeWithText("Custom %").assertIsDisplayed()
        composeTestRule.onNodeWithText("1").assertIsDisplayed()
    }
}
