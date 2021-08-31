package com.example.acmecompany.ui.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.acmecompany.R
import com.example.acmecompany.data.room.entities.Ticket
import com.example.acmecompany.databinding.ItemTicketBinding
import com.example.acmecompany.ui.view.WorkTicketActivity
import com.example.acmecompany.ui.view.helpers.PopupMenuHelper
import java.util.*

class TicketListAdapter(context: Context, events: OnTicketEvents) :
    ListAdapter<Ticket, TicketListAdapter.ViewHolder>(Comparator())
{
    // region PRIVATE VARIABLES

    private val ticketEvents: OnTicketEvents = events
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    // endregion

    // region OVERRIDES

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val itemView = inflater.inflate(R.layout.item_ticket, parent, false)
        return ViewHolder(itemView, this)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    // endregion

    // region NESTED TYPES

    open class ViewHolder(itemView: View, adapter: TicketListAdapter) :
        RecyclerView.ViewHolder(itemView)
    {
        private val b = ItemTicketBinding.bind(itemView)

        init
        {
            initEvents(adapter)
        }

        fun bind(ticket: Ticket)
        {
            b.tvClientName.text = ticket.clientName
            b.tvAddress.text = ticket.address

            // region CALCULATE SCHEDULE AND TODAY DATES

            val scheduleDate: Date
            val todayDate: Date

            // schedule
            val c: Calendar = Calendar.getInstance()
            c.timeInMillis = ticket.scheduleTime
            removeTime(c)
            scheduleDate = c.time

            // today
            c.timeInMillis = System.currentTimeMillis()
            removeTime(c)
            todayDate = c.time

            // set schedule date to text
            b.tvScheduleDate.text = WorkTicketActivity.DATE_FORMAT.format(scheduleDate)

            // endregion

            // set color state. green for normal, red for due
            var colorId: Int = R.color.ticket_normal
            if (scheduleDate.before(todayDate)) colorId = R.color.ticket_due

            b.tvScheduleDate.setBackgroundColor(
                ContextCompat.getColor(b.tvScheduleDate.context, colorId)
            )
        }

        private fun removeTime(c: Calendar)
        {
            c[Calendar.HOUR_OF_DAY] = 0
            c[Calendar.MINUTE] = 0
            c[Calendar.SECOND] = 0
            c[Calendar.MILLISECOND] = 0
        }

        private fun initEvents(adapter: TicketListAdapter)
        {
            // on ticket click event
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION)
                    adapter.ticketEvents.onTicketClick(position)
            }

            // btn options
            b.btnOptions.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION)
                {
                    val event: OnTicketEvents = adapter.ticketEvents

                    val itemClickListener: PopupMenu.OnMenuItemClickListener =
                        PopupMenu.OnMenuItemClickListener { item ->
                            when (item.itemId)
                            {
                                R.id.action_edit ->
                                {
                                    event.onTicketEdit(position)
                                    return@OnMenuItemClickListener true
                                }
                                R.id.action_delete ->
                                {
                                    event.onTicketDelete(position)
                                    return@OnMenuItemClickListener true
                                }
                            }

                            false
                        }

                    // show the options menu
                    PopupMenuHelper.show(
                        itemView.context,
                        b.btnOptions,
                        R.menu.ticket_options_popup,
                        itemClickListener
                    )
                }
            }
        }
    }

    class Comparator : DiffUtil.ItemCallback<Ticket>()
    {
        override fun areItemsTheSame(oldItem: Ticket, newItem: Ticket): Boolean
        {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Ticket, newItem: Ticket): Boolean
        {
            return oldItem.id == newItem.id
        }
    }

    // endregion
}