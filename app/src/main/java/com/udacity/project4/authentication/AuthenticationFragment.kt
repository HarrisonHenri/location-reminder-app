package com.udacity.project4.authentication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.databinding.AuthenticationFragmentBinding
import com.udacity.project4.utils.Constants.SIGN_IN_REQUEST_CODE
import org.koin.androidx.viewmodel.ext.android.viewModel

class AuthenticationFragment : BaseFragment() {

    private lateinit var binding: AuthenticationFragmentBinding
    override val _viewModel: AuthenticationViewModel by viewModel()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = AuthenticationFragmentBinding.inflate(inflater)

        binding.loginButton.setOnClickListener {
            _viewModel.onLoginButtonClicked()
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_REQUEST_CODE) {
            _viewModel.onLoginResult(resultCode)
        }
        _viewModel.navigateBack()
    }
}

