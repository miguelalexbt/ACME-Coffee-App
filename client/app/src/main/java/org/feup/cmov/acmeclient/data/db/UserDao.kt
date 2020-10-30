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
    suspend fun get(username: String) : User?

//    @Query("SELECT * FROM user ")
//    fun getAll() : Flow<List<User>>

//    @Query("SELECT * FROM user WHERE id = :id")
//    fun load(id: String) : LiveData<User?>
}