package com.example.repository

import com.example.data.model.User
import com.example.data.table.TaskTable
import com.example.data.table.UserTable
import com.example.data.table.UserTable.email
import com.example.data.table.UserTable.hashPassword
import com.example.data.table.UserTable.name
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class UserRepository {

    suspend fun addUser(user: User) {
        DatabaseFactory.dbQuery {
            UserTable.insert {
                it[email] = user.email
                it[hashPassword] = user.hashPassword
                it[name] = user.userName
            }
        }
    }

    suspend fun updateProfileImage(url: String,email: String) {
        DatabaseFactory.dbQuery {
            UserTable.update(where = {
                UserTable.email.eq(email)
            }) {
                it[profileImageUrl] = url
            }
        }
    }

    suspend fun findUserByEmail(email: String) = DatabaseFactory.dbQuery {
        UserTable.select { UserTable.email.eq(email) }
            .map { rowToUser(it) }
            .singleOrNull()
    }

    private fun rowToUser(row: ResultRow?): User? {
        if (row == null) {
            return null
        }

        return User(
            email = row[email],
            hashPassword = row[hashPassword],
            userName = row[name]
        )
    }
}