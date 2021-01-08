package com.token.tokenator.main

import android.app.Activity
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ScrollView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import com.token.tokenator.R
import com.token.tokenator.R.color.blackish
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
            navigateToSaved()
        }

        binding.fluidSlider
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

    private fun navigateToSaved() {
        Navigation.findNavController(
            requireActivity(),
            R.id.myNavHostFragment
        ).navigate(R.id.action_mainFragment_to_savedTokenFragment)
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
                showFeature()
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

        var length: Int = (binding.fluidSlider.position * 100).toInt()
        if (length == 0) {
            binding.fluidSlider.position = 0.08f
            length = 8
        }
        val password = Tokenator.generate(
            length,
            chars
        )

        when {
            password.isNotEmpty() -> {
                viewModel.setToken(password)
                binding.generatedField.text = password
                viewModel.setLength(binding.fluidSlider.position)

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

    fun showFeature() {

        binding.scrollView.fullScroll(ScrollView.FOCUS_UP)
        hideKeyboard(requireActivity())

        TapTargetView.showFor(requireActivity(),  // `this` is an Activity
            TapTarget.forView(
                binding.viewSavedButton,
                "Saved Passwords",
                "Click here to view your saved passwords"
            )
                .outerCircleColor(R.color.yellow)
                .outerCircleAlpha(0.96f)
                .targetCircleColor(R.color.white)
                .titleTextSize(20)
                .titleTextColor(R.color.white)
                .descriptionTextSize(14)
                .descriptionTextColor(R.color.yellow)
                .textColor(blackish)
                .textTypeface(Typeface.SANS_SERIF)
                .dimColor(R.color.black)
                .drawShadow(true)
                .cancelable(true)
                .tintTarget(true)
                .transparentTarget(true)
                .targetRadius(60),
            object : TapTargetView.Listener() {
                // The listener can listen for regular clicks, long clicks or cancels
                override fun onTargetClick(view: TapTargetView) {
                    super.onTargetClick(view) // This call is optional
                    navigateToSaved()
                }

                override fun onOuterCircleClick(view: TapTargetView?) {
                    super.onOuterCircleClick(view)
                    view?.dismiss(true)
                }
            })
    }
}

fun hideKeyboard(activity: Activity) {
    val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    //Find the currently focused view, so we can grab the correct window token from it.
    var view = activity.currentFocus
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(activity)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}
