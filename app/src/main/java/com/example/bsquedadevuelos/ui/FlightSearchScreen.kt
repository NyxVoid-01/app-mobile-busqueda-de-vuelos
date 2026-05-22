package com.example.bsquedadevuelos.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bsquedadevuelos.data.Airport

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightSearchApp(
    viewModel: FlightSearchViewModel = viewModel(factory = FlightSearchViewModel.Factory)
) {
    val uiState = viewModel.uiState

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Flight Search") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.searchText,
                onValueChange = { viewModel.updateSearchText(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter airport name or IATA code") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                uiState.searchText.isBlank() -> {
                    Text(
                        text = "Favorite Routes",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    FavoriteList(
                        favorites = uiState.favoriteFlights,
                        onToggleFavorite = { dep, dest -> viewModel.toggleFavorite(dep, dest) }
                    )
                }
                uiState.selectedAirport != null -> {
                    Text(
                        text = "Flights from ${uiState.selectedAirport.iataCode}",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    FlightList(
                        flights = uiState.flights,
                        onToggleFavorite = { dep, dest -> viewModel.toggleFavorite(dep, dest) }
                    )
                }
                else -> {
                    AirportSuggestions(
                        suggestions = uiState.suggestions,
                        onAirportClick = { viewModel.onAirportSelected(it) }
                    )
                }
            }
        }
    }
}

@Composable
fun AirportSuggestions(
    suggestions: List<Airport>,
    onAirportClick: (Airport) -> Unit
) {
    LazyColumn {
        items(suggestions) { airport ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAirportClick(airport) }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = airport.iataCode,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(60.dp)
                )
                Text(text = airport.name)
            }
        }
    }
}

@Composable
fun FlightList(
    flights: List<FlightRow>,
    onToggleFavorite: (String, String) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(flights) { flight ->
            FlightCard(
                departureCode = flight.departureAirport.iataCode,
                departureName = flight.departureAirport.name,
                destinationCode = flight.destinationAirport.iataCode,
                destinationName = flight.destinationAirport.name,
                isFavorite = flight.isFavorite,
                onToggleFavorite = onToggleFavorite
            )
        }
    }
}

@Composable
fun FavoriteList(
    favorites: List<FlightRow>,
    onToggleFavorite: (String, String) -> Unit
) {
    if (favorites.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No favorite routes yet")
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(favorites) { favorite ->
                FlightCard(
                    departureCode = favorite.departureAirport.iataCode,
                    departureName = favorite.departureAirport.name,
                    destinationCode = favorite.destinationAirport.iataCode,
                    destinationName = favorite.destinationAirport.name,
                    isFavorite = true,
                    onToggleFavorite = onToggleFavorite
                )
            }
        }
    }
}

@Composable
fun FlightCard(
    departureCode: String,
    departureName: String,
    destinationCode: String,
    destinationName: String,
    isFavorite: Boolean,
    onToggleFavorite: (String, String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "DEPART", style = MaterialTheme.typography.labelSmall)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = departureCode, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = departureName, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "ARRIVE", style = MaterialTheme.typography.labelSmall)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = destinationCode, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = destinationName, fontSize = 12.sp)
                }
            }
            IconButton(onClick = { onToggleFavorite(departureCode, destinationCode) }) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) Color(0xFFFFD700) else Color.Gray
                )
            }
        }
    }
}
