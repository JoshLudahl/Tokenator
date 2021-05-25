package com.token.tokenator.ui.savedpassword.passworddetails

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.token.tokenator.R
import com.token.tokenator.databinding.PasswordDetailFragmentBinding
import com.token.tokenator.utilities.Clipuous
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PasswordDetailFragment : Fragment(R.layout.password_detail_fragment) {

    private val viewModel: PasswordDetailViewModel by viewModels()
    private var _binding: PasswordDetailFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = PasswordDetailFragmentBinding.bind(view)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }

        val token = arguments?.get("token").toString().toIntOrNull()

        token?.let {
            viewModel.getToken(it)
        }

        binding.buttonUpdatePassword.setOnClickListener {
            token?.let {
                if (binding.buttonUpdatePassword.isEnabled) {
                    viewModel.insert(
                        login = binding.tokenLoginName.text.toString().trim(),
                        token = binding.tokenPassword.text.toString(),
                        passwordName = binding.tokenName.text.toString().trim()
                    )
                    Toast.makeText(requireContext(), R.string.password_saved, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.tokenName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                binding.buttonUpdatePassword.isEnabled = s?.trim()?.length != 0
            }
        })

        binding.loginTextField.setEndIconOnClickListener {
            copyToClipBoard(binding.tokenLoginName.text.toString())
        }

        binding.passwordTextField.setEndIconOnClickListener {
            copyToClipBoard(binding.tokenPassword.text.toString())
        }
    }

    private fun copyToClipBoard(text: String) {
        Clipuous.copyToClipboard(text, requireContext())
        showToast(getString(R.string.toast_copied_to_clipboard))
    }

    private fun showToast(message: String) {
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}
