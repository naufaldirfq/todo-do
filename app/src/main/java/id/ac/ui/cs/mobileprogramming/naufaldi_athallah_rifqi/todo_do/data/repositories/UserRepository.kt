package id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.data.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.data.models.local.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class UserRepository(application: Application) {

    private val userDao: UserDao
    private val allUsers: LiveData<List<UserLocal>>

    init {
        val database = UserDatabase.getInstance(application.applicationContext)
        userDao = database!!.userDao()
        allUsers = userDao.getAllUserList()
    }

    fun addUser(userLocal: UserLocal) = runBlocking {
        this.launch(Dispatchers.IO) {
            userDao.addUser(userLocal)
        }
    }

    fun updateUser(userLocal: UserLocal) = runBlocking {
        this.launch(Dispatchers.IO) {
            userDao.updateUser(userLocal)
        }
    }


    fun deleteUser(userLocal: UserLocal) {
        runBlocking {
            this.launch(Dispatchers.IO) {
                userDao.deleteUser(userLocal)
            }
        }
    }

    fun getAllUserList(): LiveData<List<UserLocal>> {
        return allUsers
    }

    fun getUserWithId(id: Int) = runBlocking {
        this.launch(Dispatchers.IO) {
            userDao.getUserWithId(id)
        }
    }
}