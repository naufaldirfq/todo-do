package id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.view.todo

import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.R
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.data.models.Todo
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.data.models.local.TodoLocal
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.databinding.ItemTodoBinding

class TodoHolder(private val binding: ItemTodoBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(todo: Todo, callback: TodoClickEvent?) {
        binding.tvTodo.text = todo.todo

        // if the task is complete make the text gray, change the icon, and hide the edit button
        // else make it black, show different icon, and show the edit icon
        if(todo.completed) {
            binding.container.strokeColor = Color.GRAY
            binding.tvTodo.setTextColor(Color.GRAY)
            binding.btnTodoComplete.setImageResource(R.drawable.ic_done_all_grey)
            binding.btnEdit.visibility = View.GONE
        }else {
            binding.container.strokeColor = ContextCompat.getColor(binding.root.context, R.color.colorPrimary)
            binding.tvTodo.setTextColor(Color.BLACK)
            binding.btnTodoComplete.setImageResource(R.drawable.ic_check)
            binding.btnEdit.visibility = View.VISIBLE
        }

        // toggle the task completion(complete/incomplete) with this action
        binding.btnTodoComplete.setOnClickListener {
            callback?.onClickTodo(todo, TodoClickEvent.ACTION_COMPLETE, adapterPosition)
        }

        // show the task's details
        binding.tvTodo.setOnClickListener {
            callback?.onClickTodo(todo, TodoClickEvent.ACTION_DETAILS, adapterPosition)
        }

        // edit task
        binding.btnEdit.setOnClickListener {
            callback?.onClickTodo(todo, TodoClickEvent.ACTION_EDIT, adapterPosition)
        }

        // delete the task
        binding.btnDelete.setOnClickListener {
            callback?.onClickTodo(todo, TodoClickEvent.ACTION_DELETE, adapterPosition)
        }
    }

    fun bindLocal(todo: TodoLocal, callback: TodoLocalClickEvent?) {
        binding.tvTodo.text = todo.todo

        // if the task is complete make the text gray, change the icon, and hide the edit button
        // else make it black, show different icon, and show the edit icon
        if(todo.isCompleted) {
            binding.container.strokeColor = Color.GRAY
            binding.tvTodo.setTextColor(Color.GRAY)
            binding.btnTodoComplete.setImageResource(R.drawable.ic_check_box)
            binding.btnEdit.visibility = View.GONE
        }else {
            binding.container.strokeColor = ContextCompat.getColor(binding.root.context, R.color.colorPrimary)
            binding.tvTodo.setTextColor(Color.BLACK)
            binding.btnTodoComplete.setImageResource(R.drawable.ic_blank_check_box)
            binding.btnEdit.visibility = View.VISIBLE
        }

        // toggle the task completion(complete/incomplete) with this action
        binding.btnTodoComplete.setOnClickListener {
            callback?.onClickTodoLocal(todo, TodoClickEvent.ACTION_COMPLETE, adapterPosition)
        }

        // show the task's details
        binding.tvTodo.setOnClickListener {
            callback?.onClickTodoLocal(todo, TodoClickEvent.ACTION_DETAILS, adapterPosition)
        }

        // edit task
        binding.btnEdit.setOnClickListener {
            callback?.onClickTodoLocal(todo, TodoClickEvent.ACTION_EDIT, adapterPosition)
        }

        // delete the task
        binding.btnDelete.setOnClickListener {
            callback?.onClickTodoLocal(todo, TodoClickEvent.ACTION_DELETE, adapterPosition)
        }
    }
}