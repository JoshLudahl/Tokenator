package com.token.tokenator.ui.main

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.token.tokenator.R
import com.token.tokenator.databinding.PrivacyPolicyDialogFragmentBinding
import kotlinx.coroutines.flow.collect

class PrivacyPolicyDialogFragment : DialogFragment() {
    private var _binding: PrivacyPolicyDialogFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PrivacyPolicyDialogViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            // Create the AlertDialog object and return it

            _binding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.privacy_policy_dialog_fragment,
                null,
                false
            )
            binding.viewModel = viewModel
            builder.setView(binding.root)

            lifecycleScope.launchWhenStarted {
                viewModel.shouldDismiss.collect {
                    when (it) {
                        true -> {
                            dialog?.cancel()
                        }
                    }
                }
            }

            builder.create()

        } ?: throw IllegalStateException("Activity cannot be null")
    }


}
