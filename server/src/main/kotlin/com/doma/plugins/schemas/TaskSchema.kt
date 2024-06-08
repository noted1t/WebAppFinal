package com.doma.plugins.schemas

import Task
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class TaskService(private val database: Database) {
    object Tasks : Table() {
        val id = integer("id").autoIncrement()
        val title = largeText("title")
        val description = largeText("description")
        val isCompleted = bool("isCompleted")

        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Tasks)
        }
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun create(task: Task): Int = dbQuery {
        Tasks.insert {
            it[title] = task.title
            it[description] = task.description
            it[isCompleted] = task.isCompleted
        }[Tasks.id]
    }

    suspend fun readAll(): List<Task> {
        return dbQuery {
            Tasks.selectAll().map { Task(it[Tasks.id], it[Tasks.title], it[Tasks.description], it[Tasks.isCompleted]) }
        }
    }

    suspend fun readById(id: Int): Task? {
        return dbQuery {
            Tasks.selectAll().where { Tasks.id eq id }
                .map { Task(it[Tasks.id], it[Tasks.title], it[Tasks.description], it[Tasks.isCompleted]) }
                .singleOrNull()
        }
    }

    suspend fun removeById(id: Int) {
        return dbQuery {
            Tasks.deleteWhere { Tasks.id eq id }
        }
    }

    suspend fun updateById(id: Int, task: Task) {
        return dbQuery {
            Tasks.update(where = { Tasks.id eq id }) {
                it[title] = task.title
                it[description] = task.description
                it[isCompleted] = task.isCompleted
            }
        }
    }
}