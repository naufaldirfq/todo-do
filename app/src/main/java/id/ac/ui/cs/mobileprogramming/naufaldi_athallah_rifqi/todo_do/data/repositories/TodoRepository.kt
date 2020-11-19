package id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.data.repositories

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.data.models.local.TodoDao
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.data.models.local.TodoDatabase
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.data.models.local.TodoLocal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class TodoRepository(application: Application) {

    private val todoDao: TodoDao
    private val allTodos: LiveData<List<TodoLocal>>

    init {
        val database = TodoDatabase.getInstance(application.applicationContext)
        todoDao = database!!.todoDao()
        allTodos = todoDao.getAllTodoList()
    }

    fun addTodo(todoLocal: TodoLocal) = runBlocking {
        this.launch(Dispatchers.IO) {
            todoDao.addTodo(todoLocal)
        }
    }

    fun updateTodo(todoLocal: TodoLocal) = runBlocking {
        this.launch(Dispatchers.IO) {
            Log.d("RP UPDATED TODO>", todoLocal.todo)
            Log.d("RP COMPLETED", todoLocal.isCompleted.toString())
            todoDao.updateTodo(todoLocal)
        }
    }


    fun deleteTodo(todoLocal: TodoLocal) {
        runBlocking {
            this.launch(Dispatchers.IO) {
                todoDao.deleteTodo(todoLocal)
            }
        }
    }

    fun getAllTodoList(): LiveData<List<TodoLocal>> {
        return allTodos
    }

    fun deleteAllTodoList()  = runBlocking {
        this.launch(Dispatchers.IO) {
            todoDao.deleteAllTodoList()
        }
    }
}