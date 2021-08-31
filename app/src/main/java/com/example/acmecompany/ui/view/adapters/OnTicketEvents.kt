package com.example.acmecompany.ui.view.adapters


interface OnTicketEvents
{
    fun onTicketClick(position: Int)
    fun onTicketEdit(position: Int)
    fun onTicketDelete(position: Int)
}