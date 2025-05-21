package com.example.smarthouse_mobile.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.*
import coil.compose.AsyncImage
import com.example.smarthouse_mobile.data.model.HomeModel
import com.example.smarthouse_mobile.data.model.UserModel
import com.example.smarthouse_mobile.data.repository.RemoteRepository
import kotlinx.coroutines.launch
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    user: UserModel,
    onHouseClicked: (HomeModel) -> Unit
) {
    var homes by remember { mutableStateOf<List<HomeModel>>(emptyList()) }
    var temperatures by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var showAddDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        homes = RemoteRepository.getAllHomes()

        val tempMap = mutableMapOf<String, String>()
        homes.forEach { home ->
            val sensors = RemoteRepository.getSensorsForHome(home.id ?: "")

            val tempSensor = sensors.find { it.unit.equals("C", ignoreCase = true) }
            val humiditySensor = sensors.find { it.unit == "%" }

            val tempValue = tempSensor?.reading ?: "N/A"
            val humidityValue = humiditySensor?.reading ?: "N/A"

            tempMap[home.id ?: ""] = "Temp: $tempValue°C, Humidity: $humidityValue%"
        }
        temperatures = tempMap
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "SmartHouse",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                actions = {
                    Text(
                        text = "Hi, ${user.name} 👋",
                        color = Color.Black,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color(0xFFFFC107)
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFFFFC107),
                contentColor = Color.Black
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Home")
            }
        },
        bottomBar = {
            BottomAppBar(containerColor = Color(0xFF1A1A1A)) {
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = {}) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        },
        containerColor = Color(0xFF0E0E0E)
    ) { padding ->
        val yellowShades = listOf(
            Color(0xFFFFF9C4),
            Color(0xFFFFF59D),
            Color(0xFFFFF176),
            Color(0xFFFFEE58)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(homes) { index, home ->
                val bgColor = yellowShades[index % yellowShades.size]
                val temperatureInfo = temperatures[home.id ?: ""] ?: "Temp: N/A°C"
                HomeCardGrid(
                    home = home,
                    bgColor = bgColor,
                    temperature = temperatureInfo,
                    onClick = { onHouseClicked(home) }
                )
            }
        }

        if (showAddDialog) {
            AddHomeDialog(
                onDismiss = { showAddDialog = false },
                onAddHome = { name ->
                    scope.launch {
                        val success = RemoteRepository.createHome(name)
                        if (success) {
                            homes = RemoteRepository.getAllHomes()
                            val tempMap = mutableMapOf<String, String>()
                            homes.forEach { home ->
                                val sensors = RemoteRepository.getSensorsForHome(home.id ?: "")
                                val tempSensor = sensors.find { it.unit.equals("C", ignoreCase = true) }
                                val humiditySensor = sensors.find { it.unit == "%" }

                                val tempValue = tempSensor?.reading ?: "N/A"
                                val humidityValue = humiditySensor?.reading ?: "N/A"

                                tempMap[home.id ?: ""] = "Temp: $tempValue°C, Humidity: $humidityValue%"
                            }
                            temperatures = tempMap
                        }
                        showAddDialog = false
                    }
                }
            )
        }
    }
}

@Composable
fun HomeCardGrid(
    home: HomeModel,
    bgColor: Color,
    temperature: String,
    onClick: () -> Unit
) {
    val imageUrl = "https://source.unsplash.com/featured/?smart,home,architecture"

    ElevatedCard(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = home.homeName.split(" ")
                    .joinToString(" ") { it.replaceFirstChar { c -> c.uppercaseChar() } },
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = home.address
                    ?.split(" ")
                    ?.joinToString(" ") { it.replaceFirstChar { c -> c.uppercaseChar() } }
                    ?: "No address",
                fontSize = 14.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = temperature,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.weight(1f))
            AsyncImage(
                model = imageUrl,
                contentDescription = "House Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHomeDialog(
    onDismiss: () -> Unit,
    onAddHome: (String) -> Unit
) {
    var homeName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Add New Home ID", fontWeight = FontWeight.Bold)
        },
        text = {
            OutlinedTextField(
                value = homeName,
                onValueChange = { homeName = it },
                label = { Text("Home Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White,
                    focusedBorderColor = Color(0xFFFFC107),
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.LightGray
                )
            )
        },
        confirmButton = {
            Button(onClick = { onAddHome(homeName) }) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        containerColor = Color(0xFF1F1F1F),
        titleContentColor = Color.White,
        textContentColor = Color.White
    )
}
