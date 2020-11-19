package id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.utils.helper

import android.content.Context
import android.content.SharedPreferences

object AppPreferences {
    private const val USER = "UserLocal"
    private const val MODE = Context.MODE_PRIVATE
    private lateinit var preferences: SharedPreferences

    //SharedPreferences variables
    private val IS_LOGIN = Pair("is_login", false)
    private val USERNAME = Pair("username", "")
    private val IMAGE = Pair("image", "")

    fun init(context: Context) {
        preferences = context.getSharedPreferences(USER, MODE)
    }

    //an inline function to put variable and save it
    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    //SharedPreferences variables getters/setters
    var isLogin: Boolean
        get() = preferences.getBoolean(IS_LOGIN.first, IS_LOGIN.second)
        set(value) = preferences.edit {
            it.putBoolean(IS_LOGIN.first, value)
        }

    var username: String
        get() = preferences.getString(USERNAME.first, USERNAME.second) ?: ""
        set(value) = preferences.edit {
            it.putString(USERNAME.first, value)
        }

    var image: String
        get() = preferences.getString(IMAGE.first, IMAGE.second) ?: ""
        set(value) = preferences.edit {
            it.putString(IMAGE.first, value)
        }

}