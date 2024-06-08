package com.ptrby.webapp.base.mainscreen

import Task
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val vm = MainViewModel()
    var expandedState by remember { mutableStateOf(false) }
    var isLoggedIn by remember { vm.isLogged }
    var isAlertOpen by remember { mutableStateOf(false) }
    var isCreateAlertOpen by remember { mutableStateOf(false) }
    val coroutine = rememberCoroutineScope()

    val list = vm.list

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Списки")
                },
                actions = {
                    IconButton(
                        onClick = {
                            expandedState = true
                        }
                    ) {
                        Icon(Icons.Default.Menu, "Открыть выпадающее меню")
                    }
                    DropdownMenu(
                        expanded = expandedState,
                        onDismissRequest = {
                            expandedState = false
                        }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    if (isLoggedIn) {
                                        "Выйти"
                                    } else {
                                        "Авторизироваться"
                                    }
                                )
                            },
                            onClick = {
                                if (isLoggedIn) {
                                    vm.unauthorization()
                                    isLoggedIn = false
                                } else {
                                    isAlertOpen = true
                                }
                                expandedState = false
                            }
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            LazyColumn {
                items(list, key = { it.id }) { task ->
                    TaskCard(
                        task = task,
                        isLoggedIn = isLoggedIn,
                        onDeleteClick = {
                            coroutine.launch { vm.delete(it) }
                        },
                        onEditClick = {
                            coroutine.launch { vm.edit(it) }
                        }
                    )
                }
                if (isLoggedIn) {
                    item("addTask") {
                        IconButton(
                            onClick = { isCreateAlertOpen = true }
                        ) {
                            Icon(Icons.Default.Add, "Add task icon")
                        }
                    }
                }
            }
        }
    }
    if (isAlertOpen) {
        var login by remember { mutableStateOf("") }
        var isError by remember { mutableStateOf(false) }
        var password by remember { mutableStateOf("") }
        AuthenticationDialog(
            login = login,
            onLoginChange = { login = it },
            password = password,
            onPasswordChange = { password = it },
            onDismiss = { isAlertOpen = false },
            onConfirm = { authLogin, authPassword ->
                coroutine.launch {
                    val status = vm.logging(authLogin, authPassword)
                    if (!status) {
                        isError = true
                    } else {
                        isAlertOpen = false
                        isLoggedIn = true
                    }
                    delay(5_000)
                    isError = false
                }
            },
            isError = isError,
            clearError = {
                isError = false
            }
        )
    }

    if (isCreateAlertOpen) {
        CreateNewTaskDialog(
            onDismissClick = { isCreateAlertOpen = false },
            onSaveClick = { coroutine.launch { vm.pushTask(it) } }
        )
    }
}

@Composable
fun TaskCard(
    task: Task,
    isLoggedIn: Boolean,
    onDeleteClick: (Int) -> Unit,
    onEditClick: (Task) -> Unit
) {
    var editOpened by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                if (task.isCompleted) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Gray
                    )
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                } else {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            if (isLoggedIn) {
                Row {
                    IconButton(onClick = {
                        editOpened = true
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = { onDeleteClick(task.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            }
        }
    }

    if (editOpened) {
        EditTaskDialog(
            task = task,
            onSaveClick = {
                onEditClick(it)
            },
            onDismissClick = {
                editOpened = false
            }
        )
    }
}

@Composable
fun EditTaskDialog(
    task: Task,
    onSaveClick: (Task) -> Unit,
    onDismissClick: () -> Unit
) {
    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description) }
    var isCompleted by remember { mutableStateOf(task.isCompleted) }

    AlertDialog(
        onDismissRequest = onDismissClick,
        title = { Text(text = "Edit Task") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") }
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") }
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Выполнено")
                    Checkbox(
                        checked = isCompleted,
                        onCheckedChange = { isCompleted = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSaveClick(
                        task.copy(
                            title = title,
                            description = description,
                            isCompleted = isCompleted
                        )
                    )
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismissClick
            ) {
                Text("Dismiss")
            }
        }
    )
}

@Composable
fun CreateNewTaskDialog(
    onDismissClick: () -> Unit,
    onSaveClick: (Task) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isCompleted by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismissClick,
        title = { Text(text = "Edit Task") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") }
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") }
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Выполнено")
                    Checkbox(
                        checked = isCompleted,
                        onCheckedChange = { isCompleted = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSaveClick(
                        Task(
                            id = 0,
                            title = title,
                            description = description,
                            isCompleted = isCompleted
                        )
                    )
                }
            ) {
                Text("Push")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismissClick
            ) {
                Text("Dismiss")
            }
        }
    )
}

@Composable
fun AuthenticationDialog(
    isError: Boolean,
    clearError: () -> Unit,
    login: String,
    onLoginChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Login") },
        text = {
            Column {
                OutlinedTextField(
                    value = login,
                    onValueChange = {
                        clearError()
                        onLoginChange(it)
                    },
                    label = { Text("Login") },
                    isError = isError
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        clearError()
                        onPasswordChange(it)
                    },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    isError = isError
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(login, password) }
            ) {
                Text("Авторизоваться")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text("Выйти")
            }
        }
    )
}