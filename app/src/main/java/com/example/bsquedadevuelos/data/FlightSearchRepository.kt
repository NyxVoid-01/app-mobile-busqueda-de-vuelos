package com.example.bsquedadevuelos.data

import kotlinx.coroutines.flow.Flow

interface FlightSearchRepository {
    fun getAutocompleteSuggestions(search: String): Flow<List<Airport>>
    fun getAirportByIata(iataCode: String): Flow<Airport>
    fun getAllDestinations(departureIata: String): Flow<List<Airport>>
    fun getAllFavorites(): Flow<List<Favorite>>
    fun getFavorite(departureCode: String, destinationCode: String): Flow<Favorite?>
    suspend fun insertFavorite(favorite: Favorite)
    suspend fun deleteFavorite(favorite: Favorite)
}

class OfflineFlightSearchRepository(private val flightSearchDao: FlightSearchDao) : FlightSearchRepository {
    override fun getAutocompleteSuggestions(search: String): Flow<List<Airport>> = 
        flightSearchDao.getAutocompleteSuggestions(search)

    override fun getAirportByIata(iataCode: String): Flow<Airport> = 
        flightSearchDao.getAirportByIata(iataCode)

    override fun getAllDestinations(departureIata: String): Flow<List<Airport>> = 
        flightSearchDao.getAllDestinations(departureIata)

    override fun getAllFavorites(): Flow<List<Favorite>> = 
        flightSearchDao.getAllFavorites()

    override fun getFavorite(departureCode: String, destinationCode: String): Flow<Favorite?> = 
        flightSearchDao.getFavorite(departureCode, destinationCode)

    override suspend fun insertFavorite(favorite: Favorite) {
        flightSearchDao.insertFavorite(favorite)
    }

    override suspend fun deleteFavorite(favorite: Favorite) {
        flightSearchDao.deleteFavorite(favorite)
    }
}
