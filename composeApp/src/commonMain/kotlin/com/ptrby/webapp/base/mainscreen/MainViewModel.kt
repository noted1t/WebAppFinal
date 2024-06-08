package com.ptrby.webapp.base.mainscreen

import LoginObject
import Task
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.ptrby.webapp.di.network.KtorRepository
import com.ptrby.webapp.di.settings.SettingsRepository
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainViewModel : KoinComponent {
    val isLogged = mutableStateOf(false)
    private val repo: KtorRepository by inject()
    private val settings: SettingsRepository by inject()
    val list = mutableStateListOf<Task>()
    private val coroutine = CoroutineScope(Dispatchers.Default)

    init {
        isLogged.value = !settings.getValue("login_key").isNullOrEmpty()
        coroutine.launch {
            list.addAll(repo.getAllTasks())
        }
    }

    suspend fun logging(login: String, password: String) : Boolean {
        val status = repo.login(LoginObject(login, password))
        val res = status == HttpStatusCode.OK
        isLogged.value = res
        return status == HttpStatusCode.OK
    }

    suspend fun delete(id: Int) {
        repo.deleteTask(id)
        updateTasks()
    }

    suspend fun edit(task: Task) {
        repo.updateTask(task)
        updateTasks()
    }

    suspend fun pushTask(task: Task) {
        repo.postTask(task)
        updateTasks()
    }

    suspend fun updateTasks() {
        list.clear()
        list.addAll(repo.getAllTasks())
    }
    fun unauthorization() {
        isLogged.value = false
        settings.removeValue("login_key")
    }
}
