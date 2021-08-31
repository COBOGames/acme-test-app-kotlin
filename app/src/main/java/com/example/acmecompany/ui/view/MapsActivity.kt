package com.example.acmecompany.ui.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.acmecompany.R
import com.example.acmecompany.databinding.ActivityMapsBinding
import java.net.URLEncoder


class MapsActivity : AppCompatActivity()
{
    companion object
    {
        private const val EXTRA_ADDRESS = "45730659aa594209aae4bc3d9b1459b5"

        fun start(fromActivity: Activity, address: String? = null)
        {
            val intent = Intent(fromActivity, MapsActivity::class.java)
            intent.putExtra(EXTRA_ADDRESS, address)

            fromActivity.startActivity(intent)
        }
    }

    // region PRIVATE VARIABLES

    private lateinit var b: ActivityMapsBinding

    // endregion

    // region OVERRIDES

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        b = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(b.root)

        initWebView()
        initBtnGo()
        initExtras()

        // go now to the address
        goToAddressInWebViewMap()

        // show go back btn in the toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(i: MenuItem): Boolean
    {
        if (i.itemId == android.R.id.home)
        {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(i)
    }

    // endregion

    // region PRIVATE METHODS

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView()
    {
        with(b.webView) {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
        }
    }

    private fun initExtras()
    {
        // set address extra to the edit text
        val addressExtra = intent.getStringExtra(EXTRA_ADDRESS)
        b.edtMapAddress.setText(addressExtra)
    }

    private fun initBtnGo()
    {
        b.btnGo.setOnClickListener {
            // if the address is empty show error
            val address: String = b.edtMapAddress.text.toString()
            if (address.isEmpty())
                b.edtMapAddress.error = getString(R.string.error_msg_can_not_be_empty)
            else
                goToAddressInWebViewMap()

            // hide the keyboard
            val imm =
                this@MapsActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(b.edtMapAddress.windowToken, 0)
        }
    }

    private fun goToAddressInWebViewMap()
    {
        var url = "https://www.google.com/maps/" // default for empty
        val address: String = b.edtMapAddress.text.toString()

        try
        {
            if (address.isNotEmpty())
                url += "search/" + URLEncoder.encode(address, "utf8")
        } catch (ignored: Exception)
        {
        }

        b.webView.loadUrl(url)
    }

    // endregion
}