package com.jocoos.flipflop.sample.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jocoos.flipflop.sample.FlipFlopSampleApp
import com.jocoos.flipflop.sample.api.ApiManager
import com.jocoos.flipflop.sample.databinding.LoginFragmentBinding
import com.jocoos.flipflop.sample.main.MainActivity
import com.jocoos.flipflop.sample.utils.IOCoroutineScope
import com.jocoos.flipflop.sample.utils.PreferenceManager.Companion.testEmail
import com.jocoos.flipflop.sample.utils.PreferenceManager.Companion.testPassword
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginFragment : Fragment() {
    companion object {
        fun newInstance() = LoginFragment()
    }

    private var _binding: LoginFragmentBinding? = null
    private val binding get() = _binding!!
    private val scope: CoroutineScope = IOCoroutineScope()

    private var username = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = LoginFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.editUsername.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                username = s.toString().trim()
            }
        })

        binding.login.setOnClickListener {
            if (username.isNotBlank()) {
                scope.launch {
                    val response = ApiManager.getInstance().createUser(username, testPassword, testEmail)
                    withContext(Dispatchers.Main) {
                        FlipFlopSampleApp.preferenceManager.setUserInfo(response.id, username, testPassword)
                        delay(200)
                        showMain()
                    }
                }
            }
        }
    }

    private fun showMain() {
        startActivity(Intent(requireContext(), MainActivity::class.java))
        requireActivity().finish()
    }
}