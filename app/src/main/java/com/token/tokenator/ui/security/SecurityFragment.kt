package com.token.tokenator.ui.security

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.token.tokenator.R
import com.token.tokenator.databinding.SecurityFragmentBinding

class SecurityFragment : Fragment(R.layout.security_fragment) {

    private val viewModel: SecurityViewModel by viewModels()
    private var _binding: SecurityFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = SecurityFragmentBinding.bind(view)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
