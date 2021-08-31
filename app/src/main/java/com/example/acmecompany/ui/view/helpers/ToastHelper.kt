package com.example.acmecompany.ui.view.helpers

import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.acmecompany.App
import com.google.android.material.R
import com.google.android.material.snackbar.Snackbar


class ToastHelper
{
    companion object
    {
        fun show(message: String?)
        {
            Toast.makeText(App.context, message, Toast.LENGTH_SHORT).show()
        }

        fun showLong(message: String?)
        {
            Toast.makeText(App.context, message, Toast.LENGTH_LONG).show()
        }

        fun showSnackbarShort(view: View?, message: String?): Snackbar?
        {
            return showSnackbar(view, message, Snackbar.LENGTH_SHORT)
        }

        fun showSnackbarLong(view: View?, message: String?): Snackbar?
        {
            return showSnackbar(view, message, Snackbar.LENGTH_LONG)
        }

        fun showSnackbar(view: View?, message: String?, length: Int): Snackbar?
        {
            val snackbar = view?.let { Snackbar.make(it, message!!, length) }
            snackbar?.show()
            setColorToSnackbarText(snackbar)
            return snackbar
        }

        private val SNACKBAR_TEXT_COLOR: Int = android.graphics.Color.parseColor("#DEffffff")

        private fun setColorToSnackbarText(snackbar: Snackbar?)
        {
            val textView = snackbar?.view?.findViewById<TextView>(R.id.snackbar_text)
            textView?.setTextColor(SNACKBAR_TEXT_COLOR)
        }
    }
}