package com.token.tokenator.ui.savedpassword.passworddetails

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.token.tokenator.R

class PasswordDetailFragment : Fragment() {

    companion object {
        fun newInstance() = PasswordDetailFragment()
    }

    private lateinit var viewModel: PasswordDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.password_detail_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PasswordDetailViewModel::class.java)
        // TODO: Use the ViewModel
    }

}