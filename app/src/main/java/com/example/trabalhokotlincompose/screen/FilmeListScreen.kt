package com.example.trabalhokotlincompose.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.navigation.NavHostController
import com.example.trabalhokotlincompose.data.Filme
import com.example.trabalhokotlincompose.data.FilmeDataBase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun FilmeListScreen(navController: NavHostController) {

    val corroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val db = FilmeDataBase.getDatabase(context)

    val filmeLiveData: LiveData<Filme> = db.filmeDao().listarFilmes()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Meus Filmes", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(16.dp))

        FilmeInputField { filmeNome ->
            corroutineScope.launch {
                db.filmeDao().addFilme(Filme(0, filmeNome))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        FilmeList(filme, corroutineScope, db, navController)
    }
}


@Composable
fun FilmeList(filmes: List<Filme>, corroutineScope: CoroutineScope, db: FilmeDataBase, navController: NavHostController) {
    LazyColumn {
        items(filmes) { filme ->
            FilmeItem(
                filme = filme,
                onCheckedChange = { isChecked ->
                    corroutineScope.launch {
                        db.filmeDao().atualizarFilme(filme.copy(isCompleted = isChecked))
                    }
                },
                onDelete = {
                    corroutineScope.launch {
                        db.filmeDao().deletarFilme(filme)
                    }
                },
                onEditClick = {
                    navController.navigate("filmeDetail/${filme.id}")
                }
            )
        }
    }
}

@Composable
fun FilmeItem(filme: Filme, onCheckedChange: (Boolean) -> Unit, onDelete: () -> Unit,  onEditClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = filme.assistido,
            onCheckedChange = { onCheckedChange(it) }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = filme.nome,
            style = MaterialTheme.typography.bodySmall,
            textDecoration = if (filme.assistido) TextDecoration.LineThrough else null
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = onEditClick) {
            Icon(imageVector = Icons.Default.Build, contentDescription = "Edit Filme")
        }

        IconButton(onClick = onDelete) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Delete Filme")
        }

    }
}

@Composable
fun FilmeInputField(onAddTask: (String) -> Unit) {
    var newFilme by remember { mutableStateOf("") }
    Column {
        TextField(
            value = newFilme,
            onValueChange = { newFilme = it },
            placeholder = { Text("Digite o filme") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                if (newFilme.isNotBlank()) {
                    onAddTask(newFilme)
                    newFilme = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Adicionar")
        }
    }
}
