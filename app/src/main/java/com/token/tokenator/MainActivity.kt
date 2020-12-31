package com.token.tokenator

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.databinding.DataBindingUtil
import com.token.tokenator.databinding.ActivityMainBinding
import kotlin.random.Random
import kotlin.random.nextInt

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.buttonGenerateToken.setOnClickListener {
            generatePassword()
        }

        binding.saveButton.setOnClickListener {
            saveToken()
        }

        binding.viewSavedButton.setOnClickListener {
            viewSavedTokens()
        }
    }

    private fun viewSavedTokens() {
        TODO("Not yet implemented")
    }

    private fun saveToken() {
        TODO("Not yet implemented")
    }

    private fun copyToClipBoard(password: TextView) {
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText(R.string.secret_sauce.toString(), password.text)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(this, R.string.toast_copied_to_clipboard, Toast.LENGTH_SHORT).show()
    }

    private fun generatePassword() {
        val newPassword = binding.generatedField
        val length = binding.editTextLength
        val lowerCase = binding.switchLowerCase
        val upperCase = binding.switchUppercase
        val numeric = binding.switchNumeric
        val specialChar = binding.switchSpecialCharacters
        val lengthInt = length.text.toString().toIntOrNull() ?: 8
        var password = ""
        val switches = listOf(lowerCase, upperCase, numeric, specialChar)
        val checkedSwitches = mutableListOf<SwitchCompat>()

        for (item in switches) {
            if (item.isChecked) {
                checkedSwitches.add(item)
            }
        }
        if (lengthInt in 1..10000) {
            for (int in 1..lengthInt) {
                checkedSwitches.shuffle()
                if (checkedSwitches.size != 0) {
                    if (lowerCase == checkedSwitches[0]) {
                        password += generateRandomLowercaseLetter()
                    }
                    if (upperCase == checkedSwitches[0]) {
                        password += generateRandomUppercaseLetter()
                    }
                    if (numeric == checkedSwitches[0]) {
                        password += generateRandomNumber()
                    }
                    if (specialChar == checkedSwitches[0]) {
                        password += generateRandomSpecialCharacter()
                    }
                } else {
                    password += generateRandomLowercaseLetter()
                }
            }

            newPassword.text = password
            copyToClipBoard(newPassword)
        } else {
            Toast.makeText(this, R.string.toast_length_warning, Toast.LENGTH_SHORT).show()
        }
    }

    private fun getRandomNumber(lowerBound: Int, upperBound: Int): Int {
        return Random.nextInt(lowerBound..upperBound)
    }

    private fun generateRandomNumber(): Int {
        return getRandomNumber(1, 9)
    }

    private fun generateRandomLowercaseLetter(): Char {
        return getRandomNumber(97, 122).toChar()
    }

    private fun generateRandomUppercaseLetter(): Char {
        return getRandomNumber(65, 90).toChar()
    }

    private fun generateRandomSpecialCharacter(): Char {
        val arrayOfSpecialCharacters = arrayListOf(33, 35, 36, 37, 38, 42, 63)
        val index = getRandomNumber(0, arrayOfSpecialCharacters.size - 1)
        return arrayOfSpecialCharacters[index].toChar()
    }
}
