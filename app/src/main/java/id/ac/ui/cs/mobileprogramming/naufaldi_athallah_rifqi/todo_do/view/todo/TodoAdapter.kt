package id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.view.todo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.R
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.data.models.Todo
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.databinding.ItemTodoBinding

class TodoAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listOfTodo = ArrayList<Todo>()
    private var listener: TodoClickEvent? = null

    fun setTodoList(listOfTodo: List<Todo>) {
        this.listOfTodo.clear()
        this.listOfTodo.addAll(listOfTodo)
        notifyDataSetChanged()
    }

    fun addTodoList(listOfTodo: List<Todo>) {
        this.listOfTodo.addAll(listOfTodo)
        notifyDataSetChanged()
    }

    fun addTodo(todo: Todo) {
        this.listOfTodo.add(todo)
        notifyDataSetChanged()
    }

    fun getTodoList(): ArrayList<Todo> {
        return listOfTodo
    }

    fun getIncompleteTodoList(): ArrayList<Todo> {
        val incompleteList = ArrayList<Todo>()
        listOfTodo.forEach {
            if(!it.completed) incompleteList.add(it)
        }

        return incompleteList
    }

    fun getCompletedTodoList(): ArrayList<Todo> {
        val completedList = ArrayList<Todo>()
        listOfTodo.forEach {
            if(it.completed) completedList.add(it)
        }

        return completedList
    }

    fun setListener(listener: TodoClickEvent) {
        this.listener = listener
    }

    fun clear() {
        listOfTodo.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = DataBindingUtil.inflate<ItemTodoBinding>(
            LayoutInflater.from(parent.context), R.layout.item_todo, parent, false
        )

        return TodoHolder(binding)
    }

    override fun getItemCount(): Int = listOfTodo.size

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val holder = viewHolder as TodoHolder

        holder.bind(listOfTodo[position], listener)
    }
}