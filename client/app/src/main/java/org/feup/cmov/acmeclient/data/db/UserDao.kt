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
    suspend fun save(user: User)

//    @Query("SELECT * FROM user WHERE id = :id")
//    fun load(id: String) : LiveData<User?>
}