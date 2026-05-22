package com.example.bsquedadevuelos.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bsquedadevuelos.FlightSearchApplication
import com.example.bsquedadevuelos.data.Airport
import com.example.bsquedadevuelos.data.Favorite
import com.example.bsquedadevuelos.data.FlightSearchRepository
import com.example.bsquedadevuelos.data.UserPreferencesRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class FlightSearchUiState(
    val searchText: String = "",
    val suggestions: List<Airport> = emptyList(),
    val selectedAirport: Airport? = null,
    val flights: List<FlightRow> = emptyList(),
    val favoriteFlights: List<FlightRow> = emptyList()
)

data class FlightRow(
    val departureAirport: Airport,
    val destinationAirport: Airport,
    val isFavorite: Boolean
)

class FlightSearchViewModel(
    private val flightSearchRepository: FlightSearchRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    var uiState by mutableStateOf(FlightSearchUiState())
        private set

    init {
        // 1. Escuchar cambios en el texto de búsqueda guardado
        viewModelScope.launch {
            userPreferencesRepository.searchText.take(1).collect { savedText ->
                if (savedText.isNotBlank()) {
                    updateSearchText(savedText)
                }
            }
        }

        // 2. Escuchar cambios en los favoritos y mapearlos con nombres de aeropuertos
        viewModelScope.launch {
            flightSearchRepository.getAllFavorites().collect { favorites ->
                val favoriteRows = favorites.map { favorite ->
                    val departure = flightSearchRepository.getAirportByIata(favorite.departureCode).first()
                    val destination = flightSearchRepository.getAirportByIata(favorite.destinationCode).first()
                    FlightRow(departure, destination, true)
                }
                uiState = uiState.copy(favoriteFlights = favoriteRows)
                
                // Refrescar vuelos actuales si hay selección para actualizar iconos de estrellas
                uiState.selectedAirport?.let { refreshFlights(it) }
            }
        }
    }

    fun updateSearchText(text: String) {
        uiState = uiState.copy(searchText = text, selectedAirport = null)
        viewModelScope.launch {
            userPreferencesRepository.saveSearchText(text)
        }

        if (text.isNotBlank()) {
            viewModelScope.launch {
                flightSearchRepository.getAutocompleteSuggestions(text).collect { list ->
                    uiState = uiState.copy(suggestions = list)
                }
            }
        } else {
            uiState = uiState.copy(suggestions = emptyList())
        }
    }

    fun onAirportSelected(airport: Airport) {
        uiState = uiState.copy(selectedAirport = airport, suggestions = emptyList())
        refreshFlights(airport)
    }

    private fun refreshFlights(departureAirport: Airport) {
        viewModelScope.launch {
            flightSearchRepository.getAllDestinations(departureAirport.iataCode).collect { destinations ->
                val flightRows = destinations.map { destination ->
                    FlightRow(
                        departureAirport = departureAirport,
                        destinationAirport = destination,
                        isFavorite = uiState.favoriteFlights.any { 
                            it.departureAirport.iataCode == departureAirport.iataCode && 
                            it.destinationAirport.iataCode == destination.iataCode 
                        }
                    )
                }
                uiState = uiState.copy(flights = flightRows)
            }
        }
    }

    fun toggleFavorite(departureCode: String, destinationCode: String) {
        viewModelScope.launch {
            val existingFavorite = flightSearchRepository.getFavorite(departureCode, destinationCode).firstOrNull()
            if (existingFavorite != null) {
                flightSearchRepository.deleteFavorite(existingFavorite)
            } else {
                flightSearchRepository.insertFavorite(
                    Favorite(departureCode = departureCode, destinationCode = destinationCode)
                )
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FlightSearchApplication)
                FlightSearchViewModel(
                    application.container.flightSearchRepository,
                    application.container.userPreferencesRepository
                )
            }
        }
    }
}
