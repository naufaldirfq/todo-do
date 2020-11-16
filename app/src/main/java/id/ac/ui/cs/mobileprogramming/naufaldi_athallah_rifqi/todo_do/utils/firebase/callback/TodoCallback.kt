package id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.utils.firebase.callback

import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.data.models.Todo

interface TodoCallback {
    fun onResponse(todo: Todo?, error: String?)
}