package com.token.tokenator.utilities.alert

import android.app.Dialog
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.token.tokenator.R
import com.token.tokenator.databinding.GenericDialogFragmentBinding
import kotlinx.coroutines.launch

class GenericAlert(
    @StringRes val title: Int,
) : DialogFragment() {
    private var _binding: GenericDialogFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GenericAlertViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { fragmentActivity ->
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(fragmentActivity)
            // Create the AlertDialog object and return it

            _binding =
                DataBindingUtil.inflate(
                    layoutInflater.cloneInContext(context),
                    R.layout.generic_dialog_fragment,
                    null,
                    false,
                )
            binding.viewModel = viewModel

            binding.privacyPolicyTextHeader.text = getText(title)

            builder.setView(binding.root)

            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
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
}
