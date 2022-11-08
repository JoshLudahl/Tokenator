package com.token.tokenator.ui.savedpassword

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.token.tokenator.R
import com.token.tokenator.databinding.SavedTokenFragmentBinding
import com.token.tokenator.model.Token
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SavedTokenFragment : Fragment(R.layout.saved_token_fragment) {

    private val viewModel: SavedTokenViewModel by viewModels()
    private var _binding: SavedTokenFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = SavedTokenFragmentBinding.bind(view)
        binding.tokenViewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        val adapter = SavedPasswordAdapter(
            TokenListener {
                delete(it)
            }
        )

        binding.passwordRecyclerView.apply {
            this.adapter = adapter
            this.layoutManager = LinearLayoutManager(requireContext())
        }

        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.tokens.observe(viewLifecycleOwner) {
            it?.let {
                adapter.setItems(it)
                if (it.isEmpty()) viewModel.setTokenListEmpty(true)
                else viewModel.setTokenListEmpty(false)
            }
        }
    }

    private fun delete(token: Token) {
        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle(getText(R.string.alert_confirm_delete))
            .setMessage(getText(R.string.alert_delete_message))
            .setPositiveButton(getText(R.string.yes)) { _, _ ->
                lifecycleScope.launch {
                    viewModel.delete(token)
                }
            }
            .setNegativeButton(getText(R.string.no)) { dialog, _ ->
                dialog.cancel()
            }
            .create()

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
