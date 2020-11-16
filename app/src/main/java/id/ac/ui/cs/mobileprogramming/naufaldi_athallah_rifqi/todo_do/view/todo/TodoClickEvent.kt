package id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.view.todo

import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.data.models.Todo

interface TodoClickEvent {

    companion object {
        // actions are used to make the callback method generic for all
        const val ACTION_COMPLETE = "complete"
        const val ACTION_DETAILS = "details"
        const val ACTION_EDIT = "edit"
        const val ACTION_DELETE = "delete"
    }

    fun onClickTodo(todo: Todo, action: String, position: Int)
}