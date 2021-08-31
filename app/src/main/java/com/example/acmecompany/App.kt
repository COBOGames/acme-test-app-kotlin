package com.example.acmecompany

import android.app.Application
import android.content.Context
import com.example.acmecompany.data.room.AcmeDatabase
import com.example.acmecompany.data.room.repo.TicketRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob


class App : Application()
{

    // No need to cancel this scope as it'll be torn down with the process
    val applicationScope = CoroutineScope(SupervisorJob())

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { AcmeDatabase.getDatabase(this, applicationScope) }
    val repository by lazy {
        TicketRepository(database.tickedDao())
    }

    override fun onCreate()
    {
        super.onCreate()
        context = applicationContext
    }

    companion object
    {
        lateinit var context: Context
    }
}