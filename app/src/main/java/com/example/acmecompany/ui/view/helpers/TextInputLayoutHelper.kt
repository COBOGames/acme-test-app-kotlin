package com.example.acmecompany.ui.view.helpers

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputLayout


class TextInputLayoutHelper
{
    companion object
    {
        fun addClearErrorOnTextChanged(til: TextInputLayout)
        {
            til.editText?.addTextChangedListener(object : TextWatcher
            {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int)
                {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int)
                {
                    clearError(til)
                }

                override fun afterTextChanged(s: Editable)
                {
                }
            })
        }

        fun setError(til: TextInputLayout, message: String?)
        {
            til.isErrorEnabled = true
            til.error = message
        }

        fun clearError(til: TextInputLayout)
        {
            til.isErrorEnabled = false
            til.error = null
        }
    }
}