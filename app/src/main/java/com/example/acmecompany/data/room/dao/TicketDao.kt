package com.example.acmecompany.data.room.dao

import androidx.room.*
import com.example.acmecompany.data.room.entities.Ticket
import kotlinx.coroutines.flow.Flow

@Dao
interface TicketDao
{
    @Query("SELECT * FROM ticket ORDER BY id ASC")
    fun getAllTickets(): Flow<List<Ticket>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(ticket: Ticket)

    @Delete()
    suspend fun delete(ticket: Ticket)

    @Update()
    suspend fun update(ticket: Ticket)

    @Query("SELECT * FROM ticket WHERE id=:id")
    suspend fun findById(id: Int): Ticket
}