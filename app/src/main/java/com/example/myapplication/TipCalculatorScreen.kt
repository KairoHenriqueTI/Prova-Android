package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun TipCalculatorScreen(
    modifier: Modifier = Modifier,
    viewModel: TipCalculatorViewModel = viewModel()
) {
    val amountInput by viewModel.amountInput.collectAsState()
    val customTipPercent by viewModel.customTipPercent.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF2C2C2C))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Header
        Text(
            "Tip Calculator",
            fontSize = 24.sp,
            color = Color.White,
            modifier = Modifier.padding(16.dp)
        )

        // Amount Display
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF444444))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Amount", fontSize = 14.sp, color = Color.LightGray)
                Text(
                    text = run { amountInput; viewModel.getFormattedAmount() },  // ✅ Usa amountInput
                    fontSize = 28.sp,
                    color = Color.White,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        // Custom Tip Slider
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF444444))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Custom %", fontSize = 14.sp, color = Color.LightGray)
                Slider(
                    value = customTipPercent,
                    onValueChange = { viewModel.updateCustomTipPercent(it) },
                    valueRange = 0f..30f,
                    steps = 29,  // ✅ Força valores inteiros (0, 1, 2... 30)
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                Text(
                    String.format("%.0f", customTipPercent) + "%",
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
        }

        // Tips and Totals Display
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TipCard(
                title = "Tip",
                percentage = "15%",
                tipAmount = run { amountInput; viewModel.getFormattedTip15() },  // ✅
                modifier = Modifier.weight(1f)
            )
            TipCard(
                title = "Tip",
                percentage = String.format("%.0f", customTipPercent) + "%",
                tipAmount = run { amountInput; viewModel.getFormattedCustomTip() },  // ✅
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TipCard(
                title = "Total",
                percentage = "15%",
                tipAmount = run { amountInput; viewModel.getFormattedTotal15() },  // ✅
                modifier = Modifier.weight(1f)
            )
            TipCard(
                title = "Total",
                percentage = String.format("%.0f", customTipPercent) + "%",
                tipAmount = run { amountInput; viewModel.getFormattedTotalCustom() },  // ✅
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Numeric Keypad
        NumericKeypad(viewModel)
    }
}

@Composable
fun TipCard(
    title: String,
    percentage: String,
    tipAmount: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(100.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF444444))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(title, fontSize = 14.sp, color = Color.LightGray)
                Text(percentage, fontSize = 14.sp, color = Color.LightGray)
            }
            Text(
                tipAmount,
                fontSize = 22.sp,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun NumericKeypad(viewModel: TipCalculatorViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val buttons = listOf(
            listOf("1", "2", "3", "⌫"),
            listOf("4", "5", "6", "."),
            listOf("7", "8", "9", ""),
            listOf("", "0", "", "")
        )

        for (row in buttons) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (button in row) {
                    if (button.isEmpty()) {
                        Spacer(modifier = Modifier.weight(1f))
                    } else if (button == "⌫") {
                        KeypadButton(
                            text = button,
                            onClick = { viewModel.deleteDigit() },
                            modifier = Modifier.weight(1f),
                            isDelete = true
                        )
                    } else {
                        KeypadButton(
                            text = button,
                            onClick = { viewModel.addDigit(button) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun KeypadButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDelete: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(50.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isDelete) Color(0xFF666666) else Color(0xFF555555)
        )
    ) {
        Text(text, fontSize = 20.sp, color = Color.White)
    }
}
