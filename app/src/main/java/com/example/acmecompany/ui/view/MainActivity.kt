package com.example.acmecompany.ui.view

import android.Manifest
import android.content.ContentValues
import android.content.DialogInterface
import android.database.Cursor
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.acmecompany.App
import com.example.acmecompany.R
import com.example.acmecompany.data.room.entities.Ticket
import com.example.acmecompany.databinding.ActivityMainBinding
import com.example.acmecompany.ui.view.adapters.OnTicketEvents
import com.example.acmecompany.ui.view.adapters.TicketListAdapter
import com.example.acmecompany.ui.view.helpers.DialogHelper
import com.example.acmecompany.ui.view.helpers.PermissionHelper
import com.example.acmecompany.ui.view.helpers.ToastHelper
import com.example.acmecompany.ui.viewmodel.TicketViewModel
import com.example.acmecompany.ui.viewmodel.TicketViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class MainActivity : AppCompatActivity()
{
    companion object
    {
        // region CONSTANTS

        private const val RC_WORK_TICKET = 910
        private const val RC_CALENDAR_PERMISSION = 245

        // endregion
    }

    // region PRIVATE VARIABLES

    private lateinit var b: ActivityMainBinding
    private lateinit var adapter: TicketListAdapter
    private val ticketViewModel: TicketViewModel by viewModels {
        TicketViewModelFactory((application as App).repository)
    }

    // endregion

    // region OVERRIDES

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        initAdapter()
        initRecyclerView(adapter)

        // Add an observer on the LiveData returned by allTickets.
        // The onChanged() method fires when the observed data changes and the activity is in the foreground.
        ticketViewModel.allTickets.observe(this) { tickets ->
            // update the cached copy of the words in the adapter.
            tickets.let { adapter.submitList(tickets) }

            if (tickets.isEmpty())
                b.containerNoItems.visibility = View.VISIBLE
            else
                b.containerNoItems.visibility = View.GONE
        }

        ticketViewModel.isLoading.observe(this) { isLoading ->
            b.loadingIndicator.container.isVisible = isLoading
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        when (item.itemId)
        {
            R.id.action_add_ticket ->
            {
                // we pass -1 to create a new one
                WorkTicketActivity.start(this, -1, RC_WORK_TICKET)
                return true
            }
            R.id.action_work_ticket ->
            {
                // if no tickets show message
                if (adapter.itemCount == 0)
                {
                    ToastHelper.show(getString(R.string.main_tickets_empty))
                } else
                {
                    // edit the most recent ticket created
                    val ticket: Ticket = adapter.currentList[adapter.currentList.size - 1]
                    WorkTicketActivity.start(this, ticket.id, RC_WORK_TICKET)
                }
                return true
            }
            R.id.action_get_directions ->
            {
                MapsActivity.start(this)
                return true
            }
            R.id.action_sync_calendar ->
            {
                if (adapter.itemCount == 0)
                {
                    ToastHelper.show(getString(R.string.main_tickets_empty))
                } else
                {
                    // check if we have Calendar permissions
                    if (PermissionHelper.checkForPermission(
                            this,
                            arrayOf(
                                Manifest.permission.READ_CALENDAR,
                                Manifest.permission.WRITE_CALENDAR
                            ),
                            RC_CALENDAR_PERMISSION
                        )
                    ) showSyncDialog()
                }
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    // endregion

    // region PRIVATE METHODS

    private fun showSyncDialog()
    {
        // show a confirm dialog to sync
        DialogHelper.showConfirm(
            this,
            DialogHelper.NO_TEXT,
            getString(R.string.confirm_sync_calendar),
            DialogInterface.OnClickListener { _, which ->
                if (which == DialogInterface.BUTTON_POSITIVE)
                {
                    syncCalendar()
                }
            },
            "Sync",
            DialogHelper.DEFAULT_TEXT
        )
    }

    private fun syncCalendar()
    {
        try
        {
            for (ticket in adapter.currentList)
                insertOrEditTicketInCalendar(ticket)
            ToastHelper.show("Calendar synced!")
        } catch (e: java.lang.Exception)
        {
            Log.e("Error", "Sync calendar", e)
            ToastHelper.show("Error syncing with the calendar: " + e.message)
        }
    }

    private fun insertOrEditTicketInCalendar(ticket: Ticket)
    {
        val ticketId: Int = ticket.id
        val contentResolver = contentResolver

        // check if edit or insert
        var edit = false
        val cursor: Cursor? = contentResolver.query(
            CalendarContract.Events.CONTENT_URI,
            arrayOf(CalendarContract.Events._ID),
            null,
            null,
            null
        )
        if (cursor != null)
        {
            while (cursor.moveToNext())
            {
                val id: Long = cursor.getLong(0)
                if (id == ticketId.toLong())
                {
                    edit = true
                    break
                }
            }
        }

        cursor?.close()
        val values = ContentValues()
        values.put(CalendarContract.Events._ID, ticketId)
        values.put(CalendarContract.Events.DTSTART, ticket.scheduleTime)
        values.put(CalendarContract.Events.TITLE, ticket.clientName)
        values.put(CalendarContract.Events.DESCRIPTION, "Address: " + ticket.address)
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
        values.put(CalendarContract.Events.CALENDAR_ID, 1) // default calendar
        values.put(CalendarContract.Events.HAS_ALARM, 1) // enable alarm
        values.put(CalendarContract.Events.DURATION, "+P1H") // set period for 1 hour
        values.put(CalendarContract.Events.ALL_DAY, 1)

        // edit or insert event to calendar
        if (edit)
            contentResolver.update(
                CalendarContract.Events.CONTENT_URI,
                values,
                "_id=$ticketId",
                null
            )
        else
            contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
    }

    private fun initAdapter()
    {
        val events = object : OnTicketEvents
        {
            override fun onTicketClick(position: Int)
            {
                // edit on click
                val ticket = adapter.currentList[position]
                WorkTicketActivity.start(this@MainActivity, ticket.id, RC_WORK_TICKET)
            }

            override fun onTicketEdit(position: Int)
            {
                val ticket = adapter.currentList[position]
                WorkTicketActivity.start(this@MainActivity, ticket.id, RC_WORK_TICKET)
            }

            override fun onTicketDelete(position: Int)
            {
                val ticket = adapter.currentList[position]

                val clickListener: DialogInterface.OnClickListener =
                    DialogInterface.OnClickListener { _, which ->
                        // delete if positive clicked
                        if (which == DialogInterface.BUTTON_POSITIVE)
                        {
                            lifecycleScope.launch {
                                try
                                {
                                    withContext(Dispatchers.IO) {
                                        ticketViewModel.delete(ticket)
                                    }
                                } catch (e: Exception)
                                {
                                    ToastHelper.show("Error deleting ticket: ${e.message}")
                                }
                            }
                        }
                    }

                // show a confirm dialog to delete
                DialogHelper.showConfirm(
                    this@MainActivity,
                    DialogHelper.NO_TEXT,
                    getString(R.string.confirm_delete_this_ticket),
                    clickListener,
                    getString(R.string.action_delete),
                    DialogHelper.DEFAULT_TEXT
                )
            }
        }

        adapter = TicketListAdapter(this, events)
    }

    private fun initRecyclerView(ticketAdapter: TicketListAdapter)
    {
        with(b.rvTickets)
        {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = ticketAdapter

            // set vertical divider
            addItemDecoration(
                DividerItemDecoration(
                    this@MainActivity,
                    DividerItemDecoration.VERTICAL
                )
            )
        }
    }

// endregion
}