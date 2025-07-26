package com.token.tokenator.ui.main

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.token.tokenator.R
import com.token.tokenator.databinding.PrivacyPolicyDialogFragmentBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PrivacyPolicyDialogFragment : DialogFragment() {
    @Suppress("ktlint:standard:backing-property-naming")
    private var _binding: PrivacyPolicyDialogFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PrivacyPolicyDialogViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        activity?.let { fragmentActivity ->
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(fragmentActivity)
            // Create the AlertDialog object and return it

            _binding =
                DataBindingUtil.inflate(
                    layoutInflater.cloneInContext(context),
                    R.layout.privacy_policy_dialog_fragment,
                    null,
                    false,
                )
            binding.viewModel = viewModel
            builder.setView(binding.root)

            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    viewModel.shouldDismiss.collect { shouldCancel ->
                        when (shouldCancel) {
                            true -> {
                                dialog?.cancel()
                            }
                            else -> Unit
                        }
                    }
                }
            }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
}
