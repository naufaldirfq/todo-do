package id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.view.todo

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.R
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.data.models.local.TodoLocal
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.databinding.ItemTodoBinding

class TodoLocalAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {



    private var listOfTodoLocal = ArrayList<TodoLocal>()
    private var listener: TodoLocalClickEvent? = null

    fun setTodoList(listOfTodoLocal: List<TodoLocal>) {
        this.listOfTodoLocal.clear()
        this.listOfTodoLocal.addAll(listOfTodoLocal)
        notifyDataSetChanged()
    }

    fun addTodoList(listOfTodoLocal: List<TodoLocal>) {
        this.listOfTodoLocal.addAll(listOfTodoLocal)
        notifyDataSetChanged()
    }

    fun addTodo(todo: TodoLocal) {
        listOfTodoLocal.add(todo)
        notifyDataSetChanged()
    }

    fun updateTodo(todo: TodoLocal) {
        listOfTodoLocal.clear()
        listOfTodoLocal.addAll(listOfTodoLocal)
        notifyDataSetChanged()
    }

    fun deleteTodo(todo: TodoLocal) {
        listOfTodoLocal.remove(todo)
        notifyDataSetChanged()
    }

    fun deleteAllTodo() {
        listOfTodoLocal.clear()
        Log.d("SIZE AFTER DELETE ALL", listOfTodoLocal.size.toString())
        notifyDataSetChanged()
    }

    fun getTodoList(): ArrayList<TodoLocal> {
        return listOfTodoLocal
    }

    fun getIncompleteTodoList(): ArrayList<TodoLocal> {
        val incompleteList = ArrayList<TodoLocal>()
        listOfTodoLocal.forEach {
            if(!it.isCompleted) incompleteList.add(it)
        }

        return incompleteList
    }

    fun getCompletedTodoList(): ArrayList<TodoLocal> {
        val completedList = ArrayList<TodoLocal>()
        listOfTodoLocal.forEach {
            if(it.isCompleted) completedList.add(it)
        }

        return completedList
    }

    fun setListener(listener: TodoLocalClickEvent) {
        this.listener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = DataBindingUtil.inflate<ItemTodoBinding>(
            LayoutInflater.from(parent.context), R.layout.item_todo, parent, false
        )

        return TodoHolder(binding)
    }

    override fun getItemCount(): Int = listOfTodoLocal.size

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val holder = viewHolder as TodoHolder

        holder.bindLocal(listOfTodoLocal[position], listener)
    }

}