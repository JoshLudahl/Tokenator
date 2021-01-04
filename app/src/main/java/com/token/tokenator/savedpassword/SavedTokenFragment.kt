package com.token.tokenator.savedpassword

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.token.tokenator.R
import com.token.tokenator.databinding.SavedTokenFragmentBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SavedTokenFragment : Fragment(R.layout.saved_token_fragment) {

    private val viewModel: SavedTokenViewModel by viewModels()
    private lateinit var binding: SavedTokenFragmentBinding
    private val adapter by lazy { SavedPasswordAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = SavedTokenFragmentBinding.bind(view)
        binding.tokenViewModel = viewModel

        setupRecyclerView()

        binding.buttonBack.setOnClickListener {
            Navigation.findNavController(requireActivity(), R.id.myNavHostFragment).navigateUp()
        }

        viewModel.tokens.observe(viewLifecycleOwner, {
            adapter.setItems(it)
            if (adapter.itemCount == 0) {
                binding.passwordRecyclerView.visibility = View.GONE
                binding.noPasswordsText.visibility = View.VISIBLE
                binding.noPasswords.visibility = View.VISIBLE
            }
        })
    }

    private fun setupRecyclerView() {
        binding.passwordRecyclerView.apply {
            adapter = this@SavedTokenFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
}
