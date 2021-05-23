package com.token.tokenator.ui.savedpassword.passworddetails

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.token.tokenator.R
import com.token.tokenator.databinding.PasswordDetailFragmentBinding
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
                viewModel.insert(
                    login = binding.tokenLoginName.text.toString(),
                    token = binding.tokenPassword.text.toString(),
                    passwordName = binding.tokenName.text.toString()
                )
                Toast.makeText(requireContext(), R.string.passphrase_saved, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
