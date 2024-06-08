package com.doma.plugins.schemas

import User
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class AdminService(private val database: Database) {
    object Users : Table() {
        val id = integer("id").autoIncrement()
        val username = varchar("username", length = 50)
        val login = varchar("login", length = 50)
        val password = varchar("password", length = 50)

        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Users)
        }
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun create(user: User): Int = dbQuery {
        Users.insert {
            it[username] = user.username
            it[login] = user.login
            it[password] = user.password
        }[Users.id]
    }

    suspend fun read(id: Int): User? {
        return dbQuery {
            Users.selectAll().where { Users.id eq id }
                .map { User(it[Users.username],it[Users.login], it[Users.password]) }
                .singleOrNull()
        }
    }

    suspend fun readAll() : List<User> {
        return dbQuery {
            Users.selectAll().map { User(it[Users.login], it[Users.username], it[Users.password] ) }
        }
    }

    suspend fun update(id: Int, user: User) {
        dbQuery {
            Users.update({ Users.id eq id }) {
                it[username] = user.username
                it[login] = user.login
                it[password] = user.password
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            Users.deleteWhere { Users.id.eq(id) }
        }
    }

    suspend fun findByLoginAndPassword(login: String, password: String): Boolean = dbQuery {
        Users.selectAll().where { (Users.login eq login) and (Users.password eq password) }
            .count() > 0
    }

    suspend fun getUserIdByLogin(login: String): Int? = dbQuery {
        Users.select(Users.id).where { Users.login eq login }.limit(1).map { it[Users.id] }.singleOrNull()
    }
}

