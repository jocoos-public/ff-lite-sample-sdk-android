package com.jocoos.flipflop.sample.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jocoos.flipflop.sample.R
import com.jocoos.flipflop.sample.databinding.LoginActivityBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: LoginActivityBinding

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, LoginFragment.newInstance())
            .commitNow()
    }
}