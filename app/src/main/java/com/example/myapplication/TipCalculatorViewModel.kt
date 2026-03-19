package com.example.myapplication

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.combine
import java.text.NumberFormat
import java.util.Locale

class TipCalculatorViewModel : ViewModel() {
    companion object {
        private const val MAX_INPUT_LENGTH = 10
        private const val MAX_TIP_PERCENT = 30f
        private const val MIN_TIP_PERCENT = 0f
        private const val DEFAULT_TIP_PERCENT = 18f
        private const val FIXED_TIP_PERCENT = 15
    }

    private val _amountInput = MutableStateFlow("")
    val amountInput: StateFlow<String> = _amountInput

    private val _customTipPercent = MutableStateFlow(DEFAULT_TIP_PERCENT)
    val customTipPercent: StateFlow<Float> = _customTipPercent

    private val numberFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault())

    fun addDigit(digit: String) {
        if (_amountInput.value.length >= MAX_INPUT_LENGTH) {
            return
        }

        when {
            // Rejeita "-" completamente
            digit == "-" -> return
            // Evita múltiplos pontos
            digit == "." && _amountInput.value.contains(".") -> return
            // Evita ponto no início
            digit == "." && _amountInput.value.isEmpty() -> return
            // Previne "0X" onde X é dígito (ex: "05")
            digit != "." && _amountInput.value == "0" -> {
                _amountInput.value = digit
                return
            }
        }

        _amountInput.value += digit
    }

    fun deleteDigit() {
        if (_amountInput.value.isNotEmpty()) {
            _amountInput.value = _amountInput.value.dropLast(1)
        }
    }

    fun updateCustomTipPercent(percent: Float) {
        if (percent in MIN_TIP_PERCENT..MAX_TIP_PERCENT) {
            _customTipPercent.value = percent
        }
    }

    private fun getAmount(): Double {
        return try {
            if (_amountInput.value.isEmpty() || _amountInput.value == ".") 0.0
            else (_amountInput.value.toDoubleOrNull() ?: 0.0) / 100.0
        } catch (e: Exception) {
            0.0
        }
    }

    fun getFormattedAmount(): String {
        return try {
            numberFormatter.format(getAmount())
        } catch (e: Exception) {
            numberFormatter.format(0.0)
        }
    }

    fun getFormattedTip15(): String {
        return try {
            val tip = getAmount() * (FIXED_TIP_PERCENT.toDouble() / 100.0)
            numberFormatter.format(tip)
        } catch (e: Exception) {
            numberFormatter.format(0.0)
        }
    }

    fun getFormattedCustomTip(): String {
        return try {
            val tip = getAmount() * (_customTipPercent.value / 100.0)
            numberFormatter.format(tip)
        } catch (e: Exception) {
            numberFormatter.format(0.0)
        }
    }

    fun getFormattedTotal15(): String {
        return try {
            val total = getAmount() + (getAmount() * (FIXED_TIP_PERCENT.toDouble() / 100.0))
            numberFormatter.format(total)
        } catch (e: Exception) {
            numberFormatter.format(0.0)
        }
    }

    fun getFormattedTotalCustom(): String {
        return try {
            val total = getAmount() + (getAmount() * (_customTipPercent.value / 100.0))
            numberFormatter.format(total)
        } catch (e: Exception) {
            numberFormatter.format(0.0)
        }
    }
}

