package com.example.acmecompany.ui.viewmodel

import androidx.lifecycle.*
import com.example.acmecompany.data.room.entities.Ticket
import com.example.acmecompany.data.room.repo.TicketRepository
import kotlinx.coroutines.launch


class TicketViewModel(private val repository: TicketRepository) : ViewModel()
{
    val allTickets: LiveData<List<Ticket>> = repository.allTickets().asLiveData()
    val isLoading = MutableLiveData<Boolean>(false)

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(ticket: Ticket) = viewModelScope.launch {
        isLoading.postValue(true)
        repository.insert(ticket)
        isLoading.postValue(false)
    }

    fun update(ticket: Ticket) = viewModelScope.launch {
        isLoading.postValue(true)
        repository.update(ticket)
        isLoading.postValue(false)
    }

    fun delete(ticket: Ticket) = viewModelScope.launch {
        isLoading.postValue(true)
        repository.delete(ticket)
        isLoading.postValue(false)
    }

    suspend fun findById(id: Int): Ticket
    {
        isLoading.postValue(true)
        val ticket = repository.findById(id)
        isLoading.postValue(false)

        return ticket
    }
}

class TicketViewModelFactory(private val repository: TicketRepository) : ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T
    {
        if (modelClass.isAssignableFrom(TicketViewModel::class.java))
        {
            @Suppress("UNCHECKED_CAST")
            return TicketViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}