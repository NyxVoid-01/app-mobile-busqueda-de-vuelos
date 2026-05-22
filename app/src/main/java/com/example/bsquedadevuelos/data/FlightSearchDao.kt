package com.example.bsquedadevuelos.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FlightSearchDao {

    @Query("SELECT * FROM airport WHERE name LIKE '%' || :search || '%' OR iata_code LIKE '%' || :search || '%' ORDER BY passengers DESC")
    fun getAutocompleteSuggestions(search: String): Flow<List<Airport>>

    @Query("SELECT * FROM airport WHERE iata_code = :iataCode")
    fun getAirportByIata(iataCode: String): Flow<Airport>

    @Query("SELECT * FROM airport WHERE iata_code != :departureIata ORDER BY passengers DESC")
    fun getAllDestinations(departureIata: String): Flow<List<Airport>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: Favorite)

    @Delete
    suspend fun deleteFavorite(favorite: Favorite)

    @Query("SELECT * FROM favorite")
    fun getAllFavorites(): Flow<List<Favorite>>
    
    @Query("SELECT * FROM airport WHERE iata_code = :iataCode")
    suspend fun getAirportByIataSingle(iataCode: String): Airport

    @Query("SELECT * FROM favorite WHERE departure_code = :departureCode AND destination_code = :destinationCode")
    fun getFavorite(departureCode: String, destinationCode: String): Flow<Favorite?>
}
