package org.feup.cmov.acmeclient.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.feup.cmov.acmeclient.data.model.User

@Dao
interface UserDao {
    @Insert(onConflict = REPLACE)
    suspend fun insert(user: User)

    @Query("SELECT * FROM user WHERE username = :username")
    fun get(username: String) : Flow<User?>
}