package com.example.acmecompany.data.room.repo

import androidx.annotation.WorkerThread
import com.example.acmecompany.data.room.dao.TicketDao
import com.example.acmecompany.data.room.entities.Ticket
import kotlinx.coroutines.flow.Flow


class TicketRepository(private val ticketDao: TicketDao)
{
    @WorkerThread
    fun allTickets() = ticketDao.getAllTickets()

    @WorkerThread
    suspend fun insert(ticket: Ticket) = ticketDao.insert(ticket)

    @WorkerThread
    suspend fun delete(ticket: Ticket) = ticketDao.delete(ticket)

    @WorkerThread
    suspend fun update(ticket: Ticket) = ticketDao.update(ticket)

    @WorkerThread
    suspend fun findById(id: Int): Ticket = ticketDao.findById(id)
}