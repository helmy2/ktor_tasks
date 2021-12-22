package com.example.repository

import com.example.data.model.LocalUser
import com.example.data.table.UserTable
import com.example.data.table.UserTable.email
import com.example.data.table.UserTable.hashPassword
import com.example.data.table.UserTable.name
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update

class UserRepository {

    suspend fun addUser(user: LocalUser) {
        DatabaseFactory.dbQuery {
            UserTable.insert {
                it[email] = user.email
                it[hashPassword] = user.hashPassword
                it[name] = user.name
            }
        }
    }

    suspend fun findUserByEmail(email: String) = DatabaseFactory.dbQuery {
        UserTable.select { UserTable.email.eq(email) }
            .map { rowToUser(it) }
            .singleOrNull()
    }

    private fun rowToUser(row: ResultRow?): LocalUser? {
        if (row == null) {
            return null
        }

        return LocalUser(
            email = row[email],
            hashPassword = row[hashPassword],
            name = row[name]
        )
    }
}