package com.example.acmecompany.ui.view.helpers

import android.content.Context

import android.view.Gravity
import android.view.View

import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import com.example.acmecompany.R


class PopupMenuHelper
{
    companion object
    {

        fun show(
            context: Context,
            anchor: View,
            @MenuRes menuResId: Int
        ): PopupMenu
        {
            val popup = PopupMenu(context, anchor)
            popup.inflate(menuResId)
            popup.show()

            return popup
        }

        fun show(
            context: Context,
            anchor: View,
            @MenuRes menuResId: Int,
            listener: PopupMenu.OnMenuItemClickListener
        ): PopupMenu
        {
            val popup = PopupMenu(context, anchor)
            popup.inflate(menuResId)
            popup.setOnMenuItemClickListener(listener)
            popup.show()

            return popup
        }

        fun showOverflow(
            context: Context,
            anchor: View,
            @MenuRes menuResId: Int,
            listener: PopupMenu.OnMenuItemClickListener
        ): PopupMenu
        {
            val popup: PopupMenu = createOverflow(context, anchor, menuResId, listener)
            popup.show()

            return popup
        }

        /**
         * Create a [PopupMenu] but without showing it. You must to show it.
         * Useful for modifying items before calling the show() method.
         */
        fun createOverflow(
            context: Context,
            anchor: View,
            @MenuRes menuResId: Int,
            listener: PopupMenu.OnMenuItemClickListener
        ): PopupMenu
        {
            val popup =
                PopupMenu(context, anchor, Gravity.START, 0, R.style.PopupMenu_Overflow)
            popup.inflate(menuResId)
            popup.setOnMenuItemClickListener(listener)

            return popup
        }
    }
}