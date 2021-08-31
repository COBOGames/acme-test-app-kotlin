package com.example.acmecompany.ui.view.helpers

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import com.example.acmecompany.App
import com.example.acmecompany.R


class DialogHelper
{
    companion object
    {
        // region CONSTANTS

        const val NO_TEXT = "0"
        const val DEFAULT_TEXT = "1"

        // endregion

        // region CONFIRM METHODS

        /**
         * Show a cancelable confirm dialog.
         * <br></br>
         * NOTE: The dialog is dismissed when a button is clicked.
         *
         * @param context            The context
         * @param title              Use [.NO_TEXT] to hide it or [.DEFAULT_TEXT] to use
         * the default CONFIRMATION text.
         * @param message            The message.
         * @param onAnyButtonClicked If 'null' the dialog is only dismissed on any button.
         * @param positiveBtnName    Use [.NO_TEXT] to hide it or [.DEFAULT_TEXT] to use
         * the built-in ACCEPT text.
         * @param negativeBtnName    Use [.NO_TEXT] to hide it or [.DEFAULT_TEXT] to use
         * the built-in CANCEL text.
         */
        fun showConfirm(
            context: Context,
            title: String,
            message: String?,
            onAnyButtonClicked: DialogInterface.OnClickListener?,
            positiveBtnName: String,
            negativeBtnName: String
        ): AlertDialog
        {
            val builder = AlertDialog.Builder(context)

            // message
            builder.setMessage(message)
            val titleText = getText(title, getConfirmationString())
            val positiveText = getText(positiveBtnName, getAcceptString())
            val negativeText = getText(negativeBtnName, getCancelString())

            // title
            if (titleText != null) builder.setTitle(titleText)

            // buttons
            val onClickListener: DialogInterface.OnClickListener =
                OnAnyButtonClicked(onAnyButtonClicked)
            if (positiveText != null) builder.setPositiveButton(positiveText, onClickListener)
            if (negativeText != null) builder.setNegativeButton(negativeText, onClickListener)

            // show it
            val dialog: AlertDialog = builder.create()
            dialog.show()
            return dialog
        }

        // endregion

        // region PUBLIC METHODS

        /**
         * Returns the built-in ACCEPT string.
         */
        fun getAcceptString(): String
        {
            return App.context.getString(android.R.string.yes)
        }

        /**
         * Returns the built-in CANCEL string.
         */
        fun getCancelString(): String
        {
            return App.context.getString(android.R.string.cancel)
        }

        fun getConfirmationString(): String
        {
            return App.context.getString(R.string.confirm_dialog_default_title)
        }

        // endregion

        // region PRIVATE METHODS

        /**
         * If the text is [NO_TEXT], the null is returned. Otherwise, if the text is not
         * [DEFAULT_TEXT], the text returned.
         */
        private fun getText(text: String, default_: String): String?
        {
            // if no text, return null
            if (text == NO_TEXT) return null

            // return the text if not using the default text
            return if (text != DEFAULT_TEXT) text else default_
        }

        // endregion
    }

    // region NESTED TYPES

    class OnAnyButtonClicked constructor(listener: DialogInterface.OnClickListener?) :
        DialogInterface.OnClickListener
    {
        private val customListener: DialogInterface.OnClickListener? = listener

        override fun onClick(dialog: DialogInterface, which: Int)
        {
            customListener?.onClick(dialog, which)
            dialog.dismiss()
        }

    }

    // endregion
}