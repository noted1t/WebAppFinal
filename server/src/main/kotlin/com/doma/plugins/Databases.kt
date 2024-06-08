package com.doma.plugins

import LoginObject
import Message
import Task
import User
import com.doma.plugins.schemas.AdminService
import com.doma.plugins.schemas.TaskService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Database
import java.util.*

fun Application.configureDatabases() {
    val database = Database.connect(
        url = "jdbc:h2:file:./database.db;DB_CLOSE_DELAY=-1",
        user = "root",
        driver = "org.h2.Driver",
        password = ""
    )
    val taskService = TaskService(database)
    routing {
        get("/tasks") {
            val list = taskService.readAll()
            call.respond(HttpStatusCode.OK, list)
        }
        get("/tasks/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest)
            }
            val task = taskService.readById(id !!)
            if (task == null) {
                call.respond(HttpStatusCode.NotFound)
            }
            call.respond(HttpStatusCode.OK, task !!)
        }
        post("/tasks/add") {
            //if (! call.request.headers.contains("login_key") || tokenList.any { it.token == call.request.headers["login_key"] }) {
             //   call.respond(HttpStatusCode.Unauthorized)
          //  }
            val task = call.receive<Task>()
            val createdId = taskService.create(task)
            call.respond(HttpStatusCode.OK, createdId)
        }
        put("/tasks/{id}") {
         //   if (! call.request.headers.contains("login_key") || tokenList.any { it.token == call.request.headers["login_key"] }) {
           //     call.respond(HttpStatusCode.Unauthorized)
          //  }
            val task = call.receive<Task>()
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest)
            }
            taskService.updateById(id = id !!, task = task)
            call.respond(HttpStatusCode.OK)
        }
        delete("/tasks/{id}") {
         //   if (! call.request.headers.contains("login_key") || tokenList.any { it.token == call.request.headers["login_key"] }) {
           //     call.respond(HttpStatusCode.Unauthorized)
          //  }
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest)
            }
            taskService.removeById(id !!)
            call.respond(HttpStatusCode.OK)
        }
    }
    val adminService = AdminService(database)
    routing {
        post("/login/logtosystem") {
            println("Зашли")
            val userSession = call.request.headers
            if (userSession.contains("login_key") && tokenList.any { it.token == userSession["login_key"] }) {
                call.respondText("You already logged in")
            } else {
                println("Собираем объект")
                val login = call.receive<LoginObject>()

                val isCorrect = adminService.findByLoginAndPassword(login.login, login.password)
                if (! isCorrect) {
                    println("Не найдены")
                    call.respond(HttpStatusCode.NotFound, Message("Логин или пароль не найдены"))
                }

                val id = adminService.getUserIdByLogin(login.login)
                val session = AdminSession(id = id !!, token = generateSessionCode())
                tokenList.add(session)
                call.respond(HttpStatusCode.OK, Message(session.token))
            }
        }

        get("/login/getId/{token}") {
            val token = call.parameters["token"] ?: throw IllegalArgumentException("Invalid token")
            val id = tokenList.firstOrNull { it.token == token }
            if (id == null) {
                call.respond(HttpStatusCode.NotFound, Message("id к данному токену не найден найдены"))
            } else {
                call.respond(HttpStatusCode.OK, Message(id.toString()))
            }
        }

        post("/login/add") {
            val user = call.receive<LoginObject>()
            val id = adminService.create(User(user.login, user.login, user.password))
            call.respond(HttpStatusCode.OK, id)
        }

        get("/login/getAll/{adminpass}") {
            val adminpass = call.parameters["adminpass"] ?: throw IllegalArgumentException("Invalid admin pass")
            val validPass = "centur"
            if (adminpass == validPass) {
                call.respond(HttpStatusCode.OK, adminService.readAll())
            }
            else {
                call.respond(HttpStatusCode.BadRequest, Message("Wrong password"))
            }
        }

        get("/login/getAll/{adminpass}/sessions") {
            val adminpass = call.parameters["adminpass"] ?: throw IllegalArgumentException("Invalid admin pass")
            val validPass = "centur"
            if (adminpass == validPass) {
                call.respond(HttpStatusCode.OK, tokenList)
            }
            else {
                call.respond(HttpStatusCode.BadRequest, Message("Wrong password"))
            }
        }
    }
}

fun generateSessionCode(): String {
    val characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    val random = Random()
    val sb = StringBuilder()
    for (i in 0 until 20) {
        sb.append(characters[random.nextInt(characters.length)])
    }
    return sb.toString()
}