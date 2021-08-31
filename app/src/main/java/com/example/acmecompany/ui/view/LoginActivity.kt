package com.example.acmecompany.ui.view

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.acmecompany.R
import com.example.acmecompany.databinding.ActivityLoginBinding
import com.example.acmecompany.ui.view.helpers.TextInputLayoutHelper


class LoginActivity : AppCompatActivity(), View.OnClickListener
{
    // region PRIVATE VARIABLES

    private lateinit var binding: ActivityLoginBinding

    // endregion

    // region OVERRIDES

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // when the edts text are changed clear the error
        TextInputLayoutHelper.addClearErrorOnTextChanged(binding.tilEmail)
        TextInputLayoutHelper.addClearErrorOnTextChanged(binding.tilPassword)

        title = getString(R.string.activity_title_login)
        binding.btnLoginStart.setOnClickListener(this)

        // start now the MainActivity
        // startActivity(Intent(this, MainActivity::class.java))
        // finish()
    }

    override fun onClick(v: View?)
    {
        when (v?.id)
        {
            R.id.btn_login_start ->
            {
                val tilEmail = binding.tilEmail
                val tilPassword = binding.tilPassword

                // clear errors
                TextInputLayoutHelper.clearError(tilEmail)
                TextInputLayoutHelper.clearError(tilPassword)

                // get values from widgets
                val username: String = tilEmail.editText?.text.toString().trim()
                val password: String = tilPassword.editText?.text.toString().trim()

                // if empty set error
                if (username.isEmpty())
                    TextInputLayoutHelper.setError(
                        tilEmail,
                        getString(R.string.error_msg_can_not_be_empty)
                    )

                // if empty set error
                if (password.isEmpty())
                    TextInputLayoutHelper.setError(
                        tilPassword,
                        getString(R.string.error_msg_can_not_be_empty)
                    )

                // continue if no errors
                val usernameHasErrors = !TextUtils.isEmpty(tilEmail.error)
                val passwordHasErrors = !TextUtils.isEmpty(tilEmail.error)
                if (!usernameHasErrors && !passwordHasErrors)
                {
                    // check username and pass
                    // for the moment use admin/admin
                    if (username != "admin" || password != "admin")
                        TextInputLayoutHelper.setError(
                            tilPassword,
                            getString(R.string.error_msg_wrong_username_or_pass)
                        ) else
                    {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                }
            }
        }
    }

    // endregion
}