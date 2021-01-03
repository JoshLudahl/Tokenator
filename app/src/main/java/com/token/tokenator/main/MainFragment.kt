package com.token.tokenator.main

import android.content.ClipData
import android.content.ClipboardManager
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.token.tokenator.R
import com.token.tokenator.databinding.MainFragmentBinding
import kotlin.random.Random
import kotlin.random.nextInt

class MainFragment : Fragment(R.layout.main_fragment) {

    private lateinit var binding: MainFragmentBinding
    val viewModel: MainViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = MainFragmentBinding.bind(view)
        binding.viewModel = viewModel

        binding.buttonGenerateToken.setOnClickListener {
            generatePassword()
            binding.tokenName.apply {
                visibility = View.VISIBLE
                requestFocus()
            }
            viewModel.tokenNameEditText = View.VISIBLE
        }

        binding.saveButton.setOnClickListener {
            saveToken()
        }

        binding.viewSavedButton.setOnClickListener {
            Navigation.findNavController(requireActivity(),
                R.id.myNavHostFragment
            ).navigate(R.id.action_mainFragment_to_savedTokenFragment)
        }

        binding.tokenName.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count>0) {
                    binding.saveButton.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 0) {
                    binding.saveButton.visibility = View.GONE
                }
            }
        })
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        viewModel.tokenLength = binding.editTextLength.editableText
    }

    private fun saveToken() {
        when {
            binding.tokenName.text.isNullOrEmpty() -> {
                Toast.makeText(requireContext(), R.string.error_enter_name_for_password, Toast.LENGTH_SHORT).show()
            }
            binding.generatedField.text.isNullOrEmpty() -> {
                Toast.makeText(requireContext(), R.string.error_generate_password_first, Toast.LENGTH_SHORT).show()
            }
            else -> {
                viewModel.insert(
                    passwordName = binding.tokenName.editableText.toString().trim(),
                    token = binding.generatedField.text.toString()
                )
                Toast.makeText(requireContext(), R.string.password_saved, Toast.LENGTH_SHORT).show()
                binding.tokenName.text?.clear()
            }
        }
    }

    private fun copyToClipBoard(password: TextView) {
        val clipboardManager = getSystemService(requireContext(), ClipboardManager::class.java) as ClipboardManager
        val clipData = ClipData.newPlainText(R.string.secret_sauce.toString(), password.text)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(requireContext(), R.string.toast_copied_to_clipboard, Toast.LENGTH_SHORT).show()
    }

    private fun generatePassword() {
        // TODO: 12/30/20 Implement string builder to build passwords

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

            viewModel.tokenLength = binding.editTextLength.editableText
            viewModel.token = password
            newPassword.text = password
            copyToClipBoard(newPassword)
        } else {
            Toast.makeText(requireContext(), R.string.toast_length_warning, Toast.LENGTH_SHORT).show()
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
