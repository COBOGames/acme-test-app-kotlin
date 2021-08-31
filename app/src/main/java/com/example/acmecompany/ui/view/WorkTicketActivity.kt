package com.example.acmecompany.ui.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.DatePicker
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import com.example.acmecompany.App
import com.example.acmecompany.R
import com.example.acmecompany.data.room.entities.Ticket
import com.example.acmecompany.databinding.ActivityWorkTicketBinding
import com.example.acmecompany.ui.view.helpers.TextInputLayoutHelper
import com.example.acmecompany.ui.view.helpers.ToastHelper
import com.example.acmecompany.ui.viewmodel.TicketViewModel
import com.example.acmecompany.ui.viewmodel.TicketViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class WorkTicketActivity : AppCompatActivity()
{
    companion object
    {
        // region CONSTANTS

        /**
         * Extra of int type. <br></br>
         * Indicates the id of the [Ticket] created or edited when your
         * [Activity.onActivityResult] is invoked.
         */
        const val EXTRA_TICKET_ID = "8ac835a69b7e4170b98a118126641e3b"

        /**
         * Extra of Boolean type. <br></br>
         * Indicates if the [Ticket] was edited (true) or created (false) when your
         * [Activity.onActivityResult] is invoked.
         */
        const val EXTRA_TICKET_WAS_EDITED = "9e24ea43fba4457ba9b06ac8de4d0a03"

        /**
         * The date format used to parse dates [d/M/yyyy]
         */
        val DATE_FORMAT: DateFormat = SimpleDateFormat("d/M/yyyy", Locale.US)

        /**
         * Extra of int type. <br></br>
         * Indicates the id of the [Ticket] to edit.
         */
        private const val EXTRA_TICKET_TO_EDIT_ID = "8dc84aff433a48f4896ccacaf3240dd8"

        // endregion

        // region FACTORY METHODS

        /**
         * Use this factory method to start new instance of this activity using the provided parameters.
         * <br></br>
         * You can receive de results in your [Activity.onActivityResult] method.
         * If [Activity.RESULT_OK] means we added or edited successfully. <br></br>
         * - To check if a ticket was edited or created check the boolean [.EXTRA_TICKET_WAS_EDITED]. <br></br>
         * - To get the uuid of the ticket created or edited, get the string [.EXTRA_TICKET_UUID]. <br></br>
         *
         * @param ticketToEditId If -1 we start the activity to create a new ticked. If not, we start
         * the activity to edit the ticket with the given id.
         */
        fun start(fromActivity: Activity, ticketToEditId: Int, requestCode: Int)
        {
            // create intent and put extras
            val intent = Intent(fromActivity, WorkTicketActivity::class.java)
            intent.putExtra(EXTRA_TICKET_TO_EDIT_ID, ticketToEditId)

            fromActivity.startActivityForResult(intent, requestCode)
        }

        // endregion
    }

    // region PRIVATE VARIABLES

    private var mTicket: Ticket = Ticket()
    private var mEditing = false
    private lateinit var b: ActivityWorkTicketBinding

    private val ticketViewModel: TicketViewModel by viewModels {
        TicketViewModelFactory((application as App).repository)
    }

    // endregion

    // region OVERRIDES

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        b = ActivityWorkTicketBinding.inflate(layoutInflater)
        setContentView(b.root)

        initEvents()
        initExtras()
        updateToolbarTitle()

        ticketViewModel.isLoading.observe(owner = this) { isLoading ->
            b.loadingIndicator.container.isVisible = isLoading
        }

        // show go back btn in the toolbar and set the close icon
        val actionBar: ActionBar? = supportActionBar
        if (actionBar != null)
        {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        menuInflater.inflate(R.menu.work_ticket, menu)
        return true
    }

    override fun onOptionsItemSelected(i: MenuItem): Boolean
    {
        when (i.itemId)
        {
            android.R.id.home ->
            {
                onBackPressed()
                return true
            }
            R.id.action_save ->
            {
                onSaveClicked()
                return true
            }
        }

        return super.onOptionsItemSelected(i)
    }

    // endregion

    // region PRIVATE METHODS

    private fun onSaveClicked()
    {
        // clear errors
        TextInputLayoutHelper.clearError(b.tilClientName)
        TextInputLayoutHelper.clearError(b.tilAddress)
        TextInputLayoutHelper.clearError(b.tilScheduleDate)

        // get values
        val clientName = b.tilClientName.editText?.text.toString()
        val address: String = b.tilAddress.editText?.text.toString()
        val date: String = b.tilScheduleDate.editText?.text.toString()
        val phone: String = b.tilPhone.editText?.text.toString()
        val reason: String = b.tilReasonForCall.editText?.text.toString()
        var errors = false

        // region CHECK REQUIRED FIELDS

        if (clientName.isEmpty())
        {
            TextInputLayoutHelper.setError(
                b.tilClientName,
                getString(R.string.error_msg_can_not_be_empty)
            )
            errors = true
        }
        if (address.isEmpty())
        {
            TextInputLayoutHelper.setError(
                b.tilAddress,
                getString(R.string.error_msg_can_not_be_empty)
            )
            errors = true
        }
        if (date.isEmpty())
        {
            TextInputLayoutHelper.setError(
                b.tilScheduleDate,
                getString(R.string.error_msg_can_not_be_empty)
            )
            errors = true
        }

        // endregion

        // end if we have errors
        if (errors) return

        // region ASSIGN VALUES

        // parse date
        try
        {
            val d: Date = DATE_FORMAT.parse(date)
            mTicket.scheduleTime = d.time
        } catch (e: Exception)
        {
            ToastHelper.show("Error parsing Schedule date")
            return
        }

        mTicket.clientName = clientName
        mTicket.address = address
        mTicket.phone = phone
        mTicket.reasonForCall = reason

        lifecycleScope.launch {
            try
            {
                withContext(Dispatchers.IO) {
                    if (mEditing)
                        ticketViewModel.update(mTicket)
                    else
                        ticketViewModel.insert(mTicket)
                }

                // set result OK result and finish the activity
                val data = Intent()
                data.putExtra(EXTRA_TICKET_WAS_EDITED, mEditing)
                data.putExtra(EXTRA_TICKET_ID, mTicket.id)
                setResult(Activity.RESULT_OK, data)

                finish()
            } catch (e: Exception)
            {
                Log.e("Error", e.message ?: "")
                ToastHelper.show("Error saving ticket")
            }
        }

        // endregion
    }

    @SuppressLint("SetTextI18n")
    private fun initEvents()
    {
        // when the edit texts are changed clear the error. only in required fields
        TextInputLayoutHelper.addClearErrorOnTextChanged(b.tilClientName)
        TextInputLayoutHelper.addClearErrorOnTextChanged(b.tilAddress)
        TextInputLayoutHelper.addClearErrorOnTextChanged(b.tilScheduleDate)

        // region EVENTS
        b.btnGetDirections.setOnClickListener {
            val address: String = b.tilAddress.editText?.text.toString()
            if (address.isEmpty())
                TextInputLayoutHelper.setError(
                    b.tilAddress,
                    getString(R.string.error_msg_can_not_be_empty)
                )
            else
                MapsActivity.start(this@WorkTicketActivity, address)
        }

        // show date picker dialog when schedule date is clicked
        val edtScheduleDate = b.tilScheduleDate.editText
        edtScheduleDate?.setOnClickListener {
            val c: Calendar = Calendar.getInstance()

            // try to set the current date in the edt if not empty
            val currentDate: String = edtScheduleDate.text.toString()
            if (!TextUtils.isEmpty(currentDate))
            {
                try
                {
                    val date: Date = DATE_FORMAT.parse(currentDate)
                    c.timeInMillis = date.time
                } catch (e: Exception)
                {
                    ToastHelper.show("Error parsing date")
                }
            }

            val y: Int = c.get(Calendar.YEAR)
            val m: Int = c.get(Calendar.MONDAY)
            val d: Int = c.get(Calendar.DAY_OF_MONTH)

            val dialog = DatePickerDialog(
                this@WorkTicketActivity,
                OnDateSetListener { _: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                    edtScheduleDate.setText("$dayOfMonth/${month + 1}/$year")
                }, y, m, d
            )
            // allow picking dates from today and on
            // for test purposes we comment the below line to allow seeing due tickets quickly
            // dialog.getDatePicker().setMinDate(System.currentTimeMillis());
            dialog.show()
        }

        // endregion
    }

    private fun updateToolbarTitle()
    {
        val id = if (mEditing) R.string.work_ticked_edit_title else R.string.action_new_ticket
        title = getString(id)
    }

    private fun initExtras()
    {
        val id = intent.getIntExtra(EXTRA_TICKET_TO_EDIT_ID, -1)

        // if no id start creating a new one
        if (id == -1)
        {
            mEditing = false

            mTicket = Ticket()
            mTicket.scheduleTime = Date().time
        } else
        {
            mEditing = true

            // find the ticket by 'id' inside a coroutine
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    mTicket = ticketViewModel.findById(id)
                }

                // update ui
                b.tilClientName.editText?.setText(mTicket.clientName)
                b.tilAddress.editText?.setText(mTicket.address)
                b.tilScheduleDate.editText?.setText(DATE_FORMAT.format(Date(mTicket.scheduleTime)))
                b.tilPhone.editText?.setText(mTicket.phone)
                b.tilReasonForCall.editText?.setText(mTicket.reasonForCall)
            }
        }
    }

    // endregion
}