package com.example.acmecompany.data.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ticket")
data class Ticket(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id: Int = 0,
    @ColumnInfo(name = "client_name") var clientName: String = "",
    @ColumnInfo(name = "address") var address: String = "",
    @ColumnInfo(name = "phone") var phone: String = "",
    @ColumnInfo(name = "schedule_time") var scheduleTime: Long = 0,
    @ColumnInfo(name = "reason_for_call") var reasonForCall: String = ""
)
