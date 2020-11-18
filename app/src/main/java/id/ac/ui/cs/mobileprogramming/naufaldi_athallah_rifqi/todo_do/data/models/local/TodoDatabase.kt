package id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.data.models.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = arrayOf(TodoLocal::class), version = 1)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao

    companion object {
        var INSTANCE: TodoDatabase? = null

        fun getInstance(context: Context): TodoDatabase? {
            if (INSTANCE == null) {
                synchronized(TodoDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context,
                        TodoDatabase::class.java,
                        "todo_db")
                        .build()
                }
            }
            return INSTANCE
        }

    }
}