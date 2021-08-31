package com.example.acmecompany.data.room

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.acmecompany.data.room.dao.TicketDao
import com.example.acmecompany.data.room.entities.Ticket
import kotlinx.coroutines.CoroutineScope


@Database(entities = [Ticket::class], version = 1, exportSchema = false)
abstract class AcmeDatabase : RoomDatabase()
{
    abstract fun tickedDao(): TicketDao

    private class DatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback()
    {
        override fun onCreate(db: SupportSQLiteDatabase)
        {
            super.onCreate(db)
//            Log.e("DatabaseCallback", "onCreate()")
        }

        override fun onOpen(db: SupportSQLiteDatabase)
        {
            super.onOpen(db)
//            Log.e("DatabaseCallback", "onOpen()")
        }
    }

    companion object
    {
        // singleton prevents multiple instances of database opening at the same time.
        @Volatile
        private var INSTANCE: AcmeDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AcmeDatabase
        {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE
                ?: synchronized(this)
                {
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AcmeDatabase::class.java,
                        "acme"
                    )
                        .addCallback(DatabaseCallback(scope))
                        .allowMainThreadQueries()
                        .build()

                    INSTANCE = instance

                    // return instance
                    instance
                }
        }
    }
}