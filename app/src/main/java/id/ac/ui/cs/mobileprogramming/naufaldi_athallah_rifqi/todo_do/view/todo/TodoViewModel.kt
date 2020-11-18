package id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.view.todo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.data.models.local.TodoLocal
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.data.repositories.TodoRepository

class TodoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TodoRepository = TodoRepository(application)
    private val allTodoLocalList: LiveData<List<TodoLocal>> = repository.getAllTodoList()

    fun addTodo(todoLocal: TodoLocal) {
        repository.addTodo(todoLocal)
    }

    fun updateTodo(todoLocal: TodoLocal){
        repository.updateTodo(todoLocal)
    }

    fun deleteTodo(todoLocal: TodoLocal) {
        repository.deleteTodo(todoLocal)
    }

    fun getAllTodoList(): LiveData<List<TodoLocal>> {
        return allTodoLocalList
    }

}