package com.ptrby.webapp.di.network

import LoginObject
import Message
import Task
import com.ptrby.webapp.di.settings.SettingsRepository
import io.github.aakira.napier.Napier
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.ContentType.Application.Json

class KtorRepository(private val httpClient: HttpClient, private val settings: SettingsRepository) {
    private val baseUrl = "http://localhost:8080"

    suspend fun login(login: LoginObject) : HttpStatusCode {
        val loginUrl = "${baseUrl}/login/logtosystem"
        val answer = httpClient.post(loginUrl) {
            url {
                accept(Json)
                contentType(Json)
                setBody(login)
            }
        }
        val id = answer.body<Message>().message
        Napier.i("id - $id")
        settings.addValue("login_key", id)
        return answer.status
    }

    suspend fun getId(token: String) : Int? {
        val url = "$baseUrl/login/getId/${token}"
        val answer = httpClient.get(url).body<Message>()
        return answer.message.toIntOrNull()
    }

    suspend fun getAllTasks() : List<Task> {
        val url = "$baseUrl/tasks"
        return httpClient.get(url).body<List<Task>>()
    }

    suspend fun getTaskById(id: Int) : Task {
        val url = "$baseUrl/tasks/$id"
        return httpClient.get(url).body<Task>()
    }

    suspend fun postTask(task: Task) : Int {
        val url = "$baseUrl/tasks/add"
        return httpClient.post(url) {
            contentType(Json)
            setBody(task)
        }.body<Int>()
    }

    suspend fun updateTask(task: Task): HttpStatusCode {
        val url = "$baseUrl/tasks/${task.id}"
        return httpClient.put(url) {
            accept(Json)
            contentType(Json)
            setBody(task)
        }.status
    }

    suspend fun deleteTask(id: Int) : HttpStatusCode {
        val url = "$baseUrl/tasks/$id"
        return httpClient.delete(url).status
    }
}