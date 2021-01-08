package com.token.tokenator.main

import android.content.ClipboardManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.token.tokenator.R
import com.token.tokenator.Utilities.Clipuous
import com.token.tokenator.databinding.MainFragmentBinding
import com.token.tokenator.model.Tokenator
import com.token.tokenator.model.Type
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.main_fragment) {

    private lateinit var binding: MainFragmentBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = MainFragmentBinding.bind(view)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.buttonGenerateToken.setOnClickListener {
            generatePassword()
        }

        binding.saveButton.setOnClickListener {
            saveToken()
        }

        binding.viewSavedButton.setOnClickListener {
            Navigation.findNavController(
                requireActivity(),
                R.id.myNavHostFragment
            ).navigate(R.id.action_mainFragment_to_savedTokenFragment)
        }

        binding.tokenName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count > 0) {
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

    private fun saveToken() {
        when {
            binding.tokenName.text.isNullOrEmpty() -> {
                Toast.makeText(
                    requireContext(),
                    R.string.error_enter_name_for_password,
                    Toast.LENGTH_SHORT
                ).show()
            }
            binding.generatedField.text.isNullOrEmpty() -> {
                Toast.makeText(
                    requireContext(),
                    R.string.error_generate_password_first,
                    Toast.LENGTH_SHORT
                ).show()
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

    private fun copyToClipBoard(password: String) {
        Clipuous.copyToClipboard(password, requireContext())
        Toast.makeText(requireContext(), R.string.toast_copied_to_clipboard, Toast.LENGTH_SHORT)
            .show()
    }

    private fun generatePassword() {

        val chars = mutableListOf<Type>()
        if (binding.switchLowerCase.isChecked) chars.add(Type.LOWERCASE)
        if (binding.switchNumeric.isChecked) chars.add(Type.NUMERIC)
        if (binding.switchSpecialCharacters.isChecked) chars.add(Type.SPECIAL)
        if (binding.switchUppercase.isChecked) chars.add(Type.UPPERCASE)

        val password = Tokenator.generate(
            binding.editTextLength.text.toString().toIntOrNull() ?: 8,
            chars
        )

        when {
            password.isNotEmpty() -> {
                viewModel.setToken(password)
                binding.generatedField.text = password
                viewModel.updateTokenLength(binding.editTextLength.editableText)

                copyToClipBoard(password)

                binding.tokenName.apply {
                    visibility = View.VISIBLE
                    requestFocus()
                    viewModel.tokenNameEditText = View.VISIBLE
                }
            }
            else -> Toast.makeText(
                requireContext(),
                R.string.toast_length_warning,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
