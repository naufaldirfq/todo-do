package id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.data.models.local

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE

@Dao
interface TodoDao {
    @Query("SELECT * FROM TodoLocal")
    fun getAllTodoList() : LiveData<List<TodoLocal>>

    @Insert
    fun addTodo(todoLocal: TodoLocal)

    @Update(onConflict = REPLACE)
    fun updateTodo(todoLocal: TodoLocal)

    @Delete
    fun deleteTodo(todoLocal: TodoLocal?)

    @Query("DELETE FROM TodoLocal")
    fun deleteAllTodoList()
}