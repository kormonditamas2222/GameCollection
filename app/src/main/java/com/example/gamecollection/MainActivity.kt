package com.example.gamecollection

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class VideoGame(
    val id: Int,
    val title: String,
    val platform: String,
    val rating: Int
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme() {
                GameCollectionScreen()
            }
        }
    }
}

@Composable
fun GameCollectionScreen() {
    val context = LocalContext.current

    var nextId: Int by remember {
        mutableIntStateOf(1)
    }
    var title: String by remember {
        mutableStateOf("")
    }
    var titleError: Boolean by remember {
        mutableStateOf(false)
    }
    var platform: String by remember {
        mutableStateOf("")
    }
    var platformError: Boolean by remember {
        mutableStateOf(false)
    }
    var rating: Int by remember {
        mutableStateOf(1)
    }
    var ratingError: Boolean by remember {
        mutableStateOf(false)
    }
    var searchQuery: String by remember {
        mutableStateOf("")
    }
    val games = remember {
        mutableStateListOf<VideoGame>()
    }

    val isSearching = searchQuery.isNotBlank()
    val searchFilter = searchQuery.trim()
    val filteredGames = games.filter { game ->
        game.title.contains(searchFilter, ignoreCase = true) ||
                game.platform.contains(searchFilter, ignoreCase = true)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp), verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Játékgyűjtemény",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Text("Kedvenc játékaid egy helyen")
        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
                titleError = false
            },
            label = {Text("Játék címe")},
            supportingText = {
                if (titleError) {
                    Text("A cím megadása kötelező", color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = platform,
            onValueChange = {
                platform = it
                platformError = false
            },
            label = {Text("Platform")},
            supportingText = {
                if (platformError) {
                    Text("A platform megadása kötelező", color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = rating.toString(),
            onValueChange = {
                rating = it.toIntOrNull() ?: 0
                if (rating in 1..10) {
                    ratingError = false
                } else {
                    ratingError = true
                }
            },
            label = {Text("Értékelés (1-10)")},
            supportingText = {
                if (ratingError) {
                    Text("Az értékelés 1 és 10 között lehet!", color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Button(
            onClick = {
                titleError = title.isBlank()
                platformError = platform.isBlank()
                ratingError = rating !in 1..10

                if (titleError || platformError || ratingError) {
                    Toast.makeText(context, "Minden mező kitöltése kötelező!", Toast.LENGTH_SHORT).show()
                }
                else {
                    val newVideoGame = VideoGame(
                        id = nextId,
                        title = title.trim(),
                        platform = platform.trim(),
                        rating = rating
                    )
                    games.add(newVideoGame)
                    nextId++
                    Toast.makeText(context, "Sikeresen hozzáadtad ${newVideoGame.title} játékot", Toast.LENGTH_SHORT).show()
                }
                title = ""
                platform = ""
                rating = 1
            }, modifier = Modifier.fillMaxWidth()
        ) {
            Text("Hozzáadás")
        }
        HorizontalDivider()
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Keresés") },
            placeholder = { Text("Keresés címre vagy platformra...") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Text("✕")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        if (games.isEmpty()) {
            Text("Játékok száma: 0", fontWeight = FontWeight.Bold)
            Text("Még nincs játék a gyűjteményben.")
        } else {
            if (isSearching) {
                Text("Találatok: ${filteredGames.size}", fontWeight = FontWeight.Bold)
            } else {
                Text("Játékok száma: ${games.size}", fontWeight = FontWeight.Bold)
            }

            val displayList = if (isSearching) filteredGames else games
            if (displayList.isEmpty()) {
                Text("Nincs a keresésnek megfelelő játék.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = displayList,
                        key = { game -> game.id }
                    ) { game ->
                        GameCard(
                            game = game,
                            onDelete = {
                                games.remove(game)
                            },
                            onEdit = { editedGame ->
                                val index = games.indexOfFirst { it.id == game.id }
                                if (index != -1) {
                                    games[index] = editedGame
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GameCard(game: VideoGame, onDelete: () -> Unit, onEdit: (VideoGame) -> Unit) {
    val context = LocalContext.current
    var showDeleteDialog: Boolean by remember {
        mutableStateOf(false)
    }
    var showEditDialog: Boolean by remember {
        mutableStateOf(false)
    }

    var editTitle: String by remember {
        mutableStateOf(game.title)
    }
    var editPlatform: String by remember {
        mutableStateOf(game.platform)
    }
    var editRating: Int by remember {
        mutableStateOf(game.rating)
    }
    var editTitleError: Boolean by remember {
        mutableStateOf(false)
    }
    var editPlatformError: Boolean by remember {
        mutableStateOf(false)
    }
    var editRatingError: Boolean by remember {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            GameAvatar(game.title)
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = game.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = game.platform, style = MaterialTheme.typography.bodyMedium)
                Text(text = "Értékelés: ${game.rating} / 10", style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(
                onClick = {showDeleteDialog = true}
            ) {
                Icon(painter = painterResource(id = R.drawable.delete_icon), contentDescription = "Játék törlése")
            }
            IconButton(
                onClick = {
                    showEditDialog = true
                    editTitle = game.title
                    editPlatform = game.platform
                    editRating = game.rating
                    editTitleError = false
                    editPlatformError = false
                    editRatingError = false
                }
            ) {
                Icon(painter = painterResource(id = R.drawable.edit_icon), contentDescription = "Játék szerkesztése")
            }
        }
    }
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
            },
            title = {Text("Játék törlése")},
            text = {Text("Biztosan törli a ${game.title} játékot?")},
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onDelete()
                }) {
                    Text("Törlés")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                }) {
                    Text("Mégsem")
                }
            }
        )
    }
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = {
                showEditDialog = false
            },
            title = {Text("Játék szerkesztése")},
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = {
                            editTitle = it
                            editTitleError = false
                        },
                        label = {Text("Játék címe")},
                        supportingText = {
                            if (editTitleError) {
                                Text("A cím megadása kötelező", color = MaterialTheme.colorScheme.error)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = editPlatform,
                        onValueChange = {
                            editPlatform = it
                            editPlatformError = false
                        },
                        label = {Text("Platform")},
                        supportingText = {
                            if (editPlatformError) {
                                Text("A platform megadása kötelező", color = MaterialTheme.colorScheme.error)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = editRating.toString(),
                        onValueChange = {
                            editRating = it.toIntOrNull() ?: 0
                            if (editRating in 1..10) {
                                editRatingError = false
                            } else {
                                editRatingError = true
                            }
                        },
                        label = {Text("Értékelés (1-10)")},
                        supportingText = {
                            if (editRatingError) {
                                Text("Az értékelés 1 és 10 között lehet!", color = MaterialTheme.colorScheme.error)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    editTitleError = editTitle.isBlank()
                    editPlatformError = editPlatform.isBlank()
                    editRatingError = editRating !in 1..10
                    if (editTitleError || editPlatformError || editRatingError) {
                        Toast.makeText(context, "Minden mező kitöltése kötelező!", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        showEditDialog = false
                        onEdit(
                            game.copy(
                                title = editTitle.trim(),
                                platform = editPlatform.trim(),
                                rating = editRating
                            )
                        )
                        Toast.makeText(
                            context,
                            "Játék adatai sikeresen frissítve",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }) { Text("Mentés") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showEditDialog = false
                }) {Text("Mégsem")}
            }
        )
    }
}

@Composable
fun GameAvatar(title: String) {
    val initials = title.take(2).uppercase()
    Box(modifier = Modifier.size(52.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary),
        Alignment.Center) {
        Text(initials, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
    }
}