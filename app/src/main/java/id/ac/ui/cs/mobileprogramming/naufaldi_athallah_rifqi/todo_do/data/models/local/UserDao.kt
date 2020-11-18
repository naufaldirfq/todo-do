package id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.data.models.local

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UserDao {
    @Query("SELECT * FROM UserLocal")
    fun getAllUserList() : LiveData<List<UserLocal>>

    @Insert
    fun addUser(userLocal: UserLocal)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateUser(userLocal: UserLocal)

    @Delete
    fun deleteUser(userLocal: UserLocal?)

    @Query("SELECT * FROM UserLocal WHERE id==:id")
    fun getUserWithId(id:Int):UserLocal
}