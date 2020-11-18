package id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.data.models.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(UserLocal::class), version = 1)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        var INSTANCE: UserDatabase? = null

        fun getInstance(context: Context): UserDatabase? {
            if (INSTANCE == null) {
                synchronized(UserDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context,
                        UserDatabase::class.java,
                        "user_db")
                        .build()
                }
            }
            return INSTANCE
        }

    }
}