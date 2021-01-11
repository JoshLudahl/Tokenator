package com.token.tokenator.savedpassword

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.token.tokenator.R
import com.token.tokenator.databinding.SavedTokenFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SavedTokenFragment : Fragment(R.layout.saved_token_fragment) {

    private val viewModel: SavedTokenViewModel by viewModels()
    private lateinit var binding: SavedTokenFragmentBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = SavedTokenFragmentBinding.bind(view)
        binding.tokenViewModel = viewModel

        val adapter = SavedPasswordAdapter(TokenListener {
            lifecycleScope.launch {
                viewModel.delete(it)
            }
        })

        binding.passwordRecyclerView.apply {
            this.adapter = adapter
            this.layoutManager = LinearLayoutManager(requireContext())
        }

        binding.buttonBack.setOnClickListener {
            Navigation.findNavController(requireActivity(), R.id.myNavHostFragment).navigateUp()
        }

        viewModel.tokens.observe(viewLifecycleOwner, {
            it?.let {
                adapter.setItems(it)
                if (adapter.itemCount == 0) {
                    binding.passwordRecyclerView.visibility = View.GONE
                    binding.noPasswordsText.visibility = View.VISIBLE
                    binding.noPasswords.visibility = View.VISIBLE
                }
            }
        })

        binding.lifecycleOwner = this
    }
}
