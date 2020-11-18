package id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.data.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.data.models.local.TodoLocal
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.data.models.local.TodoDao
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.data.models.local.TodoDatabase
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.utils.firebase.callback.TodoCallback
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
}